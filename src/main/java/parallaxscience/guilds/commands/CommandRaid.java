package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;

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
        if(args.length == 0)
        {
            sender.sendMessage(new TextComponentString("Type \"/raid help\" for help"));
        }
        else
        {
            Entity entity = sender.getCommandSenderEntity();
            if(entity == null) return;
            UUID player = entity.getUniqueID();
            Guild guild = GuildCache.getPlayerGuild(player);

            switch (args[0].toLowerCase())
            {
                case "help": displayHelp(sender, player, guild);
                    break;
                case "join":
                {

                } break;
                case "invite":
                {

                } break;
                case "leave":
                {

                } break;
                case "start":
                {

                } break;

                default: sender.sendMessage(new TextComponentString("Invalid command! Type /guild help for valid commands!"));
                    break;
            }
        }
    }

    private void displayHelp(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("Only those who are in a guild can use raid commands!"));
        else
        {
            Raid raid = RaidCache.getPlayerRaid(player);
            sender.sendMessage(new TextComponentString("/raid help"));
            if(raid == null) sender.sendMessage(new TextComponentString("/raid join"));
            else if(raid.isAttacker(player) && !raid.isActive()) sender.sendMessage(new TextComponentString("/raid invite"));
            else
            {
                //DO STUFF
            }
        }
        //Is not in raid
        //Is attacker
    }

    private void joinRaid(ICommandSender sender, UUID player, Guild guild, String raidName)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("Only those who are in a guild may join a raid!"));
        else
        {
            if(RaidCache.getPlayerRaid(player) != null) sender.sendMessage(new TextComponentString("You are already part of a raid!"));
            else
            {
                Guild defender = GuildCache.getGuild(raidName);
                //defender.getOnlineMembers().size();

                //Does raid already exist?
                //What phase is the raid in?
                //How many members for the defending side are present?
            }
        }
    }

    private void leaveRaid(ICommandSender sender)
    {
        //Is not in raid
        //Preparation has started
    }

    private void startRaid(ICommandSender sender)
    {
        //Not in raid
        //Raid already started
    }

    private void invite(ICommandSender sender)
    {
        //Is not in raid
        //Other person is in raid
        //Raid already started

        //Send invite message
    }
}
