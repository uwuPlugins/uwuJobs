package me.yellowbear.uwujobs.jobs;

public enum BlockPlace {
    BUILDER;
    public static BlockBreak getJob(String job) {
        for (BlockBreak jobs : BlockBreak.values()) {
            if (jobs.name().equalsIgnoreCase(job)) {
                return jobs;
            }
        }
        return null;
    }
}
