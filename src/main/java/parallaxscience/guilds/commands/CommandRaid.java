package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import parallaxscience.guilds.Guilds;
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

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
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
            Raid raid = RaidCache.getPlayerRaid(player);

            switch (args[0].toLowerCase())
            {
                case "help": displayHelp(sender, player, guild, raid);
                    break;
                case "join":
                {
                    joinRaid(sender, player, guild, raid, args[1]);
                } break;
                case "leave":
                {
                    leaveRaid(sender, player, raid);
                } break;
                case "start":
                {
                    startRaid(sender, raid);
                } break;

                default: sender.sendMessage(new TextComponentString("Invalid command! Type /raid help for valid commands!"));
                    break;
            }
        }
    }

    private void displayHelp(ICommandSender sender, UUID player, Guild guild, Raid raid)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("Only those who are in a guild can use raid commands!"));
        else
        {
            sender.sendMessage(new TextComponentString("/raid help - Displays raid commands."));
            if(raid == null) sender.sendMessage(new TextComponentString("/raid join <guild> - Join a raid on a guild"));
            else if(!raid.isStarted())
            {
                sender.sendMessage(new TextComponentString("/raid start - Start the raid."));
                sender.sendMessage(new TextComponentString("/raid leave - Leave the current raiding party."));
            }
        }
    }

    private void joinRaid(ICommandSender sender, UUID player, Guild guild, Raid playerRaid, String newRaidName)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("Only those who are in a guild may join a raid!"));
        else
        {
            if(playerRaid != null) sender.sendMessage(new TextComponentString("You are already part of a raid!"));
            else if(newRaidName.equals(guild.getGuildName())) sender.sendMessage(new TextComponentString("You cannot join a raid on your own guild!"));
            else
            {
                Raid raid = RaidCache.getRaid(newRaidName);
                if(raid == null) RaidCache.createRaid(newRaidName, player);
                else if(raid.isActive()) sender.sendMessage(new TextComponentString("The raid on " + newRaidName + " has already begun!"));
                else
                {
                    String alliance = guild.getAlliance();
                    if(alliance == null);
                    else if(alliance.equals(GuildCache.getGuild(newRaidName).getAlliance()))
                    {
                        if(raid.isStarted())
                        {
                            raid.addDefender(player);
                            sender.sendMessage(new TextComponentString("Successfully joined the raid on " + newRaidName + " as a defender!"));
                        }
                        else sender.sendMessage(new TextComponentString("A raid has not been started for that guild!"));
                    }
                    else
                    {
                        if(raid.canAttackerJoin())
                        {
                            raid.addAttacker(player);
                            sender.sendMessage(new TextComponentString("Successfully joined the raid on " + newRaidName + "!"));
                        }
                        else sender.sendMessage(new TextComponentString("No more attackers can join the raid at the moment!"));
                    }
                }
            }
        }
    }

    private void leaveRaid(ICommandSender sender, UUID player, Raid raid)
    {
        if(raid == null) sender.sendMessage(new TextComponentString("You are not currently a part of a raid!"));
        else if(raid.isStarted()) sender.sendMessage(new TextComponentString("The raid preparation has already begun!"));
        else
        {
            raid.removePlayer(player);
            sender.sendMessage(new TextComponentString("You have successfully left the raid."));
        }
    }

    private void startRaid(ICommandSender sender, Raid raid)
    {
        if(raid == null) sender.sendMessage(new TextComponentString("You are not currently a part of a raid!"));
        else if(raid.isStarted()) sender.sendMessage(new TextComponentString("Raid is already started!"));
        else
        {
            raid.startRaid();
            PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
            players.sendMessage(new TextComponentString("The raid on " + raid + " will begin in " + Guilds.prepSeconds + "seconds!"));
        }
    }
}
