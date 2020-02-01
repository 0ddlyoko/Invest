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
import me.oddlyoko.invest.config.PlayerInvest;
import me.oddlyoko.invest.invest.InvestType;

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
			if (args.length == 0 && sender instanceof Player) {
				Player p = (Player) sender;
				if (Invest.get().getInvestManager().hasInvest(((Player) sender))) {
					String cmd = Invest.get().getConfigManager().getCommandHaveInvest();
					if (cmd != null && !"".equalsIgnoreCase(cmd.trim()))
						p.chat((cmd.startsWith("/") ? "" : "/") + cmd);
					else {
						PlayerInvest pi = Invest.get().getInvestManager().getInvest(p);
						p.teleport(pi.getInvestType().getSpawn());
					}
					return true;
				} else {
					String cmd = Invest.get().getConfigManager().getCommandInvest();
					if (cmd != null && !"".equalsIgnoreCase(cmd.trim())) {
						p.chat((cmd.startsWith("/") ? "" : "/") + cmd);
						return true;
						// Execute command
					}
				}
			}
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
			if (sender.hasPermission("invest.list"))
				sender.sendMessage(
						ChatColor.AQUA + "- /invest list" + ChatColor.YELLOW + " : " + L.get("command.help.list"));
			if (sender.hasPermission("invest.info"))
				sender.sendMessage(ChatColor.AQUA + "- /invest info <name>" + ChatColor.YELLOW + " : "
						+ L.get("command.help.infoInvest"));
			if (sender.hasPermission("invest.tp"))
				sender.sendMessage(
						ChatColor.AQUA + "- /invest tp <name>" + ChatColor.YELLOW + " : " + L.get("command.help.tp"));
			sender.sendMessage(
					ChatColor.AQUA + "- /invest start <name>" + ChatColor.YELLOW + " : " + L.get("command.help.start"));
			sender.sendMessage(
					ChatColor.AQUA + "- /invest stop" + ChatColor.YELLOW + " : " + L.get("command.help.stop"));
			if (sender.hasPermission("invest.players"))
				sender.sendMessage(ChatColor.AQUA + "- /invest players" + ChatColor.YELLOW + " : "
						+ L.get("command.help.players"));
		} else if ("info".equalsIgnoreCase(args[0])) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW
						+ "]-----------");
				sender.sendMessage(ChatColor.AQUA + "Created by 0ddlyoko");
				sender.sendMessage(ChatColor.GREEN + "v" + Invest.get().getDescription().getVersion());
				sender.sendMessage(ChatColor.AQUA + "https://www.0ddlyoko.be");
				sender.sendMessage(ChatColor.AQUA + "https://www.github.com/0ddlyoko");
			} else {
				if (!sender.hasPermission("invest.info")) {
					sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.noperm"));
					return true;
				}
				String name = args[1];
				InvestType inv = Invest.get().getInvestManager().get(name);
				if (inv == null) {
					sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.info.nameNotExist"));
					return true;
				}
				sender.sendMessage(ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW
						+ "]-----------");
				sender.sendMessage(
						ChatColor.AQUA + "- " + L.get("command.info.name") + ": " + ChatColor.YELLOW + inv.getName());
				sender.sendMessage(ChatColor.AQUA + "- " + L.get("command.info.timeToStay") + ": " + ChatColor.YELLOW
						+ inv.getTimeToStay());
				sender.sendMessage(ChatColor.AQUA + "- " + L.get("command.info.investPrice") + ": " + ChatColor.YELLOW
						+ inv.getInvestPrice());
				sender.sendMessage(ChatColor.AQUA + "- " + L.get("command.info.investEarned") + ": " + ChatColor.YELLOW
						+ inv.getInvestEarned());
				sender.sendMessage(ChatColor.AQUA + "- " + L.get("command.info.worldguardZone") + ": "
						+ ChatColor.YELLOW + inv.getWorldguardZone());
				Location spawn = inv.getSpawn();
				StringBuilder sb = new StringBuilder();
				sb.append("x = ").append(String.format("%.2f", spawn.getX()));
				sb.append(", y = ").append(String.format("%.2f", spawn.getY()));
				sb.append(", z = ").append(String.format("%.2f", spawn.getZ()));
				sb.append(", world = ").append(spawn.getWorld().getName());
				sender.sendMessage(
						ChatColor.AQUA + "- " + L.get("command.info.spawn") + ": " + ChatColor.YELLOW + sb.toString());
			}
		} else if ("create".equalsIgnoreCase(args[0]) || "add".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("invest.create")) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.noperm"));
				return true;
			}
			if (args.length != 6) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.syntaxerror").replace("%s",
						"/invest create <name> <time-to-stay> <invest-price> <invest-earned> <worldguard-zone>"));
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.nothuman"));
				return true;
			}
			Player p = (Player) sender;
			String name = args[1];
			int timeToStay;
			int investPrice;
			int investEarned;
			String worldguardZone = args[5];
			if (Invest.get().getInvestManager().exist(name)) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.nameExist"));
				return true;
			}
			// Invalid time
			try {
				timeToStay = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.timetostay"));
				return true;
			}
			if (timeToStay < 1) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.timetostay"));
				return true;
			}
			// Invalid price
			try {
				investPrice = Integer.parseInt(args[3]);
			} catch (NumberFormatException ex) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.investprice"));
				return true;
			}
			if (investPrice < 1) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.investprice"));
				return true;
			}
			// Invalid earned
			try {
				investEarned = Integer.parseInt(args[4]);
			} catch (NumberFormatException ex) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.investearned"));
				return true;
			}
			if (investEarned < 1) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.investearned"));
				return true;
			}
			if ("__global__".equalsIgnoreCase(worldguardZone))
				p.sendMessage(Invest.prefix() + ChatColor.YELLOW + L.get("command.create.globalZone"));
			Location spawn = p.getLocation();
			if (!Invest.get().getInvestManager().createInvest(name, timeToStay, investPrice, investEarned,
					worldguardZone, spawn)) {
				// Error
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.create.error"));
				return true;
			}
			sender.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("command.create.done"));
		} else if ("delete".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("invest.delete")) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.noperm"));
				return true;
			}
			if (args.length != 2) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED
						+ L.get("command.syntaxerror").replaceAll("%s", "/invest delete <name>"));
				return true;
			}
			String name = args[1];
			if (!Invest.get().getInvestManager().exist(name)) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.delete.nameNotExist"));
				return true;
			}
			// Delete
			if (!Invest.get().getInvestManager().deleteInvest(name)) {
				// Error
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.delete.error"));
				return true;
			}
			sender.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("command.delete.done"));
		} else if ("list".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("invest.list")) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.noperm"));
				return true;
			}
			sender.sendMessage(
					ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "]-----------");
			for (InvestType invest : Invest.get().getInvestManager().list())
				sender.sendMessage(ChatColor.AQUA + "- " + invest.getName());
			sender.sendMessage(ChatColor.GREEN + L.get("command.list.count").replace("%s",
					Integer.toString(Invest.get().getInvestManager().count())));
		} else if ("tp".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("invest.tp")) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.noperm"));
				return true;
			}
			if (args.length != 2) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED
						+ L.get("command.syntaxerror").replaceAll("%s", "/invest tp <name>"));
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.nothuman"));
				return true;
			}
			Player p = (Player) sender;
			String name = args[1];
			InvestType inv = Invest.get().getInvestManager().get(name);
			if (inv == null) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.tp.nameNotExist"));
				return true;
			}
			sender.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("command.tp.teleport"));
			p.teleport(inv.getSpawn());
		} else if ("start".equalsIgnoreCase(args[0])) {
			if (args.length != 2) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED
						+ L.get("command.syntaxerror").replaceAll("%s", "/invest start <name>"));
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.nothuman"));
				return true;
			}
			Player p = (Player) sender;
			if (Invest.get().getInvestManager().hasInvest(p)) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.start.alreadyInvest"));
				return true;
			}
			String name = args[1];
			InvestType inv = Invest.get().getInvestManager().get(name);
			if (inv == null) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.start.nameNotExist"));
				return true;
			}
			if (!Invest.get().getVaultManager().hasMoney(p, inv.getInvestPrice())) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.start.notMoney"));
				return true;
			}
			if (!Invest.get().getVaultManager().remove(p, inv.getInvestPrice())) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("error"));
				return true;
			}
			Invest.get().getInvestManager().startInvest(p, inv);
			int totalSec = inv.getTimeToStay();
			int hour = totalSec / 3600;
			int min = (totalSec / 60) - (hour * 60);
			int sec = totalSec % 60;
			int price = inv.getInvestPrice();
			int earn = inv.getInvestEarned();
			p.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("command.start.done")
					.replaceAll("%player%", p.getName()).replaceAll("%displayName%", p.getDisplayName())
					.replaceAll("%hour%", Integer.toString(hour)).replaceAll("%min%", Integer.toString(min))
					.replaceAll("%sec%", Integer.toString(sec)).replaceAll("%totalsec%", Integer.toString(totalSec))
					.replaceAll("%price%", Integer.toString(price)).replaceAll("%earn%", Integer.toString(earn)));
		} else if ("stop".equalsIgnoreCase(args[0])) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.nothuman"));
				return true;
			}
			Player p = (Player) sender;
			if (!Invest.get().getInvestManager().hasInvest(p)) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.stop.notInvest"));
				return true;
			}
			if (!Invest.get().getInvestManager().stopAndRefund(p, false)) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + L.get("error"));
				return true;
			}
			p.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("command.stop.done"));
		} else if ("players".equalsIgnoreCase(args[0])) {
			if (!sender.hasPermission("invest.players")) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.noperm"));
				return true;
			}
			sender.sendMessage(Invest.prefix() + ChatColor.GREEN + L.get("command.players.total").replaceAll("%nbr%",
					Integer.toString(Invest.get().getInvestManager().getNumberPlayerInside())));
		} else if ("get".equalsIgnoreCase(args[0])) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Invest.prefix() + ChatColor.RED + L.get("command.nothuman"));
				return true;
			}
			Player p = (Player) sender;
			if (!Invest.get().getInvestManager().hasInvest(p)) {
				p.sendMessage(Invest.prefix() + ChatColor.RED + "You don't have an invest");
				return true;
			}
			PlayerInvest inv = Invest.get().getInvestManager().getInvest(p);
			p.sendMessage("- uuid = " + inv.getUUID());
			p.sendMessage("- name = " + inv.getInvestType().getName());
			p.sendMessage("- time = " + inv.getTime());
		}
		return true;
	}
}
