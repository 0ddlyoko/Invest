package me.oddlyoko.invest.libs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import me.oddlyoko.invest.Invest;
import me.oddlyoko.invest.libs.protocollib.ProtocolLibManager;
import me.oddlyoko.invest.libs.vault.VaultManager;
import me.oddlyoko.invest.libs.worldguard.WorldGuard;
import me.oddlyoko.invest.libs.worldguard.WorldGuardv6;
import me.oddlyoko.invest.libs.worldguard.WorldGuardv7;

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
public class LibManager {
	private Logger log = LoggerFactory.getLogger(getClass());
	private boolean loaded = false;
	private ProtocolLibManager protocollib;
	private VaultManager vault;
	private WorldGuard worldGuard;

	public boolean load() {
		if (loaded)
			return true;
		loaded = true;
		log.info("WorldGuard ...");
		// WorldGuard
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			log.error("Not found ! Disabling plugin");
			return false;
		}
		String version = plugin.getDescription().getVersion();
		if (version.startsWith("7"))
			worldGuard = new WorldGuardv7();
		else if (version.startsWith("6"))
			worldGuard = new WorldGuardv6();
		else {
			log.error("Version not supported !");
			return false;
		}
		try {
			worldGuard.init(Invest.get().getInvestManager().list());
			log.info("Done");
		} catch (Exception ex) {
			log.error("Error while loading WorldGuard: ", ex);
			return false;
		}
		log.info("Vault ...");
		vault = new VaultManager();
		vault.init();
		log.info("Done");
		log.info("ProtocolLib ...");
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			Invest.get().getConfigManager().setUseFakeVanish(false);
			log.info("Not found ! the \"use-fake-vanish\" key has been disabled !");
		} else {
			protocollib = new ProtocolLibManager();
			protocollib.init();
			log.info("Done");
		}
		return true;
	}

	public void close() {
		if (protocollib != null)
			protocollib.close();
	}

	public ProtocolLibManager getProtocolLib() {
		return protocollib;
	}

	public VaultManager getVault() {
		return vault;
	}

	public WorldGuard getWorldGuard() {
		return worldGuard;
	}
}
