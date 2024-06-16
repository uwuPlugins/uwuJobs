package me.yellowbear.uwujobs.jobs

import kotlinx.serialization.Serializable

@Serializable
data class Reward(
    val block: String?,
    val entity: String?,
    val amount: Int
)

@Serializable
data class Job(
    val name: String,
    val rewards: List<Reward>
)
