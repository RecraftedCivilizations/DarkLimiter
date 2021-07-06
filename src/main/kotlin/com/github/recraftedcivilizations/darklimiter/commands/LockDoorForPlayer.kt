package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.limiters.LockedFor
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

class LockDoorForPlayer(private val doorLocker: DoorLocker, private val doorSelector: DoorSelector,): CommandExecutor, Listener {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player){ sender.sendMessage("Fuck off console weirdo"); return true }

        val block = doorSelector.getSelectedBlock(sender.uniqueId)

        // Send an error message if the player hasn't selected a block
        if (block == null){
            sender.sendMessage("${ChatColor.RED}You haven't selected a door to lock yet!!")
            return false
        }

        val upperHalf = block.getUpperHalf()

        when (doorLocker.isLocked(upperHalf)) {
            null -> {
                doorLocker.lockDoor(block.getUpperHalf(), sender.uniqueId)
            }
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
}