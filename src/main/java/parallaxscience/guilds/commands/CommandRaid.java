package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import parallaxscience.guilds.config.RaidConfig;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;
import scala.actors.threadpool.Arrays;
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
public class CommandRaid extends CommandBase {

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
    @SuppressWarnings({"unchecked", "SwitchStatementWithTooFewBranches"})
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos)
    {
        if(args.length == 1) return getLastMatchingStrings(args, Arrays.asList(commands));
        else if(args.length == 2)
        {
            Entity entity = sender.getCommandSenderEntity();
            if(entity == null) return new ArrayList<>();
            UUID player = entity.getUniqueID();
            Guild guild = GuildCache.getPlayerGuild(player);

            switch(args[0])
            {
                case "join":
                    if(guild != null) getLastMatchingStrings(args, GuildCache.getGuildList());
                    break;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Finds the matching strings in the list for the last given argument
     * @param args String array of arguments given from the player
     * @param list String list to match the last argument to
     * @return String List of matching Strings
     */
    private List<String> getLastMatchingStrings(String[] args, List<String> list)
    {
        List<String> matching = new ArrayList<>();
        String string = args[args.length - 1];
        int length = string.length();
        for(String item : list)
        {
            if(string.toLowerCase().equals(item.substring(0, length).toLowerCase())) matching.add(item);
        }
        return matching;
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
            raidMessage(sender, "Type \"/raid help\" for help");
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
                default: raidMessage(sender, "Invalid command! Type /raid help for valid commands!");
                    break;
            }
        }
    }

    /**
     * Sends a "Not enough arguments" message back to the player
     * @param sender ICommandSender reference to the player
     */
    private void notEnoughArguments(ICommandSender sender) { raidMessage(sender, "Error: Not enough arguments!"); }

    /**
     * Sends a message to the player
     * Uses the raid color style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     */
    private void raidMessage(ICommandSender sender, String message)
    {
        ITextComponent textComponent = new TextComponentString(message);
        textComponent.setStyle(style);
        sender.sendMessage(textComponent);
    }

    /**
     * Displays all of the available sub-commands and their uses to the player
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     * @param raid Raid object reference of the player's current raid
     */
    private void displayHelp(ICommandSender sender, Guild guild, Raid raid)
    {
        if(guild == null) raidMessage(sender, "Only those who are in a guild can use raid commands!");
        else
        {
            raidMessage(sender, "/raid help - Displays raid commands.");
            if(raid == null) raidMessage(sender, "/raid join <guild> - Join a raid on a guild");
            else if(!raid.isStarted())
            {
                raidMessage(sender, "/raid start - Start the raid.");
                raidMessage(sender, "/raid leave - Leave the current raiding party.");
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
        if(guild == null) raidMessage(sender, "Only those who are in a guild may join a raid!");
        else
        {
            if(playerRaid != null) raidMessage(sender, "You are already part of a raid!");
            else if(newRaidName.equals(guild.getGuildName())) raidMessage(sender, "You cannot join a raid on your own guild!");
            else
            {
                Raid raid = RaidCache.getRaid(newRaidName);
                if(raid == null)
                {
                    raidMessage(sender, "Successfully joined the raid on " + newRaidName + "!");
                    RaidCache.createRaid(newRaidName, player);
                }
                else if(raid.isActive()) raidMessage(sender, "The raid on " + newRaidName + " has already begun!");
                else
                {
                    String alliance = guild.getAlliance();
                    if(alliance == null) raidMessage(sender, "Your guild is not a part of an alliance!");
                    else if(alliance.equals(GuildCache.getGuild(newRaidName).getAlliance()))
                    {
                        if(raid.isStarted())
                        {
                            raid.addDefender(player);
                            raidMessage(sender, "Successfully joined the raid on " + newRaidName + " as a defender!");
                        }
                        else raidMessage(sender, "A raid has not been started for that guild!");
                    }
                    else
                    {
                        if(raid.canAttackerJoin())
                        {
                            raid.addAttacker(player);
                            raidMessage(sender, "Successfully joined the raid on " + newRaidName + "!");
                        }
                        else raidMessage(sender, "No more attackers can join the raid at the moment!");
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
        if(guild == null) raidMessage(sender, "You are not part of a guild!");
        else if(raid == null) raidMessage(sender, "You are not currently a part of a raid!");
        else if(raid.getDefendingGuild().equals(guild.getGuildName())) raidMessage(sender, "You are not currently a part of a raid!"); //To hide a potential raid
        else if(raid.isStarted()) raidMessage(sender, "The raid preparation has already begun!");
        else
        {
            raid.removePlayer(player);
            raidMessage(sender, "You have successfully left the raid.");
        }
    }

    /**
     * Called whenever a player attempts to start a raid
     * @param sender ICommandSender reference to the player
     * @param raid Raid object reference of the player's current raid
     */
    private void startRaid(ICommandSender sender, Raid raid)
    {
        if(raid == null) raidMessage(sender, "You are not currently a part of a raid!");
        else if(raid.isStarted()) raidMessage(sender, "Raid is already started!");
        else
        {
            raid.startRaid();
            //FIX THIS
            PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
            players.sendMessage(new TextComponentString("The raid on " + raid.getDefendingGuild() + " will begin in " + RaidConfig.prepSeconds + " seconds!"));
        }
    }
}
