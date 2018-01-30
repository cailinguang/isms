package org.springframework.boot.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.Entry;
import org.springframework.boot.loader.archive.Archive.EntryFilter;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.FilteredArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.util.AsciiBytes;
import org.springframework.boot.loader.util.SystemPropertyUtils;

public class PropertiesLauncher
        extends Launcher
{
    private static final Logger logger = Logger.getLogger(Launcher.class.getName());
    public static final String MAIN = "loader.main";
    public static final String PATH = "loader.path";
    public static final String HOME = "loader.home";
    public static final String ARGS = "loader.args";
    public static final String CONFIG_NAME = "loader.config.name";
    public static final String CONFIG_LOCATION = "loader.config.location";
    public static final String SET_SYSTEM_PROPERTIES = "loader.system";
    private static final List<String> DEFAULT_PATHS = Arrays.asList(new String[0]);
    private static final Pattern WORD_SEPARATOR = Pattern.compile("\\W+");
    private static final URL[] EMPTY_URLS = new URL[0];
    private final File home;
    private final JavaAgentDetector javaAgentDetector;
    private List<String> paths = new ArrayList(DEFAULT_PATHS);
    private final Properties properties = new Properties();
    private Archive parent;

    public PropertiesLauncher()
    {
        this(new InputArgumentsJavaAgentDetector());
    }

    PropertiesLauncher(JavaAgentDetector javaAgentDetector)
    {
        if (!isDebug()) {
            logger.setLevel(Level.SEVERE);
        }
        try
        {
            this.home = getHomeDirectory();
            this.javaAgentDetector = javaAgentDetector;
            initializeProperties(this.home);
            initializePaths();
            this.parent = createArchive();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException(ex);
        }
    }

    private boolean isDebug()
    {
        String debug = System.getProperty("debug");
        if ((debug != null) && (!"false".equals(debug))) {
            return true;
        }
        debug = System.getProperty("DEBUG");
        if ((debug != null) && (!"false".equals(debug))) {
            return true;
        }
        debug = System.getenv("DEBUG");
        if ((debug != null) && (!"false".equals(debug))) {
            return true;
        }
        return false;
    }

    protected File getHomeDirectory()
    {
        return new File(SystemPropertyUtils.resolvePlaceholders(System.getProperty("loader.home", "${user.dir}")));
    }

    private void initializeProperties(File home)
            throws Exception, IOException
    {
        String config = "classpath:" + SystemPropertyUtils.resolvePlaceholders(
                SystemPropertyUtils.getProperty("loader.config.name", "application")) + ".properties";

        config = SystemPropertyUtils.resolvePlaceholders(
                SystemPropertyUtils.getProperty("loader.config.location", config));
        InputStream resource = getResource(config);
        if (resource != null)
        {
            logger.info("Found: " + config);
            try
            {
                this.properties.load(resource);
            }
            finally
            {
                resource.close();
            }
            for (Object key : Collections.list(this.properties.propertyNames()))
            {
                String text = this.properties.getProperty((String)key);
                String value = SystemPropertyUtils.resolvePlaceholders(this.properties, text);
                if (value != null) {
                    this.properties.put(key, value);
                }
            }
            if (SystemPropertyUtils.resolvePlaceholders("${loader.system:false}").equals("true"))
            {
                logger.info("Adding resolved properties to System properties");
                for (Object key : Collections.list(this.properties.propertyNames()))
                {
                    String value = this.properties.getProperty((String)key);
                    System.setProperty((String)key, value);
                }
            }
        }
        else
        {
            logger.info("Not found: " + config);
        }
    }

    private InputStream getResource(String config)
            throws Exception
    {
        if (config.startsWith("classpath:")) {
            return getClasspathResource(config.substring("classpath:".length()));
        }
        config = stripFileUrlPrefix(config);
        if (isUrl(config)) {
            return getURLResource(config);
        }
        return getFileResource(config);
    }

    private String stripFileUrlPrefix(String config)
    {
        if (config.startsWith("file:"))
        {
            config = config.substring("file:".length());
            if (config.startsWith("//")) {
                config = config.substring(2);
            }
        }
        return config;
    }

    private boolean isUrl(String config)
    {
        return config.contains("://");
    }

    private InputStream getClasspathResource(String config)
    {
        while (config.startsWith("/")) {
            config = config.substring(1);
        }
        config = "/" + config;
        logger.fine("Trying classpath: " + config);
        return getClass().getResourceAsStream(config);
    }

    private InputStream getFileResource(String config)
            throws Exception
    {
        File file = new File(config);
        logger.fine("Trying file: " + config);
        if (file.canRead()) {
            return new FileInputStream(file);
        }
        return null;
    }

    private InputStream getURLResource(String config)
            throws Exception
    {
        URL url = new URL(config);
        if (exists(url))
        {
            URLConnection con = url.openConnection();
            try
            {
                return con.getInputStream();
            }
            catch (IOException ex)
            {
                if ((con instanceof HttpURLConnection)) {
                    ((HttpURLConnection)con).disconnect();
                }
                throw ex;
            }
        }
        return null;
    }

    private boolean exists(URL url)
            throws IOException
    {
        URLConnection connection = url.openConnection();
        try
        {
            connection.setUseCaches(connection
                    .getClass().getSimpleName().startsWith("JNLP"));
            HttpURLConnection httpConnection;
            if ((connection instanceof HttpURLConnection))
            {
                httpConnection = (HttpURLConnection)connection;
                httpConnection.setRequestMethod("HEAD");
                int responseCode = httpConnection.getResponseCode();
                boolean bool;
                if (responseCode == 200) {
                    return true;
                }
                if (responseCode == 404) {
                    return false;
                }
            }
            return connection.getContentLength() >= 0 ? true : false;
        }
        finally
        {
            if ((connection instanceof HttpURLConnection)) {
                ((HttpURLConnection)connection).disconnect();
            }
        }
    }

    private void initializePaths()
            throws IOException
    {
        String path = SystemPropertyUtils.getProperty("loader.path");
        if (path == null) {
            path = this.properties.getProperty("loader.path");
        }
        if (path != null) {
            this.paths = parsePathsProperty(
                    SystemPropertyUtils.resolvePlaceholders(path));
        }
        logger.info("Nested archive paths: " + this.paths);
    }

    private List<String> parsePathsProperty(String commaSeparatedPaths)
    {
        List<String> paths = new ArrayList();
        for (String path : commaSeparatedPaths.split(","))
        {
            path = cleanupPath(path);
            if (!path.equals("")) {
                paths.add(path);
            }
        }
        if (paths.isEmpty()) {
            paths.add("lib");
        }
        return paths;
    }

    protected String[] getArgs(String... args)
            throws Exception
    {
        String loaderArgs = getProperty("loader.args");
        if (loaderArgs != null)
        {
            String[] defaultArgs = loaderArgs.split("\\s+");
            String[] additionalArgs = args;
            args = new String[defaultArgs.length + additionalArgs.length];
            System.arraycopy(defaultArgs, 0, args, 0, defaultArgs.length);
            System.arraycopy(additionalArgs, 0, args, defaultArgs.length, additionalArgs.length);
        }
        return args;
    }

    protected String getMainClass()
            throws Exception
    {
        String mainClass = getProperty("loader.main", "Start-Class");
        if (mainClass == null) {
            throw new IllegalStateException("No 'loader.main' or 'Start-Class' specified");
        }
        return mainClass;
    }

    protected ClassLoader createClassLoader(List<Archive> archives)
            throws Exception
    {
        ClassLoader loader = super.createClassLoader(archives);
        String customLoaderClassName = getProperty("loader.classLoader");
        if (customLoaderClassName != null)
        {
            loader = wrapWithCustomClassLoader(loader, customLoaderClassName);
            logger.info("Using custom class loader: " + customLoaderClassName);
        }
        return loader;
    }

    private ClassLoader wrapWithCustomClassLoader(ClassLoader parent, String loaderClassName)
            throws Exception
    {
        Class<ClassLoader> loaderClass = (Class<ClassLoader>) Class.forName(loaderClassName, true, parent);
        try
        {
            return (ClassLoader)loaderClass.getConstructor(new Class[] { ClassLoader.class }).newInstance(new Object[] { parent });
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
            try
            {
                return (ClassLoader)loaderClass.getConstructor(new Class[] { new java.net.URL[]{}.getClass(), ClassLoader.class }).newInstance(new Object[] { new URL[0], parent });
            }
            catch (NoSuchMethodException localNoSuchMethodException1) {}
        }
        return (ClassLoader)loaderClass.newInstance();
    }

    private String getProperty(String propertyKey)
            throws Exception
    {
        return getProperty(propertyKey, null);
    }

    private String getProperty(String propertyKey, String manifestKey)
            throws Exception
    {
        if (manifestKey == null)
        {
            manifestKey = propertyKey.replace(".", "-");
            manifestKey = toCamelCase(manifestKey);
        }
        String property = SystemPropertyUtils.getProperty(propertyKey);
        if (property != null)
        {
            String value = SystemPropertyUtils.resolvePlaceholders(property);
            logger.fine("Property '" + propertyKey + "' from environment: " + value);
            return value;
        }
        if (this.properties.containsKey(propertyKey))
        {
            String value = SystemPropertyUtils.resolvePlaceholders(this.properties.getProperty(propertyKey));
            logger.fine("Property '" + propertyKey + "' from properties: " + value);
            return value;
        }
        try
        {
            Manifest manifest = new ExplodedArchive(this.home, false).getManifest();
            if (manifest != null)
            {
                String value = manifest.getMainAttributes().getValue(manifestKey);
                logger.fine("Property '" + manifestKey + "' from home directory manifest: " + value);

                return value;
            }
        }
        catch (IllegalStateException localIllegalStateException) {}
        Manifest manifest = createArchive().getManifest();
        if (manifest != null)
        {
            String value = manifest.getMainAttributes().getValue(manifestKey);
            if (value != null)
            {
                logger.fine("Property '" + manifestKey + "' from archive manifest: " + value);

                return value;
            }
        }
        return null;
    }

    protected List<Archive> getClassPathArchives()
            throws Exception
    {
        List<Archive> lib = new ArrayList();
        for (String path : this.paths) {
            for (Archive archive : getClassPathArchives(path))
            {
                List<Archive> nested = new ArrayList(archive.getNestedArchives(new ArchiveEntryFilter()));
                nested.add(0, archive);
                lib.addAll(nested);
            }
        }
        addParentClassLoaderEntries(lib);
        return lib;
    }

    private List<Archive> getClassPathArchives(String path)
            throws Exception
    {
        String root = cleanupPath(stripFileUrlPrefix(path));
        List<Archive> lib = new ArrayList();
        File file = new File(root);
        if (!isAbsolutePath(root)) {
            file = new File(this.home, root);
        }
        if (file.isDirectory())
        {
            logger.info("Adding classpath entries from " + file);
            Archive archive = new ExplodedArchive(file, false);
            lib.add(archive);
        }
        Archive archive = getArchive(file);
        if (archive != null)
        {
            logger.info("Adding classpath entries from archive " + archive
                    .getUrl() + root);
            lib.add(archive);
        }
        Archive nested = getNestedArchive(root);
        if (nested != null)
        {
            logger.info("Adding classpath entries from nested " + nested.getUrl() + root);
            lib.add(nested);
        }
        return lib;
    }

    private boolean isAbsolutePath(String root)
    {
        return (root.contains(":")) || (root.startsWith("/"));
    }

    private Archive getArchive(File file)
            throws IOException
    {
        String name = file.getName().toLowerCase();
        if ((name.endsWith(".jar")) || (name.endsWith(".zip"))) {
            return new JarFileArchive(file);
        }
        return null;
    }

    private Archive getNestedArchive(String root)
            throws Exception
    {
        if ((root.startsWith("/")) ||
                (this.parent.getUrl().equals(this.home.toURI().toURL()))) {
            return null;
        }
        Archive.EntryFilter filter = new PrefixMatchingArchiveFilter(root);
        if (this.parent.getNestedArchives(filter).isEmpty()) {
            return null;
        }
        return new FilteredArchive(this.parent, filter);
    }

    private void addParentClassLoaderEntries(List<Archive> lib)
            throws IOException, URISyntaxException
    {
        ClassLoader parentClassLoader = getClass().getClassLoader();
        List<Archive> urls = new ArrayList();
        for (URL url : getURLs(parentClassLoader)) {
            if (!this.javaAgentDetector.isJavaAgentJar(url))
            {
                Archive archive = createArchiveIfPossible(url);
                if (archive != null) {
                    urls.add(archive);
                }
            }
        }
        addNestedArchivesFromParent(urls);
        Iterator<Archive> iterator = urls.iterator();
        while (iterator.hasNext()){
            Archive archive = iterator.next();
            if (findArchive(lib, archive) < 0) {
                lib.add(archive);
            }
        }
    }

    private Archive createArchiveIfPossible(URL url)
            throws IOException, URISyntaxException
    {
        if ((url.toString().endsWith(".jar")) || (url.toString().endsWith(".zip"))) {
            return new JarFileArchive(new File(url.toURI()));
        }
        if (url.toString().endsWith("/*"))
        {
            String name = url.getFile();
            File dir = new File(name.substring(0, name.length() - 1));
            return dir.exists() ? new ExplodedArchive(dir, false) : null;
        }
        String filename = URLDecoder.decode(url.getFile(), "UTF-8");
        return new ExplodedArchive(new File(filename));
    }

    private void addNestedArchivesFromParent(List<Archive> urls)
    {
        int index = findArchive(urls, this.parent);
        if (index >= 0) {
            try
            {
                Archive nested = getNestedArchive("lib/");
                if (nested != null)
                {
                    List<Archive> extra = new ArrayList(nested.getNestedArchives(new ArchiveEntryFilter()));
                    urls.addAll(index + 1, extra);
                }
            }
            catch (Exception localException) {}
        }
    }

    private int findArchive(List<Archive> urls, Archive archive)
    {
        if (archive == null) {
            return -1;
        }
        int i = 0;
        for (Archive url : urls)
        {
            if (url.toString().equals(archive.toString())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private URL[] getURLs(ClassLoader classLoader)
    {
        if ((classLoader instanceof URLClassLoader)) {
            return ((URLClassLoader)classLoader).getURLs();
        }
        return EMPTY_URLS;
    }

    private String cleanupPath(String path)
    {
        path = path.trim();
        if (path.startsWith("./")) {
            path = path.substring(2);
        }
        if ((path.toLowerCase().endsWith(".jar")) || (path.toLowerCase().endsWith(".zip"))) {
            return path;
        }
        if (path.endsWith("/*")) {
            path = path.substring(0, path.length() - 1);
        } else if ((!path.endsWith("/")) && (!path.equals("."))) {
            path = path + "/";
        }
        return path;
    }

    public static void main(String[] args)
            throws Exception
    {
        PropertiesLauncher launcher = new PropertiesLauncher();
        args = launcher.getArgs(args);
        launcher.launch(args);
    }

    public static String toCamelCase(CharSequence string)
    {
        if (string == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        Matcher matcher = WORD_SEPARATOR.matcher(string);
        int pos = 0;
        while (matcher.find())
        {
            builder.append(capitalize(string.subSequence(pos, matcher.end()).toString()));
            pos = matcher.end();
        }
        builder.append(capitalize(string.subSequence(pos, string.length()).toString()));
        return builder.toString();
    }

    private static Object capitalize(String str)
    {
        StringBuilder sb = new StringBuilder(str.length());
        sb.append(Character.toUpperCase(str.charAt(0)));
        sb.append(str.substring(1));
        return sb.toString();
    }

    private static final class ArchiveEntryFilter
            implements Archive.EntryFilter
    {
        private static final AsciiBytes DOT_JAR = new AsciiBytes(".jar");
        private static final AsciiBytes DOT_ZIP = new AsciiBytes(".zip");

        public boolean matches(Archive.Entry entry)
        {
            return (entry.getName().endsWith(DOT_JAR)) || (entry.getName().endsWith(DOT_ZIP));
        }
    }

    private static final class PrefixMatchingArchiveFilter
            implements Archive.EntryFilter
    {
        private final AsciiBytes prefix;
        private final PropertiesLauncher.ArchiveEntryFilter filter = new PropertiesLauncher.ArchiveEntryFilter();

        private PrefixMatchingArchiveFilter(String prefix)
        {
            this.prefix = new AsciiBytes(prefix);
        }

        public boolean matches(Archive.Entry entry)
        {
            return (entry.getName().startsWith(this.prefix)) && (this.filter.matches(entry));
        }
    }
}
