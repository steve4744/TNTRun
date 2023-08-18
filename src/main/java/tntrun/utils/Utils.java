/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import tntrun.TNTRun;
import tntrun.VersionChecker;
import tntrun.arena.Arena;
import tntrun.messages.Messages;

public class Utils {

	private static String[] version = {"Nothing", "Nothing"};
	private static Map<String, String> ranks = new HashMap<>();
	private static Map<String, String> colours = new HashMap<>();
	private static final Logger log = TNTRun.getInstance().getLogger();

	public static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {}
        return false;
    }

	public static boolean isDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (NumberFormatException e) {}
		return false;
	}

	public static String getFormattedTime(int totalSeconds) {
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static int playerCount() {
		int pCount = 0;
		for (Arena arena : TNTRun.getInstance().amanager.getArenas()) {
			pCount += arena.getPlayersManager().getPlayersCount();
		}
		return pCount;
	}

	public static int spectatorCount() {
		int sCount = 0;
		for (Arena arena : TNTRun.getInstance().amanager.getArenas()) {
			sCount += arena.getPlayersManager().getSpectatorsCount();
		}
		return sCount;
	}

	public static int pvpPlayerCount() {
		int pCount = 0;
		for (Arena arena : TNTRun.getInstance().amanager.getPvpArenas()) {
			pCount += arena.getPlayersManager().getPlayersCount();
		}
		return pCount;
	}

	public static int nonPvpPlayerCount() {
		int pCount = 0;
		for (Arena arena : TNTRun.getInstance().amanager.getNonPvpArenas()) {
			pCount += arena.getPlayersManager().getPlayersCount();
		}
		return pCount;
	}

	public static List<String> getTNTRunPlayers() {
		List<String> names = new ArrayList<>();
		TNTRun.getInstance().amanager.getArenas().stream().forEach(arena -> {
			arena.getPlayersManager().getAllParticipantsCopy().stream().forEach(player -> {
				names.add(player.getName());
			});
		});
		return names;
	}

	/**
	 * Compare the current version of the plugin with the latest version available on SpigotMC.
	 *
	 * @param current version of this plugin
	 */
	public static void checkUpdate(String current) {
		new BukkitRunnable() {
			@Override
			public void run() {
				log.info("Checking plugin version...");
				new VersionChecker();
				version = VersionChecker.get().getVersion().split(";");

				if (version[0].equalsIgnoreCase("error")) {
					throw new NullPointerException("An error has occurred while checking the version! Please report this here: https://www.spigotmc.org/threads/tntrun_reloaded.303586/");

				} else if (version[0].equalsIgnoreCase(current)) {
					log.info("You are running the most recent version");
					TNTRun.getInstance().setNeedUpdate(false);

				} else if (current.toLowerCase().contains("beta") || current.toLowerCase().contains("snapshot")) {
					log.info("You are running dev build: " + current);
					log.info("Latest public release    : " + version[0]);
					TNTRun.getInstance().setNeedUpdate(false);

				} else {
					log.info("Your version: " + current);
					log.info("New version : " + version[0]);
					log.info("New version available! Download now: https://www.spigotmc.org/resources/tntrun_reloaded.53359/");
					TNTRun.getInstance().setNeedUpdate(true);
					for (Player p : Bukkit.getOnlinePlayers()) {
						Utils.displayUpdate(p);
					}
				}
			}
		}.runTaskLaterAsynchronously(TNTRun.getInstance(), 30L);
	}

	public static void displayInfo(CommandSender sender) {
		Messages.sendMessage(sender, "&7============" + Messages.trprefix + "============", false);
		Messages.sendMessage(sender, "&bPlugin Version: &f" + TNTRun.getInstance().getDescription().getVersion(), false);
		Messages.sendMessage(sender, "&bWebsite: &fhttps://www.spigotmc.org/resources/tntrun_reloaded.53359/", false);
		Messages.sendMessage(sender, "&bTNTRun_reloaded Author: &fsteve4744", false);
	}

	public static void displayUpdate(Player player) {
		if (player.hasPermission("tntrun.version.check")) {
			TextComponent tc = getTextComponentPrefix();
			TextComponent message = new TextComponent(" New version available!");
			message.setColor(ChatColor.WHITE);
			tc.addExtra(message);

			TextComponent link = new TextComponent(" Click here to download");
			link.setColor(ChatColor.AQUA);
			link.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/tntrun_reloaded.53359/"));
			tc.addExtra(link);

			Content content = new Text(getUpdateMessage().create());
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));

			player.spigot().sendMessage(tc);
		}
	}

	private static TextComponent getTextComponentPrefix() {
		TextComponent tc = new TextComponent("[");
		tc.setColor(ChatColor.GRAY);
		TextComponent tc2 = new TextComponent("TNTRun");
		tc2.setColor(ChatColor.GOLD);
		TextComponent tc3 = new TextComponent("]");
		tc3.setColor(ChatColor.GRAY);
		tc.addExtra(tc2);
		tc.addExtra(tc3);
		return tc;
	}

	private static ComponentBuilder getUpdateMessage() {
		ComponentBuilder cb = new ComponentBuilder("Current version : ").color(ChatColor.AQUA).append(TNTRun.getInstance().getDescription().getVersion()).color(ChatColor.GOLD);
		cb.append("\nLatest version : ").color(ChatColor.AQUA).append(version[0]).color(ChatColor.GOLD);
		return cb;
	}

	public static void displayHelp(Player player) {
		player.spigot().sendMessage(getTextComponent("/trsetup setlobby", true), getTextComponent(Messages.setuplobby));
		player.spigot().sendMessage(getTextComponent("/trsetup create {arena}", true), getTextComponent(Messages.setupcreate));
		player.spigot().sendMessage(getTextComponent("/trsetup setarena {arena}", true), getTextComponent(Messages.setupbounds));
		player.spigot().sendMessage(getTextComponent("/trsetup setloselevel {arena}", true), getTextComponent(Messages.setuploselevel));
		player.spigot().sendMessage(getTextComponent("/trsetup setspawn {arena}", true), getTextComponent(Messages.setupspawn));
		player.spigot().sendMessage(getTextComponent("/trsetup setspectate {arena}", true), getTextComponent(Messages.setupspectate));
		player.spigot().sendMessage(getTextComponent("/trsetup finish {arena}", true), getTextComponent(Messages.setupfinish));
	}

	public static void displayJoinMessage(Player player, String arenaname, String joinMessage) {
		final String border = FormattingCodesParser.parseFormattingCodes(Messages.playerborderinvite);
		TextComponent jointc = new TextComponent(TextComponent.fromLegacyText(border + "\n"));
		jointc.addExtra(getJoinTextComponent(joinMessage, arenaname));
		jointc.addExtra(new TextComponent(TextComponent.fromLegacyText("\n" + border)));
		player.spigot().sendMessage(jointc);
	}

	private static TextComponent getJoinTextComponent(String text, String arenaname) {
		String hoverMessage = FormattingCodesParser.parseFormattingCodes(Messages.playerclickinvite.replace("{ARENA}", arenaname));
		Content content = new Text(hoverMessage);
		TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text)));

		component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tntrun joinorspectate " + arenaname));
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
		return component;
	}

	public static String getTitleCase(String input) {
		return input.substring(0,1).toUpperCase() + input.substring(1).toLowerCase();
	}

	public static TextComponent getTextComponent(String text) {
		return getTextComponent(text, false);
	}

	public static TextComponent getTextComponent(String text, Boolean click) {
		Content content = new Text("Click to select");
		TextComponent tc = new TextComponent(text);
		if (click) {
			String splitter = text.contains("{") ? "{" : "[";
			tc.setColor(ChatColor.GOLD);
			tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, StringUtils.substringBefore(text, splitter)));
			tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
			tc.addExtra(getTextComponentDelimiter(" - "));
		} else {
			tc.setColor(ChatColor.RED);
		}
		return tc;
	}

	private static TextComponent getTextComponentDelimiter(String delim) {
		TextComponent tc = new TextComponent(delim);
		tc.setColor(ChatColor.WHITE);
		return tc;
	}

	public static boolean debug() {
		return TNTRun.getInstance().getConfig().getBoolean("debug", false);
	}

	public static String getDecimalFormat(String amount) {
		String formattedAmount = amount;
		if (!isNumber(amount)) {
			DecimalFormat df = new DecimalFormat("0.00");
			formattedAmount = (amount.endsWith(".00") || amount.endsWith(".0")) ? amount.split("\\.")[0] : df.format(Double.valueOf(amount));
		}
		return formattedAmount;
	}

	public static String getFormattedCurrency(String amount) {
		return TNTRun.getInstance().getConfig().getString("currency.prefix") + getDecimalFormat(amount) +
				TNTRun.getInstance().getConfig().getString("currency.suffix");
	}

	/**
	 * Cache the player's primary group or prefix on joining an arena. If the player's
	 * rank has changed then update the cache.
	 *
	 * @param player
	 */
	public static void cachePlayerGroupData(OfflinePlayer player) {
		String rank = TNTRun.getInstance().getVaultHandler().getPermissions().getPrimaryGroup(null, player);
		if (rank == null) {
			return;
		}
		if (!rank.equalsIgnoreCase(ranks.get(player.getName()))) {
			cacheRank(player);
		}
	}

	/**
	 * Cache the player's rank or prefix.
	 * If the rank is not cached, retrieve it and cache it.
	 * If the player is offline retrieve it asynchronously and cache it.
	 *
	 * @param OfflinePlayer player
	 */
	private static void cacheRank(OfflinePlayer player) {
		FileConfiguration config = TNTRun.getInstance().getConfig();
		String rank = "";
		String cgmeta = "";
		if (TNTRun.getInstance().getVaultHandler().isPermissions() && config.getBoolean("UseRankInChat.usegroup")) {
			if (player.isOnline()) {
				rank = TNTRun.getInstance().getVaultHandler().getPermissions().getPrimaryGroup(null, player);
				if (config.getBoolean("UseRankInChat.groupcolormeta")) {
					cgmeta = TNTRun.getInstance().getVaultHandler().getChat().getGroupInfoString("", rank, "tntrun-color", "");
				}
				if (Utils.debug()) {
					log.info("[TNTRun_reloaded] Cached rank " + rank + " for online player " + player.getName());
					log.info("[TNTRun_reloaded] Cached colour " + cgmeta + " for online player " + player.getName());
				}
			} else {
				final String pn = player.getName();
				new BukkitRunnable() {
					@Override
					public void run() {
						String cgmeta = "";
						String rank = TNTRun.getInstance().getVaultHandler().getPermissions().getPrimaryGroup(null, player);
						ranks.put(pn, rank != null ? rank : "");

						if (config.getBoolean("UseRankInChat.groupcolormeta")) {
							cgmeta = TNTRun.getInstance().getVaultHandler().getChat().getGroupInfoString("", rank, "tntrun-color", "");
							colours.put(pn, cgmeta != null ? cgmeta : "");
						}

						if (Utils.debug()) {
							log.info("[TNTRun_reloaded] Cached rank " + rank + " for offline player " + pn);
							log.info("[TNTRun_reloaded] Cached colour " + cgmeta + " for offline player " + pn);
						}
					}
				}.runTaskAsynchronously(TNTRun.getInstance());
			}

		} else if (TNTRun.getInstance().getVaultHandler().isChat() && config.getBoolean("UseRankInChat.useprefix")) {
			if (player.isOnline()) {
				rank = TNTRun.getInstance().getVaultHandler().getChat().getPlayerPrefix(null, player);
				if (Utils.debug()) {
					log.info("[TNTRun_reloaded] Cached prefix " + rank + " for online player " + player.getName());
				}
			} else {
				final String pn = player.getName();
				new BukkitRunnable() {
					@Override
					public void run() {
						String rank = TNTRun.getInstance().getVaultHandler().getChat().getPlayerPrefix(null, player);
						ranks.put(pn, rank != null ? rank : "");

						if (Utils.debug()) {
							log.info("[TNTRun_reloaded] Cached prefix " + rank + "for offline player " + pn);
						}
					}
				}.runTaskAsynchronously(TNTRun.getInstance());
			}
		}
		ranks.put(player.getName(), rank != null ? rank : "");
		colours.put(player.getName(), cgmeta != null ? cgmeta : "");
	}

	/**
	 * Attempt to get a player's cached rank. This can be either the player's prefix or primary group.
	 *
	 * @param player
	 * @return player's rank.
	 */
	public static String getRank(OfflinePlayer player) {
		if (TNTRun.getInstance().getConfig().getBoolean("UseRankInChat.enabled")) {
			if (ranks.containsKey(player.getName())) {
				return ranks.get(player.getName());
			}
			cacheRank(player);
		}
		return "";
	}

	/**
	 * Get a player's cached rank. This can be either the player's prefix or primary group.
	 *
	 * @param player name
	 * @return player's rank.
	 */
	public static String getRank(String playerName) {
		if (!TNTRun.getInstance().getConfig().getBoolean("UseRankInChat.enabled")) {
			return "";
		}
		return ranks.getOrDefault(playerName, "");
	}

	/**
	 * Attempt to get a player's cached colour meta. The colour is applied to players of the same rank/group.
	 * If the rank is not cached, retrieve it asynchronously and cache it.
	 *
	 * @param player
	 * @return player's rank.
	 */
	public static String getColourMeta(OfflinePlayer player) {
		FileConfiguration config = TNTRun.getInstance().getConfig();
		if (config.getBoolean("UseRankInChat.enabled") && config.getBoolean("UseRankInChat.groupcolormeta")) {
			if (colours.containsKey(player.getName())) {
				return colours.get(player.getName());
			}
		}
		return "";
	}

	/**
	 * Get a player's cached colour meta. The colour is applied to players of the same rank/group.
	 *
	 * @param player name
	 * @return player's rank.
	 */
	public static String getColourMeta(String playerName) {
		FileConfiguration config = TNTRun.getInstance().getConfig();
		if (config.getBoolean("UseRankInChat.enabled") && config.getBoolean("UseRankInChat.groupcolormeta")) {
			return colours.get(playerName) != null ? colours.get(playerName) : "";
		}
		return "";
	}

	public static void removeRankFromCache(String playerName) {
		ranks.remove(playerName);
		colours.remove(playerName);
	}

	/**
	 * The maximum number of double jumps the player is allowed. If permissions are used,
	 * return the lower number of the maximum and number allowed by the permission node.
	 * This applies to free and purchased double jumps.
	 *
	 * @param player
	 * @param max allowed double jumps
	 * @return integer representing the number of double jumps to give player
	 */
	public static int getAllowedDoubleJumps(Player player, int max) {
		if (!TNTRun.getInstance().getConfig().getBoolean("special.UseDoubleJumpPermissions") || max <= 0) {
			return max;
		}
		String permissionPrefix = "tntrun.doublejumps.";
		for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			if (attachmentInfo.getPermission().startsWith(permissionPrefix) && attachmentInfo.getValue()) {
				String permission = attachmentInfo.getPermission();
				if (!isNumber(permission.substring(permission.lastIndexOf(".") + 1))) {
					return 0;
				}
				return Math.min(Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1)), max);
			}
		}
		return max;
	}

	public static void displayPartyInvite(Player player, String target, String joinMessage) {
		TextComponent partytc = new TextComponent(TextComponent.fromLegacyText("Would you like to "));
		partytc.addExtra(getPartyInviteComponent("Accept", "Click to Accept", player, target));
		partytc.addExtra(" or ");
		partytc.addExtra(getPartyInviteComponent("Decline", "Click to Decline", player, target));
		partytc.addExtra(" the party invitation?");
		Bukkit.getPlayer(target).spigot().sendMessage(partytc);
	}

	private static TextComponent getPartyInviteComponent(String text, String hoverMessage, Player player, String target) {
		Content content = new Text(hoverMessage);
		TextComponent component = new TextComponent(text);
		component.setColor(ChatColor.GOLD);
		component.setBold(true);
		component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tntrun party " + text + " " + player.getName()));
		component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
		return component;
	}
}
