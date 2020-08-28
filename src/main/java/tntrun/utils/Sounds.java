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

package tntrun.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class Sounds {

	public abstract void NOTE_PLING(Player p, float volume, float pitch);
	public abstract void ARENA_START(Player p);
	public abstract void ITEM_SELECT(Player p);
	public abstract void BLOCK_BREAK(Block block);

}
