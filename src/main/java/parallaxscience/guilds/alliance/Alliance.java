package parallaxscience.guilds.alliance;

import java.util.ArrayList;

public class Alliance {

    private ArrayList<String> guilds;
    private ArrayList<String> invitees;

    Alliance(String guildName)
    {
        guilds = new ArrayList<>();
        invitees = new ArrayList<>();
        guilds.add(guildName);
    }

    public ArrayList<String> getGuilds() {
        return guilds;
    }

    public void removeGuild(String guild)
    {
        guilds.remove(guild);
    }

    public void addInvitee(String guild)
    {
        invitees.add(guild);
    }

    public boolean acceptInvite(String guild)
    {
        if(!invitees.contains(guild)) return false;
        guilds.add(guild);
        invitees.remove(guild);
        return true;
    }

    public int getGuildCount()
    {
        return guilds.size();
    }
}
