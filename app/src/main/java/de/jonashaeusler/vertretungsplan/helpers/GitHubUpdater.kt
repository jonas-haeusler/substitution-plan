package de.jonashaeusler.vertretungsplan.helpers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.squareup.moshi.Json
import de.jonashaeusler.vertretungsplan.BuildConfig
import de.jonashaeusler.vertretungsplan.network.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Very rough implementation of a GitHub-powered updater.
 *
 * We basically just check whether the version number included in the latest github release
 * exceeds the one the application is build with and download the first asset of that release
 * if it does.
 */
class GitHubUpdater {
    private val gitHubService: GitHubService = GitHubService.create()

    fun isUpdateAvailable(onUpdateAvailable: (release: GitHubRelease) -> Unit) {
        gitHubService.getLatestVersionInfo().enqueue(object : Callback<GitHubRelease> {
            override fun onResponse(call: Call<GitHubRelease>,
                                    response: Response<GitHubRelease>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val version = if (it.tag.startsWith("v"))
                            it.tag.substring(1) else it.tag

                        if (BuildConfig.VERSION_NAME isMoreRecentThan version) {
                            onUpdateAvailable(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GitHubRelease>?, t: Throwable?) {}
        })
    }

    fun downloadAndInstallUpdate(context: Context, release: GitHubRelease) {
        val downloadRequest = DownloadManager.Request(Uri.parse(release.assets[0].downloadUrl))
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(downloadRequest)

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                if (downloadManager.getUriForDownloadedFile(downloadId) == null) return

                val installIntent = Intent(Intent.ACTION_VIEW)
                installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadId),
                        downloadManager.getMimeTypeForDownloadedFile(downloadId))
                context.startActivity(installIntent)

                context.unregisterReceiver(this)
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private infix fun String.isMoreRecentThan(version: String): Boolean {
        val componentsA = this.split(".").map { it.toInt() }
        val componentsB = version.split(".").map { it.toInt() }
        val length = Math.min(componentsA.size, componentsB.size)

        for (i in 0 until length) {
            val componentA = if (componentsA.size > i) componentsA[i] else 0
            val componentB = if (componentsB.size > i) componentsB[i] else 0

            if (componentA > componentB) return true
            else if (componentA < componentB) return false
        }

        return false
    }

    data class GitHubRelease(
            @Json(name = "name") val name: String,
            @Json(name = "tag_name") val tag: String,
            @Json(name = "assets") val assets: List<GitHubAssets>)

    data class GitHubAssets(
            @Json(name = "browser_download_url") val downloadUrl: String)
}