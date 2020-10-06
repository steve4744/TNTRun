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

package tntrun.selectionget;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class OwnLocations {

	private HashMap<String, Location> loc1 = new HashMap<>();
	private HashMap<String, Location> loc2 = new HashMap<>();

	protected void putPlayerLoc1(String playername, Location loc) {
		Bukkit.getLogger().info("[TNTRun] Location P1 set to " + loc.toString());
		loc1.put(playername, loc);
	}

	protected void putPlayerLoc2(String playername, Location loc) {
		Bukkit.getLogger().info("[TNTRun] Location P2 set to " + loc.toString());
		loc2.put(playername, loc);
	}

	protected Location getPlayerLoc1(String playername) {
		return loc1.get(playername);
	}

	protected Location getPlayerLoc2(String playername) {
		return loc2.get(playername);
	}

	protected void clearPoints(String playername) {
		loc1.remove(playername);
		loc2.remove(playername);
	}

	protected Location[] getLocations(Player player) {
		try {
			return sortLoc(player);
		} catch (Exception e) {
		}
		return null;
	}

	// 0 is min, 1 is max
	private Location[] sortLoc(Player player) {
		double x1 = loc1.get(player.getName()).getX();
		double x2 = loc2.get(player.getName()).getX();
		double y1 = loc1.get(player.getName()).getY();
		double y2 = loc2.get(player.getName()).getY();
		double z1 = loc1.get(player.getName()).getZ();
		double z2 = loc2.get(player.getName()).getZ();

		Location[] locs = new Location[2];
		locs[0] = new Location(loc1.get(player.getName()).getWorld(), Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
		locs[1] = new Location(loc1.get(player.getName()).getWorld(), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
		locs[0].distanceSquared(locs[1]);
		return locs;
	}

}
