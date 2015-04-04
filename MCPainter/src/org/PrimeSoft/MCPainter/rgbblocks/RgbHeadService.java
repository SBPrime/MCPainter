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
package org.PrimeSoft.MCPainter.rgbblocks;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.PrimeSoft.MCPainter.utils.InOutParam;
import org.PrimeSoft.MCPainter.utils.Utils;
import org.primesoft.simplehttpserver.api.HttpStatusCodes;
import org.primesoft.simplehttpserver.api.IHeaders;
import org.primesoft.simplehttpserver.api.IHttpRequest;
import org.primesoft.simplehttpserver.api.IService;

/**
 *
 * @author SBPrime
 */
public class RgbHeadService implements IService {

    private static final int W = 32;
    private static final int H = 16;
    private static final int MAX_CACHE = 4096;

    public static final String SERVICE = "/MCPainter/Heads/Rgb";
    public static final String IMG_FILE = ".png";

    public final LinkedHashMap<Integer, byte[]> m_colorCatch = new LinkedHashMap<Integer, byte[]>(MAX_CACHE + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, byte[]> eldest) {
            return size() > MAX_CACHE;
        }
    };

    @Override
    public void handle(IHttpRequest request) throws IOException {
        IHeaders outHeaders = request.getResponseHeader();
        Color c = getColor(request.getUri());

        if (c == null) {
            request.sendResponse(HttpStatusCodes.BAD_REQUEST, 0);
            return;
        }

        Integer color = c.getRGB();
        byte[] data;

        synchronized (m_colorCatch) {
            if (m_colorCatch.containsKey(color)) {
                data = m_colorCatch.get(color);
            } else {
                BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_4BYTE_ABGR);
                int[] array = new int[W * H];
                Arrays.fill(array, color);
                img.setRGB(0, 0, W, H, array, 0, W);

                ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
                ImageIO.write(img, "PNG", imgStream);
                imgStream.close();
                data = imgStream.toByteArray();

                m_colorCatch.put(color, data);
            }
        }

        outHeaders.add("Content-Type", "image/png");
        request.sendResponse(HttpStatusCodes.OK, data.length);
        OutputStream os = request.getResponseBody();
        os.write(data);
        os.close();
    }

    /**
     * Extract parameters from the URI
     *
     * @param uri
     * @return
     */
    private Color getColor(URI uri) {
        if (uri == null) {
            return null;
        }

        String path = uri.getPath();
        if (path == null) {
            return null;
        }

        path = path.toLowerCase();
        int pos1 = path.indexOf(SERVICE.toLowerCase());
        int pos2 = path.indexOf(IMG_FILE.toLowerCase());
        if (pos1 != 0 || pos2 < pos1) {
            return null;
        }

        String s = path.substring(pos1 + SERVICE.length() + 1, pos2);
        InOutParam<Integer> v = InOutParam.Out();
        if (!Utils.tryParse(s, v)) {
            return null;
        }
        
        return new Color(v.getValue());
    }

    public static String buildUrl(Color c, String externalAddress) {
        return externalAddress + SERVICE.substring(1)
                + "/" + c.getRGB() + IMG_FILE;
    }
}
