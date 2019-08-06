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
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

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
 * An edit version of <a href="https://gist.github.com/aadnk/5871793">that</a>
 */
public class ProtocolLibManager {
	private Logger log = LoggerFactory.getLogger(getClass());

	private Table<Integer, Integer, Boolean> observerPlayerMap = HashBasedTable.create();

	// Packets that update remote player entities
	private static final PacketType[] ENTITY_PACKETS = { ENTITY_EQUIPMENT, ANIMATION, NAMED_ENTITY_SPAWN, COLLECT,
			SPAWN_ENTITY, SPAWN_ENTITY_LIVING, ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK,
			ENTITY_TELEPORT, ENTITY_HEAD_ROTATION, ATTACH_ENTITY, ENTITY_METADATA, ENTITY_EFFECT, REMOVE_ENTITY_EFFECT,
			BLOCK_BREAK_ANIMATION };

	private ProtocolManager protocolManager;

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
				// This is strange: We must NOT cancel the "ANIMATION" event if it's sent
				// to the same player (or the player is bugged in the bed (Thanks
				// Minecraft)
				if (player.getEntityId() == entityId && event.getPacketType() == ANIMATION)
					return;
				// See if this packet should be cancelled
				if (!canSee(event.getPlayer(), player))
					event.setCancelled(true);
			}
		});
	}

	/**
	 * Set the visibility status of a given entity for a particular observer.
	 * 
	 * @param p
	 *            - the observer player.
	 * @param p2
	 *            - the other player.
	 * @param visible
	 *            - TRUE if the entity should be invisible.
	 * @return TRUE if the entity was visible before this method call, FALSE
	 *         otherwise.
	 */
	private boolean setVisibility(Player p, Player p2, boolean visible) {
		if (visible)
			return observerPlayerMap.put(p.getEntityId(), p2.getEntityId(), true) != null;
		else
			return observerPlayerMap.remove(p.getEntityId(), p2.getEntityId()) != null;
	}

	/**
	 * Toggle the visibility status of a player for a player.
	 * <p>
	 * If the player is visible, it will be hidden. If it is hidden, it will become
	 * visible.
	 * 
	 * @param p
	 *            - the player observer.
	 * @param p2
	 *            - the other player.
	 * @return TRUE if the player was visible before, FALSE otherwise.
	 */
	public final boolean toggleEntity(Player p, Player p2) {
		if (canSee(p, p2))
			return hideEntity(p, p2);
		else
			return !showEntity(p, p2);
	}

	/**
	 * Allow the observer to see a player that was previously hidden.
	 * 
	 * @param p
	 *            - the observer.
	 * @param p2
	 *            - the player to show.
	 * @return TRUE if the player was hidden before, FALSE otherwise.
	 */
	public final boolean showEntity(Player p, Player p2) {
		boolean hiddenBefore = !setVisibility(p, p2, true);

		// Resend packets
		if (protocolManager != null && hiddenBefore)
			protocolManager.updateEntity(p2, Arrays.asList(p));
		return hiddenBefore;
	}

	/**
	 * Prevent the observer from seeing a given entity.
	 * 
	 * @param p
	 *            - the player observer.
	 * @param p2
	 *            - the player to hide.
	 * @return TRUE if the entity was previously visible, FALSE otherwise.
	 */
	public final boolean hideEntity(Player p, Player p2) {
		boolean visibleBefore = setVisibility(p, p2, false);

		if (visibleBefore) {
			PacketContainer destroyEntity = new PacketContainer(ENTITY_DESTROY);
			destroyEntity.getIntegerArrays().write(0, new int[] { p2.getEntityId() });

			// Make the entity disappear
			try {
				protocolManager.sendServerPacket(p, destroyEntity);
			} catch (InvocationTargetException e) {
				throw new RuntimeException("Cannot send server packet.", e);
			}
		}
		return visibleBefore;
	}

	/**
	 * Determine if the given entity has been hidden from an observer.
	 * <p>
	 * Note that the entity may very well be occluded or out of range from the
	 * perspective of the observer. This method simply checks if an entity has been
	 * completely hidden for that observer.
	 * 
	 * @param p
	 *            - the observer.
	 * @param p2
	 *            - the player that may be hidden.
	 * @return TRUE if the player may see the other player, FALSE if the player has
	 *         been hidden.
	 */
	public final boolean canSee(Player p, Player p2) {
		return observerPlayerMap.contains(p.getEntityId(), p2.getEntityId());
	}

	public final void close() {
		if (protocolManager == null)
			return;
		protocolManager.removePacketListeners(Invest.get());
	}
}