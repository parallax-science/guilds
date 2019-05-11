package parallaxscience.guilds.events;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import parallaxscience.guilds.GuildCache;
import parallaxscience.guilds.guild.Guild;
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
    public void onServerChat(ServerChatEvent event)
    {
        UUID playerID = event.getPlayer().getUniqueID();
        Guild guild = GuildCache.getPlayerGuild(playerID);
        if(guild != null)
        {
            event.setComponent(new TextComponentString("<" + guild.getGuildName() + ">").appendSibling(event.getComponent()));
        }
    }
}
