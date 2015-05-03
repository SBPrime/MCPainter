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
package org.PrimeSoft.MCPainter.utils;

import java.util.Arrays;
import java.util.HashSet;
import static org.PrimeSoft.MCPainter.MCPainterMain.log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author SBPrime
 */
public class JSONExtensions {

    /**
     * Try get a complex value from JSON object
     *
     * @param o
     * @param property
     * @return
     */
    public static JSONObject tryGet(JSONObject o, String property) {
        if (o == null || property == null) {
            return null;
        }

        if (!o.containsKey(property)) {
            return null;
        }

        Object value = o.get(property);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        return null;
    }

    /**
     * Try to get a string value from JSON object
     *
     * @param o
     * @param property
     * @param defaultValue
     * @return
     */
    public static String tryGetString(JSONObject o, String property, String defaultValue) {
        if (o == null || property == null) {
            return defaultValue;
        }

        if (!o.containsKey(property)) {
            return defaultValue;
        }

        Object value = o.get(property);

        if (value == null) {
            return defaultValue;
        }

        return value.toString();
    }

    /**
     * Try to get a int value from JSON object
     *
     * @param o
     * @param property
     * @param defaultValue
     * @return
     */
    public static int tryGetInt(JSONObject o, String property, int defaultValue) {
        if (o == null || property == null) {
            return defaultValue;
        }

        if (!o.containsKey(property)) {
            return defaultValue;
        }

        Object value = o.get(property);

        if (value == null || !(value instanceof Integer)) {
            return defaultValue;
        }

        return (Integer) value;
    }

    /**
     * Try to get a boolean value from JSON object
     *
     * @param o
     * @param property
     * @param defaultValue
     * @return
     */
    public static boolean tryGetBool(JSONObject o, String property, boolean defaultValue) {
        if (o == null || property == null) {
            return defaultValue;
        }

        if (!o.containsKey(property)) {
            return defaultValue;
        }

        Object value = o.get(property);

        if (value == null || !(value instanceof Boolean)) {
            return defaultValue;
        }

        return (Boolean) value;
    }

    /**
     * Try to get a double value from JSON object
     *
     * @param o
     * @param property
     * @param defaultValue
     * @return
     */
    public static double tryGetDouble(JSONObject o, String property, double defaultValue) {
        if (o == null || property == null) {
            return defaultValue;
        }

        if (!o.containsKey(property)) {
            return defaultValue;
        }

        Object value = o.get(property);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Integer) {
            return (double) ((Integer) value);
        }

        if (value instanceof Double) {
            return (Double) value;
        }

        if (value instanceof Float) {
            return (Float) value;
        }

        return defaultValue;
    }

    /**
     * Try to get a long value from JSON object
     *
     * @param o
     * @param property
     * @return
     */
    public static long[] tryGetLongArray(JSONObject o, String property) {
        JSONArray array = tryGetArray(o, property);

        if (array == null) {
            return null;
        }

        long[] result = new long[array.size()];
        for (int i = 0; i < result.length; i++) {
            Object value = array.get(i);
            long dValue;

            if (value instanceof Integer) {
                dValue = (Integer) value;
            } else if (value instanceof Long) {
                dValue = (Long) value;
            } else if (value instanceof Short) {
                dValue = (Short) value;
            } else if (value instanceof Byte) {
                dValue = (Byte) value;
            } else {
                return null;
            }

            result[i] = dValue;
        }

        return result;
    }
    
    
    /**
     * Try to get a int value from JSON object
     *
     * @param o
     * @param property
     * @return
     */
    public static int[] tryGetIntArray(JSONObject o, String property) {
        JSONArray array = tryGetArray(o, property);

        if (array == null) {
            return null;
        }

        int[] result = new int[array.size()];
        for (int i = 0; i < result.length; i++) {
            Object value = array.get(i);
            int dValue;

            if (value instanceof Long) {
                dValue = ((Long) value).intValue();
            } else if (value instanceof Integer) {
                dValue = (Integer) value;
            } else if (value instanceof Short) {
                dValue = (Short) value;
            } else if (value instanceof Byte) {
                dValue = (Byte) value;
            } else {
                return null;
            }

            result[i] = dValue;
        }

        return result;
    }

    /**
     * Try to get a int value from JSON object
     *
     * @param o
     * @param property
     * @return
     */
    public static double[] tryGetDoubleArray(JSONObject o, String property) {
        JSONArray array = tryGetArray(o, property);

        if (array == null) {
            return null;
        }

        double[] result = new double[array.size()];
        for (int i = 0; i < result.length; i++) {
            Object value = array.get(i);
            double dValue;

            if (value instanceof Double) {
                dValue = (Double) value;
            } else if (value instanceof Float) {
                dValue = (Float) value;
            } else if (value instanceof Long) {
                dValue = (Long) value;
            } else if (value instanceof Integer) {
                dValue = (Integer) value;
            } else if (value instanceof Short) {
                dValue = (Short) value;
            } else if (value instanceof Byte) {
                dValue = (Byte) value;
            } else {
                return null;
            }

            result[i] = dValue;
        }

        return result;
    }

    /**
     * Print all unused properties
     *
     * @param data
     * @param usedProps
     * @param msg
     */
    public static void printUnused(JSONObject data, String[] usedProps, String msg) {
        HashSet<String> props = new HashSet<String>();
        for (String s : usedProps) {
            if (!props.contains(s)) {
                props.add(s);
            }
        }

        for (Object k : data.keySet()) {
            if (k != null && !props.contains(k.toString())) {
                log(msg + k.toString());
            }
        }
    }

    /**
     * Try to get the JSONArray
     *
     * @param o
     * @param property
     * @return
     */
    public static JSONArray tryGetArray(JSONObject o, String property) {
        if (o == null || property == null) {
            return null;
        }

        if (!o.containsKey(property)) {
            return null;
        }

        Object value = o.get(property);
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        return null;
    }
}
