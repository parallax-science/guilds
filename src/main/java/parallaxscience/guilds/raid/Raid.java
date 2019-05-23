package parallaxscience.guilds.raid;

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

    //To be implemented
    public Raid(String defendingGuild, UUID primaryAttacker)
    {
        this.defendingGuild = defendingGuild;
        attackers = new ArrayList<>();
        defenders = GuildCache.getGuild(defendingGuild).getMembers();
        attackers.add(primaryAttacker);
        phase = raidPhase.SETUP;
    }

    public boolean isRaider(UUID player)
    {
        return attackers.contains(player) || defenders.contains(player);
    }

    public boolean isAttacker(UUID player)
    {
        return attackers.contains(player);
    }

    public boolean isActive()
    {
        return phase == raidPhase.ACTIVE;
    }

    public void startRaid()
    {
        phase = raidPhase.PREP;
        raidTimer = new RaidTimer(this);
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

    public void setActive()
    {
        phase = raidPhase.ACTIVE;
    }

    public String getDefendingGuild() {
        return defendingGuild;
    }
}
