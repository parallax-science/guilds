package parallaxscience.guilds.guild;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import parallaxscience.guilds.config.GuildConfig;
import parallaxscience.guilds.alliance.Alliance;
import parallaxscience.guilds.config.RaidConfig;
import java.io.Serializable;
import java.util.*;

/**
 * Class for representing a guild
 * Contains a list of all members, as well as other guild attributes
 * @author Tristan Jay
 */
public class Guild implements Serializable
{
    /**
     * Enum used to represent a member's rank in a guild
     * Used for rank comparisons
     */
    enum Rank
    {
        MEMBER,
        ADMIN,
        MASTER
    }

    /**
     * Name of the guild
     */
    private String guildName;

    /**
     * The list of all of the guild members and their rank
     * A HashMap is used to bind a value to a specific key
     * @see HashMap
     */
    private HashMap<UUID, Rank> members;

    /**
     * The list of people invited to join the guild
     * Players cannot join the guild without being on this list
     */
    private ArrayList<UUID> invitees;

    /**
     * The ID of the current guild master
     */
    private UUID guildMaster;

    /**
     * The current amount of territory claimed by the guild
     */
    private int territoryCount = 0;

    /**
     * The name of the alliance that the guild belongs to
     */
    private String alliance;

    /**
     * The guild's style
     * Used for colorized chat messages
     * @see Style
     */
    private TextFormatting color;

    /**
     *
     */
    private long nextRaidInterval;

    /**
     * Constructor for the Guild class
     * Called whenever a new guild is formed
     * @param guildName string name of the new guild
     * @param guildMaster UUID of the new guild's master
     */
    Guild(String guildName, UUID guildMaster)
    {
        this.guildName = guildName;
        this.guildMaster = guildMaster;
        members = new HashMap<>();
        invitees = new ArrayList<>();
        members.put(guildMaster, Rank.MASTER);
    }

    /**
     * Transfers ownership of the guild to another individual
     * Replaces the old guild master with a new one
     * @param newMaster UUID of new guild master
     */
    public void transferOwnership(UUID newMaster)
    {
        members.replace(guildMaster, Rank.MEMBER);
        members.replace(newMaster, Rank.MASTER);
        guildMaster = newMaster;
        members.remove(newMaster);
    }

    /**
     * Returns the ID of the guild master
     * @return UUID of guild master
     */
    public UUID getGuildMaster()
    {
        return guildMaster;
    }

    /**
     * Returns the name of the guild
     * @return string containing the guild name
     */
    public String getGuildName() {
        return guildName;
    }

    /**
     * Removes a player from the guild
     * @param member UUID of member to be removed
     */
    public void removeMember(UUID member)
    {
        members.remove(member);
    }

    /**
     * Checks to see if a player is a member of the guild
     * @param player UUID of player
     * @return true if player is member
     */
    public boolean isMember(UUID player)
    {
        return members.containsKey(player);
    }

    /**
     * Promotes an ordinary member to admin
     * @param member UUID of member
     */
    public void promote(UUID member)
    {
        Rank currentRank = members.get(member);
        if(currentRank == Rank.ADMIN) return;
        members.replace(member, members.get(member), Rank.ADMIN);
    }

    /**
     * Demotes a member from admin status
     * @param member UUID of member
     */
    public void demote(UUID member)
    {
        Rank currentRank = members.get(member);
        if(currentRank.equals(Rank.MEMBER)) return;
        members.replace(member, members.get(member), Rank.MEMBER);
    }

    /**
     * Checks to see if player has been invited to the guild, and if so, adds them as a member
     * @param player UUID of player
     * @return true if player was successfully added as a member
     */
    public boolean acceptInvite(UUID player)
    {
        if(!invitees.contains(player)) return false;
        invitees.remove(player);
        members.put(player, Rank.MEMBER);
        return true;
    }

    /**
     * Returns a list of all member UUIDs
     * @return ArrayList of member UUIDs
     */
    public ArrayList<UUID> getAllMembers()
    {
        ArrayList<UUID> membersList = new ArrayList<>();
        for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
        {
            membersList.add(rankEntry.getKey());
        }
        return membersList;
    }

