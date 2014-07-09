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
package org.PrimeSoft.MCPainter.voxelyzer.fileParsers;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.PrimeSoft.MCPainter.Drawing.ImageHelper;
import org.PrimeSoft.MCPainter.Drawing.RawImage;
import org.PrimeSoft.MCPainter.voxelyzer.Face;
import org.PrimeSoft.MCPainter.voxelyzer.Model;
import org.PrimeSoft.MCPainter.voxelyzer.Vertex;

/**
 *
 * @author SBPrime
 */
public class WavefrontObj {

    private static final String SPLIT = "[ \t]";
    private static final String SPLIT_FACE = "/";
    private static final String COMMENT = "#";
    private static final String VERTICE = "v";
    private static final String TEXTURE_COORDS = "vt";
    private static final String FACE = "f";
    private static final String MAT_FILE = "mtllib";
    private static final String MAT_USE = "usemtl";
    private static final String MAT_NAME = "newmtl";
    private static final String MAT_COLOR = "Kd";
    private static final String MAT_TEXTURE = "map_Kd";

    /**
     * Build the model
     *
     * @param vertices
     * @param faces
     * @return
     */
    private static Model buildModel(double[][] vertices,
            double[][] textureMaping, int[][] faces, Material[] materials) {
        Vertex[] rVertex = buildVertex(vertices);
        Face[] rFaces = buildFaces(faces, materials, textureMaping);

        return new Model(rVertex, rFaces);
    }

    /**
     * Build the result face list
     *
     * @param faces
     * @param materials
     * @param textureMaping
     * @return
     */
    private static Face[] buildFaces(int[][] faces, Material[] materials, double[][] textureMaping) {
        Face[] rFaces = new Face[faces.length];
        for (int idx = 0; idx < rFaces.length; idx++) {
            final Material material = materials[idx];
            final int[] verticeData = faces[idx];
            final int[] verticeIdx = new int[]{verticeData[0] - 1, verticeData[1] - 1, verticeData[2] - 1};

            final Face face;
            if (material != null) {
                RawImage img = material.getImage();
                int[] color = material.getColor();
                if (verticeData.length == 6 && img != null) {
                    int[] textureIdx = new int[]{verticeData[3] - 1, verticeData[4] - 1, verticeData[5] - 1};
                    boolean valid = textureIdx[0] >= 0 && textureIdx[0] < textureMaping.length
                            && textureIdx[1] >= 0 && textureIdx[1] < textureMaping.length
                            && textureIdx[2] >= 0 && textureIdx[2] < textureMaping.length;

                    if (valid) {
                        int w = img.getWidth();
                        int h = img.getHeight();
                        double[][] mapping = new double[3][];
                        for (int i = 0; i < 3; i++) {
                            double[] val = textureMaping[textureIdx[i]];
                            mapping[i] = new double[]{val[0] * w, h - val[1] * h};
                        }
                        face = new Face(verticeIdx, img, mapping);
                    } else {
                        face = new Face(verticeIdx);
                    }
                } else if (color != null) {
                    face = new Face(verticeIdx, color);
                } else {
                    face = new Face(verticeIdx);
                }
            } else {
                face = new Face(verticeIdx);
            }

            rFaces[idx] = face;
        }
        return rFaces;
    }

    /**
     * Build the result vertice list
     *
     * @param vertices
     * @return
     */
    private static Vertex[] buildVertex(double[][] vertices) {
        Vertex[] rVertex = new Vertex[vertices.length];
        for (int i = 0; i < rVertex.length; i++) {
            rVertex[i] = new Vertex(vertices[i]);
        }
        return rVertex;
    }

    /**
     * Parse the vertice
     *
     * @param parts
     * @return
     */
    private static double[] parseVertice(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Vertice format");
            return null;
        }

