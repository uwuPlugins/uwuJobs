package me.yellowbear.uwujobs

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class Database {
    companion object {
        lateinit var dataSource: HikariDataSource

        fun connect() {
            val config = HikariConfig()
            if (Config.config.use_mysql) {
                config.jdbcUrl = "jdbc:mysql://${Config.config.mysql_host}:${Config.config.mysql_port}/${Config.config.mysql_database}"
                config.username = Config.config.mysql_username
                config.password = Config.config.mysql_password
                config.dataSourceProperties["cachePrepStmts"] = "true"
                config.dataSourceProperties["prepStmtCacheSize"] = "250"
            } else {
                config.jdbcUrl = "jdbc:sqlite:${UwuJobs().dataFolder}/uwu.db"
            }
            dataSource = HikariDataSource(config)
        }
    }
}
