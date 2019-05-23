package parallaxscience.guilds.raid;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import parallaxscience.guilds.Guilds;

public class RaidTimer {

    private Raid raid;
    private boolean isActive;
    private int counter;
    private int countTo;

    RaidTimer(Raid raid)
    {
        this.raid = raid;
        isActive = false;
        counter = 0;
        countTo = Guilds.prepSeconds*20;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(counter < countTo)
        {
            if(isActive)
            {
                RaidCache.stopRaid(raid.getDefendingGuild(), true);
            }
            else
            {
                raid.setActive();
                countTo = Guilds.raidSeconds*20;
                counter = 0;
            }
        }
        else counter++;
    }
}
