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
			case "create":
				createParty(player);
				break;
			case "invite":
				inviteToParty(player, args[2]);
				break;
			case "leave":
				leaveParty(player);
				break;
			case "kick":
				kickFromParty(player, args[2]);
				break;
			case "unkick":
				unkickFromParty(player, args[2]);
				break;
			case "info":
				displayPartyInfo(player);
				break;
			default:
				Messages.sendMessage(player, "&c Invalid argument supplied");
		}
	}

	private void createParty(Player player) {
		if (alreadyInParty(player)) {
			Messages.sendMessage(player, "&c You are already in a party");
			return;
		}
		partyMap.put(player.getName(), new ArrayList<>());
		Messages.sendMessage(player, "&c Party created!");
	}

	private void leaveParty(Player player) {
		if (isPartyLeader(player)) {
			for (String member : partyMap.get(player.getName())) {
				Messages.sendMessage(Bukkit.getPlayer(member), "&c You are no longer in the party as the leader has left");
			}
			removeParty(player);
			return;
		}
		if (!isPartyMember(player)) {
			Messages.sendMessage(player, "&c You are not a member of any party");
			return;
		}

		partyMap.entrySet().forEach(e -> {
			if (e.getValue().contains(player.getName())) {
				e.getValue().remove(player.getName());
				Messages.sendMessage(player, "&c You have left the party");
				Messages.sendMessage(Bukkit.getPlayer(e.getKey()), "&c " + player + " has left the party");
			}
		});
	}

	private void kickFromParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, "&cYou are not a party leader");
			return;
		}
		if (partyMap.get(player.getName()).removeIf(list -> list.contains(targetName))) {
			kickedMap.computeIfAbsent(player.getName(), k -> new ArrayList<>()).add(targetName);
			Messages.sendMessage(player, "&c" + targetName + " has been kicked from the party");
		}
	}

	private void unkickFromParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, "&cYou are not a party leader");
			return;
		}
		if (kickedMap.containsKey(player.getName())) {
			kickedMap.get(player.getName()).removeIf(list -> list.contains(targetName));
			Messages.sendMessage(player, "&c" + targetName + " is allowed to join your party");
		}
	}

	private void inviteToParty(Player player, String targetName) {
		if (!isPartyLeader(player)) {
			Messages.sendMessage(player, "&cYou are not a party leader");
			return;
		}
		if (targetName.equalsIgnoreCase(player.getName())) {
			Messages.sendMessage(player, "&cYou cannot invite yourself");
			return;
		}
		if (Bukkit.getPlayer(targetName) == null) {
			Messages.sendMessage(player, "&cPlayer " + targetName + " is not online");
			return;
		}
		Messages.sendMessage(Bukkit.getPlayer(targetName), player.getName() + " has invited you to join a party.");
		Utils.displayPartyInvite(player, targetName, "");
	}

	public void joinParty(String playerName, String targetName) {
		Player targetPlayer = Bukkit.getPlayer(targetName);
		if (alreadyInParty(targetPlayer)) {
			Messages.sendMessage(targetPlayer, "&c You are already in a party");
			return;
		}
		if (!partyExists(playerName)) {
			Messages.sendMessage(targetPlayer, "&c The selected party does not exist");
			return;
		}
		if (isKicked(playerName, targetName)) {
			Messages.sendMessage(targetPlayer, "&c You are currently kicked from this party");
			return;
		}
		partyMap.computeIfAbsent(playerName, k -> new ArrayList<>()).add(targetName);
		Messages.sendMessage(targetPlayer, "&c " + targetName + " has joined the party");
		Messages.sendMessage(Bukkit.getPlayer(playerName), "&c " + targetName + " has joined the party");
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
		Messages.sendMessage(player, "&c You have left the party, and the party has been deleted");
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
			Messages.sendMessage(player, "&c You are not currently in a party");
			return;
		}
		String leader = getPartyLeader(player);
		Messages.sendMessage(player, "Party leader: " + getPartyLeader(player));
		Messages.sendMessage(player, "Party size: " + (getPartyMembers(leader).size() + 1));
		Messages.sendMessage(player, "Party members: " + getPartyMembers(leader).toString());
	}
}
