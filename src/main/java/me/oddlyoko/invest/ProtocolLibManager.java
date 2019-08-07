/**
 * 
 */
package me.oddlyoko.invest;

import static com.comphenix.protocol.PacketType.Play.Server.ANIMATION;
import static com.comphenix.protocol.PacketType.Play.Server.ATTACH_ENTITY;
import static com.comphenix.protocol.PacketType.Play.Server.BLOCK_BREAK_ANIMATION;
import static com.comphenix.protocol.PacketType.Play.Server.COLLECT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EFFECT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EQUIPMENT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_HEAD_ROTATION;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_LOOK;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_METADATA;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_MOVE_LOOK;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_TELEPORT;
import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_VELOCITY;
import static com.comphenix.protocol.PacketType.Play.Server.NAMED_ENTITY_SPAWN;
import static com.comphenix.protocol.PacketType.Play.Server.REL_ENTITY_MOVE;
import static com.comphenix.protocol.PacketType.Play.Server.REMOVE_ENTITY_EFFECT;
import static com.comphenix.protocol.PacketType.Play.Server.SPAWN_ENTITY;
import static com.comphenix.protocol.PacketType.Play.Server.SPAWN_ENTITY_LIVING;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

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
/**
 * A completely modified version of of
 * <a href="https://gist.github.com/aadnk/5871793">this gist</a>
 */
public class ProtocolLibManager implements Listener {
	private Logger log = LoggerFactory.getLogger(getClass());

	private List<Player> vanishedPlayers;

	// Packets that update remote player entities
	private static final PacketType[] ENTITY_PACKETS = { ENTITY_EQUIPMENT, ANIMATION, NAMED_ENTITY_SPAWN, COLLECT,
			SPAWN_ENTITY, SPAWN_ENTITY_LIVING, ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK,
			ENTITY_TELEPORT, ENTITY_HEAD_ROTATION, ATTACH_ENTITY, ENTITY_METADATA, ENTITY_EFFECT, REMOVE_ENTITY_EFFECT,
			BLOCK_BREAK_ANIMATION };

	private ProtocolManager protocolManager;

	public ProtocolLibManager() {
		vanishedPlayers = new ArrayList<>();
	}

	public void init() {
		loadProtocolLib();
	}

	private void loadProtocolLib() {
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			Invest.get().getConfigManager().setUseFakeVanish(false);
			log.info("ProtocolLib isn't present, the \"use-fake-vanish\" key has been disabled !");
			return;
		}

		// Save policy
		protocolManager = ProtocolLibrary.getProtocolManager();

		// Register events and packet listener
		// Bukkit.getPluginManager().registerEvents(this, Invest.get());
		protocolManager.addPacketListener(new PacketAdapter(Invest.get(), ENTITY_PACKETS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				int entityId = event.getPacket().getIntegers().read(0);
				// See if this entityId is from a player
				Player player = null;
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.getEntityId() == entityId) {
						player = p;
						break;
					}
				if (player == null)
					return;
				// This is strange: We must NOT cancel the "ANIMATION" even if it's sent
				// to the same player (or the player is bugged in the bed (Thanks
				// Minecraft)
				if (player.getEntityId() == entityId)
					return;
				// See if this packet should be cancelled
				if (vanishedPlayers.contains(player)) {
					event.setCancelled(true);
				}
			}
		});
	}

	/**
	 * Vanish a player
	 * 
	 * @param p
	 *            The player to vanish
	 */
	public void vanish(Player p) {
		if (vanishedPlayers.add(p)) {
			// Wasn't present in the list
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p == p2)
					continue;
				vanish(p2, p);
			}
		}
	}

	/**
	 * Send a packet to p saying that p2 is now vanished
	 * 
	 * @param p
	 *            The player to vanish
	 */
	private void vanish(Player p, Player p2) {
		// Disappear the player
		PacketContainer destroyEntity = new PacketContainer(ENTITY_DESTROY);
		destroyEntity.getIntegerArrays().write(0, new int[] { p2.getEntityId() });

		try {
			protocolManager.sendServerPacket(p, destroyEntity);
		} catch (InvocationTargetException ex) {
			log.error("An error has occured while sending packet: ", ex);
		}
	}

	/**
	 * Unvanish a player
	 * 
	 * @param p
	 *            The player to unvanish
	 */
	public void unVanish(Player p) {
		if (vanishedPlayers.remove(p)) {
			// Was present in the list
			for (Player p2 : Bukkit.getOnlinePlayers()) {
				if (p == p2)
					continue;
				unVanish(p2, p);
			}
		}
	}

	/**
	 * Send a packet to p saying that p2 is not longer vanished
	 * 
	 * @param p
	 *            The player to vanish
	 */
	public void unVanish(Player p, Player p2) {
		// Reappear the player
		protocolManager.updateEntity(p2, Arrays.asList(p));
	}

	@EventHandler
	public void onPlayerConnection(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// Vanish each players
		for (Player p2 : vanishedPlayers) {
			if (p == p2)
				continue;
			vanish(p, p2);
		}
	}

	public final void close() {
		if (protocolManager == null)
			return;
		protocolManager.removePacketListeners(Invest.get());
	}
}