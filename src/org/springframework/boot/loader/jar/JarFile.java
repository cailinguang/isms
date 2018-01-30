package org.springframework.boot.loader.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.data.RandomAccessData.ResourceAccess;
import org.springframework.boot.loader.data.RandomAccessDataFile;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarFile
  extends java.util.jar.JarFile
  implements Iterable<JarEntryData>
{
  private static final AsciiBytes META_INF = new AsciiBytes("META-INF/");
  private static final AsciiBytes MANIFEST_MF = new AsciiBytes("META-INF/MANIFEST.MF");
  private static final AsciiBytes SIGNATURE_FILE_EXTENSION = new AsciiBytes(".SF");
  private static final String PROTOCOL_HANDLER = "java.protocol.handler.pkgs";
  private static final String HANDLERS_PACKAGE = "org.springframework.boot.loader";
  private static final AsciiBytes SLASH = new AsciiBytes("/");
  private final RandomAccessDataFile rootFile;
  private final String pathFromRoot;
  private final RandomAccessData data;
  private final List<JarEntryData> entries;
  private SoftReference<Map<AsciiBytes, JarEntryData>> entriesByName;
  private boolean signed;
  private JarEntryData manifestEntry;
  private SoftReference<Manifest> manifest;
  private URL url;
  
  public JarFile(File file)
    throws IOException
  {
    this(new RandomAccessDataFile(file));
  }
  
  JarFile(RandomAccessDataFile file)
    throws IOException
  {
    this(file, "", file);
  }
  
  private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data)
    throws IOException
  {
    super(rootFile.getFile());
    CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
    this.rootFile = rootFile;
    this.pathFromRoot = pathFromRoot;
    this.data = getArchiveData(endRecord, data);
    this.entries = loadJarEntries(endRecord);
  }
  
  private JarFile(RandomAccessDataFile rootFile, String pathFromRoot, RandomAccessData data, List<JarEntryData> entries, JarEntryFilter... filters)
    throws IOException
  {
    super(rootFile.getFile());
    this.rootFile = rootFile;
    this.pathFromRoot = pathFromRoot;
    this.data = data;
    this.entries = filterEntries(entries, filters);
  }
  
  private RandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, RandomAccessData data)
  {
    long offset = endRecord.getStartOfArchive(data);
    if (offset == 0L) {
      return data;
    }
    return data.getSubsection(offset, data.getSize() - offset);
  }
  
  private List<JarEntryData> loadJarEntries(CentralDirectoryEndRecord endRecord)
    throws IOException
  {
    RandomAccessData centralDirectory = endRecord.getCentralDirectory(this.data);
    int numberOfRecords = endRecord.getNumberOfRecords();
    List<JarEntryData> entries = new ArrayList(numberOfRecords);
    InputStream inputStream = centralDirectory.getInputStream(RandomAccessData.ResourceAccess.ONCE);
    try
    {
      JarEntryData entry = JarEntryData.fromInputStream(this, inputStream);
      while (entry != null)
      {
        entries.add(entry);
        processEntry(entry);
        entry = JarEntryData.fromInputStream(this, inputStream);
      }
    }
    finally
    {
      inputStream.close();
    }
    return entries;
  }
  
  private List<JarEntryData> filterEntries(List<JarEntryData> entries, JarEntryFilter[] filters)
  {
    List<JarEntryData> filteredEntries = new ArrayList(entries.size());
    for (JarEntryData entry : entries)
    {
      AsciiBytes name = entry.getName();
      for (JarEntryFilter filter : filters) {
        name = (filter == null) || (name == null) ? name : filter.apply(name, entry);
      }
      if (name != null)
      {
        JarEntryData filteredCopy = entry.createFilteredCopy(this, name);
        filteredEntries.add(filteredCopy);
        processEntry(filteredCopy);
      }
    }
    return filteredEntries;
  }
  
  private void processEntry(JarEntryData entry)
  {
    AsciiBytes name = entry.getName();
    if (name.startsWith(META_INF)) {
      processMetaInfEntry(name, entry);
    }
  }
  
  private void processMetaInfEntry(AsciiBytes name, JarEntryData entry)
  {
    if (name.equals(MANIFEST_MF)) {
      this.manifestEntry = entry;
    }
    if (name.endsWith(SIGNATURE_FILE_EXTENSION)) {
      this.signed = true;
    }
  }
  
  protected final RandomAccessDataFile getRootJarFile()
  {
    return this.rootFile;
  }
  
  RandomAccessData getData()
  {
    return this.data;
  }
  
  public Manifest getManifest()
    throws IOException
  {
    if (this.manifestEntry == null) {
      return null;
    }
    Manifest manifest = this.manifest == null ? null : (Manifest)this.manifest.get();
    if (manifest == null)
    {
      InputStream inputStream = this.manifestEntry.getInputStream();
      try
      {
        manifest = new Manifest(inputStream);
        

        inputStream.close();
      }
      finally
      {
        inputStream.close();
      }
    }
    return manifest;
  }
  
  public Enumeration<java.util.jar.JarEntry> entries()
  {
    final Iterator<JarEntryData> iterator = iterator();
    return new Enumeration()
    {
      public boolean hasMoreElements()
      {
        return iterator.hasNext();
      }
      
      public java.util.jar.JarEntry nextElement()
      {
        return ((JarEntryData)iterator.next()).asJarEntry();
      }
    };
  }
  
  public Iterator<JarEntryData> iterator()
  {
    return this.entries.iterator();
  }
  
  public JarEntry getJarEntry(String name)
  {
    return (JarEntry)getEntry(name);
  }
  
  public ZipEntry getEntry(String name)
  {
    JarEntryData jarEntryData = getJarEntryData(name);
    return jarEntryData == null ? null : jarEntryData.asJarEntry();
  }
  
  public JarEntryData getJarEntryData(String name)
  {
    if (name == null) {
      return null;
    }
    return getJarEntryData(new AsciiBytes(name));
  }
  
  public JarEntryData getJarEntryData(AsciiBytes name)
  {
    if (name == null) {
      return null;
    }
    Map<AsciiBytes, JarEntryData> entriesByName = this.entriesByName == null ? null : (Map)this.entriesByName.get();
    if (entriesByName == null)
    {
      entriesByName = new HashMap();
      for (JarEntryData entry : this.entries) {
        entriesByName.put(entry.getName(), entry);
      }
      this.entriesByName = new SoftReference(entriesByName);
    }
    JarEntryData entryData = (JarEntryData)entriesByName.get(name);
    if ((entryData == null) && (!name.endsWith(SLASH))) {
      entryData = (JarEntryData)entriesByName.get(name.append(SLASH));
    }
    return entryData;
  }
  
  boolean isSigned()
  {
    return this.signed;
  }
  
  void setupEntryCertificates()
  {
    try
    {
      JarInputStream inputStream = new JarInputStream(getData().getInputStream(RandomAccessData.ResourceAccess.ONCE));
      try
      {
        java.util.jar.JarEntry entry = inputStream.getNextJarEntry();
        while (entry != null)
        {
          inputStream.closeEntry();
          JarEntry jarEntry = getJarEntry(entry.getName());
          if (jarEntry != null) {
            jarEntry.setupCertificates(entry);
          }
          entry = inputStream.getNextJarEntry();
        }
      }
      finally
      {
        inputStream.close();
      }
    }
    catch (IOException ex)
    {
      throw new IllegalStateException(ex);
    }
  }
  
  public synchronized InputStream getInputStream(ZipEntry ze)
    throws IOException
  {
    return getContainedEntry(ze).getSource().getInputStream();
  }
  
  public synchronized JarFile getNestedJarFile(ZipEntry entry)
    throws IOException
  {
    return getNestedJarFile(getContainedEntry(entry).getSource());
  }
  
  public synchronized JarFile getNestedJarFile(JarEntryData sourceEntry)
    throws IOException
  {
    try
    {
      if (sourceEntry.nestedJar == null) {
        sourceEntry.nestedJar = createJarFileFromEntry(sourceEntry);
      }
      return sourceEntry.nestedJar;
    }
    catch (IOException ex)
    {
      throw new IOException("Unable to open nested jar file '" + sourceEntry.getName() + "'", ex);
    }
  }
  
  private JarFile createJarFileFromEntry(JarEntryData sourceEntry)
    throws IOException
  {
    if (sourceEntry.isDirectory()) {
      return createJarFileFromDirectoryEntry(sourceEntry);
    }
    return createJarFileFromFileEntry(sourceEntry);
  }
  
  private JarFile createJarFileFromDirectoryEntry(JarEntryData sourceEntry)
    throws IOException
  {
    final AsciiBytes sourceName = sourceEntry.getName();
    JarEntryFilter filter = new JarEntryFilter()
    {
      public AsciiBytes apply(AsciiBytes name, JarEntryData entryData)
      {
        if ((name.startsWith(sourceName)) && (!name.equals(sourceName))) {
          return name.substring(sourceName.length());
        }
        return null;
      }
    };
    return new JarFile(this.rootFile, this.pathFromRoot + "!/" + sourceEntry.getName().substring(0, sourceName.length() - 1), this.data, this.entries, new JarEntryFilter[] { filter });
  }
  
  private JarFile createJarFileFromFileEntry(JarEntryData sourceEntry)
    throws IOException
  {
    if (sourceEntry.getMethod() != 0) {
      throw new IllegalStateException("Unable to open nested entry '" + sourceEntry.getName() + "'. It has been compressed and nested " + "jar files must be stored without compression. Please check the " + "mechanism used to create your executable jar file");
    }
    return new JarFile(this.rootFile, this.pathFromRoot + "!/" + sourceEntry.getName(), sourceEntry.getData());
  }
  
  public synchronized JarFile getFilteredJarFile(JarEntryFilter... filters)
    throws IOException
  {
    return new JarFile(this.rootFile, this.pathFromRoot, this.data, this.entries, filters);
  }
  
  private JarEntry getContainedEntry(ZipEntry zipEntry)
    throws IOException
  {
    if (((zipEntry instanceof JarEntry)) && 
      (((JarEntry)zipEntry).getSource().getSource() == this)) {
      return (JarEntry)zipEntry;
    }
    throw new IllegalArgumentException("ZipEntry must be contained in this file");
  }
  
  public int size()
  {
    return (int)this.data.getSize();
  }
  
  public void close()
    throws IOException
  {
    this.rootFile.close();
  }
  
  public URL getUrl()
    throws MalformedURLException
  {
    if (this.url == null)
    {
      Handler handler = new Handler(this);
      String file = this.rootFile.getFile().toURI() + this.pathFromRoot + "!/";
      file = file.replace("file:////", "file://");
      this.url = new URL("jar", "", -1, file, handler);
    }
    return this.url;
  }
  
  public String toString()
  {
    return getName();
  }
  
  public String getName()
  {
    String path = this.pathFromRoot;
    return this.rootFile.getFile() + path;
  }
  
  public static void registerUrlProtocolHandler()
  {
    String handlers = System.getProperty("java.protocol.handler.pkgs");
    System.setProperty("java.protocol.handler.pkgs", handlers + "|" + "org.springframework.boot.loader");
    
    resetCachedUrlHandlers();
  }
  
  private static void resetCachedUrlHandlers()
  {
    try
    {
      URL.setURLStreamHandlerFactory(null);
    }
    catch (Error localError) {}
  }
}
