/**
 * 
 */
package me.oddlyoko.invest.config;

import java.io.File;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PlayerManager {
	private Logger log = LoggerFactory.getLogger(getClass());
	private Config config;

	public PlayerManager() {
		config = new Config(new File("plugins" + File.separator + "Invest" + File.separator + "player.yml"));
	}

	public boolean existPlayer(Player p) {
		return config.exist(p.getUniqueId().toString());
	}

	public String getType(UUID uuid) {
		return config.getString(uuid.toString() + ".type");
	}

	public int getTime(UUID uuid) {
		return config.getInt(uuid.toString() + ".time");
	}

	public void delete(UUID uuid) {
		log.info("Deleting uuid {}", uuid);
		config.set(uuid.toString(), null);
	}

	public void save(UUID uuid, String type, int time) {
		log.info("Creating / Saving an entry: uuid = {}, type = {}, time = {}", uuid, type, time);
		config.set(uuid.toString() + ".type", type);
		config.set(uuid.toString() + ".time", time);
	}
}
