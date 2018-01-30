package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

public class FilteredArchive
  extends Archive
{
  private final Archive parent;
  private final Archive.EntryFilter filter;
  
  public FilteredArchive(Archive parent, Archive.EntryFilter filter)
  {
    this.parent = parent;
    this.filter = filter;
  }
  
  public URL getUrl()
    throws MalformedURLException
  {
    return this.parent.getUrl();
  }
  
  public String getMainClass()
    throws Exception
  {
    return this.parent.getMainClass();
  }
  
  public Manifest getManifest()
    throws IOException
  {
    return this.parent.getManifest();
  }
  
  public Collection<Archive.Entry> getEntries()
  {
    List<Archive.Entry> nested = new ArrayList();
    for (Archive.Entry entry : this.parent.getEntries()) {
      if (this.filter.matches(entry)) {
        nested.add(entry);
      }
    }
    return Collections.unmodifiableList(nested);
  }
  
  public List<Archive> getNestedArchives(final Archive.EntryFilter filter)
    throws IOException
  {
    return this.parent.getNestedArchives(new Archive.EntryFilter()
    {
      public boolean matches(Archive.Entry entry)
      {
        return (FilteredArchive.this.filter.matches(entry)) && (filter.matches(entry));
      }
    });
  }
  
  public Archive getFilteredArchive(final Archive.EntryRenameFilter filter)
    throws IOException
  {
    return this.parent.getFilteredArchive(new Archive.EntryRenameFilter()
    {
      public AsciiBytes apply(AsciiBytes entryName, Archive.Entry entry)
      {
        return FilteredArchive.this.filter.matches(entry) ? filter.apply(entryName, entry) : null;
      }
    });
  }
}
