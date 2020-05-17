/**
 * 
 */
package me.oddlyoko.invest.libs.worldguard;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
public class WorldGuardv6 implements WorldGuard {
	private Logger log = LoggerFactory.getLogger(getClass());
	private WorldGuardPlugin worldGuard;
	private HashMap<InvestType, ProtectedRegion> regions;

	@Override
	public void init(Collection<InvestType> invests) {
		regions = new HashMap<>();
		worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		for (InvestType inv : invests)
			loadRegion(inv);
	}

	@Override
	public boolean loadRegion(InvestType inv) {
		World world = inv.getSpawn().getWorld();
		String region = inv.getWorldguardZone();
		RegionManager rm = worldGuard.getRegionManager(world);
		if (rm == null) {
			// Error
			log.error("Error while loading region {} in world {}: World not found !", region, world);
			return false;
		}
		ProtectedRegion pr = rm.getRegion(region);
		if (pr == null) {
			// Error
			log.error("Error while loading region {} in world {}: Region not found !", region, world);
			return false;
		}
		regions.put(inv, pr);
		return true;
	}

	@Override
	public boolean isInside(InvestType it, Location loc) {
		Location spawn = it.getSpawn();
		if ("__global__".equalsIgnoreCase(it.getWorldguardZone()) && loc.getWorld().equals(spawn.getWorld()))
			return true;
		ProtectedRegion pr = regions.get(it);
		if (pr == null)
			return false;
		if (loc.getWorld() != spawn.getWorld())
			return false;
		return pr.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
}
