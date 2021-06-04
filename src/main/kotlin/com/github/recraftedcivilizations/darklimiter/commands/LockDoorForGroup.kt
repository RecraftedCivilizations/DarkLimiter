package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darkcitizens.DarkCitizens
import com.github.recraftedcivilizations.darkcitizens.groups.GroupManager
import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.limiters.LockedFor
import com.github.recraftedcivilizations.darklimiter.limiters.getUpperHalf
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class LockDoorForGroup(private val doorLocker: DoorLocker, private val groupManager: GroupManager = DarkCitizens.groupManager): CommandExecutor, Listener {
    private val selectedBlocks = emptyMap<UUID, Block>().toMutableMap()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player){ sender.sendMessage("Fuck off console weirdo"); return true }

        val groupName = args.joinToString()

        // Get the requested group
        // Check that this group actually exists
        // if the group name is Public, then the group does not exist but we still don't want to exit because we want to
        // have public doors
        if (groupManager.getGroup(groupName) == null && !groupName.equals("public", ignoreCase = true)){
            sender.sendMessage("${ChatColor.RED}This is not a registered group, $groupName")
            return false
        }

        val block = selectedBlocks[sender.uniqueId]

        // Send an error message if the player hasn't selected a block
        if (block == null){
            sender.sendMessage("${ChatColor.RED}You haven't selected a door to lock yet!!")
            return false
        }

        // Get the upper door half
        val upperHalf = block.getUpperHalf()

        // Check if the door is already locked
        when (doorLocker.isLocked(upperHalf)) {
            // If not, good lock it
            null -> {
                doorLocker.lockDoor(block.getUpperHalf(), groupName)
            }
            // If it is locked by a player or a group throw an error
            LockedFor.Player -> {
                val lockedFor = doorLocker.lockedFor(block)
                sender.sendMessage("${ChatColor.RED}This door is already locked by the player $lockedFor")
            }
            LockedFor.Group -> {
                val lockedFor = doorLocker.lockedFor(block)
                sender.sendMessage("${ChatColor.RED}This door is already locked by the group $lockedFor")
            }
        }
        return true

    }

    @EventHandler
    fun onPlayerInteraction(e: PlayerInteractEvent){
        // Check if the clicked block is a door
        val isDoor = e.clickedBlock?.type?.name?.let { DoorLocker.WOOD_DOOR_REGEX.matches(it) }?: false
        // Check that we have a left click
        val isLeftClick = e.action == Action.LEFT_CLICK_BLOCK
        // Check that we use a stick for the left click
        val isStick = e.item?.type == Material.STICK

        // Check that we left click a door with a stick
        if (isDoor && isLeftClick && isStick){
            var doorBlock = e.clickedBlock!!.getUpperHalf()

            // Set the selected block to the door
            selectedBlocks[e.player.uniqueId] = doorBlock

            // Cancel the event so we stop clicking
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent){
        selectedBlocks.remove(e.player.uniqueId)
    }
}