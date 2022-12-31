package tntrun.lobby;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import tntrun.TNTRun;
import tntrun.utils.FormattingCodesParser;

public class LobbyScoreboard {

	private TNTRun plugin;

	public LobbyScoreboard(TNTRun plugin) {
		this.plugin = plugin;
	}

	public void createLobbyScoreboard(Player player) {
		if (!plugin.getConfig().getBoolean("special.UseScoreboard") || !plugin.getConfig().getBoolean("scoreboard.enablelobbyscoreboard")) {
			return;
		}
		plugin.getScoreboardManager().storePrejoinScoreboard(player);

		Scoreboard scoreboard = plugin.getScoreboardManager().resetScoreboard(player);
		Objective o = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		int size = plugin.getConfig().getStringList("scoreboard.lobby").size();

		for (String s : plugin.getConfig().getStringList("scoreboard.lobby")) {
			s = FormattingCodesParser.parseFormattingCodes(s);
			s = plugin.getScoreboardManager().getPlaceholderString(s, player);
			o.getScore(s).setScore(size);
			size--;
		}
		player.setScoreboard(scoreboard);
		plugin.getScoreboardManager().addLobbyScoreboard(player.getName());
	}
}
