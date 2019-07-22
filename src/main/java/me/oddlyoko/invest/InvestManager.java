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
import java.util.List;
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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

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
	// List of PlayerInvest for players that are inside their invest zone
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

	// ------------------------------------------------------------------------------

	private boolean run = false;
	private int seconds = 0;

	public void startScheduler() {
		run = true;
		// I use Thread instance of BukkitRunnable for one reason: If the server tps is
		// low this method will anyways be executed each seconds
		Thread t = new Thread(() -> {
			String msg = L.get("aboveBar");
			boolean isVanish = Invest.get().getConfigManager().isVanish();
			boolean isGlobalVanish = Invest.get().getConfigManager().isVanishGlobal();
			log.info("Entering loop");
			while (run && !Thread.interrupted()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					log.error("InterruptedException !", ex);
				}
				PlayerInvest[] playerInside = this.playerInside.values().toArray(new PlayerInvest[0]);
				for (PlayerInvest pi : playerInside) {
					Player p = Bukkit.getPlayer(pi.getUUID());
					try {
						pi.cooldown();
						int totalSec = pi.getTime();
						int hour = totalSec / 3600;
						int min = (totalSec / 60) - (hour * 60);
						int sec = totalSec % 60;
						int price = pi.getInvestType().getInvestPrice();
						int earn = pi.getInvestType().getInvestEarned();
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
								TextComponent.fromLegacyText(ChatColor.GREEN + msg.replaceAll("%player%", p.getName())
										.replaceAll("%displayName%", p.getDisplayName())
										.replaceAll("%hour%", Integer.toString(hour))
										.replaceAll("%min%", Integer.toString(min))
										.replaceAll("%sec%", Integer.toString(sec))
										.replaceAll("%totalsec%", Integer.toString(totalSec))
										.replaceAll("%price%", Integer.toString(price))
										.replaceAll("%earn%", Integer.toString(earn))));
						if (pi.getTime() <= 0) {
							// End
							stopInvest(pi.getUUID());
							List<String> cmds = Invest.get().getConfigManager().getCommandEnd();
							Bukkit.getScheduler().runTask(Invest.get(), () -> {
								for (String cmd : cmds)
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
											cmd.replace("%player%", p.getName()).replace("%displayName%",
													p.getDisplayName()));
							});
							p.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("end").replaceAll("%s",
									Integer.toString(pi.getInvestType().getInvestEarned())));
							log.info("Investisment of uuid {} is ended ! type = {}, earned = {}", pi.getUUID(),
									pi.getInvestType().getName(), pi.getInvestType().getInvestEarned());
							if (!Invest.get().getVaultManager().add(p, pi.getInvestType().getInvestEarned())) {
								p.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("error"));
								log.error(
										"Investisment of uuid {} is ended, but got error, please give him {}$ manually",
										pi.getUUID(), pi.getInvestType().getInvestEarned());
							}
						}
					} catch (Exception ex) {
						log.error("An error has occured while cooldowning player {}", p.getName());
						log.error("", ex);
					}
				}
				seconds++;
				// Save
				if (seconds % Invest.get().getConfigManager().getTimeToSave() == 0) {
					log.info("Saving players ...");
					for (Player p : Bukkit.getOnlinePlayers())
						savePlayer(p);
				}
				// Vanish
				for (Player p : Bukkit.getOnlinePlayers()) {
					boolean isInsideNotGlobalZone = false;
					boolean isInsideGlobalZone = false;
					for (InvestType it : invests.values()) {
						if (it.isInside(p.getLocation())) {
							if (!"__global__".equalsIgnoreCase(it.getWorldguardZone()))
								isInsideNotGlobalZone = true;
							else {
								isInsideGlobalZone = true;
								if (!Invest.get().getConfigManager().isShowInGlobal())
									continue;
							}
							if (players.containsKey(p.getUniqueId()))
								continue;
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(L.get("notInvest")));
							break;
						}
					}
					boolean vanish = (isInsideNotGlobalZone && isVanish) || (isInsideGlobalZone && isGlobalVanish);
					Bukkit.getScheduler().runTask(Invest.get(), () -> {
						// Hide players
						for (Player p2 : Bukkit.getOnlinePlayers())
							if (p.getUniqueId() != p2.getUniqueId()) {
								if (vanish)
									p2.hidePlayer(Invest.get(), p);
								else
									p2.showPlayer(Invest.get(), p);
							}
					});
				}
			}
			log.info("Exiting loop");
		});
		t.start();
	}

	public void stopScheduler() {
		run = false;
	}

	// ------------------------------------------------------------------------------

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
			exitZone(p.getUniqueId(), inv);
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
	}

	/**
	 * When a player leave his zone
	 * 
	 * @param uuid
	 * @param inv
	 */
	private void exitZone(UUID uuid, PlayerInvest inv) {
		if (inv == null || !playerInside.containsKey(uuid))
			return;
		// Player quit the invest zone
		playerInside.remove(uuid);
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
			p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("error"));
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
		exitZone(p.getUniqueId(), playerInside.get(p.getUniqueId()));
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
	 * @param uuid
	 */
	public void stopInvest(UUID uuid) {
		players.remove(uuid);
		Invest.get().getPlayerManager().delete(uuid);
		// Simulate a player move
		exitZone(uuid, playerInside.get(uuid));
	}

	public Collection<PlayerInvest> getPlayersInside() {
		return playerInside.values();
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
				log.error("An error has occured while loading region for invest {}", name);
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