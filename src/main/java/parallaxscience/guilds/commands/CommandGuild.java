package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import parallaxscience.guilds.alliance.AllianceCache;
import parallaxscience.guilds.config.GeneralConfig;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.guild.Guild;
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
 * Main guild command registration and handler class.
 * Registers the /guild command and all of its sub-commands, and handles its events.
 * @see CommandBase
 * @author Tristan Jay
 */
public class CommandGuild extends CommandBase
{
    /**
     * String array of sub-commands uses by the auto tab-completion
     */
    private static final String[] commands = new String[]{
            //For all:
            "help",
            //Not in guild:
            "form",
            "accept",
            //Guild members:
            "leave",
            "members",
            //Guild admins:
            "invite",
            "claim",
            "abandon",
            "kick",
            //Guild Masters:
            "disband",
            "promote",
            "demote",
            "transfer",
            "setcolor"
    };

    /**
     * Variable for storing the guild command color style
     * Called whenever a guild command is used
     * @see Style
     */
    private static final Style style = new Style();

    /**
     * Constructor for the class
     * Only used to set the guild style color
     */
    public CommandGuild()
    {
        style.setColor(TextFormatting.GREEN);
    }

    /**
     * Returns the name of the command
     */
    @Override
    @Nonnull
    public String getName()
    {
        return "guild";
    }

