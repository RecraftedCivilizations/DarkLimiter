package com.github.recraftedcivilizations.darklimiter.limiters

import com.github.recraftedcivilizations.darkcitizens.BukkitWrapper
import com.github.recraftedcivilizations.darkcitizens.DarkCitizens
import com.github.recraftedcivilizations.darkcitizens.dPlayer.DPlayerManager
import com.github.recraftedcivilizations.darkcitizens.groups.Group
import com.github.recraftedcivilizations.darkcitizens.jobs.JobManager
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Door
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

/**
 * @author DarkVanityOfLight
 */

/**
 * Get the upper half of a door block
 * @return The original block if it is no door or it is already the upper half, else return the upper half of this door
 */
fun Block.getUpperHalf(): Block{

    // Check that we have a door
    if (this.blockData !is Door){
        // If not just return the original block
        return this
    }

    // Check that we don't already have the top
    if ((blockData as Door).half == Bisected.Half.TOP){
        // If we have, just return this block
        return this
    }

    // Get the block one above this one and return it
    return world.getBlockAt(x, y+1, z)

}

/**
 * Represents the different groups a door can be locked for
 */
enum class LockedFor{
    Player,
    Group
}

/**
 * This locker can lock door blocks and prevent players from opening the,
 * @param dPlayerManager An optional dPlayerManager the default one is from DarkCitizens main class
 * @param jobManager An optional jobManager the default one is from DarkCitizens main class
 * @param bukkitWrapper An optional bukkit wrapper
 * @constructor Construct using an additional base map of group locked doors
 */
class DoorLocker(private val dPlayerManager: DPlayerManager = DarkCitizens.dPlayerManager, private val jobManager: JobManager = DarkCitizens.jobManager, private val bukkitWrapper: BukkitWrapper = BukkitWrapper()): Listener {
    private val groupLockedDoors: MutableMap<Block, String> = emptyMap<Block, String>().toMutableMap()
    private val playerLockedDoors: MutableMap<Block, UUID> = emptyMap<Block, UUID>().toMutableMap()

    /**
     * Construct using an additional base map of group locked doors
     * @param groupLockedDoor A list of already locked doors that should be checked by this locker
     * @param dPlayerManager An optional dPlayerManager the default one is from DarkCitizens main class
     * @param jobManager An optional jobManager the default one is from DarkCitizens main class
     * @param bukkitWrapper An optional bukkit wrapper
     */
    constructor(groupLockedDoor: Map<Block, String>, dPlayerManager: DPlayerManager = DarkCitizens.dPlayerManager, jobManager: JobManager = DarkCitizens.jobManager, bukkitWrapper: BukkitWrapper = BukkitWrapper()) : this(dPlayerManager, jobManager, bukkitWrapper) {
        this.groupLockedDoors.putAll(groupLockedDoor)
    }

    /**
     * Lock a door for a group
     * @param door The door to lock
     * @param group The group to lock for
     */
    fun lockDoor(door: Block, group: Group){
        lockDoor(door.getUpperHalf(), group.name)
    }

    /**
     * Lock a door for a group name
     * @param door The door to lock
     * @param group The group name to lock for
     */
    fun lockDoor(door: Block, group: String){
        // Check that we don't lock doors twice
        if (isLocked(door) != null) return
        groupLockedDoors[door.getUpperHalf()] = group
    }

    /**
     * Unlock a door
     * @param door The door to unlock
     */
    fun unlockDoor(door: Block){
        playerLockedDoors.remove(door.getUpperHalf())
        groupLockedDoors.remove(door.getUpperHalf())
    }

    /**
     * Lock a door for a player
     * @param door The door to lock
     * @param player The uuid of the player to lock the door for
     */
    fun lockDoor(door: Block, player: UUID){
        // Check that we don't lock doors twice
        if (isLocked(door) != null) return
        playerLockedDoors[door.getUpperHalf()] = player
    }

    /**
     * Check if a door is already locked
     * @param door One of the door blocks of the door
     * @return The type this door is locked for or null if it isn't locked
     */
    fun isLocked(door: Block): LockedFor?{
        return when {
            door.getUpperHalf() in groupLockedDoors.keys -> {
                LockedFor.Group
            }
            door.getUpperHalf() in playerLockedDoors.keys -> {
                LockedFor.Player
            }
            else -> {
                null
            }
        }

    }

