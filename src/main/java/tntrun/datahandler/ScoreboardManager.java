package tntrun.datahandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.clip.placeholderapi.PlaceholderAPI;
import tntrun.TNTRun;
import tntrun.utils.FormattingCodesParser;

public class ScoreboardManager {

	private TNTRun plugin;
	private final String PLUGIN_NAME = "TNTRun";
	private HashSet<String> lobbyScoreboards = new HashSet<>();
	private Map<String, Scoreboard> scoreboardMap = new HashMap<>();
	private Map<String, Scoreboard> prejoinScoreboards = new HashMap<>();

	public ScoreboardManager(TNTRun plugin) {
		this.plugin = plugin;
		updateScoreboardList();
	}

	private void updateScoreboardList() {
		if (!plugin.getConfig().getBoolean("scoreboard.displaydoublejumps")) {
			return;
		}
		List<String> ps = plugin.getConfig().getStringList("scoreboard.playing");
		if (ps.stream().noneMatch(s -> s.contains("{DJ}"))) {
			ps.add("&e ");
			ps.add("&fDouble Jumps: &6&l{DJ}");
			plugin.getConfig().set("scoreboard.playing", ps);
			plugin.saveConfig();
		}
		List<String> ws = plugin.getConfig().getStringList("scoreboard.waiting");
		if (ws.stream().noneMatch(s -> s.contains("{DJ}"))) {
			ws.add("&e ");
			ws.add("&fDouble Jumps: &6&l{DJ}");
			plugin.getConfig().set("scoreboard.waiting", ws);
			plugin.saveConfig();
		}
	}

	private Scoreboard buildScoreboard() {
		FileConfiguration config = plugin.getConfig();
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		if (config.getBoolean("special.UseScoreboard")) {
			Objective o = scoreboard.registerNewObjective(PLUGIN_NAME, Criteria.DUMMY, PLUGIN_NAME);
			o.setDisplaySlot(DisplaySlot.SIDEBAR);

			String header = FormattingCodesParser.parseFormattingCodes(config.getString("scoreboard.header", ChatColor.GOLD.toString() + ChatColor.BOLD + "TNTRUN"));
			o.setDisplayName(header);
		}
		return scoreboard;
	}

	private Scoreboard getPlayerScoreboard(Player player) {
		if (scoreboardMap.containsKey(player.getName())) {
			return scoreboardMap.get(player.getName());
		}
		Scoreboard scoreboard = buildScoreboard();
		if (plugin.getConfig().getBoolean("disablecollisions")) {
			Team team = scoreboard.registerNewTeam(PLUGIN_NAME);
			team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			team.addEntry(player.getName());
		}
		scoreboardMap.put(player.getName(), scoreboard);
		return scoreboard;
	}

	private boolean isPlaceholderString(String s) {
		return StringUtils.substringBetween(s, "%") != null && !StringUtils.substringBetween(s, "%").isEmpty();
	}

	public String getPlaceholderString(String s, OfflinePlayer player) {
		if (!plugin.isPlaceholderAPI() || !isPlaceholderString(s)) {
			return s;
		}
		String[] a = s.split("%");
		return a[0] + PlaceholderAPI.setPlaceholders(player, "%" + a[1] + "%");
	}

	public Scoreboard resetScoreboard(Player player) {
		Scoreboard scoreboard = getPlayerScoreboard(player);
		for (String entry : new ArrayList<String>(scoreboard.getEntries())) {
			scoreboard.resetScores(entry);
		}
		scoreboardMap.put(player.getName(), scoreboard);
		return scoreboard;
	}

	public void removeScoreboardFromMap(Player player) {
		scoreboardMap.remove(player.getName());
	}

	public void storePrejoinScoreboard(Player player) {
		prejoinScoreboards.putIfAbsent(player.getName(), player.getScoreboard());
	}

	public void restorePrejoinScoreboard(Player player) {
		if (prejoinScoreboards.get(player.getName()) != null) {
			player.setScoreboard(prejoinScoreboards.remove(player.getName()));
		}
	}

	public boolean hasLobbyScoreboard(Player player) {
		return lobbyScoreboards.contains(player.getName());
	}

	public void addLobbyScoreboard(String playerName) {
		lobbyScoreboards.add(playerName);
	}

	public void removeLobbyScoreboard(String playerName) {
		lobbyScoreboards.remove(playerName);
	}
}
