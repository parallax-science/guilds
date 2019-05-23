package parallaxscience.guilds.guild;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import parallaxscience.guilds.Guilds;
import java.io.Serializable;
import java.util.*;

public class Guild implements Serializable {

    enum Rank
    {
        MEMBER,
        ADMIN,
        MASTER
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
        members.put(guildMaster, Rank.MASTER);
    }

    public void transferOwnership(UUID newMaster)
    {
        members.replace(guildMaster, Rank.MEMBER);
        members.replace(newMaster, Rank.MASTER);
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

    public ArrayList<UUID> getMembers()
    {
        ArrayList<UUID> membersList = new ArrayList<>();
        for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
        {
            membersList.add(rankEntry.getKey());
        }
        return membersList;
    }

    public ArrayList<UUID> getOnlineMembers()
    {
        final List<EntityPlayer> playerList = Minecraft.getMinecraft().world.playerEntities;
        ArrayList<UUID> onlineMembers = new ArrayList<>();
        for(EntityPlayer entityPlayer : playerList)
        {
            UUID player = entityPlayer.getUniqueID();
            if(members.containsKey(player)) onlineMembers.add(player);
        }
        return onlineMembers;
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
