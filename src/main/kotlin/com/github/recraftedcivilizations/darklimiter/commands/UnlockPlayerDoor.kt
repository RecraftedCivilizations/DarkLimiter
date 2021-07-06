package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.limiters.LockedFor
import com.github.recraftedcivilizations.darklimiter.limiters.getUpperHalf
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UnlockPlayerDoor(private val doorLocker: DoorLocker, private val doorSelector: DoorSelector): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player){ sender.sendMessage("Fuck off console weirdo"); return true }

        val block = doorSelector.getSelectedBlock(sender.uniqueId)

        // Send an error message if the player hasn't selected a block
        if (block == null){
            sender.sendMessage("${ChatColor.RED}You haven't selected a door to lock yet!!")
            return false
        }

        // Get the upper door half
        val upperHalf = block.getUpperHalf()

        when(doorLocker.isLocked(upperHalf)){
            null -> {
                sender.sendMessage("${ChatColor.RED}This door is already unlocked!!")
            }
            LockedFor.Player -> {
                val uuid = doorLocker.lockedForPlayer(upperHalf)
                if (uuid == sender.uniqueId){
                    doorLocker.unlockDoor(upperHalf)
                }else{
                    sender.sendMessage("${ChatColor.RED}You didn't lock this door, you can't unlock it!!")
                }
            }
            LockedFor.Group -> {
                sender.sendMessage("${ChatColor.RED}You can't unlock this door normally")
            }
        }

        return true
    }
}