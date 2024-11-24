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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iridium.iridiumcolorapi.IridiumColorAPI;

import net.md_5.bungee.api.ChatColor;

public class FormattingCodesParser {

	private final static Pattern HEXCOLOUR = Pattern.compile("<#([A-Fa-f0-9]){6}>");

	public static String parseFormattingCodes(String message) {

		if (message.contains("<GRADIENT") || message.contains("<RAINBOW")) {
			message = IridiumColorAPI.process(message);
		}

		Matcher matcher = HEXCOLOUR.matcher(message);
		while (matcher.find()) {
			StringBuilder sb = new StringBuilder();
			final ChatColor hexColour = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
			sb.append(message.substring(0, matcher.start())).append(hexColour).append(message.substring(matcher.end()));
			message = sb.toString();
			matcher = HEXCOLOUR.matcher(message);
		}

		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
