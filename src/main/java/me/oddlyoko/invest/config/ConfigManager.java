/**
 * 
 */
package me.oddlyoko.invest.config;

import java.io.File;
import java.util.List;

/**
 * MIT License
 * 
 * Copyright (c) 2020 0ddlyoko
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
public class ConfigManager {
	private Config config;
	private String commandInvest;
	private String commandHaveInvest;
	private boolean showInGlobal;
	private int timeToSave;
	private int refund;
	private boolean vanish;
	private boolean vanishGlobal;
	private boolean useFakeVanish;
	private List<String> commandEnd;
	private String prefix;

	public ConfigManager() {
		config = new Config(new File("plugins" + File.separator + "Invest" + File.separator + "config.yml"));
		commandInvest = config.getString("commandInvest");
		commandHaveInvest = config.getString("commandHaveInvest");
		showInGlobal = config.getBoolean("showInGlobal");
		timeToSave = config.getInt("timeToSave");
		refund = config.getInt("refund-percent");
		vanish = config.getBoolean("vanish");
		vanishGlobal = config.getBoolean("vanish-global");
		useFakeVanish = config.getBoolean("use-fake-vanish");
		commandEnd = config.getStringList("commands-end");
		prefix = config.getString("prefix");
	}

	public String getCommandInvest() {
		return commandInvest;
	}

	public String getCommandHaveInvest() {
		return commandHaveInvest;
	}

	public boolean isShowInGlobal() {
		return showInGlobal;
	}

	public int getTimeToSave() {
		return timeToSave;
	}

	public int getRefund() {
		return refund;
	}

	public boolean isVanish() {
		return vanish;
	}

	public boolean isVanishGlobal() {
		return vanishGlobal;
	}

	public boolean isUseFakeVanish() {
		return useFakeVanish;
	}

	public void setUseFakeVanish(boolean useFakeVanish) {
		this.useFakeVanish = useFakeVanish;
	}

	public List<String> getCommandEnd() {
		return commandEnd;
	}

	public String getPrefix() {
		return prefix;
	}
}
