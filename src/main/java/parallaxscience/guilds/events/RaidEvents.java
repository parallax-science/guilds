package parallaxscience.guilds.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import parallaxscience.guilds.guild.ChunkCache;
import parallaxscience.guilds.guild.Guild;
import parallaxscience.guilds.guild.GuildCache;
import parallaxscience.guilds.raid.Raid;
import parallaxscience.guilds.raid.RaidCache;
import java.util.UUID;

/**
 * EventHandler class for raid events
 * Only registered while a raid is active, otherwise unregistered
 * @author Tristan Jay
 */
public class RaidEvents
{
    /**
     * Called whenever a living entity dies during a raid
     * Used to remove slain raiders from the fight
     * @param event LivingDeathEvent
     * @see LivingDeathEvent
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerDeath(LivingDeathEvent event)
    {
        EntityLivingBase entityLiving = event.getEntityLiving();
        if(entityLiving instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entityLiving;
            UUID playerID = player.getUniqueID();
            Guild guild = GuildCache.getPlayerGuild(playerID);
            if(guild != null)
            {
                Raid raid = RaidCache.getPlayerRaid(playerID);
                if(raid != null)
                {
                    if(raid.isActive())
                    {
                        raid.removePlayer(playerID);
                        player.setGameType(GameType.SPECTATOR);
                        player.connection.disconnect(new TextComponentString("You have been slain and are out of the fight!"));
                    }
                }
            }
        }
    }

    /**
     * Called every time a player attempts to join the server during a raid
     * Used to keep slain raiders from rejoining
     * @param event PlayerLoggedInEvent
     * @see PlayerEvent.PlayerLoggedInEvent
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer entityPlayer = event.player;
        if(entityPlayer instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
            Guild guild = GuildCache.getPlayerGuild(player.getUniqueID());
            if(guild != null)
            {
                Raid raid = RaidCache.getRaid(guild.getGuildName());
                if(raid != null)
                {
                    if(raid.isActive()) player.connection.disconnect(new TextComponentString("Your guild is currently being raided!"));
                }
            }
        }
    }

    /**
     * Called whenever a player leaves the server during a raid
     * Used to keep track of players and update the raider list
     * @param event PlayerLoggedOutEvent
     * @see PlayerEvent.PlayerLoggedOutEvent
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        EntityPlayer entityPlayer = event.player;
        if(entityPlayer instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
            UUID playerID = player.getUniqueID();
            if(GuildCache.getPlayerGuild(playerID) != null)
            {
                Raid raid = RaidCache.getPlayerRaid(playerID);
                if(raid != null)
                {
                    raid.removePlayer(playerID);
                }
            }
        }
    }

    /**
     * Called whenever a block is harvested (dropped) during a raid
     * Used to stop blocks from giving drops during a raid
     * @param event HarvestDropsEvent
     * @see BlockEvent.HarvestDropsEvent
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onBlockDrop(BlockEvent.HarvestDropsEvent event)
    {
        if(event.getWorld().isRemote) return;

        BlockPos blockPos = event.getPos();
        String guildName = ChunkCache.getChunkOwner(blockPos);
        if(guildName != null)
        {
            Raid raid = RaidCache.getRaid(guildName);
            if(raid != null)
            {
                if(raid.isActive())
                {
                    event.getDrops().clear();
                    event.setDropChance(0.0f);
                }
            }
        }
    }
}
