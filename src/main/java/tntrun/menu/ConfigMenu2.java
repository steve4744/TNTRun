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

public enum ConfigMenu2 {

	RED_WOOL(4),
	CLOCK(10),
	WHITE_CARPET(11),
	ORANGE_CARPET(12),
	GLOW_INK_SAC(14),
	LIGHT_BLUE_CARPET(15),
	YELLOW_CARPET(16),
	DIAMOND_SWORD(19),
	PINK_CARPET(20),
	CYAN_CARPET(21),
	SPYGLASS(23),
	PURPLE_CARPET(24),
	BLUE_CARPET(25),
	ARROW(27),
	BARRIER(31);

	private int slot;

	ConfigMenu2(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	public static ConfigMenu2 getName(int slot){
	    return Arrays.stream(values()).filter(value -> value.getSlot() == slot).findFirst().orElse(null);
	}
}
