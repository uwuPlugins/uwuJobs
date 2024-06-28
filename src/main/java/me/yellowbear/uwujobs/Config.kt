package me.yellowbear.uwujobs

import com.charleskorn.kaml.Yaml
import me.yellowbear.uwujobs.Config.jobs
import me.yellowbear.uwujobs.Config.loadJobs
import me.yellowbear.uwujobs.interfaces.Config
import me.yellowbear.uwujobs.interfaces.JobsConfig
import me.yellowbear.uwujobs.jobs.Job
import me.yellowbear.uwujobs.jobs.Jobs
import java.io.File

/**
 * Global plugin config object.
 *
 * @property jobs Array of all the configured jobs.
 * @property loadJobs Loads the job data from a config file.
 * @property getJob Returns a job with given name (if exists).
 */
object Config {
    /**
     * Array of all the configured jobs.
     */
    var jobs: Array<Job> = emptyArray()
    var config: Config = Config(false, "", 0, "", "", "")

    /**
     * Loads the job data from a config file.
     */
    fun loadJobs() {
        UwuJobs().saveResource("jobs.yml", false)

        val jobsYaml = File(UwuJobs().dataFolder, "jobs.yml").readText()
        val config = Yaml.default.decodeFromString(JobsConfig.serializer(), jobsYaml)
        jobs = config.jobs

        for (job in jobs) {
            Jobs.jobs[job.name.lowercase()] = HashMap()
        }

        UwuJobs().logger.info("Loaded ${jobs.size} jobs")
    }

    /**
     * Returns a job with given name (if exists).
     *
     * @param name Target job name.
     * @return A job with the specified target name.
     */
    fun getJob(name: String): Job? {
        return jobs.find { it.name == name || it.name.lowercase() == name }
    }

    fun loadConfig() {
        UwuJobs().saveResource("config.yml", false)

        val configYaml = File(UwuJobs().dataFolder, "config.yml").readText()
        config = Yaml.default.decodeFromString(Config.serializer(), configYaml)
    }
}
