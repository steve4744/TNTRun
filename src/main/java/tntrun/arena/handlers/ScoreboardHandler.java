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
package tntrun.arena.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.utils.FormattingCodesParser;

public class ScoreboardHandler {

	private final TNTRun plugin;
	private Scoreboard scoreboard;
	private Map<String, Scoreboard> scoreboardMap = new HashMap<>();
	private Map<String, Scoreboard> prejoinScoreboards = new HashMap<>();
	private int playingtask;
	private Arena arena;

	public ScoreboardHandler(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
	}

	private Scoreboard buildScoreboard() {
		FileConfiguration config = plugin.getConfig();
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		if (config.getBoolean("special.UseScoreboard")) {
			Objective o = scoreboard.registerNewObjective("TNTRun", "waiting", "TNTRun");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);

			String header = FormattingCodesParser.parseFormattingCodes(config.getString("scoreboard.header", ChatColor.GOLD.toString() + ChatColor.BOLD + "TNTRUN"));
			o.setDisplayName(header);
		}
		return scoreboard;
	}

	/**
	 * Create the waiting scoreboard for all players including spectator-only players.
	 */
	public void createWaitingScoreBoard() {
		if (!plugin.getConfig().getBoolean("special.UseScoreboard")) {
			return;
		}

		for (Player player : arena.getPlayersManager().getAllParticipantsCopy()) {
			updateWaitingScoreboard(player);
		}
	}

	public void updateWaitingScoreboard(Player player) {
		if (scoreboardMap.containsKey(player.getName())) {
			scoreboard = scoreboardMap.get(player.getName());
		} else {
			scoreboard = buildScoreboard();
			scoreboardMap.put(player.getName(), scoreboard);
		}
		resetScoreboard(player);
		Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		try {
			int size = plugin.getConfig().getStringList("scoreboard.waiting").size();

			for (String s : plugin.getConfig().getStringList("scoreboard.waiting")) {
				s = FormattingCodesParser.parseFormattingCodes(s).replace("{ARENA}", arena.getArenaName());
				s = s.replace("{PS}", arena.getPlayersManager().getPlayersCount() + "");
				s = s.replace("{MPS}", arena.getStructureManager().getMaxPlayers() + "");
				s = s.replace("{COUNT}", arena.getGameHandler().count + "");
				s = s.replace("{VOTES}", getVotesRequired(arena) + "");
				s = s.replace("{DJ}", arena.getPlayerHandler().getDoubleJumps(player) + "");
				s = getPlaceholderString(s, player);
				o.getScore(s).setScore(size);
				size--;
			}
			player.setScoreboard(scoreboard);

		} catch (NullPointerException ex) {

		}
	}

	private boolean isPlaceholderString(String s) {
		return StringUtils.substringBetween(s, "%") != null && !StringUtils.substringBetween(s, "%").isEmpty();
	}

	private String getPlaceholderString(String s, OfflinePlayer player) {
		if (!plugin.isPlaceholderAPI() || !isPlaceholderString(s)) {
			return s;
		}
		String[] a = s.split("%");
		return a[0] + PlaceholderAPI.setPlaceholders(player, "%" + a[1] + "%");
	}

	private void resetScoreboard(Player player) {
		scoreboard = scoreboardMap.get(player.getName());
		for (String entry : new ArrayList<String>(scoreboard.getEntries())) {
			scoreboard.resetScores(entry);
		}
	}

	private Integer getVotesRequired(Arena arena) {
		int minPlayers = arena.getStructureManager().getMinPlayers();
		double votePercent = arena.getStructureManager().getVotePercent();
		int votesCast = arena.getPlayerHandler().getVotesCast();

		return (int) (Math.ceil(minPlayers * votePercent) - votesCast);
	}

	public void removeScoreboard(Player player) {
		scoreboardMap.remove(player.getName());
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}

	public void createPlayingScoreBoard() {
		if (!plugin.getConfig().getBoolean("special.UseScoreboard")) {
			return;
		}
		playingtask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (Player player : arena.getPlayersManager().getPlayers()) {
					updatePlayingScoreboard(player);
				}
			}
		}, 0, 20);
	}

	private void updatePlayingScoreboard(Player player) {
		resetScoreboard(player);
		Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		int size = plugin.getConfig().getStringList("scoreboard.playing").size();
		for (String s : plugin.getConfig().getStringList("scoreboard.playing")) {
			s = FormattingCodesParser.parseFormattingCodes(s).replace("{ARENA}", arena.getArenaName());
			s = s.replace("{PS}", arena.getPlayersManager().getPlayersCount() + "");
			s = s.replace("{MPS}", arena.getStructureManager().getMaxPlayers() + "");
			s = s.replace("{LOST}", arena.getGameHandler().lostPlayers + "");
			s = s.replace("{LIMIT}", arena.getGameHandler().getTimeRemaining()/20 + "");
			s = s.replace("{DJ}", arena.getPlayerHandler().getDoubleJumps(player) + "");
			s = getPlaceholderString(s, player);
			o.getScore(s).setScore(size);
			size--;
		}
	}

	public int getPlayingTask() {
		return playingtask;
	}

	public void storePrejoinScoreboard(Player player) {
		prejoinScoreboards.put(player.getName(), player.getScoreboard());
	}

	public void restorePrejoinScoreboard(Player player) {
		if (prejoinScoreboards.get(player.getName()) != null) {
			player.setScoreboard(prejoinScoreboards.remove(player.getName()));
		}
	}
}
