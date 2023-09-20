package me.yellowbear.uwujobs;

import java.util.Arrays;

public enum Jobs {
    MINER,
    LUMBER,
    FARMER,
    SHOVELER;

    public static Jobs getJob(String job) {
        for (Jobs jobs : Jobs.values()) {
            if (jobs.name().equalsIgnoreCase(job)) {
                return jobs;
            }
        }
        return null;
    }
}

