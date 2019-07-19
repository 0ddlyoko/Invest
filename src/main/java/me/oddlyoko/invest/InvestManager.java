/**
 * 
 */
package me.oddlyoko.invest;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import me.oddlyoko.invest.config.L;
import me.oddlyoko.invest.config.PlayerInvest;

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
public class InvestManager {
	private Logger log = LoggerFactory.getLogger(getClass());
	private File jsonFile;
	// List of available invests
	private Map<String, InvestType> invests;
	// List of PlayerInvest (for connected players)
	private Map<UUID, PlayerInvest> players;
	// List of PlayerInvest for players that are inside their invest zone-
	private Map<UUID, PlayerInvest> playerInside;
	private Object sync = new Object();

	public InvestManager() throws Exception {
		// Load from config file
		jsonFile = new File("plugins" + File.separator + "Invest" + File.separator + "invest.json");
		if (!jsonFile.exists()) {
			// File not found
			try {
				jsonFile.getParentFile().mkdirs();
				if (!jsonFile.createNewFile())
					throw new IllegalStateException("Cannot create file invest.json");
			} catch (Exception ex) {
				log.error("An error has occured while creating invest.json:", ex);
			}
		}
		invests = new HashMap<>();
		players = new HashMap<>();
		playerInside = new HashMap<>();
		loadFromFile();
	}

	private void loadFromFile() throws IOException {
		try (JsonReader reader = new JsonReader(new FileReader(jsonFile))) {
			InvestType[] data = new GsonBuilder().create().fromJson(reader, InvestType[].class);
			if (data == null)
				return;
			for (InvestType it : data)
				invests.put(it.getName(), it);
		}
	}

	public void loadPlayers() {
		for (Player p : Bukkit.getOnlinePlayers())
			loadPlayer(p);
	}

	/**
	 * On player move
	 * 
	 * @param p
	 */
	public void playerMove(Player p) {
		Location loc = p.getLocation();
		PlayerInvest inv = getInvest(p);
		if (inv == null)
			return;
		boolean isInside = inv.getInvestType().isInside(loc);
		boolean containPlayerInside = playerInside.containsKey(p.getUniqueId());
		if (isInside && !containPlayerInside)
			enterZone(p, inv);
		else if (!isInside && containPlayerInside)
			exitZone(p, inv);
	}

	/**
	 * When a player enter to his zone
	 * 
	 * @param p
	 * @param inv
	 */
	private void enterZone(Player p, PlayerInvest inv) {
		// Player join the invest zone
		playerInside.put(p.getUniqueId(), inv);
		p.sendMessage("Entering invest zone");
	}

	/**
	 * When a player leave his zone
	 * 
	 * @param p
	 * @param inv
	 */
	private void exitZone(Player p, PlayerInvest inv) {
		if (inv == null || !playerInside.containsKey(p.getUniqueId()))
			return;
		// Player quit the invest zone
		playerInside.remove(p.getUniqueId());
		p.sendMessage("Leaving invest zone");
	}

	// ------------------------------------------------------------------------------

	/**
	 * Load specific player
	 * 
	 * @param p
	 */
	public void loadPlayer(Player p) {
		log.info("Loading player {}", p.getName());
		// Player already loaded
		if (players.containsKey(p.getUniqueId()))
			return;
		// Does the player have an invest ?
		if (!Invest.get().getPlayerManager().existPlayer(p))
			return;
		UUID uuid = p.getUniqueId();
		String type = Invest.get().getPlayerManager().getType(uuid);
		InvestType investType = invests.get(type);
		int time = Invest.get().getPlayerManager().getTime(uuid);
		if (investType == null || time == 0) {
			// Error
			log.error(
					"InvestType is null or time is empty, please check if player.yml file hasn't been corrompted (or edited manually) for this player");
			log.error("uuid = {}, type = {}, investType = {}, time = {}", uuid, type,
					investType == null ? "null" : investType.getName(), time);
			p.sendMessage(__.PREFIX + ChatColor.RED + L.get("errorLoading"));
			Invest.get().getPlayerManager().delete(uuid);
			return;
		}
		PlayerInvest pi = new PlayerInvest(uuid, investType, time);
		this.players.put(uuid, pi);
		// Simulate a move to check if he is inside the invest zone
		playerMove(p);
	}

