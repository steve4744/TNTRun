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

import org.bukkit.ChatColor;

public class FormattingCodesParser {

	public static String parseFormattingCodes(String message) {
		message = message.replaceAll("&0", ChatColor.BLACK.toString());
		message = message.replaceAll("&1", ChatColor.DARK_BLUE.toString());
		message = message.replaceAll("&2", ChatColor.DARK_GREEN.toString());
		message = message.replaceAll("&3", ChatColor.DARK_AQUA.toString());
		message = message.replaceAll("&4", ChatColor.DARK_RED.toString());
		message = message.replaceAll("&5", ChatColor.DARK_PURPLE.toString());
		message = message.replaceAll("&6", ChatColor.GOLD.toString());
		message = message.replaceAll("&7", ChatColor.GRAY.toString());
		message = message.replaceAll("&8", ChatColor.DARK_GRAY.toString());
		message = message.replaceAll("&9", ChatColor.BLUE.toString());
		message = message.replaceAll("(?i)&a", ChatColor.GREEN.toString());
		message = message.replaceAll("(?i)&b", ChatColor.AQUA.toString());
		message = message.replaceAll("(?i)&c", ChatColor.RED.toString());
		message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE.toString());
		message = message.replaceAll("(?i)&e", ChatColor.YELLOW.toString());
		message = message.replaceAll("(?i)&f", ChatColor.WHITE.toString());
		message = message.replaceAll("(?i)&l", ChatColor.BOLD.toString());
		message = message.replaceAll("(?i)&o", ChatColor.ITALIC.toString());
		message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH.toString());
		message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE.toString());
		message = message.replaceAll("(?i)&k", ChatColor.MAGIC.toString());
		message = message.replaceAll("(?i)&r", ChatColor.RESET.toString());
		return message;
	}

}
