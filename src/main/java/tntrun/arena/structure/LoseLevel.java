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

package tntrun.arena.structure;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

public class LoseLevel {

	private Vector p1 = null;

	public Vector getP1() {
		return p1;
	}

	private Vector p2 = null;

	public Vector getP2() {
		return p2;
	}

	public boolean isConfigured() {
		return (p1 != null && p2 != null);
	}

	public boolean isLooseLocation(Location loc) {
		return loc.getY() < Math.max(p2.getBlockY(), p1.getBlockY()) + 1;
	}

	public void setLooseLocation(Location p1, Location p2) {
		this.p1 = p1.toVector();
		this.p2 = p2.toVector();
	}

	public void saveToConfig(FileConfiguration config) {
		config.set("loselevel.p1", p1);
		config.set("loselevel.p2", p2);
	}

	public void loadFromConfig(FileConfiguration config) {
		p1 = config.getVector("loselevel.p1", null);
		p2 = config.getVector("loselevel.p2", null);
	}

}
