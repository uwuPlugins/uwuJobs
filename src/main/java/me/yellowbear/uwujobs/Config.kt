package me.yellowbear.uwujobs

import com.charleskorn.kaml.Yaml
import me.yellowbear.uwujobs.interfaces.Config
import me.yellowbear.uwujobs.jobs.Job
import org.bukkit.configuration.file.FileConfiguration

class Config {
    private val config: FileConfiguration = UwuJobs().config
    var jobs = arrayOf<Job>()

    fun load() {
        val configString = config.saveToString()

        val result = Yaml.default.decodeFromString(Config.serializer(), configString)
        jobs = result.jobs
    }
}

