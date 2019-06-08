package parallaxscience.guilds.commands;

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

/**
 * Class for handling alliance commands
 * @see CommandBase
 * @author Tristan Jay
 */
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

    /**
     * Variable for storing the alliance command color style
     * Called whenever an alliance command is used
     * @see Style
     */
    private static final Style style = new Style();

    /**
     * Constructor for the class
     * Only used to set the alliance style color
     */
    public CommandAlliance()
    {
        style.setColor(TextFormatting.BLUE);
    }

    /**
     * Returns the name of the command
     */
    @Override
    @Nonnull
    public String getName() {
        return "alliance";
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
        return "/alliance <action> [arguments]";
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
    @SuppressWarnings("unchecked")
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
            if(string.equals(item.substring(0, length))) matching.add(item);
        }
        return matching;
    }

    /**
     * Execution of the /alliance command
     * Called whenever the player attempts to use the command (presses Enter)
     * @param server MinecraftServer instance
     * @param sender ICommandSender reference to the player
     * @param args String array of arguments given from the player
     */
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

    /**
     * Sends a "Not enough arguments" message back to the player
     * @param sender ICommandSender reference to the player
     */
    private void notEnoughArguments(ICommandSender sender) { allianceMessage(sender, "Error: Not enough arguments!"); }

    /**
     * Sends a message to the player
     * Uses the alliance color style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     */
    private void allianceMessage(ICommandSender sender, String message)
    {
        ITextComponent textComponent = new TextComponentString(message);
        textComponent.setStyle(style);
        sender.sendMessage(textComponent);
    }

    /**
     * Displays all of the available sub-commands and their uses to the player
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     */
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

    /**
     * Called whenever a guild master attempts to form a new alliance
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     * @param alliance String name of the new alliance
     */
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

    /**
     * Called whenever a guild master attempts to join a new alliance
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     * @param allianceName String name of the new alliance
     */
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

    /**
     * Called whenever a guild master attempts to leave their current alliance
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     */
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

    /**
     * Called whenever a guild master attempts to invite a guild to their alliance
     * @param sender ICommandSender reference to the player
     * @param guild Guild object reference to the player's guild
     * @param invitee String name of the player to invite
     */
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
