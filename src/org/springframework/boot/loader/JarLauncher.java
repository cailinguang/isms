package org.springframework.boot.loader;

import java.util.List;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.Entry;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarLauncher
  extends ExecutableArchiveLauncher
{
  private static final AsciiBytes LIB = new AsciiBytes("lib/");
  
  public JarLauncher() {}
  
  protected JarLauncher(Archive archive)
  {
    super(archive);
  }
  
  protected boolean isNestedArchive(Archive.Entry entry)
  {
    return (!entry.isDirectory()) && (entry.getName().startsWith(LIB));
  }
  
  protected void postProcessClassPathArchives(List<Archive> archives)
    throws Exception
  {
    archives.add(0, getArchive());
  }
  
  public static void main(String[] args)
  {
    new JarLauncher().launch(args);
  }
}
