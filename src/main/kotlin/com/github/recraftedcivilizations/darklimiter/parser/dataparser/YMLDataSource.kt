package com.github.recraftedcivilizations.darklimiter.parser.dataparser

import com.github.darkvanityoflight.recraftedcore.api.BukkitWrapper
import com.github.recraftedcivilizations.darklimiter.limiters.getUpperHalf
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class YMLDataSource(var filePath: String, private val bukkitWrapper: BukkitWrapper = BukkitWrapper()): IParseData {
    private val dataFile : YamlConfiguration = YamlConfiguration()

    init {
        filePath = if (filePath.endsWith("/")){
            "$filePath${fileName}"
        }else{
            "$filePath/${fileName}"
        }

        val file = File(filePath)

        if (file.exists()){
            dataFile.load(file)
            bukkitWrapper.info("Found old data file, loading data")
        }else{
            file.createNewFile()
        }
    }


    /**
     * Get all stored group doors
     * @return A map of all Door blocks with their group they are locked too
     */
    override fun getAllDoors(): Map<Block, String> {
        // Load our file just in case
        load()

        val doors = emptyMap<Block, String>().toMutableMap()
        val groupDoorSection = dataFile.getConfigurationSection(groupDoorSection)!!

        for (key in groupDoorSection.getKeys(false)){
            // Get all stored info
            val location = groupDoorSection.getLocation("$key.$doorLocation")!!
            val owner = groupDoorSection.getString("$key.$doorLocation")!!
            val worldName = groupDoorSection.getString("$key.$worldName")!!

            // Get the block world
            val world = bukkitWrapper.getWorld(UUID.fromString(worldName))!!
            // Get the block
            val block = world.getBlockAt(location)

            // Store it into the map
            doors[block] = owner

        }

        return  doors
    }

    /**
     * Write a group door to the data
     * @param door The door block to write
     * @param group The group this door is locked too
     */
    override fun writeGroupDoor(door: Block, group: String) {
        // Load our file just in case
        load()

        val door = door.getUpperHalf()
        // Get the section where we store our group doors
        val groupDoorSection = dataFile.getConfigurationSection(groupDoorSection)
        // Hash the block we want to store as our key
        val blockHash = door.hashCode()
        // Create a new section for the door with our hash as key
        val blockSection = groupDoorSection?.createSection(blockHash.toString())!!
        // Set the location for the door
        blockSection.set(doorLocation, door.location)
        // Set the owner for the door
        blockSection.set(doorOwner, group)
        // Set the world this door is in
        blockSection.set(worldName, door.world.uid)

        // Save our new data
        save()
    }

    /**
     * Write group doors
     * @param doors A map with all group doors to write
     */
    override fun writeGroupDoors(doors: Map<Block, String>) {

        // Loop through all map entries
        for ((door, group) in doors.entries){
            // And write them to the file
            writeGroupDoor(door, group)
        }
    }

    /**
     * Convert a configuration section to a map of the type [K], [V]
     * @param K The type of the keys
     * @param V The type of the Values
     * @param configurationSection The config section to read from
     */
    private fun <K, V> configSectionToMap(configurationSection: ConfigurationSection): Map<K, V> {
        val output = emptyMap<K, V>().toMutableMap()
        for (key in configurationSection.getKeys(false)){
            output[key as K] = configurationSection.get(key) as V
        }

        return output
    }

    /**
     * Save a map to the given Path
     * @param path The path to save the map under
     * @param map The map to save
     */
    private fun saveMap(path: String, map: Map<String, Any>){
        for (key in map.keys){
            dataFile.set("$path.$key", map[key])
        }
    }

    private fun load(){
        dataFile.load(filePath)
    }

    private fun save(){
        dataFile.save(filePath)
    }

    companion object{
        const val groupDoorSection = "groupDoors"
        const val doorLocation = "location"
        const val doorOwner = "owner"
        const val worldName = "world"
        const val fileName = "data.yml"
    }
}