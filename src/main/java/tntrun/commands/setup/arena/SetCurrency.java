package tntrun.commands.setup.arena;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;
import tntrun.utils.Utils;

public class SetCurrency implements CommandHandlerInterface {

	private final TNTRun plugin;

	public SetCurrency(TNTRun plugin) {
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
			Material itemCurrency = Material.getMaterial(args[1].toUpperCase());
			if (itemCurrency == null || Utils.isAir(itemCurrency)) {
				itemCurrency = Material.AIR;
				Messages.sendMessage(player, "&7 Arena &6" + args[0] + "&7 Item currency has been disabled, use a valid item to re-enable");
			} else {
				Messages.sendMessage(player, "&7 Arena &6" + args[0] + "&7 Item currency set to &6" + itemCurrency.toString());
			}
			arena.getStructureManager().setCurrency(itemCurrency);
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
