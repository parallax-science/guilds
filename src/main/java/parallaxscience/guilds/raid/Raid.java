package parallaxscience.guilds.raid;

import net.minecraftforge.common.MinecraftForge;
import parallaxscience.guilds.guild.GuildCache;
import java.util.ArrayList;
import java.util.UUID;

public class Raid {

    enum raidPhase
    {
        SETUP,
        PREP,
        ACTIVE
    }

    private String defendingGuild;
    private ArrayList<UUID> defenders;
    private ArrayList<UUID> attackers;
    private RaidTimer raidTimer;
    private raidPhase phase;

    Raid(String defendingGuild, UUID primaryAttacker)
    {
        this.defendingGuild = defendingGuild;
        attackers = new ArrayList<>();
        defenders = GuildCache.getGuild(defendingGuild).getAllMembers();
        attackers.add(primaryAttacker);
        phase = raidPhase.SETUP;
    }

    boolean isRaider(UUID player)
    {
        return attackers.contains(player) || defenders.contains(player);
    }

    public boolean isActive()
    {
        return phase == raidPhase.ACTIVE;
    }

    public boolean isStarted() {
        return phase == raidPhase.PREP;
    }

    public void startRaid()
    {
        phase = raidPhase.PREP;
        raidTimer = new RaidTimer(this);
        MinecraftForge.EVENT_BUS.register(raidTimer);
    }

    public boolean canAttackerJoin()
    {
        return attackers.size() < defenders.size();
    }

    public void removePlayer(UUID player)
    {
        attackers.remove(player);
        defenders.remove(player);
        if(attackers.isEmpty())
        {
            if(isActive()) RaidCache.stopRaid(defendingGuild, true);
            else RaidCache.cancelRaid(defendingGuild);
        }
        else if(defenders.isEmpty())
        {
            if(isActive()) RaidCache.stopRaid(defendingGuild, false);
            else RaidCache.cancelRaid(defendingGuild);
        }
    }

    void setActive()
    {
        phase = raidPhase.ACTIVE;
    }

    public String getDefendingGuild() {
        return defendingGuild;
    }

    public void addAttacker(UUID player)
    {
        attackers.add(player);
    }

    public void addDefender(UUID player)
    {
        defenders.add(player);
    }

    void stopTimer()
    {
        MinecraftForge.EVENT_BUS.unregister(raidTimer);
    }
}
