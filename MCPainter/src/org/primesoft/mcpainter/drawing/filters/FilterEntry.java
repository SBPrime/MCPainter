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
package org.primesoft.mcpainter.drawing.filters;

import org.bukkit.ChatColor;

/**
 *
 * @author SBPrime
 */
public class FilterEntry {
    private IImageFilter m_filter;
    private IFilterParams m_params;
    private String m_priceEntryName;


    /**
     * Get the price entry name
     * @return 
     */
    public String getPriceName()
    {
        return m_priceEntryName;
    }
    
    
    /**
     * Get the filter instance
     * @return 
     */
    public IImageFilter getFilter() {
        return m_filter;
    }

    
    /**
     * Get filter parameters
     * @return 
     */
    public IFilterParams getParams() {
        return m_params;
    }

    
    /**
     * Create new instance of filter entry
     * @param filter
     * @param params 
     */
    public FilterEntry(IImageFilter filter, IFilterParams params) {
        m_filter = filter;
        m_params = params;
        m_priceEntryName = filter.getPriceName();
    }

    
    /**
     * COnvert filter to string
     * @return 
     */
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.YELLOW);
        sb.append(m_filter.getName());
        if (m_params != null) {
            sb.append(" ");
            sb.append(m_params.print());
        }
        return sb.toString();
    }
}
