/**
 * 
 */
package me.oddlyoko.invest.config;

import java.io.File;
import java.util.HashMap;

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
public final class L {
	private static Config config;
	private static HashMap<String, String> messages;

	static {
		config = new Config(new File("plugins" + File.separator + "Invest" + File.separator + "lang.yml"));
		put("command.syntaxerror");
		put("command.nothuman");
		put("command.help.create");
		put("command.create.nameExist");
		put("command.create.timetostay");
		put("command.create.investprice");
		put("command.create.investearned");
	}

	private L() {
	}

	public static void init() {
	}

	private static void put(String key) {
		messages.put(key, config.getString(key));
	}

	public static String get(String name) {
		return messages.get(name);
	}
}
