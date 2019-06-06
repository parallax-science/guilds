package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import parallaxscience.guilds.alliance.AllianceCache;
import parallaxscience.guilds.config.GeneralConfig;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.raid.RaidCache;

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
            "transfer"
    };


    @Override
    public String getName()
    {
        return "guild";
    }


    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/guild <action> [arguments]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
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

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0)
        {
            sender.sendMessage(new TextComponentString("Type \"/guild help\" for help"));
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
                {
                    if(args.length == 2) formGuild(sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                } break;
                case "accept":
                {
                    if(args.length == 2) joinGuild(sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                } break;
                case "leave":
                {
                    leaveGuild(sender, player, guild);
                } break;
                case "members":
                {
                    listMembers(sender, guild);
                } break;
                case "invite":
                {
                    if(args.length == 2) invite(sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                } break;
                case "claim":
                {
                    claim(sender, player, guild);
                } break;
                case "abandon":
                {
                    abandon(sender, player, guild);
                } break;
                case "kick":
                {
                    if(args.length == 2)
                    {
                        kick(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
                case "disband":
                {
                    disbandGuild(sender, player, guild);
                } break;
                case "promote":
                {
                    if(args.length == 2)
                    {
                        promote(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
                case "demote":
                {
                    if(args.length == 2)
                    {
                        demote(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;
                case "transfer":
                {
                    if(args.length == 2)
                    {
                        transfer(sender, player, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                } break;

                default: sender.sendMessage(new TextComponentString("Invalid command! Type /guild help for valid commands!"));
                    break;
            }
        }
    }


    private void notEnoughArguments(ICommandSender sender) { sender.sendMessage(new TextComponentString("Error: Not enough arguments!")); }


    private void displayHelp(ICommandSender sender, UUID player, Guild guild)
    {
        sender.sendMessage(new TextComponentString("/guild help - Lists all available commands"));
        if(guild == null)
        {
            sender.sendMessage(new TextComponentString("/guild form <guild> - Creates a new guild"));
            sender.sendMessage(new TextComponentString("/guild accept <guild> - Accept invite to join guild"));
        }
        else
        {
            sender.sendMessage(new TextComponentString("/guild members - Lists all guild members and ranks"));
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
            } else sender.sendMessage(new TextComponentString("/guild leave - Leave the guild you are currently in"));
        }
    }


    private void formGuild(ICommandSender sender, UUID player, Guild guild, String guildName)
    {
        if(guild != null) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {
            if(guildName.length() > GeneralConfig.maxCharLength) sender.sendMessage(new TextComponentString("Guild name is too long!"));
            else if(GuildCache.addGuild(guildName, player))
            {
                GuildCache.save();
                sender.sendMessage(new TextComponentString("Successfully created guild: " + guildName + "!"));
            }
            else sender.sendMessage(new TextComponentString("A guild with that name has already been formed!"));
        }
    }


    private void joinGuild(ICommandSender sender, UUID player, Guild guild, String guildName)
    {
        if(guild != null) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {
            Guild newGuild = GuildCache.getGuild(guildName);
            if(newGuild == null) sender.sendMessage(new TextComponentString("Guild does not exist!"));
            else
            {
                if(newGuild.acceptInvite(player))
                {
                    GuildCache.save();
                    sender.sendMessage(new TextComponentString("Successfully joined " + guildName + "!"));
                }
                else sender.sendMessage(new TextComponentString("You have not received an invitation from " + guildName + "!"));
            }
        }
    }


    private void disbandGuild(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("Only the Guild Master may disband the guild!"));
        else if(RaidCache.getRaid(guild.getGuildName()) != null) sender.sendMessage(new TextComponentString("You cannot disband a guild during a raid!"));
        else
        {
            if(guild.getAlliance() != null) AllianceCache.leaveAlliance(guild);
            GuildCache.removeGuild(guild);
            GuildCache.save();
            sender.sendMessage(new TextComponentString("Successfully disbanded " + guild.getGuildName() + "!"));
        }
    }


    private void invite(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null)sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You are not an admin!"));
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                UUID invitee = entityPlayer.getUniqueID();
                if(GuildCache.getPlayerGuild(invitee) != null) sender.sendMessage(new TextComponentString(playerName + " is already in a guild!"));
                else
                {
                    guild.addInvitee(invitee);
                    GuildCache.save();
                    sender.sendMessage(new TextComponentString("Successfully invited " + playerName + "!"));
                    entityPlayer.sendMessage(new TextComponentString("You have been invited to join " + guild.getGuildName() + "!"));
                }
            }

        }
    }


    private void leaveGuild(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("A Guild Master cannot leave, only disband!"));
        else if(RaidCache.getRaid(guild.getGuildName()) != null) sender.sendMessage(new TextComponentString("You cannot leave a guild during a raid!"));
        else
        {
            guild.removeMember(player);
            GuildCache.save();
            sender.sendMessage(new TextComponentString("Successfully left " + guild.getGuildName() + "!"));
        }
    }

    private void listMembers(ICommandSender sender, Guild guild)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else
        {
            PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
            sender.sendMessage(new TextComponentString("*Guild Master: " + guild.getGuildMaster()));
            sender.sendMessage(new TextComponentString("*Admins:"));
            for(UUID player : guild.getAdmins())
            {
                sender.sendMessage(new TextComponentString(" - " + players.getPlayerByUUID(player)));
            }

            sender.sendMessage(new TextComponentString("*Members:"));
            for(UUID player : guild.getMembers())
            {
                sender.sendMessage(new TextComponentString(" - " + players.getPlayerByUUID(player)));
            }
        }
    }


    private void claim(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to claim land!"));
        else
        {
            if(sender.getEntityWorld().provider.getDimension() != 0) sender.sendMessage(new TextComponentString("You can only claim land in the over world!"));
            else
            {
                ChunkPos chunkPos = new ChunkPos(sender.getPosition());
                String owner = ChunkCache.getChunkOwner(chunkPos);
                if(owner != null) sender.sendMessage(new TextComponentString("This chunk is already claimed by " + owner + "!"));
                else if(guild.hasMaxClaim()) sender.sendMessage(new TextComponentString("Your guild has reached its max claim limit!"));
                else if(!ChunkCache.isConnected(chunkPos, guild)) sender.sendMessage(new TextComponentString("You cannot claim this chunk because it is not adjacent to your existing territory!"));
                else
                {
                    ChunkCache.setChunkOwner(chunkPos, guild.getGuildName());
                    guild.incrementTerritoryCount();
                    GuildCache.save();
                    ChunkCache.save();
                    sender.sendMessage(new TextComponentString("Chunk successfully claimed!"));
                }
            }
        }
    }


    private void abandon(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to abandon land!"));
        else
        {
            ChunkPos chunkPos = new ChunkPos(sender.getPosition());
            String owner = ChunkCache.getChunkOwner(chunkPos);
            if(owner == null) sender.sendMessage(new TextComponentString("This chunk is not claimed!"));
            else if(!owner.equals(guild.getGuildName())) sender.sendMessage(new TextComponentString("This chunk belongs to " + owner + "!"));
            else
            {
                ChunkCache.removeChunkOwner(chunkPos);
                guild.decrementTerritoryCount();
                GuildCache.save();
                ChunkCache.save();
                sender.sendMessage(new TextComponentString("Chunk successfully abandoned!"));
            }
        }
    }


    private void kick(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to kick a member!"));
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(!memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else
                {
                    guild.removeMember(member);
                    GuildCache.save();
                    sender.sendMessage(new TextComponentString("Successfully kicked " + playerName + "!"));
                }
            }
        }
    }


    private void promote(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to promote a member!"));
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else if(guild.isAdmin(member)) sender.sendMessage(new TextComponentString(playerName + " is already an admin!"));
                else
                {
                    guild.promote(member);
                    GuildCache.save();
                    sender.sendMessage(new TextComponentString("Successfully promoted " + playerName + " !"));
                }
            }
        }
    }


    private void demote(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.isAdmin(player)) sender.sendMessage(new TextComponentString("You do not have permission to demote a member!"));
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else if(guild.isAdmin(member)) sender.sendMessage(new TextComponentString(playerName + " is already a regular member!"));
                else
                {
                    guild.demote(member);
                    GuildCache.save();
                    sender.sendMessage(new TextComponentString("Successfully demoted " + playerName + " !"));
                }
            }
        }
    }

    private void transfer(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently a part of a guild!"));
        else if(!guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("You are not the Guild Master!"));
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) sender.sendMessage(new TextComponentString("Player: " + playerName + " does not exist in this world!"));
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) sender.sendMessage(new TextComponentString(playerName + " is not in a guild!"));
                else if(!memberGuild.equals(guild)) sender.sendMessage(new TextComponentString(playerName + " is not in your guild!"));
                else
                {
                    guild.transferOwnership(member);
                    GuildCache.save();
                    sender.sendMessage(new TextComponentString("Successfully transferred ownership to " + playerName + " !"));
                }
            }
        }
    }
}
