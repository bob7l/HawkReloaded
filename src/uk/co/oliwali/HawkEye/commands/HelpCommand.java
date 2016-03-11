package uk.co.oliwali.HawkEye.commands;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.HawkCommand;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Displays the help data for all commands
 *
 * @author oliverw92
 */
public class HelpCommand extends BaseCommand {

    private HawkCommand baseCommand;

    public HelpCommand(HawkCommand baseCommand) {
        this.baseCommand = baseCommand;
        name = "help";
        argLength = 0;
        permission = "help";
        usage = "<- lists all HawkEye commands";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        //General help
        if (args.length == 0) {
            Util.sendMessage(sender, "&c---------------------- &7HawkEye &c----------------------");
            Util.sendMessage(sender, "&7Type &8/hawk help <command>&7 for more info on that command");
            for (BaseCommand cmd : baseCommand.getSubCommands())
                if (Util.hasPerm(sender, cmd.permission))
                    Util.sendMessage(sender, "&8-&7 /hawk &c" + cmd.name + " &7" + cmd.usage);
        }
        //Command-specific help
        else {
            for (BaseCommand cmd : baseCommand.getSubCommands()) {
                if (Util.hasPerm(sender, cmd.permission) && cmd.getName().equalsIgnoreCase(args[0])) {
                    Util.sendMessage(sender, "&c---------------------- &7HawkEye - " + cmd.name);
                    Util.sendMessage(sender, "&8-&7 /hawk &c" + cmd.name + " &7" + cmd.usage);

                    cmd.moreHelp(sender);

                    return true;
                }
            }
            Util.sendMessage(sender, "&cNo command found by that name");
        }
        return true;
    }

    @Override
    public void moreHelp(CommandSender sender) {
        Util.sendMessage(sender, "&cShows all HawkEye commands");
        Util.sendMessage(sender, "&cType &7/hawk help <command>&c for help on that command");
    }
}