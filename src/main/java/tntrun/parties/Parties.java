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
	private Map<String, List<String>> invitedMap = new HashMap<>();
	private String partyLeader;

	public Parties(TNTRun plugin) {
		this.plugin = plugin;
	}

	public void handleCommand(Player player, String[] args) {
		switch(args[1].toLowerCase()) {
			case "create"  -> createParty(player);
			case "invite"  -> inviteToParty(player, args[2]);
			case "leave"   -> leaveParty(player);
			case "kick"    -> kickFromParty(player, args[2]);
			case "unkick"  -> unkickFromParty(player, args[2]);
			case "accept"  -> joinParty(player, args[2]);
			case "decline" -> declinePartyInvite(player, args[2]);
			case "info"    -> displayPartyInfo(player);
			default        -> Messages.sendMessage(player, "&c Invalid argument supplied");
		}
	}

	/**
	 * Create a TNTRun party. The player creating the party is automatically the party leader.
	 *
	 * @param player The player creating the party.
	 */
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

	/**
	 * Leave the TNTRun party. If the party leader leaves, the party is automatically disbanded.
	 *
	 * @param player The player leaving the party.
	 */
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

	/**
	 * Remove a player from the party. Once kicked from the party, the player can only rejoin if
	 * un-kicked from the party.
	 *
	 * @param player The party leader.
	 * @param targetName The player to kick
	 */
	private void kickFromParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, Messages.partynotleader);
			return;
		}
		if (partyMap.get(player.getName()).removeIf(list -> list.contains(targetName))) {
			kickedMap.computeIfAbsent(player.getName(), k -> new ArrayList<>()).add(targetName);
			Messages.sendMessage(player, Messages.partykick.replace("{PLAYER}", targetName));
			Messages.sendMessage(Bukkit.getPlayer(targetName), Messages.partykick.replace("{PLAYER}", targetName));
		}
	}

	/**
	 * Remove the restriction preventing the player joining the party. A new invitation will be needed for
	 * the player to rejoin the party.
	 *
	 * @param player The party leader.
	 * @param targetName The player to un-kick from the party.
	 */
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

	/**
	 * Invite a player to join the party. The player will receive a clickable link in chat to accept or
	 * decline the invitation.
	 *
	 * @param player The party leader.
	 * @param targetName The player being invited to the party.
	 */
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
		invitedMap.computeIfAbsent(player.getName(), k -> new ArrayList<>()).add(targetName);

		Messages.sendMessage(Bukkit.getPlayer(targetName), Messages.partyinvite.replace("{PLAYER}", player.getName()));
		Utils.displayPartyInvite(player, targetName, "");
	}

	/**
	 * Accept a party invitation. A player joins a party only when accepting an invitation
	 * from the party leader.
	 *
	 * @param player player that wants to join the party
	 * @param leaderName party leader's name
	 */
	private void joinParty(Player player, String leaderName) {
		String playerName = player.getName();
		if (alreadyInParty(player)) {
			Messages.sendMessage(player, Messages.partyinparty);
			return;
		}
		if (!partyExists(leaderName)) {
			Messages.sendMessage(player, Messages.partynotexist);
			return;
		}
		if (isKicked(leaderName, playerName)) {
			Messages.sendMessage(player, Messages.partyban);
			return;
		}
		if (!isInvited(leaderName, playerName)) {
			Messages.sendMessage(player, Messages.partynoinvite);
			return;
		}
		partyMap.computeIfAbsent(leaderName, k -> new ArrayList<>()).add(playerName);
		invitedMap.get(leaderName).removeIf(list -> list.contains(playerName));

		String msg = Messages.partyjoin.replace("{PLAYER}", playerName);
		Messages.sendMessage(Bukkit.getPlayer(leaderName), msg);
		Messages.sendMessage(player, msg);
		if (Utils.debug()) {
			plugin.getLogger().info(playerName + " has joined party created by " + leaderName);
		}
	}

	/**
	 * Decline a party invitation. The invitation is removed and the party leader will have to
	 * re-invite the player if you subsequently want to join the party.
	 *
	 * @param player
	 * @param leaderName
	 */
	private void declinePartyInvite(Player player, String leaderName) {
		String playerName = player.getName();
		invitedMap.get(leaderName).removeIf(list -> list.contains(playerName));
		String msg = Messages.partydecline.replace("{PLAYER}", playerName);
		Messages.sendMessage(player, msg);
		Messages.sendMessage(Bukkit.getPlayer(leaderName), msg);
		if (Utils.debug()) {
			plugin.getLogger().info(playerName + " has declined the party invitation from " + leaderName);
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

	private boolean isInvited(String playerName, String targetName) {
		if (invitedMap.containsKey(playerName)) {
			return invitedMap.get(playerName).contains(targetName);
		}
		return false;
	}

	private void removeParty(Player player) {
		partyMap.remove(player.getName());
		invitedMap.remove(player.getName());
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
