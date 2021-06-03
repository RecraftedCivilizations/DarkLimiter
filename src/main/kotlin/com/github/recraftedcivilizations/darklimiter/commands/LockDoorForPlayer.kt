package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.limiters.getUpperHalf
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.type.Door
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

class LockDoorForPlayer(private val doorLocker: DoorLocker): CommandExecutor, Listener {
    private val selectedBlocks = emptyMap<UUID, Block>().toMutableMap()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player){ sender.sendMessage("Fuck off console weirdo"); return true }

        val block = selectedBlocks[sender.uniqueId]

        // Send an error message if the player hasn't selected a block
        if (block == null){
            sender.sendMessage("${ChatColor.RED}You haven't selected a door to lock yet!!")
            return false
        }

        doorLocker.lockDoor(block.getUpperHalf(), sender.uniqueId)
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
            var doorBlock = e.clickedBlock!!

            // We only want to register and check for the upper half blocks
            if ((doorBlock.blockData as Door).half == Bisected.Half.BOTTOM){
                // Get the world we are currently in
                val world = e.player.world
                // The upper half of the door is at the same position as the door, but y + 1
                doorBlock = world.getBlockAt(doorBlock.x, doorBlock.y + 1, doorBlock.z)
            }

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