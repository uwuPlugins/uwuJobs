package me.yellowbear.uwujobs.jobs

import kotlinx.serialization.Serializable

/**
 * An serializable uwuJobs reward object.
 *
 * @property brokenBlocks A list of blocks that give a player a reward for breaking them.
 * @property placedBlocks A list of blocks that give a player a reward for placing them.
 * @property killedEntities A list of entities that give a player a reward for killing them.
 * @property fertilizedBlocks A list of blocks that give a player a reward for fertilizing them.
 * @property amount Amount of XP the player is to receive.
 */
@Serializable
data class Reward(
    val brokenBlocks: List<String>? = null,
    val placedBlocks: List<String>? = null,
    val killedEntities: List<String>? = null,
    val fertilizedBlocks: List<String>? = null,
    val amount: Int
)

/**
 * An serializable uwuJobs job object.
 *
 * @property name Display name of the job.
 * @property rewards A list of rewards that apply to the job.
 */
@Serializable
data class Job(
    val name: String,
    val rewards: List<Reward>
)
