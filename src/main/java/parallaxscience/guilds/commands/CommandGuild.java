package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import parallaxscience.guilds.ChunkCache;
import parallaxscience.guilds.GuildCache;
import parallaxscience.guilds.guild.Guild;
import java.util.List;
import java.util.UUID;

/**
 * Main guild command registration and handler class.
 * Registers the /guild command and all of its sub-commands, and handles its events.
 * @see net.minecraft.command.CommandBase
 * @author Tristan Jay
 */
public class CommandGuild extends CommandBase {

    /**
     * String array of sub-commands uses by the auto tab-completion
     */
    public static final String[] commands = new String[]{
            //For all:
            "help",
            //Not in guild:
            "form",
            "accept",
            //Guild members:
            "leave",
            //Guild admins:
            "invite",
            "claim",
            "abandon",
            "kick",
            //Guild Masters:
            "disband",
            "promote",
            "demote",
            "transfer"
    };

    /**
     *
     * @return
     */
    @Override
    public String getName()
    {
        return "guild";
    }

    /**
     *
     * @param sender
     * @return
     */
    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/guild <action> [arguments]";
    }

    /**
     *
     * @return
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 1;
    }

    /*
    /**
     *
     * @param server
     * @param sender
     * @param args
     * @param targetPos
     * @return

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        //Needs to be completed!
        return null;
    }
*/
    /**
     *
     * @param server
     * @param sender
     * @param args
     * @throws CommandException
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0)
        {
            sender.sendMessage(new TextComponentString("Type \"/guild help\" for help"));
        }
        else
        {
            switch (args[0].toLowerCase())
            {
                default: sender.sendMessage(new TextComponentString("Invalid command! Type /guild help for valid commands!"));
                    break;
                case "help": displayHelp(sender);
                    break;
                case "form":
                {
                    if(args.length == 2) formGuild(sender, args[1]);
                    else notEnoughArguments(sender);
                } break;
                case "accept":
                {
                    if(args.length == 2) joinGuild(sender, args[1]);
                    else notEnoughArguments(sender);
                } break;
                case "leave":
                {
                    leaveGuild(sender);
                } break;
                case "invite":
                {
                    if(args.length == 2) invite(sender, args[1]);
                    else notEnoughArguments(sender);
                } break;
                case "claim":
                {
                    claim(sender);
                } break;
                case "abandon":
                {
                    abandon(sender);
                } break;
                case "kick":
                {
                    if(args.length == 2)
                    {
                        kick(sender, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
                case "disband":
                {
                    disbandGuild(sender);
                } break;
                case "promote":
                {
                    if(args.length == 2)
                    {
                        promote(sender, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
                case "demote":
                {
                    if(args.length == 2)
                    {
                        demote(sender, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
                case "transfer":
                {
                    if(args.length == 2)
                    {
                        transfer(sender, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
            }
        }
    }

    /**
     *
     * @param sender
     */
    private void notEnoughArguments(ICommandSender sender) { sender.sendMessage(new TextComponentString("Error: Not enough arguments!")); }

    /**
     *
     * @param sender
     */
    private void displayHelp(ICommandSender sender)
    {
        sender.sendMessage(new TextComponentString("/guild help - Lists all available commands"));
        UUID player = sender.getCommandSenderEntity().getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null)
        {
            sender.sendMessage(new TextComponentString("/guild form <guild> - Creates a new guild"));
            sender.sendMessage(new TextComponentString("/guild accept <guild> - Accept invite to join guild"));
        }
        else
        {
            if(guild.isAdmin(player))
            {
                sender.sendMessage(new TextComponentString("/guild invite <player> - Invites a player to your guild"));
                sender.sendMessage(new TextComponentString("/guild claim - Claims a chunk as guild territory"));
                sender.sendMessage(new TextComponentString("/guild abandon - Removes a chunk from guild territory"));
                sender.sendMessage(new TextComponentString("/guild kick <player> - Kick player from guild"));
                if(guild.getGuildMaster().equals(player))
                {
                    sender.sendMessage(new TextComponentString("/guild disband - Disbands the guild you have created"));
                    sender.sendMessage(new TextComponentString("/guild promote <player> - Promotes a regular member to an admin"));
                    sender.sendMessage(new TextComponentString("/guild demote <player> - Demotes an admin to a regular member"));
                    sender.sendMessage(new TextComponentString("/guild transfer <player> - Transfers ownership of the guild to another"));
                }
                else sender.sendMessage(new TextComponentString("/guild leave - Leave the guild you are currently in"));
            }
        }
    }

    /**
     *
     * @param sender
     * @param guildName
     */
    private void formGuild(ICommandSender sender, String guildName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild != null) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {
            if(GuildCache.addGuild(guildName, player))
            {
                sender.sendMessage(new TextComponentString("Successfully created guild: " + guildName + "!"));
                GuildCache.save();
            }
            else sender.sendMessage(new TextComponentString("A guild with that name has already been formed!"));
        }
    }

    /**
     *
     * @param sender
     * @param guildName
     */
    private void joinGuild(ICommandSender sender, String guildName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild != null) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {
            guild.acceptInvite(player);
            sender.sendMessage(new TextComponentString("Successfully joined " + guildName + "!"));
            GuildCache.save();
        }
    }

    /**
     *
     * @param sender
     */
    private void disbandGuild(ICommandSender sender)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("Only the Guild Master may disband the guild!"));
        else
        {
            GuildCache.removeGuild(guild);
            sender.sendMessage(new TextComponentString("Successfully disbanded " + guild.getGuildName() + "!"));
            GuildCache.save();
        }
    }

    /**
     *
     * @param sender
     * @param playerName
     */
    private void invite(ICommandSender sender, String playerName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null)sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You are not an admin!"));
        else
        {
            UUID invitee = sender.getEntityWorld().getPlayerEntityByName(playerName).getUniqueID();
            if(invitee == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else if(GuildCache.getPlayerGuild(invitee) != null) sender.sendMessage(new TextComponentString(playerName + " is already in a guild!"));
            else
            {
                guild.addInvitee(invitee);
                sender.sendMessage(new TextComponentString("Successfully invited " + playerName + " !"));
                GuildCache.save();
            }
        }
    }

    /**
     *
     * @param sender
     */
    private void leaveGuild(ICommandSender sender)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        if(guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("A Guild Master cannot leave, only disband!"));
        else
        {
            guild.removeMember(player);
            sender.sendMessage(new TextComponentString("Successfully left " + guild.getGuildName() + "!"));
            GuildCache.save();
        }
    }

    /**
     *
     * @param sender
     */
    private void claim(ICommandSender sender)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to claim land!"));
        else
        {
            BlockPos blockPos = sender.getPosition();
            Guild owner = ChunkCache.getBlockOwner(blockPos);
            if(owner != null) sender.sendMessage(new TextComponentString("This chunk is already claimed by " + owner.getGuildName() + "!"));
            else if(guild.hasMaxClaim()) sender.sendMessage(new TextComponentString("Your guild has reached its max claim limit!"));
            else if(ChunkCache.isConnected(blockPos, guild)) sender.sendMessage(new TextComponentString("You cannot claim this chunk because it is not adjacent to your existing territory!"));
            else
            {
                ChunkCache.setChunkOwner(blockPos, guild);
                sender.sendMessage(new TextComponentString("Chunk successfully claimed!"));
                guild.incrementTerritoryCount();
                GuildCache.save();
                ChunkCache.save();
            }
        }
    }

    /**
     *
     * @param sender
     */
    private void abandon(ICommandSender sender)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to abandon land!"));
        else
        {
            BlockPos blockPos = sender.getPosition();
            Guild owner = ChunkCache.getBlockOwner(blockPos);
            if(owner == null) sender.sendMessage(new TextComponentString("This chunk is not claimed!"));
            else if(!owner.equals(guild)) sender.sendMessage(new TextComponentString("This chunk belongs to " + owner.getGuildName() + "!"));
            else
            {
                ChunkCache.removeChunkOwner(blockPos);
                sender.sendMessage(new TextComponentString("Chunk successfully abandoned!"));
                guild.decrementTerritoryCount();
                GuildCache.save();
                ChunkCache.save();
            }
        }
    }

    /**
     *
     * @param sender
     * @param playerName
     */
    private void kick(ICommandSender sender, String playerName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to kick a member!"));
        else
        {
            UUID member = sender.getEntityWorld().getPlayerEntityByName(playerName).getUniqueID();
            if(member == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else
                {
                    guild.removeMember(member);
                    sender.sendMessage(new TextComponentString("Successfully kicked " + playerName + " !"));
                    GuildCache.save();
                }
            }
        }
    }

    /**
     *
     * @param sender
     * @param playerName
     */
    private void promote(ICommandSender sender, String playerName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to promote a member!"));
        else
        {
            UUID member = sender.getEntityWorld().getPlayerEntityByName(playerName).getUniqueID();
            if(member == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else if(guild.isAdmin(member)) sender.sendMessage(new TextComponentString(playerName + " is already an admin!"));
                else
                {
                    guild.promote(member);
                    sender.sendMessage(new TextComponentString("Successfully promoted " + playerName + " !"));
                    GuildCache.save();
                }
            }
        }
    }

    /**
     *
     * @param sender
     * @param playerName
     */
    private void demote(ICommandSender sender, String playerName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to demote a member!"));
        else
        {
            UUID member = sender.getEntityWorld().getPlayerEntityByName(playerName).getUniqueID();
            if(member == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else if(guild.isAdmin(member)) sender.sendMessage(new TextComponentString(playerName + " is already a regular member!"));
                else
                {
                    guild.demote(member);
                    sender.sendMessage(new TextComponentString("Successfully demoted " + playerName + " !"));
                    GuildCache.save();
                }
            }
        }
    }

    /**
     *
     * @param sender
     * @param playerName
     */
    private void transfer(ICommandSender sender, String playerName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("You are not the Guild Master!"));
        else
        {
            UUID member = sender.getEntityWorld().getPlayerEntityByName(playerName).getUniqueID();
            if(member == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else
                {
                    guild.transferOwnership(member);
                    sender.sendMessage(new TextComponentString("Successfully transferred ownership to " + playerName + " !"));
                    GuildCache.save();
                }
            }
        }
    }
}
