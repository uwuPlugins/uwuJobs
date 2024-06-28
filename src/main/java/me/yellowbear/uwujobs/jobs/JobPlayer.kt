package me.yellowbear.uwujobs.jobs

import me.yellowbear.uwujobs.Database
import java.sql.Statement

class JobPlayer(xp: Int) {
    var xp = xp
        private set

    fun addXp(amount: Int): Int {
        xp += amount
        return xp
    }

    fun saveToDb(statement: Statement, id: String, job: String) {
        statement.execute("INSERT INTO ${job.lowercase()} (id, xp) VALUES ('$id', $xp) ON DUPLICATE KEY UPDATE xp = $xp")
    }
}