	/**
	 * Unload specific player
	 * 
	 * @param p
	 */
	public void unloadPlayer(Player p) {
		log.info("Unloading player {}", p.getName());
		if (!players.containsKey(p.getUniqueId()))
			return;
		savePlayer(p);
		players.remove(p.getUniqueId());
		// Simulate a zone exit
		exitZone(p, playerInside.get(p.getUniqueId()));
	}

	/**
	 * Save the invest of specific player in config file
	 * 
	 * @param p
	 */
	public void savePlayer(Player p) {
		log.info("Saving player {}", p.getName());
		if (!players.containsKey(p.getUniqueId()))
			return;
		PlayerInvest pi = players.get(p.getUniqueId());
		Invest.get().getPlayerManager().save(p.getUniqueId(), pi.getInvestType().getName(), pi.getTime());
	}

	public boolean hasInvest(Player p) {
		return players.containsKey(p.getUniqueId());
	}

	public PlayerInvest getInvest(Player p) {
		return players.get(p.getUniqueId());
	}

	/**
	 * Start an invest for specific player
	 * 
	 * @param p
	 * @param inv
	 */
	public void startInvest(Player p, InvestType inv) {
		PlayerInvest pi = new PlayerInvest(p.getUniqueId(), inv, inv.getTimeToStay());
		players.put(p.getUniqueId(), pi);
		Invest.get().getPlayerManager().save(p.getUniqueId(), inv.getName(), inv.getTimeToStay());
		// Simulate a player move
		playerMove(p);
	}

	/**
	 * Stop current invest for specific player
	 * 
	 * @param p
	 */
	public void stopInvest(Player p) {
		players.remove(p.getUniqueId());
		Invest.get().getPlayerManager().delete(p.getUniqueId());
		// Simulate a player move
		exitZone(p, playerInside.get(p.getUniqueId()));
	}

	// ------------------------------------------------------------------------------

	private void saveToFile() throws IOException {
		InvestType[] data = invests.values().toArray(new InvestType[0]);
		try (Writer writer = new FileWriter(jsonFile)) {
			new GsonBuilder().create().toJson(data, writer);
		}
	}

	public boolean exist(String name) {
		return invests.containsKey(name);
	}

	/**
	 * Create a new invest
	 * 
	 * @param name
	 * @param timeToStay
	 * @param investPrice
	 * @param investEarned
	 * @param worldguardZone
	 * @param spawn
	 * @return
	 */
	public boolean createInvest(String name, int timeToStay, int investPrice, int investEarned, String worldguardZone,
			Location spawn) {
		if (invests.containsKey(name))
			return false;
		InvestType inv = new InvestType(name, timeToStay, investPrice, investEarned, worldguardZone, spawn);
		synchronized (sync) {
			invests.put(name, inv);
			if (!Invest.get().getWorldGuardManager().loadRegion(inv)) {
				log.error("An error has occured while loading region for invest " + name);
				invests.remove(name);
				return false;
			}
			try {
				saveToFile();
			} catch (Exception ex) {
				invests.remove(name);
				log.error("Error while saving invests to invest.json file: ", ex);
				return false;
			}
			return true;
		}
	}

	/**
	 * Delete an existing invest
	 * 
	 * @param name
	 * @return
	 */
	public boolean deleteInvest(String name) {
		if (!invests.containsKey(name))
			return false;
		synchronized (sync) {
			InvestType invest = invests.remove(name);
			try {
				saveToFile();
				return true;
			} catch (Exception ex) {
				// Re-set Invest into the Map
				invests.put(invest.getName(), invest);
				log.error("Error while saving invests to invest.json file: ", ex);
				return false;
			}
		}
	}

	public Collection<InvestType> list() {
		return invests.values();
	}

	public int count() {
		return invests.size();
	}

	public InvestType get(String name) {
		return invests.get(name);
	}
}