    /**
     * Returns a list of admin names
     * Used for tab completion and member list command
     * @param server MinecraftServer instance
     * @return String List admin names
     */
    public List<String> getAdmins(MinecraftServer server)
    {
        PlayerProfileCache playerProfileCache = server.getPlayerProfileCache();
        List<String> adminList = new ArrayList<>();
        for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
        {
            if(rankEntry.getValue() == Rank.ADMIN)
			{
				GameProfile gameProfile = playerProfileCache.getProfileByUUID(rankEntry.getKey());
				if(gameProfile != null) adminList.add(gameProfile.getName());
			}
        }
        return adminList;
    }

    /**
     * Returns a list of ordinary member names
     * Used for tab completion and member list command
     * @param server MinecraftServer instance
     * @return String List ordinary member names
     */
    public List<String> getMembers(MinecraftServer server)
    {
		PlayerProfileCache playerProfileCache = server.getPlayerProfileCache();
		List<String> memberList = new ArrayList<>();
		for(Map.Entry<UUID, Rank> rankEntry : members.entrySet())
		{
			if(rankEntry.getValue() == Rank.MEMBER)
			{
				GameProfile gameProfile = playerProfileCache.getProfileByUUID(rankEntry.getKey());
				if(gameProfile != null) memberList.add(gameProfile.getName());
			}
		}
		return memberList;
    }

    /**
     * Sets the guild's text color
     * @param textFormatting a TextFormatting color
     * @see TextFormatting
     */
    public void setColor(TextFormatting textFormatting)
    {
        this.color = textFormatting;
    }

    /**
     * Returns the guild's text
     * Used for guild color text
     * @return guild's text color
     * @see TextFormatting
     */
    public TextFormatting getColor()
    {
        return color;
    }

    /**
     * Returns the amount of territory that the guild currently has claimed
     * @return The number of current territory claims
     */
    int getTerritoryCount() {
        return territoryCount;
    }

    /**
     * Returns whether or not the maximum amount of territory has been claimed
     * @return true if the maximum territory has been claimed
     */
    public boolean hasMaxClaim()
    {
        return (members.size() * GuildConfig.claimPerPlayer) - territoryCount == 0;
    }

    /**
     * Increments the territory count value
     * Used whenever a new chunk is claimed
     */
    public void incrementTerritoryCount()
    {
        territoryCount += 1;
    }

    /**
     * Decrements the territory count value
     * Used whenever a chunk is abandoned
     */
    public void decrementTerritoryCount()
    {
        territoryCount -= 1;
    }

    /**
     * Returns whether or not a member is an admin
     * @param member UUID of member
     * @return true if the member is an admin
     */
    public boolean isAdmin(UUID member)
    {
        return members.get(member) == Rank.ADMIN || member.equals(guildMaster);
    }

    /**
     * Adds a player to the invitee list
     * @param player UUID of invited player
     */
    public void addInvitee(UUID player)
    {
        invitees.add(player);
    }

    /**
     * Returns the alliance that the guild is in
     * @return string of alliance name
     * @see Alliance
     */
    public String getAlliance()
    {
        return alliance;
    }

    /**
     * Sets the guild's alliance
     * @param alliance String name of the alliance
     * @see Alliance
     */
    public void setAlliance(String alliance)
    {
        this.alliance = alliance;
    }

    /**
     * Resets the raid shield interval to the current time + the shield duration in config
     */
    public void resetRaidInterval()
    {
        nextRaidInterval = RaidConfig.shieldDuration*60000 + System.currentTimeMillis();
    }

    /**
     * Returns how many minutes until the guild can be raided again
     * @return 0 if after interval, remaining minutes if before
     */
    public long getRemainingShield()
    {
        if(nextRaidInterval <= System.currentTimeMillis()) return 0;
        else return (nextRaidInterval - System.currentTimeMillis())/60000;
    }
}
