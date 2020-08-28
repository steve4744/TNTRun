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
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import tntrun.TNTRun;
import tntrun.arena.Arena;

public class SignEditor {

	private TNTRun plugin;
	private HashMap<String, HashSet<SignInfo>> signs = new HashMap<String, HashSet<SignInfo>>();

	private File configfile;

	public SignEditor(TNTRun plugin) {
		this.plugin = plugin;
		configfile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "signs.yml");
	}

	public void addArena(String arena) {
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

	public HashSet<Block> getSignsBlocks(String arena) {
		HashSet<Block> signs = new HashSet<Block>();
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
			int players = arena.getPlayersManager().getPlayersCount();
			int maxPlayers = arena.getStructureManager().getMaxPlayers();
			
			if (!arena.getStatusManager().isArenaEnabled()) {
				text = plugin.getConfig().getString("signs.status.disabled").replace("&", "§");
			} else
				if (arena.getStatusManager().isArenaRunning()) {
					text = plugin.getConfig().getString("signs.status.ingame").replace("&", "§").replace("{MPS}", maxPlayers + "").replace("{PS}", players + "");
			} else
				if (arena.getStatusManager().isArenaRegenerating()) {
					text = plugin.getConfig().getString("signs.status.regenerating").replace("&", "§");
			} else 
				if (players == maxPlayers) {
					text = plugin.getConfig().getString("signs.status.ingame").replace("&", "§").replace("{MPS}", maxPlayers + "").replace("{PS}", players + "");
			} else {
				text = plugin.getConfig().getString("signs.status.waiting").replace("&", "§").replace("{MPS}", maxPlayers + "").replace("{PS}", players + "");					
			}

			for (Block block : getSignsBlocks(arenaname)) {
				if (block.getState() instanceof Sign) {
					Sign sign = (Sign) block.getState();
					sign.setLine(0, plugin.getConfig().getString("signs.prefix").replace("&", "§"));
					sign.setLine(3, text);
					sign.update();
				} else {
					removeSign(block, arenaname);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfiguration() {
		FileConfiguration file = YamlConfiguration.loadConfiguration(configfile);

		for (String arena : file.getKeys(false)) {
			ConfigurationSection section = file.getConfigurationSection(arena);
			for (String block : section.getKeys(false)) {
				ConfigurationSection blockSection = section.getConfigurationSection(block);
				SignInfo si = new SignInfo(blockSection.getString("world"), blockSection.getInt("x"), blockSection.getInt("y"), blockSection.getInt("z"));
				addSignInfo(si, arena);
			}
			modifySigns(arena);
		}
	}

	public void saveConfiguration() {
		FileConfiguration file = new YamlConfiguration();

		for (String arena : signs.keySet()) {
			ConfigurationSection section = file.createSection(arena);
			int i = 0;
			for (SignInfo si : getSigns(arena)) {
				ConfigurationSection blockSection = section.createSection(Integer.toString(i++));
				blockSection.set("x", si.getX());
				blockSection.set("y", si.getY());
				blockSection.set("z", si.getZ());
				blockSection.set("world", si.getWorldName());
			}
		}

		try {
			file.save(configfile);
		} catch (IOException e) {
		}
	}

}
