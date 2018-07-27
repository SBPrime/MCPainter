/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primesoft.mcpainter.mods;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * This class provides description for a mod file
 *
 * @author SBPrime
 */
public class Mod {
    /**
     * Mod description file regex pattern
     */
    private final static Pattern s_regexPattern = Pattern.compile("[\\t\" ]*([^:\"]+)[\"\\t ]*\\:[\"\\t ]*([^\",\\r\\n]+).*");
    private final static String MODID = "modid";
    private final static String NAME = "name";
    private final static String VERSTION = "version";
    /**
     * The source zip file
     */
    private final File m_zipFile;
    /**
     * Mod name
     */
    private final String m_modName;
    /**
     * Mod version
     */
    private final String m_version;
    /**
     * The mod Id
     */
    private final String m_modId;

    /**
     * Get the mod zip file
     *
     * @return
     */
    public File getFile() {
        return m_zipFile;
    }

    /**
     * Get the mod name
     *
     * @return
     */
    public String getModName() {
        return m_modName;
    }

    /**
     * The mod Id
     *
     * @return
     */
    public String getModId() {
        return m_modId;
    }

    /**
     * The mod version
     */
    public String getModVersion() {
        return m_version;
    }

    /**
     * Initialize the mod description class based on the file
     *
     * @param f
     */
    public Mod(File f) throws IOException {
        if (!f.exists()) {
            throw new IOException("File not found");
        }
        if (f.isDirectory()) {
            throw new IOException("Not a file");
        }

        ZipFile zipFile = null;
        try {
            try {
                zipFile = new ZipFile(f);
            } catch (ZipException ex) {
                throw new IOException("Corrupted mod pack file file", ex);
            } catch (IOException ex) {
                throw new IOException("Unable to read mod pack file", ex);
            }

            HashMap<String, String> values = parseEntries(zipFile);

            m_zipFile = f;

            String modId = values.get(MODID);            
            if (modId == null || modId.trim().length() == 0) {
                modId = f.getName();
            }
            
            String modName = values.get(NAME);
            if (modName == null || modName.trim().length() == 0) {
                modName = "unkonwn mod";
            }            

            String version = values.get(VERSTION);
            if (version == null || version.trim().length() == 0) {
                version = "unknown";
            }

            m_modName = modName.trim();
            m_version = version.trim();
            m_modId = modId.trim();            
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception ex) {
                    //Ignore
                }
            }
        }
    }

    /**
     * Try loading and parsing the mcmod.info file
     *
     * @param zipFile
     * @return
     */
    private HashMap<String, String> parseEntries(ZipFile zipFile) {
        ZipEntry entry = zipFile.getEntry("mcmod.info");
        HashMap<String, String> result = new HashMap<String, String>();

        if (entry == null) {
            return result;
        }

        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));

            String line;
            do {
                line = in.readLine();

                if (line != null) {
                    Matcher m = s_regexPattern.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).toLowerCase();
                        if (result.containsKey(key)) {
                            result.remove(key);
                        }
                        result.put(key, m.group(2));
                    }
                }
            } while (line != null);
        } catch (IOException ex) {
        }

        return result;
    }
}
