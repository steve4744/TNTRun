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

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class Stats {

	private TNTRun plugin;
	private File file;
	private String lbcolour;
	private String lbentry;
	private String lbrank;
	private String lbplaceholdervalue;

	private static Map<String, Integer> pmap = new HashMap<>();
	private static Map<String, Integer> wmap = new HashMap<>();
	private static Map<String, Integer> lmap = new HashMap<>();
	private static List<String> sortedPlayed = new ArrayList<>();
	private static List<String> sortedWins = new ArrayList<>();
	private static List<String> sortedLosses = new ArrayList<>();

	public Stats(TNTRun plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "stats.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		loadStats();
	}

	/**
	 * Loads the player stats into 2 maps representing games played and games won.
	 */
	private void loadStats() {
		if (plugin.isFile()) {
			getStatsFromFile();
			return;
		}
		final String table = plugin.getConfig().getString("MySQL.table");
		if (plugin.getMysql().isConnected()) {
			getStatsFromDB(table);
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (plugin.getMysql().isConnected()) {
					getStatsFromDB(table);
				} else {
					plugin.setUseStats(false);
					plugin.getLogger().info("Failure connecting to MySQL database, disabling stats");
				}
			}
		}.runTaskLaterAsynchronously(plugin, 60L);
	}

	/**
	 * Increment the number of played games in the map, and save to file.
	 *
	 * @param player
	 * @param value
	 */
	public void addPlayedGames(Player player, int value) {
		String uuid = getPlayerUUID(player);
		if (pmap.containsKey(uuid)) {
			pmap.put(uuid, pmap.get(uuid) + value);

		} else {
			pmap.put(uuid, value);
		}
		saveStats(uuid, "played");
	}

	/**
	 * Increment the number of wins for the player in the map, and save to file.
	 *
	 * @param player
	 * @param value
	 */
	public void addWins(Player player, int value) {
		String uuid = getPlayerUUID(player);
		if (wmap.containsKey(uuid)) {
			wmap.put(uuid, wmap.get(uuid) + value);
	
		} else {
			wmap.put(uuid, value);
		}
		saveStats(uuid, "wins");
		sortedWins.clear();
		sortedLosses.clear();
	}

	public int getLosses(String uuid) {
		return getPlayedGames(uuid) - getWins(uuid);
	}

	public int getPlayedGames(String uuid) {
		return pmap.containsKey(uuid) ? pmap.get(uuid) : 0;
	}

	public int getWins(String uuid) {
		return wmap.containsKey(uuid) ? wmap.get(uuid) : 0;
	}

	/**
	 * Displays the leader board in chat. The number of entries is set in the configuration file.
	 *
	 * @param sender
	 * @param entries
	 */
	public void getLeaderboard(CommandSender sender, int entries) {
		final String type = "wins";
		getWorkingList(type).stream()
			.limit(entries)
			.forEach(uuid -> {
				if (plugin.useUuid()) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
					lbentry = player.getName();
					lbrank = Utils.getRank(player);
					lbcolour = Utils.getColourMeta(player);
				} else {
					lbentry = uuid;
					lbrank = Utils.getRank(Bukkit.getPlayer(uuid));
					lbcolour = Utils.getColourMeta(Bukkit.getPlayer(uuid));
				}
				Messages.sendMessage(sender, Messages.leaderboard
					.replace("{POSITION}", String.valueOf(getPosition(uuid, type)))
					.replace("{PLAYER}", lbentry)
					.replace("{RANK}", lbrank)
					.replace("{COLOR}", lbcolour)
					.replace("{WINS}", String.valueOf(wmap.get(uuid))), false);
		});
	}

	private boolean isValidUuid(String uuid) {
		try {
			UUID.fromString(uuid);
		} catch (IllegalArgumentException ex){
			return false;
		}
		return true;
	}

	private boolean isKnownPlayer(String identity) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(identity));
		return player.hasPlayedBefore();
	}

	/**
	 * Cache the contents of stats.yml. Online servers use players UUIDs and offline servers use player names.
	 * For online servers, validate the UUID as the file could contain player names if the server has been in
	 * offline mode. Ignore UUID entries for servers in offline mode.
	 */
	private void getStatsFromFile() {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		ConfigurationSection stats = config.getConfigurationSection("stats");

		if (stats != null) {
			if (plugin.useUuid()) {
				for (String uuid : stats.getKeys(false)) {
					if (!isValidUuid(uuid) || !isKnownPlayer(uuid)) {
						continue;
					}
					wmap.put(uuid, config.getInt("stats." + uuid + ".wins", 0));
					pmap.put(uuid, config.getInt("stats." + uuid + ".played", 0));
				}
			} else {
				for (String playerName : stats.getKeys(false)) {
					if (isValidUuid(playerName)) {
						continue;
					}
					wmap.put(playerName, config.getInt("stats." + playerName + ".wins", 0));
					pmap.put(playerName, config.getInt("stats." + playerName + ".played", 0));
				}
			}
		}
	}

	/**
	 * Cache the stats from the database. Online servers use players UUIDs and offline servers use player names.
	 * For online servers, validate the UUID as the file could contain player names if the server has been in
	 * offline mode. Ignore UUID entries for servers in offline mode.
	 */
	private void getStatsFromDB(String table) {
		Stream.of("wins", "played").forEach(stat -> {
			Map<String, Integer> workingMap = new HashMap<>();
			try {
				ResultSet rs;
				rs = plugin.getMysql().query("SELECT * FROM `" + table + "` ORDER BY " + stat + " DESC LIMIT 99999").getResultSet();

				while (rs.next()) {
					String playerName = rs.getString("username");
					if (plugin.useUuid()) {
						if (!isValidUuid(playerName) || !isKnownPlayer(playerName)) {
							continue;
						}
					} else if (isValidUuid(playerName)) {
						continue;
					}
					workingMap.put(playerName, rs.getInt(stat));
				}
				if (stat.equalsIgnoreCase("wins")) {
					wmap.putAll(workingMap);
				} else {
					pmap.putAll(workingMap);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		});
	}

	public Map<String, Integer> getWinMap() {
		return wmap;
	}

	private void saveStats(String uuid, String statname) {
		if (plugin.isFile()) {
			saveStatsToFile(uuid, statname);
			return;
		}
		saveStatsToDB(uuid, statname);
	}

	private void saveStatsToFile(String uuid, String statname) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		if (statname.equalsIgnoreCase("played")) {
			config.set("stats." + uuid + ".played", pmap.get(uuid));

		} else if (statname.equalsIgnoreCase("wins")) {
			config.set("stats." + uuid + ".wins", wmap.get(uuid));
		} else {
			config.set("stats." + uuid, null);
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveStatsToDB(String uuid, String statname) {
		if (statname.equalsIgnoreCase("played") || statname.equalsIgnoreCase("reset")) {
			updateDB("played", uuid, pmap.getOrDefault(uuid, 0));
		}
		if (statname.equalsIgnoreCase("wins") || statname.equalsIgnoreCase("reset")) {
			updateDB("wins", uuid, wmap.getOrDefault(uuid, 0));
		}
	}

	private void updateDB(String statname, String player, Integer value) {
		final String table = plugin.getConfig().getString("MySQL.table");
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.getMysql().query("UPDATE `" + table + "` SET `" + statname
						+ "`='" + value + "' WHERE `username`='" + player + "';");
			}
		}.runTaskAsynchronously(plugin);
	}

	public String getPlayerUUID(OfflinePlayer player) {
		return plugin.useUuid() ? player.getUniqueId().toString() : player.getName();
	}

	/**
	 * Returns the player name, score or rank of the player occupying the requested leader board position for the given type.
	 *
	 * @param position leader board position
	 * @param type type can be 'wins', 'played' or 'losses'.
	 * @param item item can be 'score', 'player' or 'rank'.
	 * @return the requested placeholder value.
	 */
	public String getLeaderboardPosition(int position, String type, String item) {
		List<String> workingList = getWorkingList(type);
		if (position > workingList.size()) {
			return "";
		}

		String uuid = workingList.get(position - 1);
		if (item.equalsIgnoreCase("score")) {
			if (type.equalsIgnoreCase("wins")) {
				lbplaceholdervalue = String.valueOf(wmap.get(uuid));
			} else if (type.equalsIgnoreCase("played")) {
				lbplaceholdervalue = String.valueOf(pmap.get(uuid));
			} else {
				lbplaceholdervalue = String.valueOf(lmap.get(uuid));
			}

		} else if (plugin.useUuid()) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
			lbplaceholdervalue = item.equalsIgnoreCase("player") ? p.getName() : Utils.getRank(p);

		} else {
			lbplaceholdervalue = item.equalsIgnoreCase("player") ? uuid : Utils.getRank(Bukkit.getPlayer(uuid));
		}
		return lbplaceholdervalue != null ? lbplaceholdervalue : "";
	}

	private List<String> getWorkingList(String type) {
		switch(type.toLowerCase()) {
		case "wins":
			if (sortedWins.isEmpty()) {
				sortedWins = createSortedList(wmap);
			}
			return sortedWins;

		case "played":
			if (sortedPlayed.isEmpty()) {
				sortedPlayed = createSortedList(pmap);
			}
			return sortedPlayed;

		case "losses":
			if (sortedLosses.isEmpty()) {
				sortedLosses = createSortedList(getLossMap());
			}
			return sortedLosses;

		default:
			return List.of();
		}
	}

	private List<String> createSortedList(Map<String, Integer> map) {
		List<String> sorted = new ArrayList<>();
		map.entrySet().stream()
			.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
			.forEach(e -> {
				sorted.add(e.getKey());
			}
		);
		return sorted;
	}

	/**
	 * Creates a map of player names and number of losses, calculated as the difference between
	 * the number of games played and the number of wins.
	 * @return
	 */
	private Map<String, Integer> getLossMap() {
		sortedLosses.clear();
		pmap.entrySet().forEach(e -> {
			int wins = 0;
			if (wmap.containsKey(e.getKey())) {
				wins = wmap.get(e.getKey());
			}
			lmap.put(e.getKey(), e.getValue() - wins);
		});
		return lmap;
	}

	public boolean hasDatabaseEntry(OfflinePlayer player) {
		return pmap.containsKey(getPlayerUUID(player));
	}

	public void resetStats(String uuid) {
		pmap.remove(uuid);
		wmap.remove(uuid);
		lmap.remove(uuid);
		sortedWins.remove(uuid);
		sortedPlayed.remove(uuid);
		sortedLosses.remove(uuid);
		saveStats(uuid, "reset");
	}

	public boolean hasStats(String uuid) {
		return pmap.containsKey(uuid) || wmap.containsKey(uuid);
	}

	public void clearPlayedList() {
		sortedPlayed.clear();
		sortedLosses.clear();
	}

	/**
	 * Get the position the player occupies in the leaderboard.
	 * If the player is not in the list then zero is returned.
	 *
	 * @param uuid
	 * @param type leaderboard type "wins", "played" or "losses"
	 * @return leaderboard position or zero
	 */
	public int getPosition(String uuid, String type) {
		return getWorkingList(type).indexOf(uuid) + 1;
	}
}
