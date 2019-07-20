/**
 * 
 */
package me.oddlyoko.invest;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

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
public class VaultManager {
	private Logger log = LoggerFactory.getLogger(getClass());
	private Economy economy;

	public void init() {
		loadVault();
	}

	private void loadVault() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null)
			throw new IllegalStateException("Vault isn't load or is not present");
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			throw new IllegalStateException("Not Economy found for Vault");
		economy = rsp.getProvider();
	}

	public boolean hasMoney(OfflinePlayer p, double money) {
		return economy.has(p, money);
	}

	public boolean remove(OfflinePlayer p, double money) {
		log.info("Removing {}$ for player {}", money, p.getName());
		EconomyResponse er = economy.withdrawPlayer(p, money);
		switch (er.type) {
		case FAILURE:
			log.error("An error has occured while removing $: {}", er.errorMessage);
		case NOT_IMPLEMENTED:
			log.error("This method hasn't been implemented");
		case SUCCESS:
			log.info("Success");
			return true;
		}
		return false;
	}

	public boolean add(OfflinePlayer p, double money) {
		log.info("Adding {}$ for player {}", money, p.getName());
		EconomyResponse er = economy.depositPlayer(p, money);
		switch (er.type) {
		case FAILURE:
			log.error("An error has occured while adding $: {}", er.errorMessage);
		case NOT_IMPLEMENTED:
			log.error("This method hasn't been implemented");
		case SUCCESS:
			log.info("Success");
			return true;
		}
		return false;
	}
}
