package me.oddlyoko.invest.libs.worldguard;

import java.util.Collection;

import org.bukkit.Location;

import me.oddlyoko.invest.invest.InvestType;

/**
 * MIT License
 * 
 * Copyright (c) 2020 0ddlyoko
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public interface WorldGuard {

	/**
	 * Initialize this class
	 * 
	 * @param invests
	 */
	public void init(Collection<InvestType> invests);

	/**
	 * Load specific invest
	 * 
	 * @param inv
	 *                The invest to load
	 * @return true if loaded
	 */
	public boolean loadRegion(InvestType inv);

	/**
	 * Check if specific location is in specific region of specific
	 * 
	 * @param it
	 *                The InvestType
	 * @param loc
	 *                The Location
	 * @return true if this location is inside specific invest zone
	 */
	public boolean isInside(InvestType it, Location loc);
}
