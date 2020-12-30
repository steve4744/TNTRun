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

package tntrun.signs.editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;

public class SignEditor {

	private TNTRun plugin;
	private HashMap<String, HashSet<SignInfo>> signs = new HashMap<>();
	private List<SignInfo> lbsigns = new ArrayList<>();
	private File configfile;
	private int position;
	private String lbentry;

	public SignEditor(TNTRun plugin) {
		this.plugin = plugin;
		configfile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "signs.yml");
	}

	private void addArena(String arena) {
		if (!signs.containsKey(arena)) {
			signs.put(arena, new HashSet<SignInfo>());
		}
	}

	public void removeArena(String arena) {
		for (Block block : getSignsBlocks(arena)) {
			removeSign(block, arena);
		}
		signs.remove(arena);
	}

	public void addSign(Block block, String arena) {
		SignInfo signinfo = new SignInfo(block);
		addArena(arena);
		getSigns(arena).add(signinfo);
	}

	public void addLeaderboardSign(Block block) {
		SignInfo signinfo = new SignInfo(block);
		getLBSigns().add(signinfo);
	}
	
	private List<SignInfo> getLBSigns() {
		return lbsigns;
	}

	private SignInfo getLBSignInfo(Block block) {
		for (SignInfo si : getLBSigns()) {
			if (si.getBlock().equals(block)) {
				return si;
			}
		}
		return new SignInfo(block);
	}

	private void addLBSignInfo(SignInfo si) {
		if (!getLBSigns().contains(si)) {
			getLBSigns().add(si);
		}
	}

	/**
	 * Sets the first 3 leaderboard positions. getOfflinePlayer#getName() will return null if
	 * the player has not played on the server, so need to validate player#hasPlayedBefore().
	 * @param block
	 */
	public void modifyLeaderboardSign(Block block) {
		if (!plugin.useStats()) {
			return;
		}
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			position = 0;
			sign.setLine(position, FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.prefix")));
			plugin.stats.getWinMap().entrySet().stream()
				.sorted(Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(3)
				.forEach(e -> {
					if (Bukkit.getOnlineMode()) {
						OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(e.getKey()));
						if (!p.hasPlayedBefore()) {
							plugin.getLogger().info("Invalid player data found for " + e.getKey());
							// continue to next entry
							return;
						}
						lbentry = p.getName();
					} else {
						lbentry = e.getKey();
					}
					position++;
					String line = FormattingCodesParser.parseFormattingCodes(Messages.leadersign).replace("{PLAYER}", lbentry.substring(0, Math.min(lbentry.length(), 11))).replace("{WINS}", String.valueOf(e.getValue()));
	      			sign.setLine(position, line);	
				});
			sign.update();
		}
	}

	public void refreshLeaderBoards() {
		for (SignInfo signinfo : getLBSigns()) {
			Block block = signinfo.getBlock();
			if (block != null) {
				modifyLeaderboardSign(block);
			}
		}
	}
	
	public void removeLeaderboardSign(Block block) {
		if (block.getState() instanceof Sign) {
			getLBSigns().remove(getLBSignInfo(block));
		}
	}

	public void removeSign(Block block, String arena) {
		if (block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			sign.setLine(0, "");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			sign.update();
		}
		addArena(arena);
		getSigns(arena).remove(getSignInfo(block, arena));
	}

	private HashSet<Block> getSignsBlocks(String arena) {
		HashSet<Block> signs = new HashSet<>();
		for (SignInfo signinfo : getSigns(arena)) {
			Block block = signinfo.getBlock();
			if (block != null) {
				signs.add(block);
			}
		}
		return signs;
	}

	private SignInfo getSignInfo(Block block, String arena) {
		for (SignInfo si : getSigns(arena)) {
			if (si.getBlock().equals(block)) {
				return si;
			}
		}
		return new SignInfo(block);
	}

	private void addSignInfo(SignInfo si, String arena) {
		addArena(arena);
		getSigns(arena).add(si);
	}

	private HashSet<SignInfo> getSigns(String arena) {
		addArena(arena);
		return signs.get(arena);
	}

	public void modifySigns(String arenaname) {
		try {
			Arena arena = plugin.amanager.getArenaByName(arenaname);
			if (arena == null) {
				return;
			}

			String text = null;
			String colour = null;
			int players = arena.getPlayersManager().getPlayersCount();
			int maxPlayers = arena.getStructureManager().getMaxPlayers();
			String status = (players == maxPlayers) ? "Running" : arena.getStatusManager().getArenaStatus();

			switch (status) {
				case "Disabled":
					text = FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.status.disabled"));
					colour = plugin.getConfig().getString("signs.blockcolour.disabled");
					break;
				case "Running":
					text = FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.status.ingame")).replace("{MPS}", maxPlayers + "").replace("{PS}", players + "");
					colour = plugin.getConfig().getString("signs.blockcolour.ingame");
					break;
				case "Regenerating":
					text = FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.status.regenerating"));
					colour = plugin.getConfig().getString("signs.blockcolour.ingame");
					break;
				case "Starting":
					text = FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.status.waiting")).replace("{MPS}", maxPlayers + "").replace("{PS}", players + "");
					colour = plugin.getConfig().getString("signs.blockcolour.starting");
					break;
				default:
					text = FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.status.waiting")).replace("{MPS}", maxPlayers + "").replace("{PS}", players + "");
					colour = plugin.getConfig().getString("signs.blockcolour.waiting");
			}

			for (Block block : getSignsBlocks(arenaname)) {
				if (block.getState() instanceof Sign) {
					Sign sign = (Sign) block.getState();
					sign.setLine(0, FormattingCodesParser.parseFormattingCodes(plugin.getConfig().getString("signs.prefix")));
					sign.setLine(3, text);
					sign.update();
					setBlockColour(block, colour);
				} else {
					removeSign(block, arenaname);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setBlockColour(Block block, String colour) {
		if (colour == null || colour.isEmpty()) {
			return;
		}
		BlockData data = block.getBlockData();
		if (!(data instanceof WallSign) && !(data instanceof Directional)) {
			return;
		}
		Directional directional = (Directional) data;
		Block blockBehind = block.getRelative(directional.getFacing().getOppositeFace());
		if (Tag.IMPERMEABLE.isTagged(blockBehind.getType()) && blockBehind.getType() != Material.GLASS) {
			Material material = Material.getMaterial(colour.toUpperCase() + "_STAINED_GLASS");
			if (material != null) {
				blockBehind.setType(material);
			}
		}
	}

	public void loadConfiguration() {
		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		if (!file.isConfigurationSection("arenas")) {
			for (String arena : file.getKeys(false)) {
				if (arena.equalsIgnoreCase("leaderboards")) continue;
				ConfigurationSection section = file.getConfigurationSection(arena);
				readSignInfo(section, arena);
			}
		} else {
			ConfigurationSection arenaSection = file.getConfigurationSection("arenas");
			for (String arena : arenaSection.getKeys(false)) {
				ConfigurationSection section = arenaSection.getConfigurationSection(arena);
				readSignInfo(section, arena);
			}
		}
		if (file.isConfigurationSection("leaderboards")) {
			ConfigurationSection section = file.getConfigurationSection("leaderboards");
			for (String block : section.getKeys(false)) {
				ConfigurationSection blockSection = section.getConfigurationSection(block);
				SignInfo si = new SignInfo(blockSection.getString("world"), blockSection.getInt("x"), blockSection.getInt("y"), blockSection.getInt("z"));
				addLBSignInfo(si);
			}
			refreshLeaderBoards();
		}
	}

	private void readSignInfo(ConfigurationSection section, String arena) {
		for (String block : section.getKeys(false)) {
			ConfigurationSection blockSection = section.getConfigurationSection(block);
			SignInfo si = new SignInfo(blockSection.getString("world"), blockSection.getInt("x"), blockSection.getInt("y"), blockSection.getInt("z"));
			addSignInfo(si, arena);
		}
		modifySigns(arena);
	}

	public void saveConfiguration() {
		FileConfiguration file = new YamlConfiguration();

		for (String arena : signs.keySet()) {
			ConfigurationSection section = file.createSection("arenas." + arena);
			int i = 0;
			for (SignInfo si : getSigns(arena)) {
				ConfigurationSection blockSection = section.createSection(Integer.toString(i++));
				writeSignInfo(blockSection, si);
			}
		}
		ConfigurationSection section = file.createSection("leaderboards");
		int i = 0;
		for (SignInfo si : lbsigns) {
			ConfigurationSection blockSection = section.createSection(Integer.toString(i++));
			writeSignInfo(blockSection, si);
		}

		try {
			file.save(configfile);
		} catch (IOException e) {
			plugin.getLogger().info("Error saving file " + configfile);
			e.printStackTrace();
		}
	}

	private void writeSignInfo(ConfigurationSection blockSection, SignInfo si) {
		blockSection.set("x", si.getX());
		blockSection.set("y", si.getY());
		blockSection.set("z", si.getZ());
		blockSection.set("world", si.getWorldName());
	}
}
