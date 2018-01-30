package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.springframework.boot.loader.util.AsciiBytes;

public abstract class Archive
{
    public abstract URL getUrl()
            throws MalformedURLException;

    public String getMainClass()
            throws Exception
    {
        Manifest manifest = getManifest();
        String mainClass = null;
        if (manifest != null) {
            mainClass = manifest.getMainAttributes().getValue("Start-Class");
        }
        if (mainClass == null) {
            throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
        }
        return mainClass;
    }

    public String toString()
    {
        try
        {
            return getUrl().toString();
        }
        catch (Exception ex) {}
        return "archive";
    }

    public abstract Manifest getManifest()
            throws IOException;

    public abstract Collection<Entry> getEntries();

    public abstract List<Archive> getNestedArchives(EntryFilter paramEntryFilter)
            throws IOException;

    public abstract Archive getFilteredArchive(EntryRenameFilter paramEntryRenameFilter)
            throws IOException;

    public static abstract interface EntryRenameFilter
    {
        public abstract AsciiBytes apply(AsciiBytes paramAsciiBytes, Archive.Entry paramEntry);
    }

    public static abstract interface EntryFilter
    {
        public abstract boolean matches(Archive.Entry paramEntry);
    }

    public static abstract interface Entry
    {
        public abstract boolean isDirectory();

        public abstract AsciiBytes getName();
    }
}
