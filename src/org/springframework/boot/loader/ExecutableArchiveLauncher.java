package org.springframework.boot.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.Archive.Entry;
import org.springframework.boot.loader.archive.Archive.EntryFilter;

public abstract class ExecutableArchiveLauncher
  extends Launcher
{
  private final Archive archive;
  private final JavaAgentDetector javaAgentDetector;
  
  public ExecutableArchiveLauncher()
  {
    this(new InputArgumentsJavaAgentDetector());
  }
  
  public ExecutableArchiveLauncher(JavaAgentDetector javaAgentDetector)
  {
    try
    {
      this.archive = createArchive();
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
    this.javaAgentDetector = javaAgentDetector;
  }
  
  protected ExecutableArchiveLauncher(Archive archive)
  {
    this.javaAgentDetector = new InputArgumentsJavaAgentDetector();
    this.archive = archive;
  }
  
  protected final Archive getArchive()
  {
    return this.archive;
  }
  
  protected String getMainClass()
    throws Exception
  {
    return this.archive.getMainClass();
  }
  
  protected List<Archive> getClassPathArchives()
    throws Exception
  {
    List<Archive> archives = new ArrayList(this.archive.getNestedArchives(new Archive.EntryFilter()
    {
      public boolean matches(Archive.Entry entry)
      {
        return ExecutableArchiveLauncher.this.isNestedArchive(entry);
      }
    }));
    postProcessClassPathArchives(archives);
    return archives;
  }
  
  protected ClassLoader createClassLoader(URL[] urls)
    throws Exception
  {
    Set<URL> copy = new LinkedHashSet(urls.length);
    ClassLoader loader = getDefaultClassLoader();
    if ((loader instanceof URLClassLoader)) {
      for (URL url : ((URLClassLoader)loader).getURLs()) {
        if (addDefaultClassloaderUrl(urls, url)) {
          copy.add(url);
        }
      }
    }
    Collections.addAll(copy, urls);
    return super.createClassLoader((URL[])copy.toArray(new URL[copy.size()]));
  }
  
  private boolean addDefaultClassloaderUrl(URL[] urls, URL url)
  {
    String jarUrl = "jar:" + url + "!/";
    for (URL nestedUrl : urls) {
      if ((nestedUrl.equals(url)) || (nestedUrl.toString().equals(jarUrl))) {
        return false;
      }
    }
    return !this.javaAgentDetector.isJavaAgentJar(url);
  }
  
  protected abstract boolean isNestedArchive(Archive.Entry paramEntry);
  
  protected void postProcessClassPathArchives(List<Archive> archives)
    throws Exception
  {}
  
  private static ClassLoader getDefaultClassLoader()
  {
    ClassLoader classloader = null;
    try
    {
      classloader = Thread.currentThread().getContextClassLoader();
    }
    catch (Throwable localThrowable) {}
    if (classloader == null) {
      classloader = ExecutableArchiveLauncher.class.getClassLoader();
    }
    return classloader;
  }
}
