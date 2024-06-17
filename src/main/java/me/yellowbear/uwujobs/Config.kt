package me.yellowbear.uwujobs

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import me.yellowbear.uwujobs.interfaces.Config
import me.yellowbear.uwujobs.jobs.Job
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

class Config {
    var jobs = emptyArray<Job>()

    fun loadJobs() {
        UwuJobs().saveResource("jobs.yml", false)

        val jobsYaml = File(UwuJobs().dataFolder, "jobs.yml").readText()
        UwuJobs().logger.info(jobsYaml)
        jobs = Yaml.default.decodeFromString(Config.serializer(), jobsYaml).jobs
    }
}
