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
package org.PrimeSoft.MCPainter.voxelyzer;

import java.text.DecimalFormat;

/**
 * 3D transformations matrix
 *
 * @author SBPrime
 */
public class Matrix {

    /**
     * The matrix data
     */
    private final double[][] m_matrix;

    /**
     * Gets the identity matrix
     *
     * @return the matrix
     */
    public static Matrix getIdentity() {
        return new Matrix(new double[][]{
                    new double[]{1, 0, 0, 0},
                    new double[]{0, 1, 0, 0},
                    new double[]{0, 0, 1, 0},
                    new double[]{0, 0, 0, 1}
                });
    }

    /**
     * Gets the translation matrix
     *
     * @param x x axis move
     * @param y y axis move
     * @param z z axis move
     * @return the matrix
     */
    public static Matrix getTranslation(double x, double y, double z) {
        return new Matrix(new double[][]{
                    new double[]{1, 0, 0, x},
                    new double[]{0, 1, 0, y},
                    new double[]{0, 0, 1, z},
                    new double[]{0, 0, 0, 1}
                });
    }

    /**
     * Gets the scaling matrix
     *
     * @param x x axis scaling
     * @param y y axis scaling
     * @param z z axis scaling
     * @return the matrix
     */
    public static Matrix getScaling(double x, double y, double z) {
        return new Matrix(new double[][]{
                    new double[]{x, 0, 0, 0},
                    new double[]{0, y, 0, 0},
                    new double[]{0, 0, z, 0},
                    new double[]{0, 0, 0, 1}
                });
    }

    /**
     * Gets the x axis rotation matrix
     *
     * @param angle rotation angle
     * @return the matrix
     */
    public static Matrix getRotationX(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        return new Matrix(new double[][]{
                    new double[]{1, 0, 0, 0},
                    new double[]{0, c, s, 0},
                    new double[]{0, -s, c, 0},
                    new double[]{0, 0, 0, 1}
                });
    }

    /**
     * Gets the y axis rotation matrix
     *
     * @param angle rotation angle
     * @return the matrix
     */
    public static Matrix getRotationY(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        return new Matrix(new double[][]{
                    new double[]{c, 0, -s, 0},
                    new double[]{0, 1, 0, 0},
                    new double[]{s, 0, c, 0},
                    new double[]{0, 0, 0, 1}
                });
    }

    /**
     * Gets the z axis rotation matrix
     *
     * @param angle rotation angle
     * @return the matrix
     */
    public static Matrix getRotationZ(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);

