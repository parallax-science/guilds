package parallaxscience.guilds.raid;

import net.minecraftforge.common.MinecraftForge;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.utility.MessageUtility;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Class that represents a raid
 * Contains a list of all attackers and defenders, and other raid information
 * Note: raids are identified by the name of the defending guild
 * @author Tristan Jay
 */
public class Raid {

    /**
     * Enum to represent the raid phase
     * SETUP - Setup phase - Before /raid start is ran, attackers join
     * PREP - Preparation phase - After raid is declared, attackers and defenders can join
     * ACTIVE - Raid phase - The actual raid, lasts until all defenders die or the timer runs out
     */
    enum raidPhase
    {
        SETUP,
        PREP,
        ACTIVE
    }

    /**
     * Name of the defending guild, used as the ID for the raid
     */
    private String defendingGuild;

    /**
     * List of all defender UUIDs
     */
    private ArrayList<UUID> defenders;

    /**
     * List of all attacker UUIDs
     */
    private ArrayList<UUID> attackers;

    /**
     * Timer used to handle preparation and active phase countdowns
     * Instantiated when preparation phase is entered
     * @see RaidTimer
     */
    private RaidTimer raidTimer;

    /**
     * The current phase that the raid is in
     * @see raidPhase
     */
    private raidPhase phase;

    /**
     * Constructor for the raid class
     * Called whenever a new raid is joined
     * @param defendingGuild the guild being raided
     * @param primaryAttacker the first person to join the raid
     */
    Raid(String defendingGuild, UUID primaryAttacker)
    {
        this.defendingGuild = defendingGuild;
        attackers = new ArrayList<>();
        defenders = GuildCache.getGuild(defendingGuild).getAllMembers();
        attackers.add(primaryAttacker);
        phase = raidPhase.SETUP;
    }

    /**
     * Returns if the player is involved in the raid
     * @param player UUID of player
     * @return true if player is part of the raid
     */
    boolean isRaider(UUID player)
    {
        return attackers.contains(player) || defenders.contains(player);
    }

    /**
     * Returns whether or not the raid has begun
     * @return true if the raid has begun
     */
    public boolean isActive()
    {
        return phase == raidPhase.ACTIVE;
    }

    /**
     * Returns whether or not the raid has been initiated by the attackers
     * @return true if the raid has been started
     */
    public boolean isStarted() {
        return phase == raidPhase.PREP;
    }

    /**
     * Starts the raid
     * This does not begin the actual raid phase, but begins the preparation phase
     * Also instantiates and activates the raid timer
     * @see RaidTimer
     */
    public void startRaid()
    {
        phase = raidPhase.PREP;
        raidTimer = new RaidTimer(this);
        MinecraftForge.EVENT_BUS.register(raidTimer);
    }

    /**
     * Returns whether or not another attacker can join
     * Keeps the number of attackers from exceeding the number of defenders
     * @return true if another attacker can join
     */
    public boolean canAttackerJoin()
    {
        return attackers.size() < defenders.size();
    }

    /**
     * Removes a player from the raid
     * Also stops the raid if the player is the last one left on their side
     * @param player UUID of the player
     */
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

    /**
     * Sets the raid active
     * Used by the Raid Timer
     * This is where the raid actually starts
     * @see RaidTimer
     */
    void setActive()
    {
        phase = raidPhase.ACTIVE;
        MessageUtility.raidMessageAll("The raid on " + defendingGuild + " has begun!");
    }

    /**
     * Returns the name of the defending guild of the raid
     * @return String name of the defending guild
     */
    public String getDefendingGuild() {
        return defendingGuild;
    }

    /**
     * Adds a player to the attacking side
     * @param player UUID of the player
     */
    public void addAttacker(UUID player)
    {
        attackers.add(player);
    }

    /**
     * Adds a player to the defending side
     * @param player UUID of the player
     */
    public void addDefender(UUID player)
    {
        defenders.add(player);
    }

    /**
     * Stops and unregisters the raid timer
     * @see RaidTimer
     */
    void stopTimer()
    {
        MinecraftForge.EVENT_BUS.unregister(raidTimer);
    }
}
