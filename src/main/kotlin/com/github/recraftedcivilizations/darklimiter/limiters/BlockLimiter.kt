package com.github.recraftedcivilizations.darklimiter.limiters

import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
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
        TODO("NOT YET IMPLEMENTED")
    }

    /**
     * Remove a block from the [playerBlocks]
     */
    fun removeBlock(block: Block){
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


}