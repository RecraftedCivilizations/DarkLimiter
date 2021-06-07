package com.github.recraftedcivilizations.darklimiter.limiters

import com.github.darkvanityoflight.recraftedcore.api.BukkitWrapper
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

/**
 * @author DarkVanityOfLight
 */

/**
 * Limit the amount of blocks a player can place at once
 */
class BlockLimiter: Listener {
    private val playerBlocks: MutableMap<UUID, MutableSet<Block>> = emptyMap<UUID, MutableSet<Block>>().toMutableMap()


    /**
     * Check if a player can place another block
     * @param uuid The uuid of the player you want to check for
     */
    fun canPlaceOneMore(uuid: UUID): Boolean{
        TODO("NOT YET IMPLEMENTED")
    }

    /**
     * This gets called when a player places a block
     * and it will add an entry to the [playerBlocks]
     */
    private fun placedBlock(uuid: UUID, block: Block){
        // If we don't have a set of blocks yet
        if (playerBlocks[uuid] == null){
            //Create one
            playerBlocks[uuid] = emptySet<Block>().toMutableSet()
        }

        // Add the block to the player set
        playerBlocks[uuid]!!.add(block)
    }

    /**
     * Remove a block from the [playerBlocks]
     * @param block The block to remove
     */
    private fun removeBlock(block: Block){
        // Loop through all map entries
        for ((player, blocks) in playerBlocks){
            // Loop through all blocks for every player
            for (pBlock in blocks){
                // Check if the block to remove equals the current block
                if (pBlock == block){
                    // If yes remove it and return
                    playerBlocks[player]!!.remove(block)
                    return
                }
            }
        }
    }

    /**
     * Reset all blocks for a certain player
     */
    fun resetAllBlocksForPlayer(uuid: UUID){
        val blocks = playerBlocks[uuid]?: return
        for (block in blocks){
            deleteBlock(block)
        }
    }

    /**
     * Delete a block from the world and from the [playerBlocks]
     */
    fun deleteBlock(block: Block){
        // Set the block to air, essentially removing it
        block.type = Material.AIR

        // Delete it from the list
        deleteBlockFromPlayerBlocks(block)
    }

    /**
     * Delete a block from the [playerBlocks]
     */
    private fun deleteBlockFromPlayerBlocks(block: Block){
        TODO("NOT YET IMPLEMENTED")
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(e: BlockPlaceEvent){
        if (canPlaceOneMore(e.player.uniqueId)){
            // Register the placed block
            placedBlock(e.player.uniqueId, e.block)
        }else{
            e.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(e: BlockBreakEvent){
        removeBlock(e.block)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLeave(e: PlayerQuitEvent){
        resetAllBlocksForPlayer(e.player.uniqueId)
    }

}