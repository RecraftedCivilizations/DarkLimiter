package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darkcitizens.BukkitWrapper
import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class ResetAllDoors(private val doorLocker: DoorLocker, private val bukkitWrapper: BukkitWrapper = BukkitWrapper()): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        // Check if we have args
        if (args.isNotEmpty()) {
            // Join all args to one name
            val name = args.joinToString()
            // Get the player with this name
            val player = bukkitWrapper.getPlayer(name)

            // If he does exist
            if (player != null){
                // Reset all doors for this player
                doorLocker.resetAllForPlayer(player.uniqueId)
            }else{
                // If not reset all doors for the group with this name
                doorLocker.resetAllForGroup(name)
            }

        }else{
            // If we have no args just reset everything
            doorLocker.resetAll()
        }
        return true
    }
}