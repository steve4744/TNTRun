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
package tntrun.parties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class Parties {

	private final TNTRun plugin;
	private Map<String, List<String>> partyMap = new HashMap<>();
	private Map<String, List<String>> kickedMap = new HashMap<>();
	private String partyLeader;

	public Parties(TNTRun plugin) {
		this.plugin = plugin;
	}

	public void handleCommand(Player player, String[] args) {
		switch(args[1]) {
			case "create" -> createParty(player);
			case "invite" -> inviteToParty(player, args[2]);
			case "leave"  -> leaveParty(player);
			case "kick"   -> kickFromParty(player, args[2]);
			case "unkick" -> unkickFromParty(player, args[2]);
			case "info"   -> displayPartyInfo(player);
			default       -> Messages.sendMessage(player, "&c Invalid argument supplied");
		}
	}

	private void createParty(Player player) {
		if (alreadyInParty(player)) {
			Messages.sendMessage(player, Messages.partyinparty);
			return;
		}
		partyMap.put(player.getName(), new ArrayList<>());
		Messages.sendMessage(player, Messages.partycreate);
		if (Utils.debug()) {
			plugin.getLogger().info("Party created by " + player.getName());
		}
	}

	private void leaveParty(Player player) {
		if (isPartyLeader(player)) {
			for (String member : partyMap.get(player.getName())) {
				Messages.sendMessage(Bukkit.getPlayer(member), Messages.partyleaderleave.replace("{PLAYER}", player.getName()));
			}
			removeParty(player);
			return;
		}
		if (!isPartyMember(player)) {
			Messages.sendMessage(player, Messages.partynotmember);
			return;
		}

		partyMap.entrySet().forEach(e -> {
			if (e.getValue().contains(player.getName())) {
				e.getValue().remove(player.getName());
				String msg = Messages.partyleave.replace("{PLAYER}", player.getName());
				Messages.sendMessage(player, msg);
				Messages.sendMessage(Bukkit.getPlayer(e.getKey()), msg);
				if (Utils.debug()) {
					plugin.getLogger().info(player.getName() + " has left party created by " + e.getKey());
				}
			}
		});
	}

	private void kickFromParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, Messages.partynotleader);
			return;
		}
		if (partyMap.get(player.getName()).removeIf(list -> list.contains(targetName))) {
			kickedMap.computeIfAbsent(player.getName(), k -> new ArrayList<>()).add(targetName);
			Messages.sendMessage(player, Messages.partykick.replace("{PLAYER}", targetName));
		}
	}

	private void unkickFromParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, Messages.partynotleader);
			return;
		}
		if (kickedMap.containsKey(player.getName())) {
			kickedMap.get(player.getName()).removeIf(list -> list.contains(targetName));
			Messages.sendMessage(player, Messages.partyunkick.replace("{PLAYER}", targetName));
		}
	}

	private void inviteToParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, Messages.partynotleader);
			return;
		}
		if (targetName.equalsIgnoreCase(player.getName())) {
			Messages.sendMessage(player, Messages.partyinviteself);
			return;
		}
		if (Bukkit.getPlayer(targetName) == null) {
			Messages.sendMessage(player, Messages.playernotonline.replace("{PLAYER}", targetName));
			return;
		}
		Messages.sendMessage(Bukkit.getPlayer(targetName), Messages.partyinvite.replace("{PLAYER}", player.getName()));
		Utils.displayPartyInvite(player, targetName, "");
	}

	public void joinParty(String playerName, String targetName) {
		Player targetPlayer = Bukkit.getPlayer(targetName);
		if (alreadyInParty(targetPlayer)) {
			Messages.sendMessage(targetPlayer, Messages.partyinparty);
			return;
		}
		if (!partyExists(playerName)) {
			Messages.sendMessage(targetPlayer, Messages.partynotexist);
			return;
		}
		if (isKicked(playerName, targetName)) {
			Messages.sendMessage(targetPlayer, Messages.partyban);
			return;
		}
		partyMap.computeIfAbsent(playerName, k -> new ArrayList<>()).add(targetName);
		String msg = Messages.partyjoin.replace("{PLAYER}", targetName);
		Messages.sendMessage(targetPlayer, msg);
		Messages.sendMessage(Bukkit.getPlayer(playerName), msg);
		if (Utils.debug()) {
			plugin.getLogger().info(targetName + " has joined party created by " + playerName);
		}
	}

	public boolean isPartyLeader(Player player) {
		return partyMap.containsKey(player.getName());
	}

	private boolean isPartyMember(Player player) {
		return partyMap.values().stream().anyMatch(list -> list.contains(player.getName()));
	}

	private boolean alreadyInParty(Player player) {
		return isPartyLeader(player) || isPartyMember(player);
	}

	private boolean isKicked(String playerName, String targetName) {
		if (kickedMap.containsKey(playerName)) {
			return kickedMap.get(playerName).contains(targetName);
		}
		return false;
	}

	private void removeParty(Player player) {
		partyMap.remove(player.getName());
		Messages.sendMessage(player, Messages.partyleaderleave.replace("{PLAYER}", player.getName()));
		if (Utils.debug()) {
			plugin.getLogger().info("Party leader " + player.getName() + " has left party");
		}
	}

	public List<String> getPartyMembers(String playerName) {
		return partyMap.get(playerName);
	}

	private boolean partyExists(String playerName) {
		return partyMap.containsKey(playerName);
	}

	private String getPartyLeader(Player player) {
		if (isPartyLeader(player)) {
			return player.getName();
		}
		partyMap.entrySet().forEach(e -> {
			if (e.getValue().contains(player.getName())) {
				partyLeader = e.getKey();
				return;
			}
		});
		return partyLeader != null ? partyLeader : "unknown";
	}

	private void displayPartyInfo(Player player) {
		if (!alreadyInParty(player)) {
			Messages.sendMessage(player, Messages.partynotmember);
			return;
		}
		String leader = getPartyLeader(player);
		Messages.sendMessage(player, " Party leader: " + leader);
		Messages.sendMessage(player, " Party size: " + (getPartyMembers(leader).size() + 1));
		Messages.sendMessage(player, " Party members: " + getPartyMembers(leader).toString());
	}
}
