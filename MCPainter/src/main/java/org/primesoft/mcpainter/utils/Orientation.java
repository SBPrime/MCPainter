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
package org.primesoft.mcpainter.utils;

import org.primesoft.mcpainter.Configuration.ConfigProvider;

/**
 *
 * @author SBPrime
 */
public class Orientation {

    private int[][] m_vectors;

    public int calcX(int x, int y, int z) {
        return m_vectors[0][0] * x
                + m_vectors[0][1] * y
                + m_vectors[0][2] * z;
    }

    public int calcY(int x, int y, int z) {
        return m_vectors[1][0] * x
                + m_vectors[1][1] * y
                + m_vectors[1][2] * z;
    }

    public int calcZ(int x, int y, int z) {
        return m_vectors[2][0] * x
                + m_vectors[2][1] * y
                + m_vectors[2][2] * z;
    }

    public Vector moveStart(Vector position, double yaw, double pitch) {
        return moveStart(position, yaw, pitch, ConfigProvider.BLOCK_SIZE);
    }

    
    public Vector moveStart(Vector position, double yaw, double pitch, Vector size)
    {
        return moveStart(position, yaw, pitch, (int)size.getX(), (int)size.getY(), (int)size.getZ());
    }
    
    public Vector moveStart(Vector position, double yaw, double pitch, int size) {
        return moveStart(position, yaw, pitch, size, size, size);
    }

    public Vector moveStart(Vector position, double yaw, double pitch, int sizeX, int sizeY, int sizeZ) {
        int wx = calcX(sizeX, sizeY, sizeZ);
        int wy = calcY(sizeX, sizeY, sizeZ);
        int wz = calcZ(sizeX, sizeY, sizeZ);
        int sx = (int)Math.signum(wx);
        int sy = (int)Math.signum(wy);
        int sz = (int)Math.signum(wz);

        int heding = 0;

        yaw = (yaw + 360) % 360;
        if (yaw < 45) {
            heding = 0;
        } else if (yaw < 135) {
            heding = 1;
        } else if (yaw < 225) {
            heding = 2;
        } else if (yaw < 315) {
            heding = 3;
        } else {
            heding = 0;
        }        
        
        Vector toMove = new Vector(0,0,0);
        
        if (pitch > 45) {
            switch (heding) {
                case 0://BOTTOM FRONT
                    toMove = new Vector(0, 0, sz < 0 ? -wz  : 1);
                    break;
                case 1://BOTTOM RIGHT
                    toMove = new Vector(sx > 0 ? -wx : -1, 0, 0);
                    break;
                case 2://BOTTOM BACK
                    toMove = new Vector(sx < 0 ? -(wx + 1) : 0, 0, sz > 0 ? -wz  : -1);
                    break;
                case 3://BOTTOM LEFT
                    toMove = new Vector(sx < 0 ? -wx : 1, 0, sz < 0 ? -(wz + 1) : 0);
                    break;
            }
        } else if (pitch < -45) {
            switch (heding) {
                case 0://TOP FRONT
                    toMove = new Vector(0, sy < 0 ? -( wy + 1) : 0, sz < 0 ? -wz  : 1);
                    break;
                case 1://TOP RIGHT
                    toMove = new Vector(sx > 0 ? -wx : -1, sy < 0 ? -( wy + 1) : 0, 0);
                    break;
                case 2://TOP BACK
                    toMove = new Vector(sx < 0 ? -(wx + 1) : 0, sy < 0 ? -( wy + 1) : 0, sz > 0 ? -wz  : -1);
                    break;
                case 3://TOP LEFT
                    toMove = new Vector(sx < 0 ? -wx : 1, sy < 0 ? -( wy + 1) : 0, sz < 0 ? -(wz + 1) : 0);
                    break;
            }
        } else {
            switch (heding) {
                case 0://FRONT
                    toMove = new Vector(0, 0, sz < 0 ? -wz  : 0);
                    break;
                case 1://RIGHT
                    toMove = new Vector(sx > 0 ? -wx : -1, 0, 0);
                    break;
                case 2://BACK
                    toMove = new Vector(sx < 0 ? -(wx + 1) : 0, 0, sz > 0 ? -wz  : -1);
                    break;
                case 3://LEFT
                    toMove = new Vector(sx < 0 ? -wx : 0, 0, sz < 0 ? -(wz + 1) : 0);
                    break;
            }
        }
        
        return position.add(toMove);
    }

    public Orientation(double yaw, double pitch) {
        int heding = 0;

        yaw = (yaw + 360) % 360;

        if (yaw < 45) {
            heding = 0;
        } else if (yaw < 135) {
            heding = 1;
        } else if (yaw < 225) {
            heding = 2;
        } else if (yaw < 315) {
            heding = 3;
        } else {
            heding = 0;
        }

        if (pitch > 45) {
            switch (heding) {
                case 0:
                    m_vectors = new int[][]{{-1, 0, 0}, {0, 0, 1}, {0, 1, 0}}; //BOTTOM FRONT
                    break;
                case 1:
                    m_vectors = new int[][]{{0, -1, 0}, {0, 0, 1}, {-1, 0, 0}}; //BOTTOM RIGHT
                    break;
                case 2:
                    m_vectors = new int[][]{{1, 0, 0}, {0, 0, 1}, {0, -1, 0}}; //BOTTOM BACK
                    break;
                case 3:
                    m_vectors = new int[][]{{0, 1, 0}, {0, 0, 1}, {1, 0, 0}}; //BOTTOM LEFT
                    break;

            }
        } else if (pitch < -45) {
            switch (heding) {
                case 0:
                    m_vectors = new int[][]{{-1, 0, 0}, {0, 0, -1}, {0, -1, 0}}; //TOP FRONT
                    break;
                case 1:
                    m_vectors = new int[][]{{0, 1, 0}, {0, 0, -1}, {-1, 0, 0}}; //TOP RIGHT
                    break;
                case 2:
                    m_vectors = new int[][]{{1, 0, 0}, {0, 0, -1}, {0, 1, 0}}; //TOP BACK
                    break;
                case 3:
                    m_vectors = new int[][]{{0, -1, 0}, {0, 0, -1}, {1, 0, 0}}; //TOP LEFT
                    break;
            }
        } else {
            switch (heding) {
                case 0:
                    m_vectors = new int[][]{{-1, 0, 0}, {0, 1, 0}, {0, 0, -1}}; //FRONT
                    break;
                case 1:
                    m_vectors = new int[][]{{0, 0, 1}, {0, 1, 0}, {-1, 0, 0}}; //RIGHT
                    break;
                case 2:
                    m_vectors = new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}; //BACK
                    break;
                case 3:
                    m_vectors = new int[][]{{0, 0, -1}, {0, 1, 0}, {1, 0, 0}}; //LEFT
                    break;
            }
        }
    }
}