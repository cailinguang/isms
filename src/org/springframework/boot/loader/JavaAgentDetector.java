package org.springframework.boot.loader;

import java.net.URL;

public abstract interface JavaAgentDetector
{
  public abstract boolean isJavaAgentJar(URL paramURL);
}