    /**
     * Returns the usage of the command
     * @param sender ICommandSender reference to the player
     * @return String usage of the command
     */
    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender)
    {
        return "/guild <action> [arguments]";
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
                case "accept":
                    return CommandUtility.getLastMatchingStrings(args, GuildCache.getGuildList());
                case "invite":
                    if(guild != null)
                    {
                        List<String> names = new ArrayList<>();
                        for(EntityPlayer entityPlayer : server.getPlayerList().getPlayers())
                        {
                            if(GuildCache.getPlayerGuild(entityPlayer.getUniqueID()) == null) names.add(entity.getDisplayName().getUnformattedText());
                        }
                        return CommandUtility.getLastMatchingStrings(args, names);
                    }
                    break;
                case "kick":
                    if(guild != null) if(guild.isAdmin(player))
                    {
                        List<String> members = guild.getMembers(server);
                        members.addAll(guild.getAdmins(server));
                        return CommandUtility.getLastMatchingStrings(args, members);
                    }
                    break;
                case "promote":
                    if(guild != null) if(guild.isAdmin(player)) return CommandUtility.getLastMatchingStrings(args, guild.getMembers(server));
                    break;
                case "demote":
                    if(guild != null) if(guild.isAdmin(player)) return CommandUtility.getLastMatchingStrings(args, guild.getAdmins(server));
                    break;
                case "transfer":
                    if(guild != null) if(guild.getGuildMaster().equals(player))
                    {
                        List<String> members = guild.getMembers(server);
                        members.addAll(guild.getAdmins(server));
                        return CommandUtility.getLastMatchingStrings(args, members);
                    }
                    break;
                case "setcolor":
                    if(guild != null) if(guild.getGuildMaster().equals(player)) return CommandUtility.getLastMatchingStrings(args, new ArrayList<>(TextFormatting.getValidValues(false, false)));
                    break;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Execution of the /guild command
     * Called whenever the player attempts to use the command (presses Enter)
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param args String array of arguments given from the player
     */
    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0)
        {
            MessageUtility.guildMessage(sender, "Type \"/guild help\" for help");
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
                case "form":
                    if(args.length == 2) formGuild(sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                    break;
                case "accept":
                    if(args.length == 2) joinGuild(server, sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                    break;
                case "leave":
                    leaveGuild(sender, player, guild);
                    break;
                case "members":
                    listMembers(server, sender, guild);
                    break;
                case "invite":
                    if(args.length == 2) invite(sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                    break;
                case "claim":
                    claim(sender, player, guild);
                    break;
                case "abandon":
                    abandon(sender, player, guild);
                    break;
                case "kick":
                    if(args.length == 2)
                    {
                        kick(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "disband":
                    disbandGuild(server, sender, player, guild);
                    break;
                case "promote":
                    if(args.length == 2)
                    {
                        promote(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "demote":
                    if(args.length == 2)
                    {
                        demote(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "transfer":
                    if(args.length == 2)
                    {
                        transfer(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "setcolor":
                    if(args.length == 2)
                    {
                        setColor(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                default: MessageUtility.guildMessage(sender, "Invalid command! Type /guild help for valid commands!");
                    break;
            }
        }
    }

    /**
     * Sends a "Not enough arguments" message back to the player
     * @param sender ICommandSender reference to the player
     */
    private void notEnoughArguments(ICommandSender sender) { MessageUtility.guildMessage(sender, "Error: Not enough arguments!"); }

    /**
     * Displays all of the available sub-commands and their uses to the player
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     */
    private void displayHelp(ICommandSender sender, UUID player, Guild guild)
    {
        MessageUtility.guildMessage(sender, "/guild help - Lists all available commands");
        if(guild == null)
        {
            MessageUtility.guildMessage(sender, "/guild form <guild> - Creates a new guild");
            MessageUtility.guildMessage(sender, "/guild accept <guild> - Accept invite to join guild");
        }
        else
        {
            MessageUtility.guildMessage(sender, "/guild members - Lists all guild members and ranks");
            if(guild.isAdmin(player))
            {
                MessageUtility.guildMessage(sender, "/guild invite <player> - Invites a player to your guild");
                MessageUtility.guildMessage(sender, "/guild claim - Claims a chunk as guild territory");
                MessageUtility.guildMessage(sender, "/guild abandon - Removes a chunk from guild territory");
                MessageUtility.guildMessage(sender, "/guild kick <player> - Kick player from guild");
                if(guild.getGuildMaster().equals(player))
                {
                    MessageUtility.guildMessage(sender, "/guild disband - Disbands the guild you have created");
                    MessageUtility.guildMessage(sender, "/guild promote <player> - Promotes a regular member to an admin");
                    MessageUtility.guildMessage(sender, "/guild demote <player> - Demotes an admin to a regular member");
                    MessageUtility.guildMessage(sender, "/guild transfer <player> - Transfers ownership of the guild to another");
                    MessageUtility.guildMessage(sender, "/guild setcolor <color> - Sets the color of the guild");
                }
                else MessageUtility.guildMessage(sender, "/guild leave - Leave the guild you are currently in");
            } else MessageUtility.guildMessage(sender, "/guild leave - Leave the guild you are currently in");
        }
    }

    /**
     * Called whenever a player attempts to form a new guild
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param guildName String name of the new guild
     */
    private void formGuild(ICommandSender sender, UUID player, Guild guild, String guildName)
    {
        if(guild != null) MessageUtility.guildMessage(sender, "You are already in a guild!");
        else
        {
            if(guildName.length() > GeneralConfig.maxCharLength) MessageUtility.guildMessage(sender, "Guild name is too long!");
            else if(GuildCache.addGuild(guildName, player))
            {
                GuildCache.save();
                MessageUtility.guildMessage(sender, "Successfully created guild: " + guildName + "!");
            }
            else MessageUtility.guildMessage(sender, "A guild with that name has already been formed!");
        }
    }

    /**
     * Called whenever a player attempts to join a guild
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param guildName String name of the new guild
     */
    private void joinGuild(MinecraftServer server, ICommandSender sender, UUID player, Guild guild, String guildName)
    {
        if(guild != null) MessageUtility.guildMessage(sender, "You are already in a guild!");
        else
        {
            Guild newGuild = GuildCache.getGuild(guildName);
            if(newGuild == null) MessageUtility.guildMessage(sender, "Guild does not exist!");
            else
            {
                if(newGuild.acceptInvite(player))
                {
                    GuildCache.save();
                    MessageUtility.guildMessage(sender, "Successfully joined " + guildName + "!");

                    //Notify members
                    PlayerList playerList = server.getPlayerList();
                    String playerName = playerList.getPlayerByUUID(player).getDisplayNameString();
                    for(UUID playerID : newGuild.getAllMembers()) if(!playerID.equals(player)) MessageUtility.guildMessage(playerList.getPlayerByUUID(playerID), playerName + " has joined the guild!");
                }
                else MessageUtility.guildMessage(sender, "You have not received an invitation from " + guildName + "!");
            }
        }
    }

    /**
     * Called whenever a player attempts to disband their guild
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     */
    private void disbandGuild(MinecraftServer server, ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.getGuildMaster().equals(player)) MessageUtility.guildMessage(sender, "Only the Guild Master may disband the guild!");
        else if(RaidCache.getRaid(guild.getGuildName()) != null) MessageUtility.guildMessage(sender, "You cannot disband a guild during a raid!");
        else
        {
            if(guild.getAlliance() != null) AllianceCache.leaveAlliance(guild);
            List<UUID> members = guild.getAllMembers();
            GuildCache.removeGuild(guild);
            GuildCache.save();
            MessageUtility.guildMessage(sender, "Successfully disbanded " + guild.getGuildName() + "!");

            //Notify members
            PlayerList playerList = server.getPlayerList();
            for(UUID playerID : members) if(!playerID.equals(player)) MessageUtility.guildMessage(playerList.getPlayerByUUID(playerID), "Your guild has been disbanded!");

        }
    }

    /**
     * Called whenever a player attempts to invite a player to their guild
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param playerName String name of the target player
     */
    private void invite(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null)MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) MessageUtility.guildMessage(sender, "You are not an admin!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) MessageUtility.guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID invitee = entityPlayer.getUniqueID();
                if(GuildCache.getPlayerGuild(invitee) != null) MessageUtility.guildMessage(sender, playerName + " is already in a guild!");
                else
                {
                    guild.addInvitee(invitee);
                    GuildCache.save();
                    MessageUtility.guildMessage(sender, "Successfully invited " + playerName + "!");
                    MessageUtility.guildMessage(entityPlayer, "You have been invited to join " + guild.getGuildName() + "!");
                }
            }

        }
    }

    /**
     * Called whenever a player attempts to leave their guild
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     */
    private void leaveGuild(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(guild.getGuildMaster().equals(player)) MessageUtility.guildMessage(sender, "A Guild Master cannot leave, only disband!");
        else if(RaidCache.getRaid(guild.getGuildName()) != null) MessageUtility.guildMessage(sender, "You cannot leave a guild during a raid!");
        else
        {
            guild.removeMember(player);
            GuildCache.save();
            MessageUtility.guildMessage(sender, "Successfully left " + guild.getGuildName() + "!");
        }
    }

    /**
     * Called whenever a player attempts to get a list of their guild members
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     */
    private void listMembers(MinecraftServer server, ICommandSender sender, Guild guild)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else
        {
            MessageUtility.guildMessage(sender, "*Guild Master: " + server.getPlayerList().getPlayerByUUID(guild.getGuildMaster()).getDisplayNameString());
            MessageUtility.guildMessage(sender, "*Admins:");
            for(String player : guild.getAdmins(server))
            {
                MessageUtility.guildMessage(sender, " - " + player);
            }

            MessageUtility.guildMessage(sender, "*Members:");
            for(String player : guild.getMembers(server))
            {
                MessageUtility.guildMessage(sender, " - " + player);
            }
        }
    }

    /**
     * Called whenever a player attempts to claim land
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     */
    private void claim(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) MessageUtility.guildMessage(sender, "You do not have permission to claim land!");
        else
        {
            if(sender.getEntityWorld().provider.getDimension() != 0) MessageUtility.guildMessage(sender, "You can only claim land in the over world!");
            else
            {
                ChunkPos chunkPos = new ChunkPos(sender.getPosition());
                String owner = ChunkCache.getChunkOwner(chunkPos);
                if(owner != null) MessageUtility.guildMessage(sender, "This chunk is already claimed by " + owner + "!");
                else if(guild.hasMaxClaim()) MessageUtility.guildMessage(sender, "Your guild has reached its max claim limit!");
                else if(!ChunkCache.isConnected(chunkPos, guild)) MessageUtility.guildMessage(sender, "You cannot claim this chunk because it is not adjacent to your existing territory!");
                else
                {
                    ChunkCache.setChunkOwner(chunkPos, guild.getGuildName());
                    guild.incrementTerritoryCount();
                    GuildCache.save();
                    ChunkCache.save();
                    MessageUtility.guildMessage(sender, "Chunk successfully claimed!");
                }
            }
        }
    }

    /**
     * Called whenever a player attempts to abandon claimed land
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     */
    private void abandon(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) MessageUtility.guildMessage(sender, "You do not have permission to abandon land!");
        else
        {
            ChunkPos chunkPos = new ChunkPos(sender.getPosition());
            String owner = ChunkCache.getChunkOwner(chunkPos);
            if(owner == null) MessageUtility.guildMessage(sender, "This chunk is not claimed!");
            else if(!owner.equals(guild.getGuildName())) MessageUtility.guildMessage(sender, "This chunk belongs to " + owner + "!");
            else
            {
                ChunkCache.removeChunkOwner(chunkPos);
                guild.decrementTerritoryCount();
                GuildCache.save();
                ChunkCache.save();
                MessageUtility.guildMessage(sender, "Chunk successfully abandoned!");
            }
        }
    }

    /**
     * Called whenever a player attempts to kick another player from their guild
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param playerName String name of the target player
     */
    private void kick(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) MessageUtility.guildMessage(sender, "You do not have permission to kick a member!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) MessageUtility.guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                if(member.equals(player)) MessageUtility.guildMessage(sender, "You cannot kick yourself, that would hurt!");
                else
                {
                    Guild memberGuild = GuildCache.getPlayerGuild(member);
                    if(memberGuild == null) MessageUtility.guildMessage(sender, playerName + " is not in a guild!");
                    else if(!memberGuild.equals(guild)) MessageUtility.guildMessage(sender, playerName + " is not in your guild!");
                    else
                    {
                        guild.removeMember(member);
                        GuildCache.save();
                        MessageUtility.guildMessage(sender, "Successfully kicked " + playerName + "!");
                        MessageUtility.guildMessage(entityPlayer, "You have been kicked out of your guild!");
                    }
                }
            }
        }
    }

    /**
     * Called whenever a player attempts to promote another player to guild admin
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param playerName String name of the target player
     */
    private void promote(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) MessageUtility.guildMessage(sender, "You do not have permission to promote a member!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) MessageUtility.guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) MessageUtility.guildMessage(sender, playerName + " is not in a guild!");
                else if(!memberGuild.equals(guild)) MessageUtility.guildMessage(sender, playerName + " is not in your guild!");
                else if(guild.isAdmin(member)) MessageUtility.guildMessage(sender, playerName + " is already an admin!");
                else
                {
                    guild.promote(member);
                    GuildCache.save();
                    MessageUtility.guildMessage(sender, "Successfully promoted " + playerName + "!");
                }
            }
        }
    }

    /**
     * Called whenever a player attempts to demote another player from guild admin
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param playerName String name of the target player
     */
    private void demote(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) MessageUtility.guildMessage(sender, "You do not have permission to demote a member!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) MessageUtility.guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) MessageUtility.guildMessage(sender, playerName + " is not in a guild!");
                else if(!memberGuild.equals(guild)) MessageUtility.guildMessage(sender, playerName + " is not in your guild!");
                else if(guild.getGuildMaster().equals(member)) MessageUtility.guildMessage(sender, "You cannot demote the guild master!");
                else if(!guild.isAdmin(member)) MessageUtility.guildMessage(sender, playerName + " is already a regular member!");
                else
                {
                    guild.demote(member);
                    GuildCache.save();
                    MessageUtility.guildMessage(sender, "Successfully demoted " + playerName + "!");
                }
            }
        }
    }

    /**
     * Called whenever a player attempts to transfer ownership of their guild
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param playerName String name of the target player
     */
    private void transfer(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.getGuildMaster().equals(player)) MessageUtility.guildMessage(sender, "You are not the Guild Master!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) MessageUtility.guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                if(member.equals(player)) MessageUtility.guildMessage(sender, "You are already the Guild Master!");
                else
                {
                    Guild memberGuild = GuildCache.getPlayerGuild(member);
                    if(memberGuild == null) MessageUtility.guildMessage(sender, playerName + " is not in a guild!");
                    else if(!memberGuild.equals(guild)) MessageUtility.guildMessage(sender, playerName + " is not in your guild!");
                    else
                    {
                        guild.transferOwnership(member);
                        GuildCache.save();
                        MessageUtility.guildMessage(sender, "Successfully transferred ownership to " + playerName + " !");
                    }
                }
            }
        }
    }

    /**
     * Called whenever a player attempts to set their guild color
     * @param sender ICommandSender reference to the player
     * @param player UUID of player
     * @param guild Guild object reference to the player's guild
     * @param color String name of the new color
     */
    private void setColor(ICommandSender sender, UUID player, Guild guild, String color)
    {
        if(guild == null) MessageUtility.guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.getGuildMaster().equals(player)) MessageUtility.guildMessage(sender, "Only the Guild Master may do this!");
        else
        {
            TextFormatting textFormatting = TextFormatting.getValueByName(color);
            if(textFormatting == null) MessageUtility.guildMessage(sender, "That is not a valid color name!");
            else
            {
                guild.setColor(textFormatting);
                MessageUtility.guildMessage(sender, "Successfully set color to " + color + "!");
            }
        }
    }
}
