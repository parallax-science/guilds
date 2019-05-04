package parallaxscience.guilds.guild;

import parallaxscience.guilds.alliance.Alliance;

import java.util.HashMap;
import java.util.UUID;

public class Guild {

    enum Rank
    {
        MEMBER,
        ADMIN
    }

    private String guildName;
    private String guildDescription = "";
    private HashMap<UUID, Rank> members;
    private UUID guildMaster;
    private int territoryCount = 0;
    private int wins = 0;
    private int losses = 0;
    private Alliance alliance;

    public Guild(String guildName, UUID guildMaster){
        this.guildName = guildName;
        this.guildMaster = guildMaster;
        members = new HashMap<>();
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

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public void setGuildDescription(String guildDescription) {
        this.guildDescription = guildDescription;
    }

    public String getGuildName() {
        return guildName;
    }

    public String getGuildDescription() {
        return guildDescription;
    }

    public void incrementWins()
    {
        wins += 1;
    }

    public void incrementLosses()
    {
        losses += 1;
    }

    public void kickMember(UUID member)
    {
        members.remove(member);
    }

    public boolean isMember(UUID member)
    {
        return members.containsKey(member);
    }

    public boolean promote(UUID member)
    {
        Rank currentRank = members.get(member);
        if(currentRank == Rank.ADMIN) return true;

        return members.replace(member, members.get(member), Rank.ADMIN);
    }

    public boolean demote(UUID member)
    {
        Rank currentRank = members.get(member);
        if(currentRank == Rank.MEMBER) return true;

        return members.replace(member, members.get(member), Rank.MEMBER);
    }

    public void addMember(UUID member)
    {
        members.put(member, Rank.MEMBER);
    }

    public int getTerritoryCount() {
        return territoryCount;
    }

    public void incrementTerritoryCount()
    {
        territoryCount += territoryCount;
    }

    public Rank getRank(UUID member)
    {
        return members.get(member);
    }

    public boolean isAdmin(UUID player)
    {
        return members.get(player) == Rank.ADMIN || player == guildMaster;
    }

    @Override
    public String toString()
    {
        return guildName;
    }
}