package parallaxscience.guilds.raid;

import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import parallaxscience.guilds.Guilds;

public class RaidTimer {

    private Raid raid;
    private long startAt;
    private long endAt;

    RaidTimer(Raid raid)
    {
        this.raid = raid;
        startAt = System.currentTimeMillis() + Guilds.prepSeconds*1000;
        endAt = startAt + Guilds.raidSeconds*1000;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        long currentTime = System.currentTimeMillis();
        if(currentTime > endAt)
        {
            RaidCache.stopRaid(raid.getDefendingGuild(), true);
        }
        else if(currentTime > startAt)
        {
            raid.setActive();
            PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
            players.sendMessage(new TextComponentString("The raid on " + raid.getDefendingGuild() + " has begun!"));
        }
    }
}
