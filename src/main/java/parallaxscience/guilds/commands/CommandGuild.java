package parallaxscience.guilds.commands;

import com.sun.istack.internal.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import parallaxscience.guilds.alliance.AllianceCache;
import parallaxscience.guilds.config.GeneralConfig;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.raid.RaidCache;
import scala.actors.threadpool.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
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

    private static final Style style = new Style();

    public CommandGuild()
    {
        style.setColor(TextFormatting.GREEN);
    }

    @Override
    @Nonnull
    public String getName()
    {
        return "guild";
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
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

    @Override
    @Nonnull
    @SuppressWarnings("unchecked")
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
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
                case "accept":
                    return getLastMatchingStrings(args, GuildCache.getGuildList());
                case "invite":
                    if(guild != null)
                    {
                        List<String> names = new ArrayList<>();
                        for(EntityPlayer entityPlayer : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
                        {
                            if(GuildCache.getPlayerGuild(entityPlayer.getUniqueID()) == null) names.add(entity.getDisplayName().getUnformattedText());
                        }
                        return getLastMatchingStrings(args, names);
                    }
                    break;
                case "kick":
                    if(guild != null) if(guild.isAdmin(player))
                    {
                        List<String> members = guild.getMembers();
                        members.addAll(guild.getAdmins());
                        return getLastMatchingStrings(args, members);
                    }
                    break;
                case "promote":
                    if(guild != null) if(guild.isAdmin(player)) return getLastMatchingStrings(args, guild.getMembers());
                    break;
                case "demote":
                    if(guild != null) if(guild.isAdmin(player)) return getLastMatchingStrings(args, guild.getAdmins());
                    break;
                case "transfer":
                    if(guild != null) if(guild.getGuildMaster().equals(player))
                    {
                        List<String> members = guild.getMembers();
                        members.addAll(guild.getAdmins());
                        return getLastMatchingStrings(args, members);
                    }
                    break;
                case "setcolor":
                    if(guild != null) if(guild.getGuildMaster().equals(player)) return getLastMatchingStrings(args, new ArrayList<>(TextFormatting.getValidValues(false, false)));
                    break;
            }
        }
        return new ArrayList<>();
    }

    private List<String> getLastMatchingStrings(String[] args, List<String> list)
    {
        List<String> matching = new ArrayList<>();
        String string = args[args.length - 1];
        int length = string.length();
        for(String item : list)
        {
            if(string.equals(item.substring(0, length))) matching.add(item);
        }
        return matching;
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0)
        {
            guildMessage(sender, "Type \"/guild help\" for help");
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
                    if(args.length == 2) joinGuild(sender, player, guild, args[1]);
                    else notEnoughArguments(sender);
                    break;
                case "leave":
                    leaveGuild(sender, player, guild);
                    break;
                case "members":
                    listMembers(sender, guild);
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
                    disbandGuild(sender, player, guild);
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
                default: guildMessage(sender, "Invalid command! Type /guild help for valid commands!");
                    break;
            }
        }
    }


    private void notEnoughArguments(ICommandSender sender) { guildMessage(sender, "Error: Not enough arguments!"); }

    private void guildMessage(ICommandSender sender, String message)
    {
        ITextComponent textComponent = new TextComponentString(message);
        textComponent.setStyle(style);
        sender.sendMessage(textComponent);
    }

    private void displayHelp(ICommandSender sender, UUID player, Guild guild)
    {
        guildMessage(sender, "/guild help - Lists all available commands");
        if(guild == null)
        {
            guildMessage(sender, "/guild form <guild> - Creates a new guild");
            guildMessage(sender, "/guild accept <guild> - Accept invite to join guild");
        }
        else
        {
            guildMessage(sender, "/guild members - Lists all guild members and ranks");
            if(guild.isAdmin(player))
            {
                guildMessage(sender, "/guild invite <player> - Invites a player to your guild");
                guildMessage(sender, "/guild claim - Claims a chunk as guild territory");
                guildMessage(sender, "/guild abandon - Removes a chunk from guild territory");
                guildMessage(sender, "/guild kick <player> - Kick player from guild");
                if(guild.getGuildMaster().equals(player))
                {
                    guildMessage(sender, "/guild disband - Disbands the guild you have created");
                    guildMessage(sender, "/guild promote <player> - Promotes a regular member to an admin");
                    guildMessage(sender, "/guild demote <player> - Demotes an admin to a regular member");
                    guildMessage(sender, "/guild transfer <player> - Transfers ownership of the guild to another");
                    guildMessage(sender, "/guild setcolor <color> - Sets the color of the guild");
                }
                else guildMessage(sender, "/guild leave - Leave the guild you are currently in");
            } else guildMessage(sender, "/guild leave - Leave the guild you are currently in");
        }
    }


    private void formGuild(ICommandSender sender, UUID player, Guild guild, String guildName)
    {
        if(guild != null) guildMessage(sender, "You are already in a guild!");
        else
        {
            if(guildName.length() > GeneralConfig.maxCharLength) guildMessage(sender, "Guild name is too long!");
            else if(GuildCache.addGuild(guildName, player))
            {
                GuildCache.save();
                guildMessage(sender, "Successfully created guild: " + guildName + "!");
            }
            else guildMessage(sender, "A guild with that name has already been formed!");
        }
    }


    private void joinGuild(ICommandSender sender, UUID player, Guild guild, String guildName)
    {
        if(guild != null) guildMessage(sender, "You are already in a guild!");
        else
        {
            Guild newGuild = GuildCache.getGuild(guildName);
            if(newGuild == null) guildMessage(sender, "Guild does not exist!");
            else
            {
                if(newGuild.acceptInvite(player))
                {
                    GuildCache.save();
                    guildMessage(sender, "Successfully joined " + guildName + "!");
                }
                else guildMessage(sender, "You have not received an invitation from " + guildName + "!");
            }
        }
    }


    private void disbandGuild(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.getGuildMaster().equals(player)) guildMessage(sender, "Only the Guild Master may disband the guild!");
        else if(RaidCache.getRaid(guild.getGuildName()) != null) guildMessage(sender, "You cannot disband a guild during a raid!");
        else
        {
            if(guild.getAlliance() != null) AllianceCache.leaveAlliance(guild);
            GuildCache.removeGuild(guild);
            GuildCache.save();
            guildMessage(sender, "Successfully disbanded " + guild.getGuildName() + "!");
        }
    }


    private void invite(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null)guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) guildMessage(sender, "You are not an admin!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID invitee = entityPlayer.getUniqueID();
                if(GuildCache.getPlayerGuild(invitee) != null) guildMessage(sender, playerName + " is already in a guild!");
                else
                {
                    guild.addInvitee(invitee);
                    GuildCache.save();
                    guildMessage(sender, "Successfully invited " + playerName + "!");
                    entityPlayer.sendMessage(new TextComponentString("You have been invited to join " + guild.getGuildName() + "!"));
                }
            }

        }
    }


    private void leaveGuild(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(guild.getGuildMaster().equals(player)) guildMessage(sender, "A Guild Master cannot leave, only disband!");
        else if(RaidCache.getRaid(guild.getGuildName()) != null) guildMessage(sender, "You cannot leave a guild during a raid!");
        else
        {
            guild.removeMember(player);
            GuildCache.save();
            guildMessage(sender, "Successfully left " + guild.getGuildName() + "!");
        }
    }

    private void listMembers(ICommandSender sender, Guild guild)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else
        {
            guildMessage(sender, "*Guild Master: " + guild.getGuildMaster());
            guildMessage(sender, "*Admins:");
            for(String player : guild.getAdmins())
            {
                guildMessage(sender, " - " + player);
            }

            guildMessage(sender, "*Members:");
            for(String player : guild.getMembers())
            {
                guildMessage(sender, " - " + player);
            }
        }
    }


    private void claim(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) guildMessage(sender, "You do not have permission to claim land!");
        else
        {
            if(sender.getEntityWorld().provider.getDimension() != 0) guildMessage(sender, "You can only claim land in the over world!");
            else
            {
                ChunkPos chunkPos = new ChunkPos(sender.getPosition());
                String owner = ChunkCache.getChunkOwner(chunkPos);
                if(owner != null) guildMessage(sender, "This chunk is already claimed by " + owner + "!");
                else if(guild.hasMaxClaim()) guildMessage(sender, "Your guild has reached its max claim limit!");
                else if(!ChunkCache.isConnected(chunkPos, guild)) guildMessage(sender, "You cannot claim this chunk because it is not adjacent to your existing territory!");
                else
                {
                    ChunkCache.setChunkOwner(chunkPos, guild.getGuildName());
                    guild.incrementTerritoryCount();
                    GuildCache.save();
                    ChunkCache.save();
                    guildMessage(sender, "Chunk successfully claimed!");
                }
            }
        }
    }


    private void abandon(ICommandSender sender, UUID player, Guild guild)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) guildMessage(sender, "You do not have permission to abandon land!");
        else
        {
            ChunkPos chunkPos = new ChunkPos(sender.getPosition());
            String owner = ChunkCache.getChunkOwner(chunkPos);
            if(owner == null) guildMessage(sender, "This chunk is not claimed!");
            else if(!owner.equals(guild.getGuildName())) guildMessage(sender, "This chunk belongs to " + owner + "!");
            else
            {
                ChunkCache.removeChunkOwner(chunkPos);
                guild.decrementTerritoryCount();
                GuildCache.save();
                ChunkCache.save();
                guildMessage(sender, "Chunk successfully abandoned!");
            }
        }
    }


    private void kick(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) guildMessage(sender, "You do not have permission to kick a member!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                if(member.equals(player)) guildMessage(sender, "You cannot kick yourself, that would hurt!");
                else
                {
                    Guild memberGuild = GuildCache.getPlayerGuild(member);
                    if(memberGuild == null) guildMessage(sender, playerName + " is not in a guild!");
                    else if(!memberGuild.equals(guild)) guildMessage(sender, playerName + " is not in your guild!");
                    else
                    {
                        guild.removeMember(member);
                        GuildCache.save();
                        guildMessage(sender, "Successfully kicked " + playerName + "!");
                    }
                }
            }
        }
    }


    private void promote(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) guildMessage(sender, "You do not have permission to promote a member!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) guildMessage(sender, playerName + " is not in a guild!");
                else if(memberGuild.equals(guild)) guildMessage(sender, playerName + " is not in your guild!");
                else if(guild.isAdmin(member)) guildMessage(sender, playerName + " is already an admin!");
                else
                {
                    guild.promote(member);
                    GuildCache.save();
                    guildMessage(sender, "Successfully promoted " + playerName + " !");
                }
            }
        }
    }


    private void demote(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.isAdmin(player)) guildMessage(sender, "You do not have permission to demote a member!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                Guild memberGuild = GuildCache.getPlayerGuild(member);
                if(memberGuild == null) guildMessage(sender, playerName + " is not in a guild!");
                else if(memberGuild.equals(guild)) guildMessage(sender, playerName + " is not in your guild!");
                else if(guild.isAdmin(member)) guildMessage(sender, playerName + " is already a regular member!");
                else
                {
                    guild.demote(member);
                    GuildCache.save();
                    guildMessage(sender, "Successfully demoted " + playerName + " !");
                }
            }
        }
    }

    private void transfer(ICommandSender sender, UUID player, Guild guild, String playerName)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.getGuildMaster().equals(player)) guildMessage(sender, "You are not the Guild Master!");
        else
        {
            EntityPlayer entityPlayer = sender.getEntityWorld().getPlayerEntityByName(playerName);
            if(entityPlayer == null) guildMessage(sender, "Player: " + playerName + " does not exist in this world!");
            else
            {
                UUID member = entityPlayer.getUniqueID();
                if(member.equals(player)) guildMessage(sender, "You are already the Guild Master!");
                else
                {
                    Guild memberGuild = GuildCache.getPlayerGuild(member);
                    if(memberGuild == null) guildMessage(sender, playerName + " is not in a guild!");
                    else if(!memberGuild.equals(guild)) guildMessage(sender, playerName + " is not in your guild!");
                    else
                    {
                        guild.transferOwnership(member);
                        GuildCache.save();
                        guildMessage(sender, "Successfully transferred ownership to " + playerName + " !");
                    }
                }
            }
        }
    }

    private void setColor(ICommandSender sender, UUID player, Guild guild, String color)
    {
        if(guild == null) guildMessage(sender, "You are not currently a part of a guild!");
        else if(!guild.getGuildMaster().equals(player)) guildMessage(sender, "Only the Guild Master may do this!");
        else
        {
            TextFormatting textFormatting = TextFormatting.getValueByName(color);
            if(textFormatting == null) guildMessage(sender, "That is not a valid color name!");
            else
            {
                guild.setColor(textFormatting);
                guildMessage(sender, "Successfully set color to " + color + "!");
            }
        }
    }
}
