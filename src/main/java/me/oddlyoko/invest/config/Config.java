package me.oddlyoko.invest.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
public class Config {
	private Logger log = LoggerFactory.getLogger(getClass());
	private File fichierConfig = null;
	private FileConfiguration fconfig = null;

	/**
	 * public Config: This method allow you to interact with an yml file.
	 * 
	 * @param file
	 *            Yml file who you want to interact.
	 * 
	 */
	public Config(File file) {
		this.fichierConfig = file;
		loadConfig();
	}

	/**
	 * public void save: This method will save the yml file.
	 */
	public void save() {
		try {
			fconfig.save(fichierConfig);
		} catch (IOException ex) {
			log.error("An error has occured while saving file " + fichierConfig.getPath(), ex);
		}
	}

	/**
	 * public void loadConfig: This method will load the yml file.
	 */
	private void loadConfig() {
		fconfig = YamlConfiguration.loadConfiguration(fichierConfig);
	}

	/**
	 * public void set: This method will set an object into the yml file.
	 * 
	 * @param path
	 *            The path location where you would save the object.
	 * @param obj
	 *            The object who you would save.
	 * 
	 */
	public void set(String path, Object obj) {
		fconfig.set(path, obj);
		save();
	}

	/**
	 * public String getString: This method will return the String value.
	 * 
	 * @param path
	 *            The path location where you would get the String.
	 * 
	 */
	public String getString(String path) {
		String name = fconfig.getString(path);
		return name == null ? null : name.replace("&", "�");
	}

	/**
	 * public int getInt: This method will return the Integer value.
	 * 
	 * @param path
	 *            The path location where you would get the Integer.
	 * 
	 */
	public int getInt(String path) {
		return fconfig.getInt(path);
	}

	/**
	 * public long getLong: This method will return the Long value.
	 * 
	 * @param path
	 *            The path location where you would get the Long.
	 * 
	 */
	public long getLong(String path) {
		return fconfig.getLong(path);
	}

	/**
	 * public boolean getBoolean: This method will return the Boolean value.
	 * 
	 * @param path
	 *            The path location where you would get the Boolean.
	 * 
	 */
	public boolean getBoolean(String path) {
		return fconfig.getBoolean(path);
	}

	/**
	 * public double getDouble: This method will return the Double value.
	 * 
	 * @param path
	 *            The path location where you would get the Double.
	 * 
	 */
	public double getDouble(String path) {
		return fconfig.getDouble(path);
	}

	/**
	 * public List<String> getStringList: This method will return a list of String.
	 * 
	 * @param path
	 *            The path location where you would get the StringList.
	 * 
	 */
	public List<String> getStringList(String path) {
		List<String> name = new ArrayList<>();
		for (String nom : fconfig.getStringList(path)) {
			name.add(nom.replace("&", "�"));
		}
		return name;
	}

	/**
	 * public List<Integer> getIntegerList: This method will return a list of
	 * Integer.
	 * 
	 * @param path
	 *            The path location where you would get the IntegerList.
	 * 
	 */
	public List<Integer> getIntegerList(String path) {
		List<Integer> name = new ArrayList<>();
		for (Integer nom : fconfig.getIntegerList(path)) {
			name.add(nom);
		}
		return name;
	}

	/**
	 * public List<String> getKeys: This method will return a list of Keys.
	 * 
	 * @param path
	 *            The path location where you would get the KeysList.
	 * 
	 */
	public List<String> getKeys(String path) {
		List<String> list = new ArrayList<>();
		if ("".equalsIgnoreCase(path)) {
			for (String section : fconfig.getKeys(false)) {
				list.add(section);
			}
		} else {
			for (String section : fconfig.getConfigurationSection(path).getKeys(false)) {
				list.add(section);
			}
		}
		return list;
	}

	/**
	 * public boolean exist: This method will return true if the path exist, however
	 * false.
	 * 
	 * @param path
	 *            The location where you would see if path exist.
	 * 
	 */
	public boolean exist(String path) {
		return fconfig.contains(path);
	}
}
