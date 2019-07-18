/**
 * 
 */
package me.oddlyoko.invest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.oddlyoko.invest.config.ConfigManager;
import me.oddlyoko.invest.config.L;

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

	@Override
	public void onEnable() {
		invest = this;
		configManager = new ConfigManager();
		L.init();
		try {
			investManager = new InvestManager();
		} catch (Exception ex) {
			log.error("An unexpected error has occured while loading InvestManager: ", ex);
		}
		Bukkit.getPluginCommand("invest").setExecutor(new CommandInvest());
		log.info("Plugin enabled");
	}

	@Override
	public void onDisable() {
		log.info("Plugin disabled");
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public InvestManager getInvestManager() {
		return investManager;
	}

	public static Invest get() {
		return invest;
	}
}
