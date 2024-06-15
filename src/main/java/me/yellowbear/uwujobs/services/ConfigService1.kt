package me.yellowbear.uwujobs.services

import me.yellowbear.uwujobs.UwuJobs
import me.yellowbear.uwujobs.interfaces.IConfigurableService
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.function.BiFunction

object ConfigService {
    private val plugin: Plugin = JavaPlugin.getPlugin(UwuJobs::class.java)
    private val configurations: MutableMap<String, FileConfiguration> = HashMap()
    private val registeredServices: MutableMap<IConfigurableService, String> = HashMap()
    fun getConfiguration(s: String?): FileConfiguration? {
        return configurations[s]
    }


    fun loadConfigs(): Boolean {
        plugin.reloadConfig()
        plugin.saveDefaultConfig()
        plugin.saveConfig()
        reloadConfigs()
        reloadServices()
        return true //Success
    }

    @Throws(IOException::class)
    fun registerCustomConfig(fileName: String) {
        configurations.put(fileName, createCustomConfig(fileName))
    }

    private fun reloadConfigs() {
        configurations.put("default", plugin.config)
        configurations.replaceAll { s: String, v: FileConfiguration? ->
            try {
                createCustomConfig(s)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    fun registerService(service: IConfigurableService, configName: String) {
        registeredServices.put(service, configName)
        service.reloadConfig(configurations[configName])
    }

    fun reloadServices() {
        for (service in registeredServices.keys) {
            service.reloadConfig(configurations[registeredServices[service]])
        }
    }

    @Throws(IOException::class)
    private fun createCustomConfig(fileName: String): FileConfiguration {
        if (fileName == "default") {
            return plugin.config
        }
        val customConfig: FileConfiguration = YamlConfiguration()
        val file = createCustomConfigFile(fileName)
        try {
            customConfig.load(file)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
        customConfig.save(file)
        return customConfig
    }

    private fun createCustomConfigFile(fileName: String): File {
        val customConfigFile = File(plugin.dataFolder, fileName)
        if (!customConfigFile.exists()) {
            customConfigFile.parentFile.mkdirs()
            plugin.saveResource(fileName, false)
        }
        return customConfigFile
    }
}
