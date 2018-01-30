package org.springframework.boot.loader.jar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

class JarURLConnection
  extends java.net.JarURLConnection
{
  private static final FileNotFoundException FILE_NOT_FOUND_EXCEPTION = new FileNotFoundException();
  private static final String SEPARATOR = "!/";
  private static final URL EMPTY_JAR_URL;
  
  static
  {
    try
    {
      EMPTY_JAR_URL = new URL("jar:", null, 0, "file:!/", new URLStreamHandler()
      {
        protected URLConnection openConnection(URL u)
          throws IOException
        {
          return null;
        }
      });
    }
    catch (MalformedURLException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  private static final JarEntryName EMPTY_JAR_ENTRY_NAME = new JarEntryName("");
  private static final String FILE_COLON_DOUBLE_SLASH = "file://";
  private static ThreadLocal<Boolean> useFastExceptions = new ThreadLocal();
  private final JarFile jarFile;
  private JarEntryData jarEntryData;
  private URL jarFileUrl;
  private JarEntryName jarEntryName;
  
  protected JarURLConnection(URL url, JarFile jarFile)
    throws IOException
  {
    super(EMPTY_JAR_URL);
    this.url = url;
    
    String spec = getNormalizedFile(url).substring(jarFile.getUrl().getFile().length());
    int separator;
    while ((separator = spec.indexOf("!/")) > 0)
    {
      jarFile = getNestedJarFile(jarFile, spec.substring(0, separator));
      spec = spec.substring(separator + "!/".length());
    }
    this.jarFile = jarFile;
    this.jarEntryName = getJarEntryName(spec);
  }
  
  private String getNormalizedFile(URL url)
  {
    if (!url.getFile().startsWith("file://")) {
      return url.getFile();
    }
    return "file:" + url.getFile().substring("file://".length());
  }
  
  private JarFile getNestedJarFile(JarFile jarFile, String name)
    throws IOException
  {
    JarEntry jarEntry = jarFile.getJarEntry(name);
    if (jarEntry == null) {
      throwFileNotFound(jarEntry, jarFile);
    }
    return jarFile.getNestedJarFile(jarEntry);
  }
  
  private JarEntryName getJarEntryName(String spec)
  {
    if (spec.length() == 0) {
      return EMPTY_JAR_ENTRY_NAME;
    }
    return new JarEntryName(spec);
  }
  
  public void connect()
    throws IOException
  {
    if (!this.jarEntryName.isEmpty())
    {
      this.jarEntryData = this.jarFile.getJarEntryData(this.jarEntryName.asAsciiBytes());
      if (this.jarEntryData == null) {
        throwFileNotFound(this.jarEntryName, this.jarFile);
      }
    }
    this.connected = true;
  }
  
  private void throwFileNotFound(Object entry, JarFile jarFile)
    throws FileNotFoundException
  {
    if (Boolean.TRUE.equals(useFastExceptions.get())) {
      throw FILE_NOT_FOUND_EXCEPTION;
    }
    throw new FileNotFoundException("JAR entry " + entry + " not found in " + jarFile.getName());
  }
  
  public Manifest getManifest()
    throws IOException
  {
    try
    {
      return super.getManifest();
    }
    finally
    {
      this.connected = false;
    }
  }
  
  public JarFile getJarFile()
    throws IOException
  {
    connect();
    return this.jarFile;
  }
  
  public URL getJarFileURL()
  {
    if (this.jarFileUrl == null) {
      this.jarFileUrl = buildJarFileUrl();
    }
    return this.jarFileUrl;
  }
  
  private URL buildJarFileUrl()
  {
    try
    {
      String spec = this.jarFile.getUrl().getFile();
      if (spec.endsWith("!/")) {
        spec = spec.substring(0, spec.length() - "!/".length());
      }
      if (spec.indexOf("!/") == -1) {
        return new URL(spec);
      }
      return new URL("jar:" + spec);
    }
    catch (MalformedURLException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  public JarEntry getJarEntry()
    throws IOException
  {
    connect();
    return this.jarEntryData == null ? null : this.jarEntryData.asJarEntry();
  }
  
  public String getEntryName()
  {
    return this.jarEntryName.toString();
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    connect();
    if (this.jarEntryName.isEmpty()) {
      throw new IOException("no entry name specified");
    }
    return this.jarEntryData.getInputStream();
  }
  
  public int getContentLength()
  {
    try
    {
      connect();
      if (this.jarEntryData != null) {
        return this.jarEntryData.getSize();
      }
      return this.jarFile.size();
    }
    catch (IOException ex) {}
    return -1;
  }
  
  public Object getContent()
    throws IOException
  {
    connect();
    return this.jarEntryData == null ? this.jarFile : super.getContent();
  }
  
  public String getContentType()
  {
    return this.jarEntryName.getContentType();
  }


  static void setUseFastExceptions(boolean useFastExceptions)
  {
      JarURLConnection.useFastExceptions.set(Boolean.valueOf(useFastExceptions));
  }
  
  private static class JarEntryName
  {
    private final AsciiBytes name;
    private String contentType;
    
    JarEntryName(String spec)
    {
      this.name = decode(spec);
    }
    
    private AsciiBytes decode(String source)
    {
      int length = source == null ? 0 : source.length();
      if ((length == 0) || (source.indexOf('%') < 0)) {
        return new AsciiBytes(source);
      }
      ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
      for (int i = 0; i < length; i++)
      {
        int ch = source.charAt(i);
        if (ch == 37)
        {
          if (i + 2 >= length) {
            throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
          }
          ch = decodeEscapeSequence(source, i);
          i += 2;
        }
        bos.write(ch);
      }
      return new AsciiBytes(bos.toByteArray());
    }
    
    private char decodeEscapeSequence(String source, int i)
    {
      int hi = Character.digit(source.charAt(i + 1), 16);
      int lo = Character.digit(source.charAt(i + 2), 16);
      if ((hi == -1) || (lo == -1)) {
        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
      }
      return (char)((hi << 4) + lo);
    }
    
    public String toString()
    {
      return this.name.toString();
    }
    
    public AsciiBytes asAsciiBytes()
    {
      return this.name;
    }
    
    public boolean isEmpty()
    {
      return this.name.length() == 0;
    }
    
    public String getContentType()
    {
      if (this.contentType == null) {
        this.contentType = deduceContentType();
      }
      return this.contentType;
    }
    
    private String deduceContentType()
    {
      String type = isEmpty() ? "x-java/jar" : null;
      type = type != null ? type : URLConnection.guessContentTypeFromName(toString());
      type = type != null ? type : "content/unknown";
      return type;
    }
  }
}
