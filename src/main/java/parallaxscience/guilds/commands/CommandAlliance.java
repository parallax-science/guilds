package parallaxscience.guilds.commands;

import com.sun.istack.internal.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import parallaxscience.guilds.alliance.Alliance;
import parallaxscience.guilds.alliance.AllianceCache;
import parallaxscience.guilds.config.GeneralConfig;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;
import scala.actors.threadpool.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandAlliance extends CommandBase {

    /**
     * String array of sub-commands uses by the auto tab-completion
     */
    private static final String[] commands = new String[]{
            //For all guild masters:
            "help",
            //Not in alliance:
            "form",
            "accept",
            //Alliance members:
            "leave",
            "invite",
    };

    private static final Style style = new Style();

    public CommandAlliance()
    {
        style.setColor(TextFormatting.BLUE);
    }

    /**
     * Gets the name of the command
     */
    @Override
    @Nonnull
    public String getName() {
        return "alliance";
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
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
                    if(guild != null) if(guild.getGuildMaster().equals(player)) return getLastMatchingStrings(args, AllianceCache.getAllianceList());
                    break;
                case "invite":
                    if(guild != null) if(guild.getGuildMaster().equals(player)) return getLastMatchingStrings(args, GuildCache.getFreeGuilds());
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
    {
        Entity entity = sender.getCommandSenderEntity();
        if(entity == null) return;
        UUID player = entity.getUniqueID();

        Guild guild = GuildCache.getPlayerGuild(player);
        if(guild == null) allianceMessage(sender, "You are not currently in a guild!");
        else if(!guild.getGuildMaster().equals(player)) allianceMessage(sender, "Only guild masters can use alliance commands!");
        else if(args.length == 0)
        {
            allianceMessage(sender, "Type \"/alliance help\" for help");
        }
        else
        {
            switch (args[0].toLowerCase())
            {
                case "help":
                    displayHelp(sender, guild);
                    break;
                case "form":
                    if(args.length == 2)
                    {
                        formAlliance(sender, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "accept":
                    if(args.length == 2)
                    {
                        acceptInvitation(sender, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                case "leave":
                    leaveAlliance(sender, guild);
                    break;
                case "invite":
                    if(args.length == 2)
                    {
                        inviteGuild(sender, guild, args[1]);
                    }
                    else notEnoughArguments(sender);
                    break;
                default:
                    allianceMessage(sender, "Invalid command! Type /guild help for valid commands!");
                    break;
            }
        }
    }

    private void notEnoughArguments(ICommandSender sender) { allianceMessage(sender, "Error: Not enough arguments!"); }

    private void allianceMessage(ICommandSender sender, String message)
    {
        ITextComponent textComponent = new TextComponentString(message);
        textComponent.setStyle(style);
        sender.sendMessage(textComponent);
    }

    private void displayHelp(ICommandSender sender, Guild guild)
    {
        allianceMessage(sender, "/alliance help - Lists all available commands");
        if(guild.getAlliance() == null)
        {
            allianceMessage(sender, "/alliance form <alliance> - Creates a new alliance");
            allianceMessage(sender, "/alliance accept <alliance> - Accept invite to join alliance");
        }
        else
        {
            allianceMessage(sender, "/alliance leave - Remove your guild from the alliance");
            allianceMessage(sender, "/alliance invite <guild> - Invites a guild to your alliance");
        }
    }

    private void formAlliance(ICommandSender sender, Guild guild, String alliance)
    {
        if(guild.getAlliance() != null) allianceMessage(sender, "Your guild is already part of an alliance!");
        else if(AllianceCache.getAlliance(alliance) != null) allianceMessage(sender, "Alliance " + alliance + " already exists!");
        else if(alliance.length() > GeneralConfig.maxCharLength) allianceMessage(sender, "Alliance name is too long!");
        else
        {
            guild.setAlliance(alliance);
            AllianceCache.createAlliance(alliance, guild.getGuildName());
            GuildCache.save();
            AllianceCache.save();
            allianceMessage(sender, "New alliance: " + alliance + " has been formed!");
        }
    }

    private void acceptInvitation(ICommandSender sender, Guild guild, String allianceName)
    {
        if(guild.getAlliance() != null) allianceMessage(sender, "Your guild is already part of an alliance!");
        else
        {
            Alliance alliance = AllianceCache.getAlliance(allianceName);
            if(alliance == null) allianceMessage(sender, "That alliance does not exist!");
            else if(!alliance.acceptInvite(guild.getGuildName())) allianceMessage(sender, "Your guild has not been invited to " + allianceName);
            else
            {
                guild.setAlliance(allianceName);
                GuildCache.save();
                AllianceCache.save();
                allianceMessage(sender, "Successfully joined " + alliance + "!");
            }
        }
    }

    private void leaveAlliance(ICommandSender sender, Guild guild)
    {
        String allianceName = guild.getAlliance();
        if(allianceName == null) allianceMessage(sender, "Your guild is not part of an alliance!");
        else
        {
            AllianceCache.leaveAlliance(guild);
            GuildCache.save();
            AllianceCache.save();
            allianceMessage(sender, "Successfully left alliance!");
        }
    }

    private void inviteGuild(ICommandSender sender, Guild guild, String invitee)
    {
        String allianceName = guild.getAlliance();
        if(allianceName == null) allianceMessage(sender, "Your guild is not part of an alliance!");
        else
        {
            Guild inviteeGuild = GuildCache.getGuild(invitee);
            if(inviteeGuild == null) allianceMessage(sender, "That guild does not exist!");
            else if(inviteeGuild.getAlliance() != null) allianceMessage(sender, "Invitee guild is already part of an alliance!");
            else
            {
                AllianceCache.getAlliance(allianceName).addInvitee(invitee);
                AllianceCache.save();
                allianceMessage(sender, "Successfully invited " + invitee + " to alliance!");
                EntityPlayer guildMaster = sender.getEntityWorld().getPlayerEntityByUUID(inviteeGuild.getGuildMaster());
                if(guildMaster != null) guildMaster.sendMessage(new TextComponentString("Your guild has been invited to join " + allianceName + "!"));
            }
        }
    }
}
