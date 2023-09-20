package me.yellowbear.uwujobs.commands;


import co.aikar.commands.*;
import me.yellowbear.uwujobs.Jobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CmdCompletions {
    public static void RegisterCompletions(PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> completions = manager.getCommandCompletions();
        completions.registerStaticCompletion("jobs", () -> {
            Collection<String> jobs = new HashSet<>();
            for (Jobs job : Jobs.values()) {
                jobs.add(job.name().toLowerCase());
            }
            return jobs;
        });
    }
}