    /**
     * Check to whom this door belongs
     * @param door One of the door blocks of the door to check
     * @return null if this door doesn't belong to anyone, else the group or player name
     */
    fun lockedFor(door: Block): String?{
        val isLockedFor = isLocked(door)
        return if (isLockedFor != null){
            if (isLockedFor == LockedFor.Player){
                val playerUUID = playerLockedDoors[door.getUpperHalf()]!!
                bukkitWrapper.getPlayer(playerUUID)!!.displayName
            }else{
                groupLockedDoors[door.getUpperHalf()]
            }
        }else{
            null
        }
    }

    /**
     * Return the player uuid the door is locked for
     * @param door The door block to check
     * @return the player UUID or null if the door doesn't belong to a player
     */
    fun lockedForPlayer(door: Block): UUID?{
        val isLockedFor = isLocked(door)
        // Check if the door is locked for a player
        return  if (isLockedFor == LockedFor.Player){
            // Get the player uuid for the door
            playerLockedDoors[door.getUpperHalf()]
        }else{
            // The door belongs to no one or a group
            null
        }
    }

    /**
     * Check if someone can open the door
     * @param door The door to check for, can be upper or lower half
     * @param player The player to check for
     */
    private fun canOpen(door: Block, player: UUID): Boolean{
        val dPlayer = dPlayerManager.getDPlayer(player)
        val playerGroup = jobManager.getJob(dPlayer?.job)?.group

        var upperHalf = door.getUpperHalf()

        // Get the group or null that owns this door
        val group = groupLockedDoors[upperHalf]
        // Get the owner or null who owns this door
        val doorOwnerUUID = playerLockedDoors[upperHalf]

        // (group == playerGroup && group != null) -> Check if we have a group owned door and if the player group matches it
        // doorOwnerUUID == player -> Check if the player is the same as bought the door
        // group == "Public" -> Check if the door belongs to the public, if yes the player can open it
        // So in all we check if either the door is owned by a group and the player is in this group
        // or if the player himself bought the door
        if ((group == playerGroup && group != null)|| group == "Public" || doorOwnerUUID == player){
            return true
        }

        return false

    }

    /**
     * Reset ALL locked doors
     */
    fun resetAll() {
        resetForAllGroups()
        resetForAllPlayers()
    }

    /**
     * Reset all doors claimed by a specific player
     * @param uuid The players uuid
     */
    fun resetAllForPlayer(uuid: UUID){
        // Loop through all player locked door entries
        for ((door, iUUID) in playerLockedDoors.entries){
            // If the door uuid is the same as the from the player
            if (iUUID == uuid){
                // Remove the door from the locked doors
                playerLockedDoors.remove(door)
            }
        }
    }

    /**
     * Reset all doors claimed by a specific group
     * @param name The name of the group
     */
    fun resetAllForGroup(name: String){
        // Loop through all group doors
        for ((door, gName) in groupLockedDoors.entries){
            // If we find the same name as we search for
            if (gName == name){
                // Remove the entry
                groupLockedDoors.remove(door)
            }
        }
    }

    /**
     * Reset all doors for all players
     */
    fun resetForAllPlayers(){
        // Remove all player doors
        val pIterator = playerLockedDoors.iterator()
        while (pIterator.hasNext()){
            pIterator.remove()
        }
    }

    /**
     * Reset all doors for all groups
     */
    fun resetForAllGroups(){
        // Remove all group doors
        val gIterator = groupLockedDoors.iterator()
        while (gIterator.hasNext()){
            gIterator.remove()
        }
    }

    companion object{
        // This regex will check if a  block is a door
        val WOOD_DOOR_REGEX = Regex("\\w+_DOOR")
    }

    /**
     * Cancel the door opening if the player isn't allowed to open the door
     */
    @EventHandler(ignoreCancelled = true)
    fun onInteraction(e: PlayerInteractEvent){
        // Check if the clicked block is a door
        val isDoor = e.clickedBlock?.type?.name?.let { WOOD_DOOR_REGEX.matches(it) }?: false
        //Check that we have a right click to open the door
        val isRightClick = e.action == Action.RIGHT_CLICK_BLOCK

        // Player tried to open a door
        if (isDoor  && isRightClick){
            var doorBlock = e.clickedBlock!!.getUpperHalf()

            val canOpen = canOpen(doorBlock, e.player.uniqueId)
            // If the player cannot open this door
            if (!canOpen){
                // Cancel the click event
                e.isCancelled = true
            }
        }
    }

    /**
     * Clean all locked doors for a leaving player
     */
    @EventHandler(ignoreCancelled = true)
    fun onLeave(e: PlayerQuitEvent){
        for ((door, player) in playerLockedDoors.entries){
            if (player == e.player.uniqueId){
                unlockDoor(door)
            }
        }
    }
}