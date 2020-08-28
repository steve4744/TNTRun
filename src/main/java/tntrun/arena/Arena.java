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

package tntrun.arena;

import java.io.File;

import tntrun.TNTRun;
import tntrun.arena.handlers.ArenaEconomy;
import tntrun.arena.handlers.GameHandler;
import tntrun.arena.handlers.PlayerHandler;
import tntrun.arena.handlers.ScoreboardHandler;
import tntrun.arena.status.PlayersManager;
import tntrun.arena.status.StatusManager;
import tntrun.arena.structure.StructureManager;

public class Arena {

	public TNTRun plugin;
	private String arenaname;
	private ArenaEconomy arenaeco;

	public Arena(String name, TNTRun plugin) {
		arenaname = name;
		this.plugin = plugin;
		arenagh = new GameHandler(plugin, this);
		arenaph = new PlayerHandler(plugin, this);
		arenafile = new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + arenaname + ".yml");
		arenasb = new ScoreboardHandler(plugin, this);
		arenaeco = new ArenaEconomy(plugin, this);
	}

	public String getArenaName() {
		return arenaname;
	}

	private File arenafile;
	public File getArenaFile() {
		return arenafile;
	}

	private GameHandler arenagh;
	public GameHandler getGameHandler() {
		return arenagh;
	}

	private PlayerHandler arenaph;
	public PlayerHandler getPlayerHandler() {
		return arenaph;
	}

	private ScoreboardHandler arenasb;
	public ScoreboardHandler getScoreboardHandler() {
		return arenasb;
	}

	private StatusManager statusManager = new StatusManager(this);
	public StatusManager getStatusManager() {
		return statusManager;
	}

	private StructureManager structureManager = new StructureManager(this);
	public StructureManager getStructureManager() {
		return structureManager;
	}

	private PlayersManager playersManager = new PlayersManager();
	public PlayersManager getPlayersManager() {
		return playersManager;
	}

	public ArenaEconomy getArenaEconomy() {
		return arenaeco;
	}
}
