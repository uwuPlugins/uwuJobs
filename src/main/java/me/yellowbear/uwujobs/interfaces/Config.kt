package me.yellowbear.uwujobs.interfaces

import kotlinx.serialization.Serializable
import me.yellowbear.uwujobs.jobs.Job

@Serializable
data class Config(
    val jobs: Array<Job>
)
