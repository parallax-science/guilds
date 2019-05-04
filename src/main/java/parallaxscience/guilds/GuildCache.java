package parallaxscience.guilds;

import parallaxscience.guilds.guild.Guild;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GuildCache {

    private static HashMap<String, Guild> guilds;
    private static HashMap<UUID, Guild> players;
    private static HashMap<UUID, Guild> invites;

    public static void intitialize()
    {
        guilds = new HashMap<>();
        players = new HashMap<>();
    }

    public static Guild getPlayerGuild(UUID player)
    {
        return players.get(player);
    }

    public static boolean formGuild(String guildName, UUID guildMaster)
    {
        if(guilds.containsKey(guildName)) return false;
        Guild guild = new Guild(guildName, guildMaster);
        guilds.put(guildName, guild);
        players.put(guildMaster, guild);
        return true;
    }

    public static void addMember(String guildName, UUID player)
    {
        //False if no invite
        //True if invite successful
        //Delete all other invites
    }

    public static boolean isInGuild(UUID player)
    {
        return players.containsKey(player);
    }

    public static boolean removeGuild(UUID sender, String guildName)
    {
        Guild guild = guilds.get(guildName);
        if(sender != guild.getGuildMaster()) return false;
        for(Map.Entry<UUID, Guild> g : players.entrySet())
        {
            if(g.getValue().equals(guild))
            {
                players.remove(g.getKey());
            }
        }
        guilds.remove(guildName);
        //Remove all claims
        return true;
    }

    public static boolean invite(UUID sender, UUID invitee)
    {
        Guild guild = players.get(sender);
        if(!guild.isAdmin(sender)) return false;
        invites.putIfAbsent(invitee, guild);
        return true;
    }
}
