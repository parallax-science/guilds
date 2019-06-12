package parallaxscience.guilds.alliance;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class used to represent an alliance
 * Created when a new alliance is formed
 * @author Tristan Jay
 */
public class Alliance implements Serializable
{
    /**
     * List of all of the guilds in the alliance
     */
    private ArrayList<String> guilds;

    /**
     * List of the guilds that have been invited to the alliance
     */
    private ArrayList<String> invitees;

    /**
     * Constructor for the Alliance class
     * @param guildName String representing new guild name
     */
    Alliance(String guildName)
    {
        guilds = new ArrayList<>();
        invitees = new ArrayList<>();
        guilds.add(guildName);
    }

    /**
     * Returns a list of guilds in the alliance
     * @return String ArrayList of names of guilds in the alliance
     */
    public ArrayList<String> getGuilds() {
        return guilds;
    }

    /**
     * Removes a guild from the alliance
     * @param guild String of guild name
     */
    void removeGuild(String guild)
    {
        guilds.remove(guild);
    }

    /**
     * Invites a guild to the alliance
     * @param guild String of guild name
     */
    public void addInvitee(String guild)
    {
        invitees.add(guild);
    }

    /**
     * Accepts an invite for the alliance, then removes them from the invite list
     * A guild can only join if they have been invited
     * @param guild String of guild name
     * @return true if guild successfully joins
     */
    public boolean acceptInvite(String guild)
    {
        if(!invitees.contains(guild)) return false;
        guilds.add(guild);
        invitees.remove(guild);
        return true;
    }

    /**
     * Returns the amount of guilds in the alliance
     * @return int of guild count
     */
    int getGuildCount()
    {
        return guilds.size();
    }
}
