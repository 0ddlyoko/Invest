/**
 * 
 */
package me.oddlyoko.invest;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

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
public class WorldGuardManager {
	private Logger log = LoggerFactory.getLogger(getClass());
	private WorldGuardPlugin worldGuard;

	public void init(Collection<InvestType> invests) {
		loadWorldGuard();
		loadRegions(invests);
	}

	private void loadWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin))
			throw new IllegalStateException("WorldGuard isn't load or is not present");
		worldGuard = (WorldGuardPlugin) plugin;
	}

	private void loadRegions(Collection<InvestType> invests) {
		for (InvestType inv : invests)
			loadRegion(inv);
	}

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
		inv.setWorldGuardRegion(pr);
		return true;
	}
}
