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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.RegionSelector;

import tntrun.messages.Messages;


public class WEIntegration {

	private WorldEditPlugin weplugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	private WorldEdit we = WorldEdit.getInstance();

	protected Location[] getLocations(Player player) {
		try {
			return getPlayerSelection(player);
		} catch (Exception e) {
		}
		return null;
	}

	private Location[] getPlayerSelection(Player player) {
		Location[] locs = new Location[2];
		
		BukkitPlayer bplayer = new BukkitPlayer(weplugin, player);
		
		RegionSelector selector = we.getSessionManager().get(bplayer).getRegionSelector(bplayer.getWorld());
		try {
			BlockVector3 v1 = selector.getRegion().getMinimumPoint();
			BlockVector3 v2 = selector.getRegion().getMaximumPoint();
			
			locs[0] = new Location(player.getWorld(), v1.getX(), v1.getY(), v1.getZ());
			locs[1] = new Location(player.getWorld(), v2.getX(), v2.getY(), v2.getZ());	
			
		} catch (IncompleteRegionException e1) {
			Messages.sendMessage(player, "&c Invalid WorldEdit selection");
			return null;
		}

		return locs;
	}

}
