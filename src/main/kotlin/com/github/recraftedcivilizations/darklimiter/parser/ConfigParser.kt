package com.github.recraftedcivilizations.darklimiter.parser

import com.github.darkvanityoflight.recraftedcore.configparser.ARecraftedConfigParser
import org.bukkit.configuration.file.FileConfiguration
import kotlin.properties.Delegates

class ConfigParser(config: FileConfiguration) : ARecraftedConfigParser(config) {
    var maxNumOfBlocks: Int? = null

    override fun read() {
        maxNumOfBlocks = config.getInt(maxNumOfBlocksName)
    }

    companion object{
        const val maxNumOfBlocksName = "maxBlocks"
    }
}