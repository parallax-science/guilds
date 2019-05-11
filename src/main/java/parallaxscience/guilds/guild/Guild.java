package parallaxscience.guilds.guild;

import parallaxscience.guilds.Guilds;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Guild implements Serializable {

    enum Rank
    {
        MEMBER,
        ADMIN
    }

    private String guildName;
    private HashMap<UUID, Rank> members;
    private ArrayList<UUID> invitees;
    private UUID guildMaster;
    private int territoryCount = 0;
    private String alliance;

    Guild(String guildName, UUID guildMaster){
        this.guildName = guildName;
        this.guildMaster = guildMaster;
        members = new HashMap<>();
        invitees = new ArrayList<>();
    }

    public void transferOwnership(UUID newMaster)
    {
        members.put(guildMaster, Rank.MEMBER);
        guildMaster = newMaster;
        members.remove(newMaster);
    }

    public UUID getGuildMaster()
    {
        return guildMaster;
    }

    public String getGuildName() {
        return guildName;
    }

    public void removeMember(UUID member)
    {
        members.remove(member);
    }

    public boolean isMember(UUID member)
    {
        return members.containsKey(member) || getGuildMaster().equals(member);
    }

    public void promote(UUID member)
    {
        Rank currentRank = members.get(member);
        if(currentRank == Rank.ADMIN) return;
        members.replace(member, members.get(member), Rank.ADMIN);
    }

    public void demote(UUID member)
    {
        Rank currentRank = members.get(member);
        if(currentRank == Rank.MEMBER) return;
        members.replace(member, members.get(member), Rank.MEMBER);
    }

    public boolean acceptInvite(UUID player)
    {
        if(!invitees.contains(player)) return false;
        invitees.remove(player);
        members.put(player, Rank.MEMBER);
        return true;
    }

    int getTerritoryCount() {
        return territoryCount;
    }

    public boolean hasMaxClaim()
    {
        return (members.size() + 1)* Guilds.claimPerPlayer - territoryCount == 0;
    }

    public void incrementTerritoryCount()
    {
        territoryCount += 1;
    }

    public void decrementTerritoryCount()
    {
        territoryCount -= 1;
    }

    public boolean isAdmin(UUID player)
    {
        return members.get(player) == Rank.ADMIN || player.equals(guildMaster);
    }

    public void addInvitee(UUID player)
    {
        invitees.add(player);
    }

    public String getAlliance()
    {
        return alliance;
    }

    public void setAlliance(String alliance)
    {
        this.alliance = alliance;
    }
}
