package com.github.recraftedcivilizations.darklimiter.limiters

import com.github.recraftedcivilizations.darkcitizens.BukkitWrapper
import com.github.recraftedcivilizations.darkcitizens.DarkCitizens
import com.github.recraftedcivilizations.darkcitizens.dPlayer.DPlayerManager
import com.github.recraftedcivilizations.darkcitizens.groups.Group
import com.github.recraftedcivilizations.darkcitizens.jobs.JobManager
import org.bukkit.block.Block
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Door
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class DoorLocker(private val dPlayerManager: DPlayerManager = DarkCitizens.dPlayerManager, private val jobManager: JobManager = DarkCitizens.jobManager, private val bukkitWrapper: BukkitWrapper = BukkitWrapper()): Listener {
    private val groupLockedDoors: MutableMap<Block, String> = emptyMap<Block, String>().toMutableMap()
    private val playerLockedDoors: MutableMap<Block, UUID> = emptyMap<Block, UUID>().toMutableMap()

    /**
     * Lock a door for a group
     * @param door The door to lock, IMPORTANT: ONLY REGISTER THE UPPER HALF OF THE DOOR HERE
     * @param group The group to lock for
     */
    fun lockDoor(door: Block, group: Group){
        lockDoor(door, group.name)
    }

    /**
     * Lock a door for a group name
     * @param door The door to lock, IMPORTANT: ONLY REGISTER THE UPPER HALF OF THE DOOR HERE
     * @param group The group name to lock for
     */
    fun lockDoor(door: Block, group: String){
        groupLockedDoors[door] = group
    }

    /**
     * Unlock a door
     * @param door The door to unlock, IMPORTANT: YOU HAVE TO USE THE UPPER HALF OF THE DOOR
     */
    fun unlockDoor(door: Block){
        playerLockedDoors.remove(door)
        groupLockedDoors.remove(door)
    }

    /**
     * Lock a door for a player
     * @param door The door to lock
     * @param player The uuid of the player to lock the door for
     */
    fun lockDoor(door: Block, player: UUID){

        var upperHalf = door

        if ((door.blockData as Door).half == Bisected.Half.BOTTOM){
            // Get the world we are currently in
            val world = bukkitWrapper.getPlayer(player)!!.world
            // The upper half of the door is at the same position as the door, but y + 1
            upperHalf = world.getBlockAt(door.x, door.y + 1, door.z)
        }

        playerLockedDoors[door] = player
    }

    /**
     * Check if someone can open the door
     * @param door The door to check for, can be upper or lower half
     * @param player The player to check for
     */
    private fun canOpen(door: Block, player: UUID): Boolean{
        val dPlayer = dPlayerManager.getDPlayer(player)
        val playerGroup = jobManager.getJob(dPlayer?.job)?.group

        var upperHalf = door

        if ((door.blockData as Door).half == Bisected.Half.BOTTOM){
            // Get the world we are currently in
            val world = bukkitWrapper.getPlayer(player)!!.world
            // The upper half of the door is at the same position as the door, but y + 1
            upperHalf = world.getBlockAt(door.x, door.y + 1, door.z)
        }

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

    companion object{
        // This regex will check if a  block is a door
        val WOOD_DOOR_REGEX = Regex("\\w+_DOOR")
    }

    /**
     * Cancel the door opening if the player isn't allowed to open the door
     */
    @EventHandler
    fun onInteraction(e: PlayerInteractEvent){
        // Check if the clicked block is a door
        val isDoor = e.clickedBlock?.type?.name?.let { WOOD_DOOR_REGEX.matches(it) }?: false
        //Check that we have a right click to open the door
        val isRightClick = e.action == Action.RIGHT_CLICK_BLOCK

        // Player tried to open a door
        if (isDoor  && isRightClick){
            var doorBlock = e.clickedBlock!!

            // We only want to register and check for the upper half blocks
            if ((doorBlock.blockData as Door).half == Bisected.Half.BOTTOM){
                // Get the world we are currently in
                val world = e.player.world
                // The upper half of the door is at the same position as the door, but y + 1
                doorBlock = world.getBlockAt(doorBlock.x, doorBlock.y + 1, doorBlock.z)
            }

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
    @EventHandler
    fun onLeave(e: PlayerQuitEvent){
        for ((door, player) in playerLockedDoors.entries){
            if (player == e.player.uniqueId){
                unlockDoor(door)
            }
        }
    }
}