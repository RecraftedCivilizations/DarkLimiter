package com.github.recraftedcivilizations.darklimiter.commands

import com.github.recraftedcivilizations.darkcitizens.DarkCitizens
import com.github.recraftedcivilizations.darkcitizens.groups.GroupManager
import com.github.recraftedcivilizations.darklimiter.limiters.DoorLocker
import com.github.recraftedcivilizations.darklimiter.limiters.LockedFor
import com.github.recraftedcivilizations.darklimiter.limiters.getUpperHalf
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LockDoorForGroup(private val doorLocker: DoorLocker, private val doorSelector: DoorSelector, private val groupManager: GroupManager = DarkCitizens.groupManager): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player){ sender.sendMessage("Fuck off console weirdo"); return true }

        val groupName = args.joinToString()

        // Get the requested group
        // Check that this group actually exists
        // if the group name is Public, then the group does not exist but we still don't want to exit because we want to
        // have public doors
        if (groupManager.getGroup(groupName) == null && !groupName.equals("public", ignoreCase = true)){
            sender.sendMessage("${ChatColor.RED}This is not a registered group, $groupName")
            return false
        }

        val block = doorSelector.getSelectedBlock(sender.uniqueId)

        // Send an error message if the player hasn't selected a block
        if (block == null){
            sender.sendMessage("${ChatColor.RED}You haven't selected a door to lock yet!!")
            return false
        }

        // Get the upper door half
        val upperHalf = block.getUpperHalf()

        // Check if the door is already locked
        when (doorLocker.isLocked(upperHalf)) {
            // If not, good lock it
            null -> {
                doorLocker.lockDoor(block.getUpperHalf(), groupName)
            }
            // If it is locked by a player or a group throw an error
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