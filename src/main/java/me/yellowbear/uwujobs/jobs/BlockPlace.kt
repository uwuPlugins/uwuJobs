package me.yellowbear.uwujobs.jobs

enum class BlockPlace {
    BUILDER;

    companion object {
        fun getJob(job: String?): BlockBreak? {
            for (jobs in BlockBreak.entries) {
                if (jobs.name.equals(job, ignoreCase = true)) {
                    return jobs
                }
            }
            return null
        }
    }
}
