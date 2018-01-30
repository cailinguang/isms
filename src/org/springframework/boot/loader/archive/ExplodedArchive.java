package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

public class ExplodedArchive
        extends Archive
{
    private static final Set<String> SKIPPED_NAMES = new HashSet(
            Arrays.asList(new String[] { ".", ".." }));
    private static final AsciiBytes MANIFEST_ENTRY_NAME = new AsciiBytes("META-INF/MANIFEST.MF");
    private final File root;
    private Map<AsciiBytes, Archive.Entry> entries = new LinkedHashMap();
    private Manifest manifest;
    private boolean filtered = false;

    public ExplodedArchive(File root)
    {
        this(root, true);
    }

    public ExplodedArchive(File root, boolean recursive)
    {
        if ((!root.exists()) || (!root.isDirectory())) {
            throw new IllegalArgumentException("Invalid source folder " + root);
        }
        this.root = root;
        buildEntries(root, recursive);
        this.entries = Collections.unmodifiableMap(this.entries);
    }

    private ExplodedArchive(File root, Map<AsciiBytes, Archive.Entry> entries)
    {
        this.root = root;

        this.filtered = true;
        this.entries = Collections.unmodifiableMap(entries);
    }

    private void buildEntries(File file, boolean recursive)
    {
        FileEntry entry;
        if (!file.equals(this.root))
        {
            String name = file.toURI().getPath().substring(this.root.toURI().getPath().length());
            entry = new FileEntry(new AsciiBytes(name), file);
            this.entries.put(entry.getName(), entry);
        }
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File child : files) {
                if ((!SKIPPED_NAMES.contains(child.getName())) && (
                        (file.equals(this.root)) || (recursive) ||
                                (file.getName().equals("META-INF")))) {
                    buildEntries(child, recursive);
                }
            }
        }
    }

    public URL getUrl()
            throws MalformedURLException
    {
        FilteredURLStreamHandler handler = this.filtered ? new FilteredURLStreamHandler() : null;

        return new URL("file", "", -1, this.root.toURI().getPath(), handler);
    }

    public Manifest getManifest()
            throws IOException
    {
        if ((this.manifest == null) && (this.entries.containsKey(MANIFEST_ENTRY_NAME)))
        {
            FileEntry entry = (FileEntry)this.entries.get(MANIFEST_ENTRY_NAME);
            FileInputStream inputStream = new FileInputStream(entry.getFile());
            try
            {
                this.manifest = new Manifest(inputStream);


                inputStream.close();
            }
            finally
            {
                inputStream.close();
            }
        }
        return this.manifest;
    }

    public List<Archive> getNestedArchives(Archive.EntryFilter filter)
            throws IOException
    {
        List<Archive> nestedArchives = new ArrayList();
        for (Archive.Entry entry : getEntries()) {
            if (filter.matches(entry)) {
                nestedArchives.add(getNestedArchive(entry));
            }
        }
        return Collections.unmodifiableList(nestedArchives);
    }

    public Collection<Archive.Entry> getEntries()
    {
        return Collections.unmodifiableCollection(this.entries.values());
    }

    protected Archive getNestedArchive(Archive.Entry entry)
            throws IOException
    {
        File file = ((FileEntry)entry).getFile();
        return file.isDirectory() ? new ExplodedArchive(file) : new JarFileArchive(file);
    }

    public Archive getFilteredArchive(Archive.EntryRenameFilter filter)
            throws IOException
    {
        Map<AsciiBytes, Archive.Entry> filteredEntries = new LinkedHashMap();
        for (Map.Entry<AsciiBytes, Archive.Entry> entry : this.entries.entrySet())
        {
            AsciiBytes filteredName = filter.apply((AsciiBytes)entry.getKey(), (Archive.Entry)entry.getValue());
            if (filteredName != null) {
                filteredEntries.put(filteredName, new FileEntry(filteredName,
                        ((FileEntry)entry.getValue()).getFile()));
            }
        }
        return new ExplodedArchive(this.root, filteredEntries);
    }

    private class FileEntry
            implements Archive.Entry
    {
        private final AsciiBytes name;
        private final File file;

        FileEntry(AsciiBytes name, File file)
        {
            this.name = name;
            this.file = file;
        }

        public File getFile()
        {
            return this.file;
        }

        public boolean isDirectory()
        {
            return this.file.isDirectory();
        }

        public AsciiBytes getName()
        {
            return this.name;
        }
    }

    private class FilteredURLStreamHandler
            extends URLStreamHandler
    {
        private FilteredURLStreamHandler() {}

        protected URLConnection openConnection(URL url)
                throws IOException
        {
            String name = url.getPath().substring(ExplodedArchive.this.root.toURI().getPath().length());
            if (ExplodedArchive.this.entries.containsKey(new AsciiBytes(name))) {
                return new URL(url.toString()).openConnection();
            }
            return new ExplodedArchive.FileNotFoundURLConnection(url, name);
        }
    }

    private static class FileNotFoundURLConnection
            extends URLConnection
    {
        private final String name;

        FileNotFoundURLConnection(URL url, String name)
        {
            super(url);
            this.name = name;
        }

        public void connect()
                throws IOException
        {
            throw new FileNotFoundException(this.name);
        }
    }
}
