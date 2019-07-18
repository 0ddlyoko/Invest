/**
 * 
 */
package me.oddlyoko.invest;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * MIT License
 * 
 * Copyright (c) 2019 0ddlyoko
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
public class InvestType implements Serializable {
	private static final long serialVersionUID = -471238877617274026L;

	private String name;
	private int timeToStay;
	private int investPrice;
	private int investEarned;
	private String worldguardZone;
	private transient Location spawnLoc;
	// Gson
	private SpawnType spawn;

	public InvestType() {
	}

	public InvestType(String name, int timeToStay, int investPrice, int investEarned, String worldguardZone,
			Location spawnLoc) {
		this.name = name;
		this.timeToStay = timeToStay;
		this.investPrice = investPrice;
		this.investEarned = investEarned;
		this.worldguardZone = worldguardZone;
		this.spawnLoc = spawnLoc;
		this.spawn = new SpawnType();
		this.spawn.world = spawnLoc.getWorld().getName();
		this.spawn.pos = new float[] { (float) spawnLoc.getX(), (float) spawnLoc.getY(), (float) spawnLoc.getZ(),
				spawnLoc.getYaw(), spawnLoc.getPitch() };
	}

	public String getName() {
		return name;
	}

	public int getTimeToStay() {
		return timeToStay;
	}

	public int getInvestPrice() {
		return investPrice;
	}

	public int getInvestEarned() {
		return investEarned;
	}

	public String getWorldguardZone() {
		return worldguardZone;
	}

	public Location getSpawn() {
		if (spawnLoc == null && spawn != null) {
			spawnLoc = new Location(Bukkit.getWorld(spawn.world), spawn.pos[0], spawn.pos[1], spawn.pos[2]);
			spawnLoc.setYaw(spawn.pos[3]);
			spawnLoc.setPitch(spawn.pos[4]);
		}
		return spawnLoc;
	}

	public class SpawnType implements Serializable {
		private static final long serialVersionUID = -2407207681877080591L;

		private String world;
		private float[] pos;
	}
}
