/*
 * The MIT License
 *
 * Copyright 2012 SBPrime.
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
package org.PrimeSoft.MCPainter.Commands;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SBPrime
 */
public final class Commands {

    public final static String COMMAND_MAIN = "MCPainter";
    public final static String COMMAND_IMAGE = "Image";
    public final static String COMMAND_IMAGEMAP = "ImageMap";
//    public final static String COMMAND_IMAGEHD = "ImageHd";
    public final static String COMMAND_BLOCK = "Block";
    public final static String COMMAND_STATUE = "Statue";
    public final static String COMMAND_RELOAD = "Reload";
    public final static String COMMAND_HELP = "Help";
    public final static String COMMAND_PURGE = "Purge";
    public final static String COMMAND_JOBS = "Jobs";
    public final static String COMMAND_FILTER = "Filter";
    public final static String COMMAND_MOB = "Mob";
    public final static String COMMAND_PALETTE = "Palette";
    public final static String ALT_STATUE = "/Statue";
    public final static String ALT_STATUE_URL = "/Statue_File";
    public final static String ALT_IMAGE = "/Image";
    public final static String ALT_MAP = "/Map";
    public final static String ALT_MOB = "/Mob";
    public final static String ALT_BLOCK = "/Block";
    
    public static String[] insertArgs(String[] args, String toInsert) {
        List<String> nArgs = new ArrayList<String>();
        nArgs.add(toInsert);
        for (String s : args) {
            nArgs.add(s);
        }
        return nArgs.toArray(new String[0]);
    }
}
