package de.jonashaeusler.vertretungsplan.data

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import de.jonashaeusler.vertretungsplan.BuildConfig
import de.jonashaeusler.vertretungsplan.data.entities.GitHubRelease
import de.jonashaeusler.vertretungsplan.data.network.github.GitHubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Super simple github based updater, which fetches the tag of the latest github release
 * and compares it to the current version. If they are different, we fetch the newest release asset
 * and prompt the user to install it.
 */
class GitHubUpdater {
    private val gitHubService: GitHubService = GitHubService.create()

    fun isUpdateAvailable(onUpdateAvailable: (release: GitHubRelease) -> Unit) {
        gitHubService.getLatestVersion().enqueue(object : Callback<GitHubRelease> {
            override fun onResponse(call: Call<GitHubRelease>,
                                    response: Response<GitHubRelease>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val latest = it.version.replace("[^\\d.]".toRegex(), "")

                        if (latest != BuildConfig.VERSION_NAME) {
                            onUpdateAvailable(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GitHubRelease>?, t: Throwable?) {}
        })
    }

    fun downloadAndInstallUpdate(context: Context, release: GitHubRelease) {
        val downloadRequest = DownloadManager.Request(Uri.parse(release.downloadLink))
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
}
