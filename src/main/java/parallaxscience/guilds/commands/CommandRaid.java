package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import parallaxscience.guilds.guild.Guild;

import java.util.UUID;

public class CommandRaid extends CommandBase {

    /**
     * String array of sub-commands uses by the auto tab-completion
     */
    public static final String[] commands = new String[]{
            //For all in a guild:
            "help",
            //Not in raid:
            "join",
            //Part of attackers:
            "invite",
            "leave",
            "start"
    };

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "raid";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return "/raid <action> [arguments]";
    }

    /**
     * Callback for when the command is executed
     *
     * @param server
     * @param sender
     * @param args
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    }

    private void displayHelp()
    {
        //Is not in guild
        //Is not in raid
        //Is attacker
    }

    private void joinRaid()
    {
        //Is not in guild
        //Is already in raid
        //Guild members is not adequate/full
    }

    private void leaveRaid()
    {
        //Is not in raid
        //Preparation has started
    }

    private void startRaid()
    {
        //Not in raid
        //Raid already started
    }

    private void invite()
    {
        //Is not in raid
    }
}
