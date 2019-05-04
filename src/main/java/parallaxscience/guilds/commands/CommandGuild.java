package parallaxscience.guilds.commands;

import com.sun.istack.internal.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import parallaxscience.guilds.GuildCache;

import java.util.List;
import java.util.UUID;

public class CommandGuild extends CommandBase {

    public static final String[] commands = new String[]{
            "help",
            "form",
            "disband",
            "invite",
            "claim",
            "promote",
            "leave",
            "transfer",
            "join"
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
        return 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0)
        {
            sender.sendMessage(new TextComponentString("Type \"/guilds help\" for help"));
            return;
        }
    }

    private void displayHelp(ICommandSender sender)
    {
        sender.sendMessage(new TextComponentString("/"));
    }

    public void formGuild(ICommandSender sender, String guildName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        if(GuildCache.isInGuild(player)) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {
            if(GuildCache.formGuild(guildName, player)) sender.sendMessage(new TextComponentString("Successfully created guild: " + guildName + "!"));
            else sender.sendMessage(new TextComponentString("A guild with that name has already been formed!"));
        }
    }

    public void joinGuild(ICommandSender sender, String guildName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        if(GuildCache.isInGuild(player)) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {
            GuildCache.addMember(guildName, player);
            sender.sendMessage(new TextComponentString("Successfully joined " + guildName + "!"));
        }
    }

    public void disbandGuild(ICommandSender sender, String guildName)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        if(GuildCache.isInGuild(player)) sender.sendMessage(new TextComponentString("You are already in a guild!"));
        else
        {

            if(GuildCache.removeGuild(player, guildName)) sender.sendMessage(new TextComponentString("Successfully disbanded " + guildName + "!"));
            else sender.sendMessage(new TextComponentString("Only the Guild Master may disband the guild!"));
        }
    }

    public void invite(ICommandSender sender, String invitee_string)
    {
        UUID player = (sender.getCommandSenderEntity()).getUniqueID();
        UUID invitee = sender.getEntityWorld().getPlayerEntityByName(invitee_string).getUniqueID();
        if(invitee == null) sender.sendMessage(new TextComponentString("Player: " + invitee_string + " does not exist!"));
        else if(GuildCache.isInGuild(invitee)) sender.sendMessage(new TextComponentString(invitee_string + " is already in a guild!"));
        else
        {
            if(GuildCache.invite(player, invitee)) sender.sendMessage(new TextComponentString("Sucessfully invited " + invitee_string + " !"));
        }
    }
}
