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
    private int counter;
    private int countTo;

    RaidTimer(Raid raid)
    {
        this.raid = raid;
        counter = 0;
        countTo = Guilds.prepSeconds*20;
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(counter > countTo)
        {
            if(raid.isActive())
            {
                RaidCache.stopRaid(raid.getDefendingGuild(), true);
            }
            else
            {
                raid.setActive();
                PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
                players.sendMessage(new TextComponentString("The raid on " + raid.getDefendingGuild() + " has begun!"));
                countTo = Guilds.raidSeconds*20;
                counter = 0;
            }
        }
        else counter++;
    }
}
