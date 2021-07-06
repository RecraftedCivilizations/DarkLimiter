package com.github.recraftedcivilizations.darklimiter.commands

import com.github.darkvanityoflight.recraftedcore.api.BukkitWrapper
import com.github.recraftedcivilizations.darklimiter.limiters.BlockLimiter
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ClearAllBlocks(private val blockLimiter: BlockLimiter, private val bukkitWrapper: BukkitWrapper = BukkitWrapper()): CommandExecutor {
    /**
     * Executes the given command, returning its success.
     * <br></br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val resetFor = args.joinToString()

        // If the sender is not a player we can only reset for a given player
        if (sender !is Player){
            // If we don't have any args we can't reset for the given player cuz there is none
            if (args.isEmpty()) { sender.sendMessage("${ChatColor.RED}You need to specify a player name"); return false }

            // If the sender can reset the blocks for someone else
            if (sender.hasPermission("dlm.blocks.reset.other")){
                // Reset them blocks
                resetForPlayer(resetFor, sender)
            }else{
                // If he doesn't have the permissions, tell him
                sender.sendMessage("${ChatColor.RED}You can't reset the blocks of other people!!")
            }
            return true

        }

        // If we have no target
        if (args.isEmpty()){
            // Just reset all blocks for the sender
            blockLimiter.resetAllBlocksForPlayer(sender.uniqueId)
        }else{
            // We specified a target and want to check if the sender can really reset the blocks
            if (sender.hasPermission("dlm.blocks.reset.other")){
                // If yes reset them
                resetForPlayer(resetFor, sender)
            }else{
                // If not, tell him
                sender.sendMessage("${ChatColor.RED}You can't reset the blocks of other people!!")
            }
        }
        return true
    }

    private fun resetForPlayer(name: String, sender: CommandSender){
        val p = bukkitWrapper.getPlayer(name)
        if (p == null){ sender.sendMessage("${ChatColor.RED}Couldn't find that player, try again!!"); return }
        blockLimiter.resetAllBlocksForPlayer(p.uniqueId)
    }
}