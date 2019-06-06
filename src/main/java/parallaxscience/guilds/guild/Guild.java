package parallaxscience.guilds.guild;

import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import parallaxscience.guilds.config.GuildConfig;
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
    private Style color;

    Guild(String guildName, UUID guildMaster){
        this.guildName = guildName;
        this.guildMaster = guildMaster;
        members = new HashMap<>();
        invitees = new ArrayList<>();
        members.put(guildMaster, Rank.MASTER);
        color = new Style();
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
        return members.containsKey(member);
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

    public ArrayList<UUID> getAllMembers()
    {
        ArrayList<UUID> membersList = new ArrayList<>();
        for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
        {
            membersList.add(rankEntry.getKey());
        }
        return membersList;
    }

    public List<String> getAdmins()
    {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        List<String> adminList = new ArrayList<>();
        for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
        {
            if(rankEntry.getValue() == Rank.ADMIN) adminList.add(playerList.getPlayerByUUID(rankEntry.getKey()).getDisplayNameString());
        }
        return adminList;
    }

    public List<String> getMembers()
    {
        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        List<String> adminList = new ArrayList<>();
        for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
        {
            if(rankEntry.getValue() == Rank.MEMBER) adminList.add(playerList.getPlayerByUUID(rankEntry.getKey()).getDisplayNameString());
        }
        return adminList;
    }

    public void setColor(TextFormatting textFormatting)
    {
        color.setColor(textFormatting);
    }

    public Style getColor()
    {
        return color;
    }

    int getTerritoryCount() {
        return territoryCount;
    }

    public boolean hasMaxClaim()
    {
        return (members.size() + 1)* GuildConfig.claimPerPlayer - territoryCount == 0;
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
