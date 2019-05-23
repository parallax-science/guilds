package parallaxscience.guilds.raid;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RaidTimer {

    private Raid raid;
    private boolean isActive;
    private int counter;
    private int countTo;

    //Temporary, fill in for config file
    private final int prepSeconds = 300;
    private final int raidSeconds = 300;

    RaidTimer(Raid raid)
    {
        this.raid = raid;
        isActive = false;
        counter = 0;
        countTo = prepSeconds*20;
    }

    @SubscribeEvent
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
                countTo = raidSeconds*20;
                counter = 0;
            }
        }
        else counter++;
    }
}
