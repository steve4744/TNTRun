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

package tntrun.arena.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.entity.Player;

public class PlayersManager {

	private HashMap<String, Player> players = new HashMap<>();
	private HashMap<String, Player> spectators = new HashMap<>();
	private List<String> spectatoronly = new ArrayList<>();
	private String winner;

	public boolean isInArena(String name) {
		return players.containsKey(name) || spectators.containsKey(name);
	}

	public int getPlayersCount() {
		return players.size();
	}

	public HashSet<Player> getAllParticipantsCopy() {
		HashSet<Player> p = new HashSet<>();
		p.addAll(players.values());
		p.addAll(spectators.values());
		return p;
	}

	public Collection<Player> getPlayers() {
		return Collections.unmodifiableCollection(players.values());
	}

	public HashSet<Player> getPlayersCopy() {
		return new HashSet<Player>(players.values());
	}

	public void add(Player player) {
		players.put(player.getName(), player);
	}

	public void remove(Player player) {
		players.remove(player.getName());
	}

	public boolean isSpectator(String name) {
		return spectators.containsKey(name);
	}

	public void addSpectator(Player player) {
		spectators.put(player.getName(), player);
	}

	public void removeSpecator(String name) {
		spectators.remove(name);
	}

	public Collection<Player> getSpectators() {
		return Collections.unmodifiableCollection(spectators.values());
	}

	public HashSet<Player> getSpectatorsCopy() {
		return new HashSet<Player>(spectators.values());
	}

	public void setWinner(String playername) {
		winner = playername;
	}

	public boolean isWinner(String playername) {
		return playername.equals(winner);
	}

	public void addSpectatorOnly(String name) {
		if (!isSpectatorOnly(name)) {
			spectatoronly.add(name);
		}
	}

	public void removeSpectatorOnly(String name) {
		spectatoronly.removeIf(name::equals);
	}

	public boolean isSpectatorOnly(String name) {
		return spectatoronly.contains(name);
	}
}
