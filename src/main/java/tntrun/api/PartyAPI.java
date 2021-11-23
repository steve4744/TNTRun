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
package tntrun.api;

import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class PartyAPI {

	private static final String[] CREATE = {"party", "create"};
	private static final String[] JOIN = {"party", "accept"};
	private static final String[] LEAVE = {"party", "leave"};

	public static void createParty(Player player) {
		TNTRun.getInstance().getParties().handleCommand(player, CREATE);
	}

	public static void joinParty(Player player) {
		TNTRun.getInstance().getParties().handleCommand(player, JOIN);
	}

	public static void leaveParty(Player player) {
		TNTRun.getInstance().getParties().handleCommand(player, LEAVE);
	}
}
