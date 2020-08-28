package tntrun.commands.setup.reload;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.commands.setup.CommandHandlerInterface;

public class ReloadConfig  implements CommandHandlerInterface {

	private TNTRun plugin;
	
	public ReloadConfig(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		plugin.reloadConfig();
		plugin.signEditor.loadConfiguration();
		player.sendMessage("Config reloaded");
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 0;
	}
}
