package parallaxscience.guilds.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;

import java.util.UUID;

public class RaidEvents {

    //To be implemented

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        //Implement

        //Is player part of active raid? Remove from raid
        //Entity entity = event.getEntity();
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        //Event

        UUID player = event.player.getUniqueID();
        Raid raid = RaidCache.getPlayerRaid(player);
        if(raid != null) raid.removePlayer();
    }
}
