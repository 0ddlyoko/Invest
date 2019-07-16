/**
 * 
 */
package me.oddlyoko.invest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.oddlyoko.invest.config.L;

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
public class CommandInvest implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!"invest".equalsIgnoreCase(command.getLabel()))
			return false;
		if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
			// Help
			sender.sendMessage(
					ChatColor.YELLOW + "---------" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "---------");
			if (sender.hasPermission("invest.create"))
				sender.sendMessage(ChatColor.AQUA
						+ "- /invest create <name> <time-to-stay> <invest-price> <invest-earned> <worldguard-zone>"
						+ ChatColor.YELLOW + "");
		} else if ("info".equalsIgnoreCase(args[0])) {
			sender.sendMessage(
					ChatColor.YELLOW + "---------" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "---------");
			sender.sendMessage("Created by 0ddlyoko");
			sender.sendMessage("https://www.0ddlyoko.be");
			sender.sendMessage("https://www.github.com/0ddlyoko");
		} else if ("create".equalsIgnoreCase(args[0])) {
			if (args.length != 6) {
				sender.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.syntaxerror").replace("%s",
						"/invest create <name> <time-to-stay> <invest-price> <invest-earned> <worldguard-zone>"));
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.nothuman"));
				return true;
			}
			Player p = (Player) sender;
			String name = args[1];
			int timeToStay;
			int investPrice;
			int investEarned;
			String worldguardZone = args[5];
			// TODO Check if name already exists
			if (false) {
				p.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.create.nameExist"));
				return true;
			}
			// Invalid time
			try {
				timeToStay = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				p.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.create.timetostay"));
				return true;
			}
			// Invalid price
			try {
				investPrice = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				p.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.create.investprice"));
				return true;
			}
			// Invalid earned
			try {
				investEarned = Integer.parseInt(args[4]);
			} catch (NumberFormatException ex) {
				p.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.create.investearned"));
				return true;
			}
			if ("__global__".equalsIgnoreCase(worldguardZone))
				p.sendMessage(__.PREFIX + ChatColor.YELLOW + L.get("command.create.globalZone"));
		} else if ("delete".equalsIgnoreCase(args[0])) {

		}
		return true;
	}
}
