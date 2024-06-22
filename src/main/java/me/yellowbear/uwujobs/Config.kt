package me.yellowbear.uwujobs

import com.charleskorn.kaml.Yaml
import me.yellowbear.uwujobs.interfaces.Config
import me.yellowbear.uwujobs.jobs.Job
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

    /**
     * Loads the job data from a config file.
     */
    fun loadJobs() {
        UwuJobs().saveResource("jobs.yml", false)

        val jobsYaml = File(UwuJobs().dataFolder, "jobs.yml").readText()
        val config = Yaml.default.decodeFromString(Config.serializer(), jobsYaml)
        jobs = config.jobs
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
}