        return new Matrix(new double[][]{
                    new double[]{c, s, 0, 0},
                    new double[]{-s, c, 0, 0},
                    new double[]{0, 0, 1, 0},
                    new double[]{0, 0, 0, 1}
                });
    }

    /**
     * Gets the rotation matrix
     *
     * @param x vector x
     * @param y vector y
     * @param z vector z
     * @param angle rotation angle
     * @return the matrix
     */
    public static Matrix getRotation(double x, double y, double z, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1 - c;
        double x2 = x * x;
        double y2 = y * y;
        double z2 = z * z;
        double xy = x * y;
        double xz = x * z;
        double yz = y * z;

        return new Matrix(new double[][]{
                    new double[]{t * x2 + c,        t * xy + s * z,     t * xz - s * y, 0},
                    new double[]{t * xy - s * z,    t * y2 + c,         t * yz + s * x, 0},
                    new double[]{t * xz + s * y,    t * yz - s * x,     t * z2 + c,     0},
                    new double[]{0,                 0,                  0,              1}
                });
    }

    /**
     * Creates a new instance of the matrix
     *
     * @param m the matrix values
     */
    private Matrix(double[][] m) {
        m_matrix = m;
    }

    /**
     * Get the matrix internal values
     *
     * @return value array
     */
    double[][] getM() {
        return m_matrix;
    }

    /**
     * Multiply two matrixs
     *
     * @param m1 matrix 1
     * @param m2 matrix 2
     * @return Multiplication result
     */
    public static Matrix multiply(Matrix m1, Matrix m2) {
        double m[][] = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += m1.m_matrix[j][k] * m2.m_matrix[k][i];
                }

                m[j][i] = sum;
            }
        }

        Matrix r = new Matrix(m);
        return r;
    }

    /**
     * Transform the matrix
     *
     * @return transformed matrix
     */
    public Matrix transform() {
        double m[][] = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = m_matrix[j][i];
            }
        }

        return new Matrix(m);
    }

    /**
     * Calculate the determinant
     *
     * @return determinant value
     */
    public double determinant() {
        double sum = 0.0;

        for (int i = 0; i < 4; i++) {
            double tmp = 1;
            for (int j = 0; j < 4; j++) {
                tmp *= m_matrix[j][(i + j) % 4];
            }
            sum += tmp;


            tmp = 1;
            for (int j = 0; j < 4; j++) {
                tmp *= m_matrix[j][(4 + 3 - i - j) % 4];
            }
            sum -= tmp;
        }
        return sum;
    }

    /**
     * Calculates the inverse matrix
     *
     * @return inverse matrix
     */
    public Matrix inverse() {
        double[][] m = new double[4][8];
        for (int i = 0; i < 4; i++) {
            double[] t = m_matrix[i];
            for (int j = 0; j < 4; j++) {
                m[i][j] = t[j];
            }
            m[i][4 + i] = 1;
        }

        for (int i = 0; i < 4; i++) {
            double multi = m[i][i];
            for (int j = 0; j < 8; j++) {
                m[i][j] /= multi;
            }

            for (int j = 0; j < 4; j++) {
                if (j != i) {
                    multi = m[j][i];
                    for (int k = 0; k < 8; k++) {
                        m[j][k] -= m[i][k] * multi;
                    }
                }
            }
        }

        double[][] n = new double[4][4];
        for (int i = 0; i < 4; i++) {
            double[] t = m[i];
            for (int j = 0; j < 4; j++) {
                n[i][j] = t[j + 4];
            }
        }

        return new Matrix(n);
    }

    /**
     * Copy matrix values from another matrix
     *
     * @param m source matrix
     */
    private void copyFrom(Matrix m) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m_matrix[i][j] = m.m_matrix[i][j];
            }
        }
    }

    /**
     * Rotate matrix
     *
     * @param x vector x
     * @param y vector y
     * @param z vector z
     * @param angle rotation angle
     */
    public void rotate(double x, double y, double z, double angle) {
        Matrix m = getRotation(x, y, z, angle);
        Matrix result = multiply(this, m);

        copyFrom(result);
    }

    /**
     * Rotate matrix along the x axis
     *
     * @param angle rotation angle
     */
    public void rotateX(double angle) {
        Matrix m = getRotationX(angle);
        Matrix result = multiply(this, m);

        copyFrom(result);
    }

    /**
     * Rotate matrix along the y axis
     *
     * @param angle rotation angle
     */
    public void rotateY(double angle) {
        Matrix m = getRotationY(angle);
        Matrix result = multiply(this, m);

        copyFrom(result);
    }

    /**
     * Rotate matrix along the z axis
     *
     * @param angle rotation angle
     */
    public void rotateZ(double angle) {
        Matrix m = getRotationZ(angle);
        Matrix result = multiply(this, m);

        copyFrom(result);
    }

    /**
     * Scale the matrix
     *
     * @param x x axis scale
     * @param y y axis scale
     * @param z z axis scale
     */
    public void scale(double x, double y, double z) {
        Matrix m = getScaling(x, y, z);
        Matrix result = multiply(this, m);

        copyFrom(result);
    }

    /**
     * Translate the matrix
     *
     * @param x x axis translation
     * @param y y axis translation
     * @param z z axis translation
     */
    public void translate(double x, double y, double z) {
        Matrix m = getTranslation(x, y, z);
        Matrix result = multiply(this, m);

        copyFrom(result);
    }

    /**
     * Apply matrix transformation
     *
     * @param v
     * @return
     */
    public Vertex applyMatrix(Vertex v) {
        final double[][] m = m_matrix;
        final double[] data = v.getData();

        final double[] l0 = m[0];
        final double[] l1 = m[1];
        final double[] l2 = m[2];

        double[] r = new double[]{
            l0[0] * data[0] + l0[1] * data[1] + l0[2] * data[2] + l0[3],
            l1[0] * data[0] + l1[1] * data[1] + l1[2] * data[2] + l1[3],
            l2[0] * data[0] + l2[1] * data[1] + l2[2] * data[2] + l2[3],
            data[3], data[3], data[4]};

        return new Vertex(r);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final DecimalFormat df = new DecimalFormat("#.##");

        for (int i = 0; i < m_matrix.length; i++) {
            double[] t = m_matrix[i];
            for (int j = 0; j < t.length; j++) {
                sb.append(df.format(t[j]));
                sb.append("\t");
            }
            sb.append("\n");
        }
        sb.append("------------------------");

        return sb.toString();
    }
}