package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import parallaxscience.guilds.config.RaidConfig;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;
import parallaxscience.guilds.utility.CommandUtility;
import parallaxscience.guilds.utility.MessageUtility;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for handling raid commands
 * @see CommandBase
 * @author Tristan Jay
 */
public class CommandRaid extends CommandBase
{
    /**
     * String array of sub-commands uses by the auto tab-completion
     */
    private static final String[] commands = new String[]{
            //For all in a guild:
            "help",
            //Not in raid:
            "join",
            //Part of attackers:
            "leave",
            "start"
    };

    /**
     * Variable for storing the raid command color style
     * Called whenever a raid command is used
     * @see Style
     */
    private static final Style style = new Style();

    /**
     * Constructor for the class
     * Only used to set the raid style color
     */
    public CommandRaid()
    {
        style.setColor(TextFormatting.RED);
    }

    /**
     * Returns the name of the command
     */
    @Override
    @Nonnull
    public String getName() {
        return "raid";
    }

    /**
     * Returns the usage of the command
     * @param sender ICommandSender reference to the player
     * @return String usage of the command
     */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "/raid <action> [arguments]";
    }

    /**
     * Returns the required permission level in order to use this command
     * @return integer of required permission level
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    /**
     * Returns whether or not the user has permission to use this command
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @return true if the player has permission to use this command
     */
    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    /**
     * Command tab completion function
     * Returns a list of potential matching commands
     * Called when tab is pressed while entering a command
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param args String array of arguments given from the player
     * @param targetPos BlockPos of the player
     * @return String List of potential tab completions
     */
    @Override
    @Nonnull
    @SuppressWarnings( "SwitchStatementWithTooFewBranches")
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
    {
        if(args.length == 1) return CommandUtility.getLastMatchingStrings(args, Arrays.asList(commands));
        else if(args.length == 2)
        {
            Entity entity = sender.getCommandSenderEntity();
            if(entity == null) return new ArrayList<>();
            UUID player = entity.getUniqueID();
            Guild guild = GuildCache.getPlayerGuild(player);

            switch(args[0])
            {
                case "join":
                    if(guild != null) return CommandUtility.getLastMatchingStrings(args, GuildCache.getGuildList());
                    break;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Execution of the /raid command
     * Called whenever the player attempts to use the command (presses Enter)
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param args String array of arguments given from the player
     */
    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        if(args.length == 0)
        {
            MessageUtility.raidMessage(sender, "Type \"/raid help\" for help");
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
                case "help": displayHelp(sender, guild, raid);
                    break;
                case "join":
                    if(args.length == 2)
                    {
                        joinRaid(sender, player, guild, raid, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "leave":
                    leaveRaid(sender, player, guild, raid);
                    break;
                case "start":
                    startRaid(sender, raid);
                    break;
                default: MessageUtility.raidMessage(sender, "Invalid command! Type /raid help for valid commands!");
                    break;
            }
        }
    }

    /**
     * Sends a "Not enough arguments" message back to the player
     * @param sender ICommandSender reference to the player
     */
    private void notEnoughArguments(ICommandSender sender) { MessageUtility.raidMessage(sender, "Error: Not enough arguments!"); }

    /**
     * Displays all of the available sub-commands and their uses to the player
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     * @param raid Raid object reference of the player's current raid
     */
    private void displayHelp(ICommandSender sender, Guild guild, Raid raid)
    {
        if(guild == null) MessageUtility.raidMessage(sender, "Only those who are in a guild can use raid commands!");
        else
        {
            MessageUtility.raidMessage(sender, "/raid help - Displays raid commands.");
            if(raid == null) MessageUtility.raidMessage(sender, "/raid join <guild> - Join a raid on a guild");
            else if(!raid.isStarted())
            {
                MessageUtility.raidMessage(sender, "/raid start - Start the raid.");
                MessageUtility.raidMessage(sender, "/raid leave - Leave the current raiding party.");
            }
        }
    }

    /**
     * Called whenever a player attempts to join a new raid
     * @param sender ICommandSender reference to the player
     * @param player UUID of the player
     * @param guild Guild object reference to the player's guild
     * @param playerRaid Raid object reference of the player's current raid
     * @param newRaidName String name of the new raid
     */
    private void joinRaid(ICommandSender sender, UUID player, Guild guild, Raid playerRaid, String newRaidName)
    {
        if(guild == null) MessageUtility.raidMessage(sender, "Only those who are in a guild may join a raid!");
        else
        {
            if(playerRaid != null) MessageUtility.raidMessage(sender, "You are already part of a raid!");
            else if(newRaidName.equals(guild.getGuildName())) MessageUtility.raidMessage(sender, "You cannot join a raid on your own guild!");
            else
            {
                Guild newGuild = GuildCache.getGuild(newRaidName);
                if(newGuild == null) MessageUtility.raidMessage(sender, "That guild does not exist!");
                else
                {
                    Raid raid = RaidCache.getRaid(newRaidName);
                    if(raid == null)
                    {
                        long remainingShield = guild.getRemainingShield();
                        if(remainingShield == 0)
                        {
                            MessageUtility.raidMessage(sender, "Successfully joined the raid on " + newRaidName + "!");
                            RaidCache.createRaid(newRaidName, player);
                        }
                        else MessageUtility.raidMessage(sender, "That guild is still shielded for another " + remainingShield + " minutes!");
                    }
                    else if(raid.isActive()) MessageUtility.raidMessage(sender, "The raid on " + newRaidName + " has already begun!");
                    else
                    {
                        String alliance = guild.getAlliance();
                        if(alliance != null)
                        {
                            if(alliance.equals(GuildCache.getGuild(newRaidName).getAlliance()))
                            {
                                if(raid.isStarted())
                                {
                                    raid.addDefender(player);
                                    MessageUtility.raidMessage(sender, "Successfully joined the raid on " + newRaidName + " as a defender!");
                                }
                                else MessageUtility.raidMessage(sender, "A raid has not been started for that guild!");
                                return;
                            }
                        }
                        if(raid.canAttackerJoin())
                        {
                            raid.addAttacker(player);
                            MessageUtility.raidMessage(sender, "Successfully joined the raid on " + newRaidName + "!");
                        }
                        else MessageUtility.raidMessage(sender, "No more attackers can join the raid at the moment!");
                    }
                }
            }
        }
    }

    /**
     * Called whenever a player attempts to leave a raid
     * @param sender ICommandSender reference to the player
     * @param player UUID of the player
     * @param guild Guild object reference to the player's guild
     * @param raid Raid object reference of the player's current raid
     */
    private void leaveRaid(ICommandSender sender, UUID player, Guild guild, Raid raid)
    {
        if(guild == null) MessageUtility.raidMessage(sender, "You are not part of a guild!");
        else if(raid == null) MessageUtility.raidMessage(sender, "You are not currently a part of a raid!");
        else if(raid.getDefendingGuild().equals(guild.getGuildName())) MessageUtility.raidMessage(sender, "You are not currently a part of a raid!"); //To hide a potential raid
        else if(raid.isStarted()) MessageUtility.raidMessage(sender, "The raid preparation has already begun!");
        else
        {
            raid.removePlayer(player);
            MessageUtility.raidMessage(sender, "You have successfully left the raid.");
        }
    }

    /**
     * Called whenever a player attempts to start a raid
     * @param sender ICommandSender reference to the player
     * @param raid Raid object reference of the player's current raid
     */
    private void startRaid(ICommandSender sender, Raid raid)
    {
        if(raid == null) MessageUtility.raidMessage(sender, "You are not currently a part of a raid!");
        else if(raid.isStarted()) MessageUtility.raidMessage(sender, "Raid is already started!");
        else
        {
            raid.startRaid();
            MessageUtility.raidMessageAll("The raid on " + raid.getDefendingGuild() + " will begin in " + RaidConfig.prepSeconds + " seconds!");
        }
    }
}
