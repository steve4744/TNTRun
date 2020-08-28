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

package tntrun.conversation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.BooleanPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import tntrun.arena.Arena;
import tntrun.conversation.TNTRunConversation;


public class ArenaRewardConversation extends FixedSetPrompt {
	private Arena arena;
	private Boolean isFirstItem = true;
	
	public ArenaRewardConversation(Arena arena) {
		super("material", "command", "xp");
		this.arena = arena;
	}
	
	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.GOLD + " What type of reward would you like to set?\n" 
				+ ChatColor.GREEN + formatFixedSet();
	}
	
	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String choice) {
		if (choice.equalsIgnoreCase("material"))
			return new ChooseMaterial();
	
		if (choice.equalsIgnoreCase("command"))
			return new ChooseCommand();

		if (choice.equalsIgnoreCase("xp"))
			return new ChooseXP();

		return null;
	}
	
	/* === Reward Material === */
	private class ChooseMaterial extends StringPrompt {
		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.GOLD + " What Material do you want to reward the player with?";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String message) {
			Material material = Material.getMaterial(message.toUpperCase());
			if (material == null){
				TNTRunConversation.sendErrorMessage(context, "This is not a valid material");
				return this;
			}

			context.setSessionData("material", message.toUpperCase());
			return new ChooseAmount();
		}		
	}

	private class ChooseAmount extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.GOLD + " How many would you like to reward the player with?";
		}

		@Override
		protected boolean isNumberValid(ConversationContext context, Number input) {
			return input.intValue() >= 0 && input.intValue() <= 255;
		}

		@Override
		protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
			return "Amount must be between 0 and 255.";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
			context.setSessionData("amount", amount.intValue());

			return new MaterialProcessComplete();
		}
	}

	private class MaterialProcessComplete extends BooleanPrompt {
		public String getPromptText(ConversationContext context) {
            return ChatColor.GOLD + " Reward saved - would you like to add another Material?\n" +
                    ChatColor.GREEN + "[yes, no]";
        }
		@Override
        protected Prompt acceptValidatedInput(ConversationContext context, boolean nextMaterial) {
			arena.getStructureManager().getRewards().setMaterialReward(
                    context.getSessionData("material").toString(),
                    context.getSessionData("amount").toString(),
                    isFirstItem);

			if (isFirstItem) {
				context.getForWhom().sendRawMessage("§7[§6TNTRun§7] Material reward for " + ChatColor.GOLD + arena.getArenaName() + ChatColor.GRAY + " set to " + ChatColor.GOLD + context.getSessionData("amount") + ChatColor.GRAY + " x " + ChatColor.GOLD + context.getSessionData("material"));
			} else {
				context.getForWhom().sendRawMessage("§7[§6TNTRun§7] " + ChatColor.GOLD + context.getSessionData("amount") + ChatColor.GRAY + " x " + ChatColor.GOLD + context.getSessionData("material") + ChatColor.GRAY + " added to Material reward for " + ChatColor.GOLD + arena.getArenaName());
			}
			
			if (nextMaterial) {
				isFirstItem = false;
				return new ChooseMaterial();
			}
			isFirstItem = true;
			return Prompt.END_OF_CONVERSATION;
		}
	}
	
	/* === Reward Command === */
	private class ChooseCommand extends StringPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage(ChatColor.GRAY + "Remember you can include %PLAYER% to apply it to that player.\nExample: 'perm setrank %PLAYER% vip'");
			return ChatColor.GOLD + " What would you like the Command reward to be?";
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String message) {
			String command = message.replace("/", "");
			context.setSessionData("command", command);

			return new ChooseRunNow();
		}
	}

	private class ChooseRunNow extends BooleanPrompt {

		@Override
		public String getPromptText(ConversationContext arg0) {
			return ChatColor.GOLD + " Would you like to run this command now? (to test)\n" +
            ChatColor.GREEN + "[yes, no]";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, boolean runNow) {
			if (runNow)
				Bukkit.getServer().dispatchCommand(
						Bukkit.getServer().getConsoleSender(), 
						context.getSessionData("command").toString()
						.replace("%PLAYER%", context.getSessionData("playerName").toString()));

			return new CommandProcessComplete();
		}
	}

	private class CommandProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			arena.getStructureManager().getRewards().setCommandReward(
                    context.getSessionData("command").toString());

			return "§7[§6TNTRun§7] The Command reward for " + ChatColor.GOLD + arena.getArenaName() + ChatColor.GRAY + " was set to /" + ChatColor.GOLD + context.getSessionData("command");
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}
	
	/* === Reward XP === */
	private class ChooseXP extends NumericPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.GOLD + " How much XP would you like to reward the player with?";
		}

		@Override
		protected boolean isNumberValid(ConversationContext context, Number input) {
			return input.intValue() >= 0 && input.intValue() <= 10000;
		}

		@Override
		protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
			return "Amount must be between 0 and 10,000.";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, Number amount) {
			context.setSessionData("amount", amount.intValue());

			return new XPProcessComplete();
		}
	}

	private class XPProcessComplete extends MessagePrompt {
		public String getPromptText(ConversationContext context) {
			arena.getStructureManager().getRewards().setXPReward(
                    Integer.parseInt(context.getSessionData("amount").toString()));

			return "§7[§6TNTRun§7] The XP reward for " + ChatColor.GOLD + arena.getArenaName() + ChatColor.GRAY + " was set to " + ChatColor.GOLD + context.getSessionData("amount");
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext context) {
			return Prompt.END_OF_CONVERSATION;
		}
	}
}
