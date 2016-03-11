package uk.co.oliwali.HawkEye.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author bob7l
 */
public abstract class BaseCommand {

    protected String name;
    protected int argLength = 0;
    protected String usage;
    protected String permission;

    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
        return true;
    }

    public boolean execute(Player player, String[] args) {
        return execute(((CommandSender)player), args);
    }

    /**
     * Sends advanced help to the sender
     */
    public abstract void moreHelp(CommandSender sender);

    public String getName() {
        return name;
    }

    public int getArgLength() {
        return argLength;
    }

    public String getUsage() {
        return usage;
    }

    public String getPermission() {
        return permission;
    }

}