        try {
            return new double[]{Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])};
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Parse 3d face entry
     *
     * @param parts
     * @return
     */
    private static List<int[]> parseFace(String[] parts) {
        final List<int[]> result = new ArrayList<int[]>();
        if (parts.length < 4) {
            System.out.println("Face format");
            return result;
        }

        final int[][] parsed = new int[parts.length - 1][];

        try {
            for (int i = 1; i < parts.length; i++) {
                String[] l = parts[i].split(SPLIT_FACE);
                int[] pp;
                if (l.length > 1) {
                    pp = new int[]{Integer.parseInt(l[0]), Integer.parseInt(l[1])};
                } else {
                    pp = new int[]{Integer.parseInt(l[0])};
                }
                parsed[i - 1] = pp;
            }

            int a = 0;
            int b = 1;
            int c = 2;
            boolean left = true;
            while (a != c) {
                int[] vA = parsed[a];
                int[] vB = parsed[b];
                int[] vC = parsed[c];

                if (vA.length > 1 && vB.length > 1 && vC.length > 1) {
                    result.add(new int[]{vA[0], vB[0], vC[0], vA[1], vB[1], vC[1]});
                } else {
                    result.add(new int[]{vA[0], vB[0], vC[0]});
                }

                if (left) {
                    b = a;
                    a = (a - 1 + parsed.length) % parsed.length;
                } else {
                    b = c;
                    c = (c + 1) % parsed.length;
                }
                left = !left;
            }

            return result;
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
            return result;
        }

    }

    /**
     * parse material files
     *
     * @param modelFolder
     * @param parts
     * @return
     */
    private static List<Material> parseMaterial(File modelFolder, String[] parts) {
        final List<Material> result = new ArrayList<Material>();
        if (parts.length < 2) {
            return result;
        }
        File file = new File(modelFolder, parts[1]);
        if (!file.exists() || !file.canRead()) {
            return result;
        }

        BufferedReader reader = null;
        RawImage texture = null;
        String name = null;
        int[] color = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith(COMMENT)) {
                    line = reader.readLine();
                    continue;
                }
                final String[] matParts = splitLine(line);
                if (matParts.length > 0) {
                    if (matParts[0].equalsIgnoreCase(MAT_NAME)) {
                        if (name != null && name.length() > 0) {
                            Material m = null;
                            if (texture != null) {
                                result.add(new Material(name, texture));
                            } else if (color != null) {
                                result.add(new Material(name, color));
                            }
                        }

                        name = matParts.length > 1 ? matParts[1] : null;
                        color = null;
                        texture = null;
                    } else if (matParts[0].equalsIgnoreCase(MAT_COLOR)) {
                        color = parseMatColor(matParts);
                    } else if (matParts[0].equalsIgnoreCase(MAT_TEXTURE)) {
                        texture = parseMatTexture(matParts, modelFolder);
                    }

                }
                line = reader.readLine();
            }

            if (name != null && name.length() > 0) {
                Material m = null;
                if (texture != null) {
                    result.add(new Material(name, texture));
                } else if (color != null) {
                    result.add(new Material(name, color));
                }
            }

            return result;
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Parse the texture entry
     *
     * @param parts
     * @return
     */
    private static double[] parseTexture(String[] parts) {
        if (parts.length < 3) {
            System.out.println("texture format");
            return null;
        }
        try {
            return new double[]{Double.parseDouble(parts[1]), Double.parseDouble(parts[2])};
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Split string line
     *
     * @param line
     * @return
     */
    private static String[] splitLine(String line) {
        if (line == null) {
            return new String[0];
        }

        final List<String> tP = new ArrayList<String>();
        for (String ss : line.split(SPLIT)) {
            if (ss != null && ss.length() != 0) {
                tP.add(ss);
            }
        }
        return tP.toArray(new String[0]);
    }

    /**
     * Load model from file
     *
     * @param modelFolder
     * @param fileName
     * @return
     */
    public static Model load(File modelFolder, String fileName) {
        File file = new File(modelFolder, fileName);
        if (!file.exists() || !file.canRead()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            final HashMap<String, Material> materials = new HashMap<String, Material>();
            final List<double[]> vertices = new ArrayList<double[]>();
            final List<double[]> texture = new ArrayList<double[]>();
            final List<int[]> faces = new ArrayList<int[]>();
            final List<Material> facesMaterial = new ArrayList<Material>();
            Material selectedMaterial = null;

            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.startsWith(COMMENT)) {
                    line = reader.readLine();
                    continue;
                }
                String[] parts = splitLine(line);

                if (parts.length > 0) {
                    if (parts[0].equalsIgnoreCase(VERTICE)) {
                        vertices.add(parseVertice(parts));
                    } else if (parts[0].equalsIgnoreCase(FACE)) {
                        for (int[] ff : parseFace(parts)) {
                            faces.add(ff);
                            facesMaterial.add(selectedMaterial);
                        }
                    } else if (parts[0].equalsIgnoreCase(TEXTURE_COORDS)) {
                        texture.add(parseTexture(parts));
                    } else if (parts[0].equalsIgnoreCase(MAT_USE)) {
                        if (parts.length > 1) {
                            String name = parts[1];
                            if (materials.containsKey(name)) {
                                selectedMaterial = materials.get(name);
                            } else {
                                selectedMaterial = tryRecreateMaterial(modelFolder, name);
                                if (selectedMaterial != null) {
                                    materials.put(name, selectedMaterial);
                                }
                            }
                        }
                    } else if (parts[0].equalsIgnoreCase(MAT_FILE)) {
                        for (Material m : parseMaterial(modelFolder, parts)) {
                            if (m != null && !materials.containsKey(m.getName())) {
                                materials.put(m.getName(), m);
                            }
                        }
                    }
                }
                line = reader.readLine();
            }

            return buildModel(vertices.toArray(new double[0][]), texture.toArray(new double[0][]),
                    faces.toArray(new int[0][]), facesMaterial.toArray(new Material[0]));
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
            }
        }
    }

    private static RawImage parseMatTexture(String[] matParts, File modelFolder) {
        if (matParts.length < 2) {
            return null;
        }
        File imgFile = new File(modelFolder, matParts[1]);
        if (!imgFile.exists()) {
            imgFile = tryFind(modelFolder, matParts[1]);
            if (imgFile == null) {
                return null;
            }
        }
        if (!imgFile.exists() || !imgFile.canRead()) {
            return null;
        }

        final BufferedImage img = ImageHelper.openImage(imgFile);
        if (img == null) {
            return null;
        }

        return new RawImage(img, img.getWidth());
    }

    private static int[] parseMatColor(String[] matParts) {
        if (matParts.length < 4) {
            return null;
        }
        try {
            final double r = Double.parseDouble(matParts[1]);
            final double g = Double.parseDouble(matParts[2]);
            final double b = Double.parseDouble(matParts[3]);

            return new int[]{(int) (r * 0xff), (int) (g * 0xff), (int) (b * 0xff)};
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private static File tryFind(File modelFolder, String texture) {
        if (texture == null) {
            return null;
        }
        int idx = texture.lastIndexOf(".");
        if (idx > 0) {
            texture = texture.substring(0, idx - 1);
        }
        texture = texture.replace(' ', '_');

        for (final File f : modelFolder.listFiles()) {
            String name = f.getName();
            idx = name.lastIndexOf(".");
            if (idx > 0) {                
                name = name.substring(0, idx);
            }
            name = name.replace(' ', '_');

            if (texture.equalsIgnoreCase(name)) {
                return f;
            }
        }

        return null;
    }

    private static Material tryRecreateMaterial(File modelFolder, String name) {
        File imgFile = tryFind(modelFolder, name);
        if (imgFile == null)
        {
            return null;
        }
        
        if (!imgFile.canRead()) {
            return null;
        }

        final BufferedImage img = ImageHelper.openImage(imgFile);
        if (img == null) {
            return null;
        }

        return new Material(name, new RawImage(img, img.getWidth()));
    }
}
