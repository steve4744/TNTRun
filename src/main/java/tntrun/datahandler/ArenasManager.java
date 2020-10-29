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

package tntrun.datahandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import tntrun.arena.Arena;

public class ArenasManager {

	private HashMap<String, Arena> arenanames = new HashMap<>();

	public void registerArena(Arena arena) {
		arenanames.put(arena.getArenaName(), arena);
	}

	public void unregisterArena(Arena arena) {
		arenanames.remove(arena.getArenaName());
	}

	public Collection<Arena> getArenas() {
		return arenanames.values();
	}

	public Set<String> getArenasNames() {
		return arenanames.keySet();
	}

	public Arena getArenaByName(String name) {
		return arenanames.get(name);
	}

	public Arena getPlayerArena(String name) {
		for (Arena arena : arenanames.values()) {
			if (arena.getPlayersManager().isInArena(name)) {
				return arena;
			}
		}
		return null;
	}

	public Set<Arena> getPvpArenas() {
		return arenanames.entrySet().stream()
				.filter(e -> (!"no".equalsIgnoreCase(e.getValue().getStructureManager().getDamageEnabled().toString())))
				.map(e -> e.getValue())
				.collect(Collectors.toSet());
	}

	public Set<Arena> getNonPvpArenas() {
		return arenanames.entrySet().stream()
				.filter(e -> ("no".equalsIgnoreCase(e.getValue().getStructureManager().getDamageEnabled().toString())))
				.map(e -> e.getValue())
				.collect(Collectors.toSet());
	}
}
