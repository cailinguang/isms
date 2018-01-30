package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.jar.Manifest;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.data.RandomAccessData.ResourceAccess;
import org.springframework.boot.loader.jar.JarEntryData;
import org.springframework.boot.loader.jar.JarEntryFilter;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarFileArchive
  extends Archive
{
  private static final AsciiBytes UNPACK_MARKER = new AsciiBytes("UNPACK:");
  private static final int BUFFER_SIZE = 32768;
  private final JarFile jarFile;
  private final List<Archive.Entry> entries;
  private URL url;
  private File tempUnpackFolder;
  
  public JarFileArchive(File file)
    throws IOException
  {
    this(file, null);
  }
  
  public JarFileArchive(File file, URL url)
    throws IOException
  {
    this(new JarFile(file));
    this.url = url;
  }
  
  public JarFileArchive(JarFile jarFile)
  {
    this.jarFile = jarFile;
    ArrayList<Archive.Entry> jarFileEntries = new ArrayList();
    for (JarEntryData data : jarFile) {
      jarFileEntries.add(new JarFileEntry(data));
    }
    this.entries = Collections.unmodifiableList(jarFileEntries);
  }
  
  public URL getUrl()
    throws MalformedURLException
  {
    if (this.url != null) {
      return this.url;
    }
    return this.jarFile.getUrl();
  }
  
  public Manifest getManifest()
    throws IOException
  {
    return this.jarFile.getManifest();
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
    return Collections.unmodifiableCollection(this.entries);
  }
  
  protected Archive getNestedArchive(Archive.Entry entry)
    throws IOException
  {
    JarEntryData data = ((JarFileEntry)entry).getJarEntryData();
    if (data.getComment().startsWith(UNPACK_MARKER)) {
      return getUnpackedNestedArchive(data);
    }
    JarFile jarFile = this.jarFile.getNestedJarFile(data);
    return new JarFileArchive(jarFile);
  }
  
  private Archive getUnpackedNestedArchive(JarEntryData data)
    throws IOException
  {
    String name = data.getName().toString();
    if (name.lastIndexOf("/") != -1) {
      name = name.substring(name.lastIndexOf("/") + 1);
    }
    File file = new File(getTempUnpackFolder(), name);
    if ((!file.exists()) || (file.length() != data.getSize())) {
      unpack(data, file);
    }
    return new JarFileArchive(file, file.toURI().toURL());
  }
  
  private File getTempUnpackFolder()
  {
    if (this.tempUnpackFolder == null)
    {
      File tempFolder = new File(System.getProperty("java.io.tmpdir"));
      this.tempUnpackFolder = createUnpackFolder(tempFolder);
    }
    return this.tempUnpackFolder;
  }
  
  private File createUnpackFolder(File parent)
  {
    int attempts = 0;
    while (attempts++ < 1000)
    {
      String fileName = new File(this.jarFile.getName()).getName();
      
      File unpackFolder = new File(parent, fileName + "-spring-boot-libs-" + UUID.randomUUID());
      if (unpackFolder.mkdirs()) {
        return unpackFolder;
      }
    }
    throw new IllegalStateException("Failed to create unpack folder in directory '" + parent + "'");
  }
  
  private void unpack(JarEntryData data, File file)
    throws IOException
  {
    InputStream inputStream = data.getData().getInputStream(RandomAccessData.ResourceAccess.ONCE);
    try
    {
      OutputStream outputStream = new FileOutputStream(file);
      try
      {
        byte[] buffer = new byte[32768];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, bytesRead);
        }
      }
      finally {}
    }
    finally
    {
      inputStream.close();
    }
  }
  
  public Archive getFilteredArchive(final Archive.EntryRenameFilter filter)
    throws IOException
  {
    JarFile filteredJar = this.jarFile.getFilteredJarFile(new JarEntryFilter[] { new JarEntryFilter()
    {
      public AsciiBytes apply(AsciiBytes name, JarEntryData entryData)
      {
        return filter.apply(name, new JarFileArchive.JarFileEntry(entryData));
      }
    } });
    return new JarFileArchive(filteredJar);
  }
  
  private static class JarFileEntry
    implements Archive.Entry
  {
    private final JarEntryData entryData;
    
    JarFileEntry(JarEntryData entryData)
    {
      this.entryData = entryData;
    }
    
    public JarEntryData getJarEntryData()
    {
      return this.entryData;
    }
    
    public boolean isDirectory()
    {
      return this.entryData.isDirectory();
    }
    
    public AsciiBytes getName()
    {
      return this.entryData.getName();
    }
  }
}
