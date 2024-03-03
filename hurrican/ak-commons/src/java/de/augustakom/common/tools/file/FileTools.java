/*
 * Copyright (c) 2004 - AugustaKom Telekommunikation GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 10.08.2005 13:23:27
 */
package de.augustakom.common.tools.file;

import java.io.*;
import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;

import de.augustakom.common.InitializeLog4J;
import de.augustakom.common.tools.lang.DateTools;
import de.augustakom.common.tools.lang.StringTools;


/**
 * Klasse fuer File-Hilfsoperationen.
 */
public class FileTools {

    private static final Logger LOGGER = Logger.getLogger(FileTools.class);

    /**
     * Kopiert die Datei <code>toCopy</code> in die Datei <code>destination</code>.
     *
     * @param toCopy        zu kopierende Datei
     * @param destination   Ziel-Datei
     * @param deleteSrcFile Flag bestimmt, ob die Quell-Datei nach dem Kopieren geloescht werden soll.
     * @throws IOException wenn beim Kopieren ein Fehler auftritt.
     */
    public static void copyFile(File toCopy, File destination, boolean deleteSrcFile) throws IOException {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(toCopy);
            fout = new FileOutputStream(destination);
            byte[] buf = new byte[8096];
            int read;
            while ((read = fin.read(buf)) > 0) {
                fout.write(buf, 0, read);
            }
            fin.close();
            fout.flush();
            fout.close();

            if (deleteSrcFile) {
                if (destination.exists()) {
                    boolean deleted = toCopy.delete();
                    if (!deleted) {
                        throw new IOException("Source-Datei wurde nicht geloescht!");
                    }
                }
                else {
                    throw new IOException("Die Ziel-Datei konnte nicht gefunden werden. Source-Datei wurde " +
                            "deshalb nicht geloescht!");
                }
            }
        }
        finally {
            closeStreamSilent(fin);
            closeStreamSilent(fout);
        }
    }

    /**
     * Kopiert oder verschiebt die angegebene Datei in das Ziel-Verzeichnis.
     *
     * @param toMove
     * @param destDir
     * @param deleteSrc
     * @throws IOException
     */
    public static void copyFile2Dir(File toMove, File destDir, boolean deleteSrc) throws IOException {
        File destFile = new File(destDir, toMove.getName());
        copyFile(toMove, destFile, deleteSrc);
    }

    /**
     * Kopiert oder verschiebt die angegebene Datei in das Ziel-Verzeichnis. <br> Durch die Angabe von 'makeTmp' (true)
     * kann angegeben werden, ob waehrend der Kopie die Datei die Endung '.tmp' erhalten soll. Nach dem erfolgreichen
     * Kopieren wird diese Endung wieder entfernt.
     *
     * @param toMove
     * @param destDir
     * @param deleteSrc
     * @param makeTmp
     * @throws IOException
     *
     */
    public static void copyFile2Dir(File toMove, File destDir, boolean deleteSrc, boolean makeTmp) throws IOException {
        String filename = (makeTmp) ? toMove.getName() + ".tmp" : toMove.getName();
        File destFile = new File(destDir, filename);
        copyFile(toMove, destFile, deleteSrc);
        if (makeTmp) {
            boolean renamed = destFile.renameTo(new File(destDir, toMove.getName()));
            if (!renamed) {
                throw new IOException(StringTools.formatString(
                        "File was not renamed! File: {0}; Renamed: {1}",
                        new Object[] { toMove.getAbsolutePath(), destFile.getAbsolutePath() }));
            }
        }
    }

    public static void deleteFile(File toDelete) throws IOException {
        if (toDelete != null) {
            boolean deleted = toDelete.delete();
            if (!deleted) {
                throw new IOException("File not deleted: " + toDelete.getAbsolutePath());
            }
        }
    }

    /**
     * Ermittelt die File-Extension von <code>file</code>.
     *
     * @param file
     * @return die File-Extension (z.B. 'xml')
     */
    public static String getExtension(File file) {
        if (file != null) {
            return StringUtils.substringAfterLast(file.getName(), ".");
        }
        return null;
    }

    public static void closeStreamSilent(Closeable toClose) {
        if (toClose != null) {
            try {
                toClose.close();
            }
            catch (Exception e) {
                LOGGER.debug("closeStreamSilent() - got exception closing stream", e);
            }
        }
    }

    /**
     * Wandelt das File <code>file</code> in ein byte-Array um.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] convertToByteArray(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            long length = file.length();

            if (length > Integer.MAX_VALUE) {
                throw new IOException("File is to large to convert in a byte-Array");
            }

            byte[] bytes = new byte[(int) length];
            int offset = 0;
            int numRead;
            while ((offset < bytes.length)
                    && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            return bytes;
        }
        finally {
            // Close the input stream and return bytes
            closeStreamSilent(is);
        }
    }

    /**
     * Erzeugt aus dem byte-Array 'data' ein persistentes File.
     *
     * @param data
     * @param parent Pfad fuer das File
     * @param file   Name fuer das File
     * @throws IOException
     */
    public static File createFile(byte[] data, String parent, String file) throws IOException {
        File f = new File(parent, file);
        FileOutputStream fout = new FileOutputStream(f);
        try {
            fout.write(data);
        }
        finally {
            closeStreamSilent(fout);
        }
        return f;
    }

    /**
     * Versucht eine Datei mit <em>fileName</em> in einem vordefinierten Set von Verzeichnissen anzulegen. Gelingt dies
     * nicht, so wird die IOException geworfen.
     *
     * @param preferredParentDir
     * @param fileName
     * @return
     * @throws IOException
     */
    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    public static File createWriteFile(String preferredParentDir, String fileName) throws IOException {
        final List<String> parents = ImmutableList.of(preferredParentDir, InitializeLog4J.getLogDirectory(), "H:\\");
        for (String parent : parents) {
            final File file = new File(parent, fileName);
            try {
                // testen, ob auf file geschrieben werden kann
                new FileOutputStream(file).close();
            }
            catch (IOException e) {
                continue;
            }
            return file;
        }
        throw new IOException("Kann Datei in keinem der Ordner anlegen:" + parents);
    }

}
