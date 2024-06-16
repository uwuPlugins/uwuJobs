package me.yellowbear.uwujobs

import me.yellowbear.uwujobs.interfaces.IConfigurableService
import me.yellowbear.uwujobs.jobs.BlockBreak
import me.yellowbear.uwujobs.jobs.BlockPlace
import me.yellowbear.uwujobs.jobs.MobKill
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType
import java.util.*

class BlockSets : IConfigurableService {
    override fun reloadConfig(file: FileConfiguration?) {
        loadConfig(file)
    }

    companion object {
        var breakJobsMap: Map<BlockBreak, Map<Material?, Int?>> = EnumMap(BlockBreak::class.java)
        var placeJobsMap: Map<BlockPlace, Map<Material?, Int?>> = EnumMap(BlockPlace::class.java)
        var killJobsMap: Map<MobKill, Map<EntityType?, Int?>> = EnumMap(MobKill::class.java)
        fun loadConfig(file: FileConfiguration?) {
            breakJobsMap = loadMap(file, BlockBreak.entries.toSet(), Material::getMaterial)
            placeJobsMap = loadMap(file, BlockPlace.entries.toSet(), Material::getMaterial)
            killJobsMap = loadMap(file, MobKill.entries.toSet(), EntityType::fromName)
        }

        private fun <T, U> loadMap(
            file: FileConfiguration?,
            entries: Set<T>,
            convert: (String) -> U?
        ): Map<T, Map<U?, Int?>> {
            val map: MutableMap<T, Map<U?, Int?>> = HashMap()
            for (job in entries) {
                val section = file!!.getConfigurationSection(job.toString())
                val tempMap: MutableMap<U?, Int?> = HashMap()
                if (section != null) {
                    for (key in section.getKeys(true)) {
                        tempMap[convert(key)] = file.getInt(job.toString() + "." + key)
                    }
                    map[job] = tempMap
                }
            }
            return map
        }
    }

}
