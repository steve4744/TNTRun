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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.utils.FormattingCodesParser;

public class ScoreboardHandler {

	private final TNTRun plugin;
	private int playingtask;
	private Arena arena;

	public ScoreboardHandler(TNTRun plugin, Arena arena) {
		this.plugin = plugin;
		this.arena = arena;
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
		Scoreboard scoreboard = plugin.getScoreboardManager().resetScoreboard(player);
		Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		int size = plugin.getConfig().getStringList("scoreboard.waiting").size();

		for (String s : plugin.getConfig().getStringList("scoreboard.waiting")) {
			s = plugin.getScoreboardManager().getPlaceholderString(s, player);
			s = FormattingCodesParser.parseFormattingCodes(s).replace("{ARENA}", arena.getArenaName());
			s = s.replace("{PS}", arena.getPlayersManager().getPlayersCount() + "");
			s = s.replace("{MPS}", arena.getStructureManager().getMaxPlayers() + "");
			s = s.replace("{COUNT}", arena.getGameHandler().count + "");
			s = s.replace("{VOTES}", arena.getPlayerHandler().getVotesRequired(arena) + "");
			s = s.replace("{DJ}", arena.getPlayerHandler().getDoubleJumps(player.getName()) + "");

			o.getScore(plugin.getScoreboardManager().getTeamEntry(scoreboard, size, s)).setScore(size);
			size--;
		}
		player.setScoreboard(scoreboard);
	}

	public void removeScoreboard(Player player) {
		if (!plugin.getConfig().getBoolean("special.UseScoreboard")) {
			return;
		}
		plugin.getScoreboardManager().removeScoreboardFromMap(player);
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
				if (!plugin.getConfig().getBoolean("scoreboard.removefromspectators")) {
					for (Player player : arena.getPlayersManager().getSpectators()) {
						updatePlayingScoreboard(player);
					}
				}
			}
		}, 0, 20);
	}

	private void updatePlayingScoreboard(Player player) {
		Scoreboard scoreboard = plugin.getScoreboardManager().resetScoreboard(player);
		Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		int size = plugin.getConfig().getStringList("scoreboard.playing").size();

		for (String s : plugin.getConfig().getStringList("scoreboard.playing")) {
			s = plugin.getScoreboardManager().getPlaceholderString(s, player);
			s = FormattingCodesParser.parseFormattingCodes(s).replace("{ARENA}", arena.getArenaName());
			s = s.replace("{PS}", arena.getPlayersManager().getPlayersCount() + "");
			s = s.replace("{MPS}", arena.getStructureManager().getMaxPlayers() + "");
			s = s.replace("{LOST}", arena.getGameHandler().lostPlayers + "");
			s = s.replace("{LIMIT}", arena.getGameHandler().getTimeRemaining()/20 + "");
			s = s.replace("{DJ}", arena.getPlayerHandler().getDoubleJumps(player.getName()) + "");

			o.getScore(plugin.getScoreboardManager().getTeamEntry(scoreboard, size, s)).setScore(size);
			size--;
		}
	}

	public int getPlayingTask() {
		return playingtask;
	}
}
