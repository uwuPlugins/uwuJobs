package me.yellowbear.uwujobs.interfaces

import kotlinx.serialization.Serializable
import me.yellowbear.uwujobs.jobs.Job

/**
 * Global plugin config interface.
 *
 * @property jobs Array of all jobs configured.
 */
@Serializable
data class Config(
    val jobs: Array<Job>
)
