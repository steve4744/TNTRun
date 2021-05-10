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

package tntrun.menu;

import java.util.Arrays;

public enum ConfigMenu {

	RED_WOOL(4),
	BLAZE_ROD(10),
	WOODEN_AXE(11),
	BONE(12),
	MAGMA_CREAM(14),
	NETHER_STAR(15),
	ENDER_PEARL(16),
	GUNPOWDER(19),
	REDSTONE(20),
	GLOWSTONE_DUST(21),
	OAK_SIGN(23),
	DIAMOND(25);

	private int slot;

	ConfigMenu(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	public static ConfigMenu getName(int slot){
	    return Arrays.stream(values()).filter(value -> value.getSlot() == slot).findFirst().orElse(null);
	}
}
