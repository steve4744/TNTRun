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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Streams;

import tntrun.TNTRun;
import tntrun.messages.Messages;

public class Stats {

	private TNTRun plugin;
	private File file;
	private int position;
	private String lbentry;
	private String lbplaceholdername;
	private String lbplaceholdervalue;

	private static Map<String, Integer> pmap = new HashMap<>();
	private static Map<String, Integer> wmap = new HashMap<>();

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
		if (plugin.mysql.isConnected()) {
			getStatsFromDB(table);
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (plugin.mysql.isConnected()) {
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
		saveStats(player, "played");
	}

	/**
	 * Increment the number of wins for the player in the map, and save to file.
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
		saveStats(player, "wins");
	}

	public int getLosses(OfflinePlayer player) {
		return getPlayedGames(player) - getWins(player);
	}

	public int getPlayedGames(OfflinePlayer player) {
		String uuid = getPlayerUUID(player);
		return pmap.containsKey(uuid) ? pmap.get(uuid) : 0;
	}

	public int getWins(OfflinePlayer player) {
		String uuid = getPlayerUUID(player);
		return wmap.containsKey(uuid) ? wmap.get(uuid) : 0;
	}

	public void getLeaderboard(CommandSender sender, int entries) {
		position = 0;
		wmap.entrySet().stream()
			.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
			.limit(entries)
			.forEach(e -> {
			if (Bukkit.getOnlineMode()) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(e.getKey()));
				lbentry = p.getName();
			} else {
				lbentry = e.getKey();
			}
			position++;
			Messages.sendMessage(sender, Messages.leaderboard
					.replace("{POSITION}", String.valueOf(position))
					.replace("{PLAYER}", lbentry)
					.replace("{WINS}", String.valueOf(e.getValue())), false);
			});
		return;
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
			if (Bukkit.getOnlineMode()) {
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
				rs = plugin.mysql.query("SELECT * FROM `" + table + "` ORDER BY " + stat + " DESC LIMIT 99999").getResultSet();

				while (rs.next()) {
					String playerName = rs.getString("username");
					if (Bukkit.getOnlineMode()) {
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

	private void saveStats(Player player, String statname) {
		if (plugin.isFile()) {
			saveStatsToFile(player, statname);
			return;
		}
		saveStatsToDB(player, statname);
	}

	private void saveStatsToFile(Player player, String statname) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		String uuid = getPlayerUUID(player);

		if (statname.equalsIgnoreCase("played")) {
			config.set("stats." + uuid + ".played", pmap.get(uuid));

		} else if (statname.equalsIgnoreCase("wins")) {
			config.set("stats." + uuid + ".wins", wmap.get(uuid));
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveStatsToDB(Player player, String statname) {
		String uuid = getPlayerUUID(player);

		if (statname.equalsIgnoreCase("played")) {
			updateDB("played", uuid, pmap.get(uuid));

		} else if (statname.equalsIgnoreCase("wins")) {
			updateDB("wins", uuid, wmap.get(uuid));
		}
	}

	private void updateDB(String statname, String player, Integer value) {
		final String table = plugin.getConfig().getString("MySQL.table");
		new BukkitRunnable() {
			@Override
			public void run() {
				plugin.mysql.query("UPDATE `" + table + "` SET `" + statname
						+ "`='" + value + "' WHERE `username`='" + player + "';");
			}
		}.runTaskAsynchronously(plugin);
	}

	private String getPlayerUUID(OfflinePlayer player) {
		return Bukkit.getOnlineMode() ? player.getUniqueId().toString() : player.getName();
	}

	/**
	 * Returns the player name or score occupying the requested leader board position for the given type.
	 * Type can be 'wins', 'played' or 'losses'.
	 * @param rank
	 * @param type
	 * @param item
	 * @return
	 */
	public String getLeaderboardPosition(Integer rank, String type, String item) {
		Map<String, Integer> workingMap = new HashMap<>();

		switch(type.toLowerCase()) {
		case "wins":
			workingMap.putAll(wmap);
			break;
		case "played":
			workingMap.putAll(pmap);
			break;
		case "losses":
			workingMap.putAll(getLossMap());
			break;
		default:
			return null;
		}

		if (rank > workingMap.size()) {
			return "";
		}
		Optional<Entry<String, Integer>> opt = Streams.findLast(
				workingMap.entrySet().stream()
				.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(rank));
		opt.ifPresent(x -> {
			lbplaceholdername = opt.get().getKey();
			if (Bukkit.getOnlineMode()) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(opt.get().getKey()));
				lbplaceholdername = p.getName();
			}
			lbplaceholdervalue = String.valueOf(opt.get().getValue());
		});
		return item.equalsIgnoreCase("player") ? lbplaceholdername : lbplaceholdervalue;
	}

	/**
	 * Creates a map of player names and number of losses, calculated as the difference between
	 * the number of games played and the number of wins.
	 * @return
	 */
	private Map<String, Integer> getLossMap() {
		Map<String, Integer> lmap = new HashMap<>();
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
}
