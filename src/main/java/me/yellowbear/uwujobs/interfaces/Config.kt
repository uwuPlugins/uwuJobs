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
    val use_mysql: Boolean,
    val mysql_host: String,
    val mysql_port: Int,
    val mysql_username: String,
    val mysql_password: String,
    val mysql_database: String,
    val save_interval: Long
)

@Serializable
data class JobsConfig(
    val jobs: Array<Job>,
)
