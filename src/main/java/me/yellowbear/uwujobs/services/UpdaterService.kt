package me.yellowbear.uwujobs.services

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import me.yellowbear.uwujobs.interfaces.IConfigurableService
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * A service responsible for managing updates and automatic updating.
 *
 * @property autoUpdate Information about whether the auto updating is turned on in the plugin config.
 */
object UpdaterService : IConfigurableService {
    private var autoUpdate: Boolean = false;
    private val gson: Gson = Gson();
    private val client: HttpClient = HttpClient.newHttpClient();

    /**
     * Performs checks for new available versions of the plugin against GitHub API.
     *
     * @return True if the plugin is up-to-date. Otherwise, false.
     */
    fun Plugin.checkForUpdates(): Boolean {
        val currentVersion = this.description.version
        val latestRelease: GithubReleaseData = getLatestReleaseData();
        val upToDate: Boolean = currentVersion == latestRelease.tagName;

        println()
        return upToDate
    }

    /**
     * GitHub release data from GitHub API
     *
     * @property id ID of the release.
     * @property url URL of the release.
     * @property assetsUrl URL of the release assets.
     * @property uploadUrl Upload URL of the release.
     * @property tagName Tag name of the release.
     */
    data class GithubReleaseData(
            @SerializedName("id") val id: Int,
            @SerializedName("url") val url: String,
            @SerializedName("assets_url") val assetsUrl: String,
            @SerializedName("upload_url") val uploadUrl: String,
            @SerializedName("tag_name") val tagName: String,
    )

    /**
     * Uses GitHub API to get information about the latest release of the plugin
     *
     * @return GitHub release data
     * @see GithubReleaseData
     */
    private fun getLatestReleaseData(): GithubReleaseData {
        val request: HttpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/uwuPlugins/uwuJobs/releases/latest"))
                .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return gson.fromJson(response.body(), GithubReleaseData::class.java);
    }

    /**
     * Reloads data from the sufficient config file
     *
     * @param file The sufficient configuration file that the class subscribes to.
     */
    override fun reloadConfig(file: FileConfiguration?) {
        autoUpdate = file!!.getBoolean("auto-update");
    }
}