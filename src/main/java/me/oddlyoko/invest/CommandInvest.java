/**
 * 
 */
package me.oddlyoko.invest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
					ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "]-----------");
			sender.sendMessage(
					ChatColor.AQUA + "- /invest info" + ChatColor.YELLOW + " : " + L.get("command.help.info"));
			sender.sendMessage(
					ChatColor.AQUA + "- /invest help" + ChatColor.YELLOW + " : " + L.get("command.help.help"));
			if (sender.hasPermission("invest.create"))
				sender.sendMessage(ChatColor.AQUA
						+ "- /invest create <name> <time-to-stay> <invest-price> <invest-earned> <worldguard-zone>"
						+ ChatColor.YELLOW + " : " + L.get("command.help.create"));
			if (sender.hasPermission("invest.delete"))
				sender.sendMessage(ChatColor.AQUA + "- /invest delete <name>" + ChatColor.YELLOW + " : "
						+ L.get("command.help.delete"));
		} else if ("info".equalsIgnoreCase(args[0])) {
			sender.sendMessage(
					ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "]-----------");
			sender.sendMessage(ChatColor.AQUA + "Created by 0ddlyoko");
			sender.sendMessage(ChatColor.AQUA + "https://www.0ddlyoko.be");
			sender.sendMessage(ChatColor.AQUA + "https://www.github.com/0ddlyoko");
		} else if ("create".equalsIgnoreCase(args[0]) || "add".equalsIgnoreCase(args[0])) {
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
			if (Invest.get().getInvestManager().exist(name)) {
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
			if (timeToStay < 1) {
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
			if (investPrice < 1) {
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
			if (investEarned < 1) {
				p.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.create.investearned"));
				return true;
			}
			if ("__global__".equalsIgnoreCase(worldguardZone))
				p.sendMessage(__.PREFIX + ChatColor.YELLOW + L.get("command.create.globalZone"));
			Location spawn = p.getLocation();
			if (!Invest.get().getInvestManager().createInvest(name, timeToStay, investPrice, investEarned,
					worldguardZone, spawn)) {
				// Error
				sender.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.create.error"));
				return true;
			}
			sender.sendMessage(__.PREFIX + ChatColor.GREEN + L.get("command.create.done"));
		} else if ("delete".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0])) {
			if (args.length != 2) {
				sender.sendMessage(__.PREFIX + ChatColor.RED
						+ L.get("command.syntaxerror").replaceAll("%s", "/invest delete <name>"));
				return true;
			}
			String name = args[1];
			if (!Invest.get().getInvestManager().exist(name)) {
				sender.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.delete.nameNotExist"));
				return true;
			}
			// Delete
			if (!Invest.get().getInvestManager().deleteInvest(name)) {
				// Error
				sender.sendMessage(__.PREFIX + ChatColor.RED + L.get("command.delete.error"));
				return true;
			}
			sender.sendMessage(__.PREFIX + ChatColor.GREEN + L.get("command.delete.done"));
		}
		return true;
	}
}
