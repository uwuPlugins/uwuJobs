package me.yellowbear.uwujobs.jobs

enum class BlockBreak {
    MINER,
    LUMBER,
    FARMER,
    SHOVELER;

    companion object {
        fun getJob(job: String?): BlockBreak? {
            for (jobs in entries) {
                if (jobs.name.equals(job, ignoreCase = true)) {
                    return jobs
                }
            }
            return null
        }
    }
}

