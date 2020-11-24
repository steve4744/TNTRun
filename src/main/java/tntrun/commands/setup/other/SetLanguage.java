package tntrun.commands.setup.other;

import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.commands.setup.CommandHandlerInterface;
import tntrun.messages.Messages;

public class SetLanguage implements CommandHandlerInterface {

	private TNTRun plugin;

	public SetLanguage(TNTRun plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean handleCommand(Player player, String[] args) {
		if (!plugin.getLanguage().getTranslatedLanguages().contains(args[0])) {
			Messages.sendMessage(player, "&7 Language not currently supported: &c" + args[0]);
			return true;
		}
		plugin.getLanguage().setLang(args[0]);
		plugin.reloadConfig();
		Messages.loadMessages(plugin);
		Messages.sendMessage(player, "&7 Language set to &6" + args[0]);
		return true;
	}

	@Override
	public int getMinArgsLength() {
		return 1;
	}
}
