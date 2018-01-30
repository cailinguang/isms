package org.springframework.boot.loader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import org.springframework.boot.loader.jar.Handler;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.lang.UsesJava7;

public class LaunchedURLClassLoader
  extends URLClassLoader
{
  private static LockProvider LOCK_PROVIDER = new LockProvider();
  private final ClassLoader rootClassLoader;
  
  public LaunchedURLClassLoader(URL[] urls, ClassLoader parent)
  {
    super(urls, parent);
    this.rootClassLoader = findRootClassLoader(parent);
  }
  
  private ClassLoader findRootClassLoader(ClassLoader classLoader)
  {
    while (classLoader != null)
    {
      if (classLoader.getParent() == null) {
        return classLoader;
      }
      classLoader = classLoader.getParent();
    }
    return null;
  }
  
  public URL getResource(String name)
  {
    URL url = null;
    if (this.rootClassLoader != null) {
      url = this.rootClassLoader.getResource(name);
    }
    return url == null ? findResource(name) : url;
  }
  
  public URL findResource(String name)
  {
    try
    {
      if ((name.equals("")) && (hasURLs())) {
        return getURLs()[0];
      }
      Handler.setUseFastConnectionExceptions(true);
      try
      {
        return super.findResource(name);
      }
      finally
      {
        Handler.setUseFastConnectionExceptions(false);
      }
    }
    catch (IllegalArgumentException ex) {
        return null;
    }
  }
  
  public Enumeration<URL> findResources(String name)
    throws IOException
  {
    if ((name.equals("")) && (hasURLs())) {
      return Collections.enumeration(Arrays.asList(getURLs()));
    }
    Handler.setUseFastConnectionExceptions(true);
    try
    {
      return super.findResources(name);
    }
    finally
    {
      Handler.setUseFastConnectionExceptions(false);
    }
  }
  
  private boolean hasURLs()
  {
    return getURLs().length > 0;
  }
  
  public Enumeration<URL> getResources(String name)
    throws IOException
  {
    if (this.rootClassLoader == null) {
      return findResources(name);
    }
    return new ResourceEnumeration(this.rootClassLoader.getResources(name), findResources(name));
  }
  
  protected Class<?> loadClass(String name, boolean resolve)
    throws ClassNotFoundException
  {
    synchronized (LOCK_PROVIDER.getLock(this, name))
    {
      Class<?> loadedClass = findLoadedClass(name);
      if (loadedClass == null)
      {
        Handler.setUseFastConnectionExceptions(true);
        try
        {
          loadedClass = doLoadClass(name);
        }
        finally
        {
          Handler.setUseFastConnectionExceptions(false);
        }
      }
      if (resolve) {
        resolveClass(loadedClass);
      }
      return loadedClass;
    }
  }
  
  private Class<?> doLoadClass(String name)
    throws ClassNotFoundException
  {
    try
    {
      if (this.rootClassLoader != null) {
        return this.rootClassLoader.loadClass(name);
      }
    }
    catch (Exception localException) {}
    try
    {
      findPackage(name);
      return findClass(name);
    }
    catch (Exception localException1) {}
    return super.loadClass(name, false);
  }
  
  private void findPackage(String name)
    throws ClassNotFoundException
  {
    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      String packageName = name.substring(0, lastDot);
      if (getPackage(packageName) == null) {
        try
        {
          definePackageForFindClass(name, packageName);
        }
        catch (Exception localException) {}
      }
    }
  }
  
  private void definePackageForFindClass(final String name, final String packageName)
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws ClassNotFoundException
        {
          String path = name.replace('.', '/').concat(".class");
          for (URL url : LaunchedURLClassLoader.this.getURLs()) {
            try
            {
              if ((url.getContent() instanceof JarFile))
              {
                JarFile jarFile = (JarFile)url.getContent();
                if ((jarFile.getJarEntryData(path) != null) && 
                  (jarFile.getManifest() != null))
                {
                  LaunchedURLClassLoader.this.definePackage(packageName, jarFile.getManifest(), url);
                  
                  return null;
                }
              }
            }
            catch (IOException localIOException) {}
          }
          return null;
        }
      }, AccessController.getContext());
    }
    catch (PrivilegedActionException localPrivilegedActionException) {}
  }
  
  @UsesJava7
  private static LockProvider setupLockProvider()
  {
    try
    {
      ClassLoader.registerAsParallelCapable();
      return new Java7LockProvider();
    }
    catch (NoSuchMethodError ex) {}
    return new LockProvider();
  }
  
  private static class LockProvider
  {
    public Object getLock(LaunchedURLClassLoader classLoader, String className)
    {
      return classLoader;
    }
  }
  
  @UsesJava7
  private static class Java7LockProvider
    extends LaunchedURLClassLoader.LockProvider
  {
    private Java7LockProvider()
    {
      super();
    }
    
    public Object getLock(LaunchedURLClassLoader classLoader, String className)
    {
      return classLoader.getClassLoadingLock(className);
    }
  }
  
  private static class ResourceEnumeration
    implements Enumeration<URL>
  {
    private final Enumeration<URL> rootResources;
    private final Enumeration<URL> localResources;
    
    ResourceEnumeration(Enumeration<URL> rootResources, Enumeration<URL> localResources)
    {
      this.rootResources = rootResources;
      this.localResources = localResources;
    }
    
    public boolean hasMoreElements()
    {
      try
      {
        Handler.setUseFastConnectionExceptions(true);
        
        return (this.rootResources.hasMoreElements()) || (this.localResources.hasMoreElements());
      }
      finally
      {
        Handler.setUseFastConnectionExceptions(false);
      }
    }
    
    public URL nextElement()
    {
      if (this.rootResources.hasMoreElements()) {
        return (URL)this.rootResources.nextElement();
      }
      return (URL)this.localResources.nextElement();
    }
  }
}
