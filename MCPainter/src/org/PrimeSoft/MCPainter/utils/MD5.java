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
package org.PrimeSoft.MCPainter.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is based on: http://www.rgagnon.com/javadetails/java-0416.html
 * @author SBPrime
 */
public class MD5 {
    /**
     * File read buffer size
     */
    private static final int BUF_SIZE = 1024 * 128;
    
    
    /**
     * Hex string array
     */
    private static final char[] HEX_CHAR_TABLE = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Calculate MD5 checksum for file
     *
     * @param file
     * @return MD5 checksum
     */
    private static byte[] createChecksum(File file) {
        try {
            InputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[BUF_SIZE];
            MessageDigest complete = MessageDigest.getInstance("MD5");

            int numRead;

            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            fis.close();
            return complete.digest();
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    /**
     * Convert byte array to hex string
     *
     * @param raw Byte array
     * @return Hex string
     */
    private static String getHexString(byte[] raw) {
        if (raw == null) {
            return "";
        }

        char[] result = new char[raw.length * 2];
        for (int i = 0; i < raw.length; i++) {
            int val = raw[i];
            int lo = val & 0x0f;
            int hi = (val & 0xf0) >> 4;
            result[i * 2 + 0] = HEX_CHAR_TABLE[hi];
            result[i * 2 + 1] = HEX_CHAR_TABLE[lo];
        }

        return new String(result);
    }

    
    /**
     * Get MD5 hash string for file
     * @param file file
     * @return MD5
     */
    public static String getMD5Checksum(File file) {
        try {
            byte[] b = createChecksum(file);
            return getHexString(b);
        } catch (Exception ex) {
            return "?";
        }
    }
}