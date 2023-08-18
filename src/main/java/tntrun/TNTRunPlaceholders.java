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

package tntrun;

import java.util.HashSet;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import tntrun.arena.Arena;
import tntrun.messages.Messages;
import tntrun.utils.FormattingCodesParser;
import tntrun.utils.Utils;

public class TNTRunPlaceholders extends PlaceholderExpansion {
	private final TNTRun plugin;

	public TNTRunPlaceholders(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist(){
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String getIdentifier() {
		return "tntrun";
	}

	@Override
	public String onRequest(OfflinePlayer p, String identifier) {

		if (p == null) {
			return "";
		}
		String uuid = plugin.getStats().getPlayerUUID(p);
		if (identifier.equals("version")) {
			return String.valueOf(plugin.getDescription().getVersion());

		} else if (identifier.equals("arena_count")) {
			return String.valueOf(plugin.amanager.getArenas().size());

		} else if (identifier.equals("pvp_arena_count")) {
			return String.valueOf(plugin.amanager.getPvpArenas().size());

		} else if (identifier.equals("nopvp_arena_count")) {
			return String.valueOf(plugin.amanager.getNonPvpArenas().size());

		} else if (identifier.equals("played")) {
			return String.valueOf(plugin.getStats().getPlayedGames(uuid));

		} else if (identifier.equals("wins")) {
			return String.valueOf(plugin.getStats().getWins(uuid));

		} else if (identifier.equals("losses")) {
			return String.valueOf(plugin.getStats().getLosses(uuid));

		} else if (identifier.equals("winstreak")) {
			return String.valueOf(plugin.getPData().getWinStreak(p));

		} else if (identifier.equals("current_arena")) {
			Arena arena = plugin.amanager.getPlayerArena(p.getName());
			return arena != null ? arena.getArenaName() : FormattingCodesParser.parseFormattingCodes(Messages.playernotinarena);

		} else if (identifier.equals("player_count")) {
			return String.valueOf(Utils.playerCount());

		} else if (identifier.equals("spectator_count")) {
			return String.valueOf(Utils.spectatorCount());

		} else if (identifier.equals("pvp_player_count")) {
			return String.valueOf(Utils.pvpPlayerCount());

		} else if (identifier.equals("nopvp_player_count")) {
			return String.valueOf(Utils.nonPvpPlayerCount());

		} else if (identifier.startsWith("allplayers")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? getNames(arena.getPlayersManager().getAllParticipantsCopy()) : null;

		} else if (identifier.startsWith("players")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? getNames(arena.getPlayersManager().getPlayersCopy()) : null;

		} else if (identifier.startsWith("spectators")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? getNames(arena.getPlayersManager().getSpectatorsCopy()) : null;

		} else if (identifier.startsWith("maxplayers")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? String.valueOf(arena.getStructureManager().getMaxPlayers()) : null;

		} else if (identifier.startsWith("minplayers")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? String.valueOf(arena.getStructureManager().getMinPlayers()) : null;

		} else if (identifier.startsWith("player_count")) {
			Arena arena = getArenaFromPlaceholder(identifier, 3);
			return arena != null ? String.valueOf(arena.getPlayersManager().getPlayersCount()) : null;

		} else if (identifier.startsWith("spectator_count")) {
			Arena arena = getArenaFromPlaceholder(identifier, 3);
			return arena != null ? String.valueOf(arena.getPlayersManager().getSpectatorsCount()) : null;

		} else if (identifier.startsWith("status")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? arena.getStatusManager().getArenaStatusMesssage() : null;

		} else if (identifier.startsWith("seconds_remaining")) {
			Arena arena = getArenaFromPlaceholder(identifier, 3);
			return arena != null ? String.valueOf(arena.getGameHandler().getTimeRemaining() / 20) : null;

		} else if (identifier.startsWith("time_remaining")) {
			Arena arena = getArenaFromPlaceholder(identifier, 3);
			return arena != null ? Utils.getFormattedTime(arena.getGameHandler().getTimeRemaining() / 20) : null;

		} else if (identifier.startsWith("pvp_status")) {
			Arena arena = getArenaFromPlaceholder(identifier, 3);
			if (arena == null) {
				return null;
			}
			return arena.getStructureManager().isPvpEnabled() ? "Enabled" : "Disabled";

		} else if (identifier.startsWith("damage_status")) {
			Arena arena = getArenaFromPlaceholder(identifier, 3);
			return arena != null ? Utils.getTitleCase(arena.getStructureManager().getDamageEnabled().toString()) : null;

		} else if (identifier.equals("doublejumps")) {
			Arena arena = plugin.amanager.getPlayerArena(p.getName());
			return arena != null ? String.valueOf(arena.getPlayerHandler().getDoubleJumps(p.getName())) : String.valueOf(getUncachedDoubleJumps(p));

		} else if (identifier.startsWith("joinfee")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null ? String.valueOf(arena.getStructureManager().getFee()) : null;

		} else if (identifier.startsWith("currency")) {
			Arena arena = getArenaFromPlaceholder(identifier, 2);
			return arena != null && arena.getStructureManager().isCurrencyEnabled() ? arena.getStructureManager().getCurrency().toString() : null;

		} else if (identifier.startsWith("position")) {
			String[] temp = identifier.split("_");
			if (!isValidType(temp[1])) {
				return null;
			}
			int pos = plugin.getStats().getPosition(uuid, temp[1]);
			return pos > 0 ? String.valueOf(pos) : "";

		} else if (identifier.startsWith("leaderboard") || identifier.startsWith("lb")) {
			if (!isValidLeaderboardIdentifier(identifier)) {
				return null;
			}
			String[] temp = identifier.split("_");
			String type = temp[1];
			String entry = temp[2];
			int pos = Integer.parseInt(temp[3]);

			return plugin.getStats().getLeaderboardPosition(pos, type, entry);
		}
		return null;
	}

	/**
	 * Returns the arena from the placeholder. The arena name should be
	 * at the end of each placeholder. If the placeholder is invalid or
	 * the arena does not exist then null is returned.
	 *
	 * @param identifier
	 * @param length
	 * @return arena
	 */
	private Arena getArenaFromPlaceholder(String identifier, int length) {
		String[] temp = identifier.split("_");
		if (temp.length != length) {
			return null;
		}
		return plugin.amanager.getArenaByName(temp[length - 1]);
	}

	private boolean isValidLeaderboardIdentifier(String identifier) {
		String[] temp = identifier.split("_");
		if (temp.length != 4) {
			return false;
		}
		if (!Utils.isNumber(temp[3]) || Integer.parseInt(temp[3]) < 1) {
			return false;
		}
		if (!Stream.of("player", "score", "rank").anyMatch(temp[2]::equalsIgnoreCase)) {
			return false;
		}
		if (!isValidType(temp[1])) {
			return false;
		}
		return true;
	}

	private boolean isValidType(String type) {
		return Stream.of("wins", "played", "losses").anyMatch(type::equalsIgnoreCase);
	}

	private String getNames(HashSet<Player> playerSet) {
		StringJoiner names = new StringJoiner(", ");
		playerSet.stream().forEach(player -> {
			names.add(player.getName());
		});
		return names.toString();
	}

	/**
	 * Returns the number of doublejumps for a player that is not in an arena.
	 *
	 * @param player
	 * @return number of doublejumps
	 */
	private int getUncachedDoubleJumps(OfflinePlayer p) {
		boolean free = plugin.getConfig().getBoolean("freedoublejumps.enabled");
		return free ? Utils.getAllowedDoubleJumps((Player) p, plugin.getConfig().getInt("freedoublejumps.amount", 0)) : plugin.getPData().getDoubleJumpsFromFile(p);
	}
}
