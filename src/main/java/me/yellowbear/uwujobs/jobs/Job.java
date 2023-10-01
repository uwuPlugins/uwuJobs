package me.yellowbear.uwujobs.jobs;

public enum Job {
    MINER (JobType.BLOCK_BREAK),
    LUMBER (JobType.BLOCK_BREAK),
    FARMER (JobType.BLOCK_BREAK),
    SHOVELER (JobType.BLOCK_BREAK),
    BUILDER (JobType.BLOCK_PLACE),
    HUNTER (JobType.MOB_KILL);
    private final JobType jobType;
    private Job(final JobType jobType) {
        this.jobType = jobType;
    }
    public static Job getJob(String job) {
        for (Job jobs : Job.values()) {
            if (jobs.name().equalsIgnoreCase(job)) {
                return jobs;
            }
        }
        return null;
    }

    public static JobType getType(Job job) {
        for (Job jobs : Job.values()) {
            if (jobs == job) {
                return jobs.jobType;
            }
        }
        return null;
    }
}
