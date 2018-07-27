/*
 * The MIT License
 *
 * Copyright 2015 SBPrime.
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
package org.primesoft.mcpainter.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primesoft.mcpainter.MCPainterMain;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author SBPrime
 */
public class HttpUtils {

    /**
     * Download version page from the www
     *
     * @param url Version file http page
     * @return Version page content
     */
    public static String downloadPage(String url) {
        try {
            InputStreamReader is = new InputStreamReader(new URL(url).openStream());
            BufferedReader br = new BufferedReader(is);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            return sb.toString();
        } catch (Exception e) {
            MCPainterMain.log("Error downloading " + url + " " + e.getMessage());
            return null;
        }
    }

    public static JSONObject downloadJson(String url) {
        String content = downloadPage(url);

        if (content == null || content.isEmpty()) {
            return null;
        }

        try {
            return (JSONObject) JSONValue.parseWithException(content);
        } catch (ParseException e) {
            MCPainterMain.log("Unable to parse JSON for " + url + " " + e.getMessage());
            return null;
        }
    }
}
