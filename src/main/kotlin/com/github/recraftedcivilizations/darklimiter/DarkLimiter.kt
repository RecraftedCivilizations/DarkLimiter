package com.github.recraftedcivilizations.darklimiter

import com.github.darkvanityoflight.recraftedcore.ARecraftedPlugin
import com.github.recraftedcivilizations.darklimiter.commands.*
import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.parser.ConfigParser
import com.github.recraftedcivilizations.darklimiter.parser.dataparser.YMLDataSource
import org.bukkit.Bukkit

class DarkLimiter: ARecraftedPlugin() {

    override fun onEnable() {
        val dataParser = YMLDataSource(dataFolder.absolutePath)
        val configParser = ConfigParser(config)

        // Create the Door locker and selector
        val locker = DoorLocker(dataParser.getAllDoors())
        val selector = DoorSelector()
        // Register them as listeners to receive events
        Bukkit.getPluginManager().registerEvents(locker, this)
        Bukkit.getPluginManager().registerEvents(selector, this)

        // Register all commands
        getCommand("lock")?.setExecutor(LockDoorForPlayer(locker, selector))
        getCommand("unlock")?.setExecutor(UnlockPlayerDoor(locker, selector))
        getCommand("lockForGroup")?.setExecutor(LockDoorForGroup(locker, selector))
        getCommand("unlockGroupDoor")?.setExecutor(UnlockGroupDoors(locker, selector))
        getCommand("reset")?.setExecutor(ResetAllDoors(locker))
        getCommand("forceUnlock")?.setExecutor(ForceUnlock(locker, selector))
    }

}