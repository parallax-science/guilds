package parallaxscience.guilds.alliance;

import parallaxscience.guilds.guild.Guild;

import java.util.ArrayList;

public class Alliance {

    private String allianceName;
    private ArrayList<Guild> guilds;

    public Alliance(String allianceName)
    {
        this.allianceName = allianceName;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public ArrayList<Guild> getGuilds() {
        return guilds;
    }

    public boolean addClan(Guild guild)
    {
        return guilds.add(guild);
    }

    public boolean removeClan(Guild guild)
    {
        if(guilds.size() == 2)
        {
            return false;
        }
        return guilds.remove(guild);
    }

    public boolean isClanInAlliance(Guild guild)
    {
        return guilds.contains(guild);
    }
}
