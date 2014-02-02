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

/**
 *
 * @author SBPrime
 */
public class Pair<T1, T2> {
    private final T1 m_first;

    private final T2 m_second;

    public T1 getFirst() {
        return m_first;
    }

    public T2 getSecond() {
        return m_second;
    }

    public Pair(T1 first, T2 second) {
        m_first = first;
        m_second = second;
    }

    @Override
    public int hashCode() {
        return (m_first != null ? m_first.hashCode() : 0)
                ^ (m_second != null ? m_second.hashCode() : 0);
    }

    private static boolean safeEqual(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair first = (Pair) obj;
        return safeEqual(first.m_first, m_first)
                && safeEqual(first.m_second, m_second);
    }

    @Override
    public String toString() {
        return String.format("{%s,%s}", new Object[]{m_first, m_second});
    }
}
