package me.yellowbear.uwujobs

import me.yellowbear.uwujobs.jobs.BlockBreak
import me.yellowbear.uwujobs.jobs.BlockPlace
import me.yellowbear.uwujobs.jobs.MobKill
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import java.io.IOException

//Nezamenovat s Entity.Ageable!!!
object Jobs {
    @Throws(IOException::class)
    fun handleJobEvent(event: BlockBreakEvent, jobsMap: Map<BlockBreak, Map<Material?, Int?>>) {
        var ageable = try {
            event.block.blockData as Ageable
        } catch (ex: Exception) {
            null
        }
        if (!(ageable == null || ageable.age == 7)) {
            return
        }
        for (job in BlockBreak.entries) {
            if (jobsMap[job]!![event.block.type] != null) {
                Level.awardXp(event.player, jobsMap[job]!![event.block.type]!!, job)
            }
        }
    }

    @Throws(IOException::class)
    fun handleJobEvent(event: BlockPlaceEvent, jobsMap: Map<BlockPlace, Map<Material?, Int?>>) {
        for (job in BlockPlace.entries) {
            if (jobsMap[job]!![event.block.type] != null) {
                Level.awardXp(event.player, jobsMap[job]!![event.block.type]!!, job)
            }
        }
    }

    @Throws(IOException::class)
    fun handleJobEvent(event: EntityDeathEvent, jobsMap: Map<MobKill, Map<EntityType?, Int?>>) {
        for (job in MobKill.entries) {
            if (jobsMap[job]!![event.entity.type] != null) {
                if (event.entity.killer == null) {
                    return
                }
                Level.awardXp(event.entity.killer, jobsMap[job]!![event.entity.type]!!, job)
            }
        }
    }

    fun handleJobEvent(event: BlockFertilizeEvent) {
        if (event.player == null) {
            return
        }
        Level.awardXp(event.player, 1, BlockBreak.FARMER) //TODO: make reward configurable in #40
    }
}
