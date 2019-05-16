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

    public boolean isActive()
    {
        return phase == raidPhase.ACTIVE;
    }

    public void startRaid()
    {
        phase = raidPhase.PREP;
    }

    public void removePlayer(UUID player)
    {
        attackers.remove(player);
        defenders.remove(player);
        //Last player, declare
    }

    public void setActive()
    {
        phase = raidPhase.ACTIVE;
    }
}
