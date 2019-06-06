package parallaxscience.guilds.events;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.guild.Guild;

import java.time.format.TextStyle;
import java.util.UUID;

/**
 *
 */
public class GuildEvents {

    /**
     *
     * @param event
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerChat(ServerChatEvent event)
    {
        UUID playerID = event.getPlayer().getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(playerID);
        if(guild != null)
        {
            ITextComponent textComponent = new TextComponentString("<" + guild.getGuildName() + ">").appendSibling(event.getComponent());
            textComponent.setStyle(guild.getColor());
            event.setComponent(textComponent);
        }
    }
}
