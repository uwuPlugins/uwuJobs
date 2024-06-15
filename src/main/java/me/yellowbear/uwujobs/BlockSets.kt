package me.yellowbear.uwujobs

import me.yellowbear.uwujobs.interfaces.IConfigurableService
import me.yellowbear.uwujobs.jobs.BlockBreak
import me.yellowbear.uwujobs.jobs.BlockPlace
import me.yellowbear.uwujobs.jobs.MobKill
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType

class BlockSets : IConfigurableService {
    override fun reloadConfig(file: FileConfiguration?) {
        loadConfig(file)
    }

    //TODO: Remove duplicate code
    companion object {
        var breakJobsMap: Map<BlockBreak, Map<Material?, Int?>> = HashMap()
        var placeJobsMap: Map<BlockPlace, Map<Material?, Int?>> = HashMap()
        var killJobsMap: Map<MobKill, Map<EntityType?, Int?>> = HashMap()
        fun loadConfig(file: FileConfiguration?) {
            val breakMap: MutableMap<BlockBreak, Map<Material?, Int?>> = HashMap()
            for (job in BlockBreak.entries) {
                val section = file!!.getConfigurationSection(job.name)
                val tempMap: MutableMap<Material?, Int?> = HashMap()
                if (section != null) {
                    for (key in section.getKeys(true)) {
                        tempMap[Material.getMaterial(key)] = file.getInt(job.name + "." + key)
                    }
                    breakMap[job] = tempMap
                }
            }
            breakJobsMap = breakMap

            val placeMap: MutableMap<BlockPlace, Map<Material?, Int?>> = HashMap()
            for (job in BlockPlace.entries) {
                val section = file!!.getConfigurationSection(job.name)
                val tempMap: MutableMap<Material?, Int?> = HashMap()
                if (section != null) {
                    for (key in section.getKeys(true)) {
                        tempMap[Material.getMaterial(key)] = file.getInt(job.name + "." + key)
                    }
                    placeMap[job] = tempMap
                }
            }
            placeJobsMap = placeMap

            val killMap: MutableMap<MobKill, Map<EntityType?, Int?>> = HashMap()
            for (job in MobKill.entries) {
                val section = file!!.getConfigurationSection(job.name)
                val tempMap: MutableMap<EntityType?, Int?> = HashMap()
                if (section != null) {
                    for (key in section.getKeys(true)) {
                        tempMap[EntityType.fromName(key)] = file.getInt(job.name + "." + key)
                    }
                    killMap[job] = tempMap
                }
            }
            killJobsMap = killMap
        }
    }
}
