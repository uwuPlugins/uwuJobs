package me.yellowbear.uwujobs.jobs

import kotlinx.serialization.Serializable

@Serializable
data class Reward(
    val block: String? = null,
    val entity: String? = null,
    val amount: Int
)

@Serializable
data class Job(
    val name: String,
    val rewards: List<Reward>
)
