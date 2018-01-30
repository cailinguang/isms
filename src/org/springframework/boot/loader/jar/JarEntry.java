package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

public class JarEntry
  extends java.util.jar.JarEntry
{
  private final JarEntryData source;
  private Certificate[] certificates;
  private CodeSigner[] codeSigners;
  
  public JarEntry(JarEntryData source)
  {
    super(source.getName().toString());
    this.source = source;
  }
  
  public JarEntryData getSource()
  {
    return this.source;
  }
  
  public URL getUrl()
    throws MalformedURLException
  {
    return new URL(this.source.getSource().getUrl(), getName());
  }
  
  public Attributes getAttributes()
    throws IOException
  {
    Manifest manifest = this.source.getSource().getManifest();
    return manifest == null ? null : manifest.getAttributes(getName());
  }
  
  public Certificate[] getCertificates()
  {
    if ((this.source.getSource().isSigned()) && (this.certificates == null)) {
      this.source.getSource().setupEntryCertificates();
    }
    return this.certificates;
  }
  
  public CodeSigner[] getCodeSigners()
  {
    if ((this.source.getSource().isSigned()) && (this.codeSigners == null)) {
      this.source.getSource().setupEntryCertificates();
    }
    return this.codeSigners;
  }
  
  void setupCertificates(java.util.jar.JarEntry entry)
  {
    this.certificates = entry.getCertificates();
    this.codeSigners = entry.getCodeSigners();
  }
}
