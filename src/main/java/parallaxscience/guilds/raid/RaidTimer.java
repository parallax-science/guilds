package parallaxscience.guilds.raid;

import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import parallaxscience.guilds.config.RaidConfig;

/**
 * Timer used for raid events
 * Used for preparation phase and active phase timings
 * @see Raid
 * @author Tristan Jay
 */
public class RaidTimer {

    /**
     * Reference to the raid object
     */
    private Raid raid;

    /**
     * Time (in milliseconds) that the raid starts
     */
    private long startAt;

    /**
     * Time (in milliseconds) that the raid ends
     */
    private long endAt;


    /**
     * Constructor for the raid
     * @param raid Raid that the timer is used for
     */
    RaidTimer(Raid raid)
    {
        this.raid = raid;
        startAt = System.currentTimeMillis() + RaidConfig.prepSeconds*1000;
        endAt = startAt + RaidConfig.raidSeconds*1000;
    }

    /**
     * Main timing function for the Raid Timer
     * Checks to see if the time has passed either the raid start time or the raid end time
     * If so, the timer moves on to the next phase, or stops the raid
     * Called every time the server ticks (20 times per second)
     * @param event ServerTickEvent
     * @see TickEvent.ServerTickEvent
     */
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
