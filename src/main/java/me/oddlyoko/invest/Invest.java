/**
 * 
 */
package me.oddlyoko.invest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.oddlyoko.invest.config.ConfigManager;
import me.oddlyoko.invest.config.L;
import me.oddlyoko.invest.config.PlayerManager;

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
public class Invest extends JavaPlugin {
	private static Invest invest;
	private Logger log = LoggerFactory.getLogger(getClass());
	private ConfigManager configManager;
	private InvestManager investManager;
	private WorldGuardManager worldGuardManager;
	private PlayerManager playerManager;

	boolean end = false;

	@Override
	public void onEnable() {
		try {
			log.info("Loading plugin Invest");
			invest = this;
			log.info("Loading ConfigManager");
			configManager = new ConfigManager();
			log.info("done");
			log.info("Loading Languages");
			L.init();
			log.info("done");
			log.info("Loading PlayerManager");
			playerManager = new PlayerManager();
			log.info("done");
			try {
				log.info("Loading InvestManager");
				investManager = new InvestManager();
				log.info("done");
			} catch (Exception ex) {
				log.error("An unexpected error has occured while loading InvestManager: ", ex);
				Bukkit.getPluginManager().disablePlugin(this);
				setEnabled(false);
				return;
			}
			log.info("Loading WorldGuardManager");
			worldGuardManager = new WorldGuardManager();
			try {
				worldGuardManager.init(investManager.list());
				log.info("done");
			} catch (Exception ex) {
				log.error("Error while loading WorldGuard: ", ex);
				Bukkit.getPluginManager().disablePlugin(this);
				setEnabled(false);
				return;
			}
			log.info("Loading Commands");
			Bukkit.getPluginCommand("invest").setExecutor(new CommandInvest());
			log.info("done");
			log.info("Loading Listeners");
			Bukkit.getPluginManager().registerEvents(new InvestListener(), this);
			log.info("done");
			log.info("Loading Players");
			investManager.loadPlayers();
			log.info("done");
			investManager.startScheduler();
			log.info("Plugin enabled");
		} catch (Exception ex) {
			log.error("An error has occured while loading Invest");
			Bukkit.getPluginManager().disablePlugin(this);
			setEnabled(false);
		}
	}

	@Override
	public void onDisable() {
		end = true;
		if (investManager != null) {
			for (Player p : Bukkit.getOnlinePlayers())
				investManager.unloadPlayer(p);
			investManager.stopScheduler();
		}
		log.info("Plugin disabled");
	}

	public WorldGuardManager getWorldGuardManager() {
		return worldGuardManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public InvestManager getInvestManager() {
		return investManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static Invest get() {
		return invest;
	}
}
