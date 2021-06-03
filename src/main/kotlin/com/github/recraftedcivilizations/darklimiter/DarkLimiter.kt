package com.github.recraftedcivilizations.darklimiter

import com.github.darkvanityoflight.recraftedcore.ARecraftedPlugin
import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import org.bukkit.Bukkit

class DarkLimiter: ARecraftedPlugin() {

    override fun onEnable() {
        val locker = DoorLocker()
        Bukkit.getPluginManager().registerEvents(locker, this)
    }

}