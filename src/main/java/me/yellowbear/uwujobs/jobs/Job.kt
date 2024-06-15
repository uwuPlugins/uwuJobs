package me.yellowbear.uwujobs.jobs

enum class Job(private val jobType: JobType) {
    MINER(JobType.BLOCK_BREAK),
    LUMBER(JobType.BLOCK_BREAK),
    FARMER(JobType.BLOCK_BREAK),
    SHOVELER(JobType.BLOCK_BREAK),
    BUILDER(JobType.BLOCK_PLACE),
    HUNTER(JobType.MOB_KILL);

    companion object {
        fun getJob(job: String?): Job? {
            for (jobs in entries) {
                if (jobs.name.equals(job, ignoreCase = true)) {
                    return jobs
                }
            }
            return null
        }

        fun getType(job: Job): JobType? {
            for (jobs in entries) {
                if (jobs == job) {
                    return jobs.jobType
                }
            }
            return null
        }
    }
}
