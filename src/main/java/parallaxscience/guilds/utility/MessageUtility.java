package parallaxscience.guilds.utility;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MessageUtility {

    private final static Style guildStyle = new Style();
    private final static Style raidStyle = new Style();
    private final static Style allianceStyle = new Style();
    private final static Style chunkStyle = new Style();

    private final static MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

    public static void initialize()
    {
        guildStyle.setColor(TextFormatting.GREEN);
        raidStyle.setColor(TextFormatting.RED);
        allianceStyle.setColor(TextFormatting.BLUE);
        chunkStyle.setColor(TextFormatting.YELLOW);
    }

    /**
     * Sends a message to the player
     * Uses a specified style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     * @param style Style to set the message to
     */
    private static void message(ICommandSender sender, String message, Style style)
    {
        ITextComponent textComponent = new TextComponentString(message);
        textComponent.setStyle(style);
        sender.sendMessage(textComponent);
    }

    /**
     * Uses the message function to send a message to the player
     * Uses the guild color style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     */
    public static void guildMessage(ICommandSender sender, String message)
    {
        message(sender, message, guildStyle);
    }

    /**
     * Uses the message function to send a message to the player
     * Uses the raid color style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     */
    public static void raidMessage(ICommandSender sender, String message)
    {
        message(sender, message, raidStyle);
    }

    /**
     * Sends a raid notification to all players
     * Uses the raid color style
     * @param message String to send to the players
     */
    public static void raidMessageAll(String message)
    {
        ITextComponent textComponent = new TextComponentString(message);
        textComponent.setStyle(raidStyle);
        server.getPlayerList().sendMessage(textComponent);
    }

    /**
     * Uses the message function to send a message to the player
     * Uses the alliance color style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     */
    public static void allianceMessage(ICommandSender sender, String message)
    {
        message(sender, message, allianceStyle);
    }

    /**
     * Uses the message function to send a message to the player
     * Uses the chunk color style
     * @param sender ICommandSender reference to the player
     * @param message String to send to the player
     */
    public static void chunkMessage(ICommandSender sender, String message)
    {
        message(sender, message, chunkStyle);
    }
}
