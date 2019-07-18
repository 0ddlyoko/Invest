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

import org.bukkit.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

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
	private Map<String, InvestType> invests;
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
		loadFromFile();
	}

	public void loadFromFile() throws IOException {
		try (JsonReader reader = new JsonReader(new FileReader(jsonFile))) {
			InvestType[] data = new GsonBuilder().create().fromJson(reader, InvestType[].class);
			invests = new HashMap<>();
			if (data == null)
				return;
			for (InvestType it : data)
				invests.put(it.getName(), it);
		}
	}

	private void saveToFile() throws IOException {
		InvestType[] data = invests.values().toArray(new InvestType[0]);
		try (Writer writer = new FileWriter(jsonFile)) {
			new GsonBuilder().create().toJson(data, writer);
		}
	}

	public boolean exist(String name) {
		return invests.containsKey(name);
	}

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
