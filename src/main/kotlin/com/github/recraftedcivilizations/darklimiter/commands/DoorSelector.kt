package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.limiters.getUpperHalf
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
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
 * The door selector listens to interaction events and stores the last door selected with a stick by a player
 */
class DoorSelector: Listener {
    private val selectedBlocks = emptyMap<UUID, Block>().toMutableMap()

    /**
     * Get the selected door for a player
     * @param player The player to get the selected door for
     * @return The selected door block or null if no door was selected
     */
    fun getSelectedBlock(player: Player): Block?{
        return getSelectedBlock(player.uniqueId)
    }

    /**
     * Get the selected door for a uuid
     * @param uuid The player to get the selected door for
     * @return The selected door block or null if no door was selected
     */
    fun getSelectedBlock(uuid: UUID): Block?{
        return selectedBlocks[uuid]
    }

    @EventHandler(ignoreCancelled = true)
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

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLeave(e: PlayerQuitEvent){
        selectedBlocks.remove(e.player.uniqueId)
    }
}