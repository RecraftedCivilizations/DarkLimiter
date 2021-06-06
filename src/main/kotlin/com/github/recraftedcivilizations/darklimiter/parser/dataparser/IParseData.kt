package com.github.recraftedcivilizations.darklimiter.parser.dataparser

import org.bukkit.block.Block

/**
 * @author DarkVanityOfLight
 */

/**
 * Parse data to a storage and read from it
 */
interface IParseData {

    /**
     * Get all stored group doors
     * @return A map of all Door blocks with their group they are locked too
     */
    fun getAllDoors(): Map<Block, String>

    /**
     * Write a group door to the data
     * @param door The door block to write
     * @param group The group this door is locked too
     */
    fun writeGroupDoor(door: Block, group: String)

    /**
     * Write group doors
     * @param doors A map with all group doors to write
     */
    fun writeGroupDoors(doors: Map<Block, String>)
}