package me.yellowbear.uwujobs.jobs

import me.yellowbear.uwujobs.Config
import java.sql.Statement

class JobPlayer(xp: Int) {
    var xp = xp
        private set

    fun addXp(amount: Int): Int {
        xp += amount
        return xp
    }

    fun saveToDb(statement: Statement, id: String, job: String) {
        if (Config.config.use_mysql) {
            statement.execute("INSERT INTO ${job.lowercase()} (id, xp) VALUES ('$id', $xp) ON DUPLICATE KEY UPDATE xp = $xp")
        } else {
            statement.execute("INSERT OR REPLACE INTO ${job.lowercase()} (id, xp) VALUES ('$id', $xp)")
        }
    }
}
