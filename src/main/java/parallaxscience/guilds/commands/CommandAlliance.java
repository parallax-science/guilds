package parallaxscience.guilds.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import parallaxscience.guilds.Guilds;
import parallaxscience.guilds.alliance.Alliance;
import parallaxscience.guilds.alliance.AllianceCache;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;

import java.util.UUID;

public class CommandAlliance extends CommandBase {

    /**
     * String array of sub-commands uses by the auto tab-completion
     */
    public static final String[] commands = new String[]{
            //For all guild masters:
            "help",
            //Not in alliance:
            "form",
            "accept",
            //Alliance members:
            "leave",
            "invite",
    };

    /**
     * Gets the name of the command
     */
    @Override
    public String getName() {
        return "alliance";
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender
     */
    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/alliance <action> [arguments]";
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Entity entity = sender.getCommandSenderEntity();
        if(entity == null) return;
        UUID player = entity.getUniqueID();

        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) sender.sendMessage(new TextComponentString("You are not currently in a guild!"));
        else if(!guild.getGuildMaster().equals(player)) sender.sendMessage(new TextComponentString("Only guild masters can use alliance commands!"));
        else if(args.length == 0)
        {
            sender.sendMessage(new TextComponentString("Type \"/alliance help\" for help"));
        }
        else
        {
            switch (args[0].toLowerCase())
            {
                case "help":
                    displayHelp(sender, guild);
                    break;
                case "form":
                    formAlliance(sender, guild, args[1]);
                    break;
                case "accept":
                    acceptInvitation(sender, guild, args[1]);
                    break;
                case "leave":
                    leaveAlliance(sender, guild);
                    break;
                case "invite":
                    inviteGuild(sender, guild, args[1]);
                    break;
                default:
                    sender.sendMessage(new TextComponentString("Invalid command! Type /guild help for valid commands!"));
                    break;
            }
        }
    }

    private void displayHelp(ICommandSender sender, Guild guild)
    {
        sender.sendMessage(new TextComponentString("/alliance help - Lists all available commands"));
        if(guild.getAlliance() == null)
        {
            sender.sendMessage(new TextComponentString("/alliance form <alliance> - Creates a new alliance"));
            sender.sendMessage(new TextComponentString("/alliance accept <alliance> - Accept invite to join alliance"));
        }
        else
        {
            sender.sendMessage(new TextComponentString("/alliance leave - Remove your guild from the alliance"));
            sender.sendMessage(new TextComponentString("/alliance invite <guild> - Invites a guild to your alliance"));
        }
    }

    private void formAlliance(ICommandSender sender, Guild guild, String alliance)
    {
        if(guild.getAlliance() != null) sender.sendMessage(new TextComponentString("Your guild is already part of an alliance!"));
        else if(AllianceCache.getAlliance(alliance) != null) sender.sendMessage(new TextComponentString("Alliance " + alliance + " already exists!"));
        else if(alliance.length() > Guilds.maxCharLength) sender.sendMessage(new TextComponentString("Alliance name is too long!"));
        else
        {
            guild.setAlliance(alliance);
            AllianceCache.createAlliance(alliance, guild.getGuildName());
            GuildCache.save();
            AllianceCache.save();
            sender.sendMessage(new TextComponentString("New alliance: " + alliance + " has been formed!"));
        }
    }

    private void acceptInvitation(ICommandSender sender, Guild guild, String allianceName)
    {
        if(guild.getAlliance() != null) sender.sendMessage(new TextComponentString("Your guild is already part of an alliance!"));
        else
        {
            Alliance alliance = AllianceCache.getAlliance(allianceName);
            if(alliance == null) sender.sendMessage(new TextComponentString("That alliance does not exist!"));
            else if(!alliance.acceptInvite(guild.getGuildName())) sender.sendMessage(new TextComponentString("Your guild has not been invited to " + allianceName));
            else
            {
                guild.setAlliance(allianceName);
                GuildCache.save();
                AllianceCache.save();
                sender.sendMessage(new TextComponentString("Successfully joined " + alliance + "!"));
            }
        }
    }

    private void leaveAlliance(ICommandSender sender, Guild guild)
    {
        String allianceName = guild.getAlliance();
        if(allianceName == null) sender.sendMessage(new TextComponentString("Your guild is not part of an alliance!"));
        else
        {
            AllianceCache.leavelAlliance(guild);
            GuildCache.save();
            AllianceCache.save();
            sender.sendMessage(new TextComponentString("Successfully left alliance!"));
        }
    }

    private void inviteGuild(ICommandSender sender, Guild guild, String invitee)
    {
        String allianceName = guild.getAlliance();
        if(allianceName == null) sender.sendMessage(new TextComponentString("Your guild is not part of an alliance!"));
        else
        {
            Guild inviteeGuild = GuildCache.getGuild(invitee);
            if(inviteeGuild == null) sender.sendMessage(new TextComponentString("That guild does not exist!"));
            else if(inviteeGuild.getAlliance() != null) sender.sendMessage(new TextComponentString("Invitee guild is already part of an alliance!"));
            else
            {
                AllianceCache.getAlliance(allianceName).addInvitee(invitee);
                AllianceCache.save();
                sender.sendMessage(new TextComponentString("Successfully invited " + invitee + " to alliance!"));
                sender.getEntityWorld().getPlayerEntityByUUID(inviteeGuild.getGuildMaster()).sendMessage(new TextComponentString("Your guild has been invited to join " + allianceName + "!"));
            }
        }
    }
}
