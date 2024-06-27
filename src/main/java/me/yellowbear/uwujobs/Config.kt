package me.yellowbear.uwujobs

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.decodeFromString
import me.yellowbear.uwujobs.interfaces.Config
import me.yellowbear.uwujobs.interfaces.JobsConfig
import me.yellowbear.uwujobs.jobs.Job
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

object Config {
    var jobs: Array<Job> = emptyArray()
    var config: Config = Config(false, "", 0, "", "", "")

    fun loadJobs() {
        UwuJobs().saveResource("jobs.yml", false)

        val jobsYaml = File(UwuJobs().dataFolder, "jobs.yml").readText()
        val config = Yaml.default.decodeFromString(JobsConfig.serializer(), jobsYaml)
        jobs = config.jobs
        UwuJobs().logger.info("Loaded ${jobs.size} jobs")
    }

    fun getJob(name: String): Job? {
        return jobs.find { it.name == name || it.name.lowercase() == name }
    }

    fun loadConfig() {
        UwuJobs().saveResource("config.yml", false)

        val configYaml = File(UwuJobs().dataFolder, "config.yml").readText()
        config = Yaml.default.decodeFromString(Config.serializer(), configYaml)
    }
}
