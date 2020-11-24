package tntrun.commands.setup.arena;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.arena.structure.StructureManager.DamageEnabled;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;

public class SetDamage implements CommandHandlerInterface {

	private TNTRun plugin;
	public SetDamage(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		Arena arena = plugin.amanager.getArenaByName(args[0]);
		if (arena != null) {
			if (arena.getStatusManager().isArenaEnabled()) {
				Messages.sendMessage(player, Messages.arenanotdisabled.replace("{ARENA}", args[0]));
				return true;
			}
			if (args[1].equalsIgnoreCase("yes")) {
				arena.getStructureManager().setDamageEnabled(DamageEnabled.YES);
			} else if (args[1].equalsIgnoreCase("no")) {
				arena.getStructureManager().setDamageEnabled(DamageEnabled.NO);
			} else if (args[1].equalsIgnoreCase("zero")) {
				arena.getStructureManager().setDamageEnabled(DamageEnabled.ZERO);
			} else {
				Messages.sendMessage(player, "&c SetDamage must be &6YES, NO &cor &6ZERO");
				return true;
			}
			Messages.sendMessage(player, "&7 Arena &6" + args[0] + "&7 set damage to: &6" + args[1]);
		} else {
			Messages.sendMessage(player, Messages.arenanotexist.replace("{ARENA}", args[0]));
		}
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 2;
	}

}
