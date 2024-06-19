package me.yellowbear.uwujobs

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import me.yellowbear.uwujobs.interfaces.Config
import me.yellowbear.uwujobs.jobs.Job
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

object Config {
    var jobs: Array<Job> = emptyArray()

    fun loadJobs() {
        UwuJobs().saveResource("jobs.yml", false)

        val jobsYaml = File(UwuJobs().dataFolder, "jobs.yml").readText()
        val config = Yaml.default.decodeFromString(Config.serializer(), jobsYaml)
        jobs = config.jobs
        UwuJobs().logger.info("Loaded ${jobs.size} jobs")
    }

    fun getJob(name: String): Job? {
        return jobs.find { it.name == name || it.name.lowercase() == name }
    }
}
