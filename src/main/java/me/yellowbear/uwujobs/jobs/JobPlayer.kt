package me.yellowbear.uwujobs.jobs

class JobPlayer(job: String, id: String, xp: Int) {
    var job = job
        private set
    var id = id
        private set
    var xp = xp
        private set

    fun addXp(amount: Int): Int {
        xp += amount
        return xp
    }

    fun saveToDb() {
        // TODO: Save to database
        // Run periodically (time set in config)
    }
}
