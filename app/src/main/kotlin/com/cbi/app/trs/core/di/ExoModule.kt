package com.cbi.app.trs.core.di

import android.app.Application
import android.content.SharedPreferences
import android.util.Base64
import com.cbi.app.trs.features.fragments.movies.DownloadTracker
import com.cbi.app.trs.features.utils.AppLog
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import java.io.File
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.inject.Named
import javax.inject.Singleton

@Module
class ExoModule(private val context: Application) {
    companion object {
        const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
        private const val TAG = "ExoModule"
        private const val DOWNLOAD_ACTION_FILE = "actions"
        private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
    }

    @Provides
    @Singleton
    @Named("UserAgent")
    fun provideUserAgent(): String {
        return Util.getUserAgent(context, "trs")
    }

    @Provides
    @Singleton
    fun provideDatabaseProvider(): ExoDatabaseProvider = ExoDatabaseProvider(context)

    @Provides
    @Singleton
    fun provideDownloadDirectory(): File {
        context.getExternalFilesDir(null)?.let { return it }
        return context.filesDir
    }

    @Provides
    @Singleton
    fun provideDownloadCache(sharedPreferences: SharedPreferences, downloadDirectory: File, databaseProvider: ExoDatabaseProvider): Cache {
        var key: String? = sharedPreferences.getString("AES_128", null)
        if (key.isNullOrEmpty()) {
            val rand = SecureRandom()
            val generator: KeyGenerator = KeyGenerator.getInstance("AES")
            generator.init(128, rand)
            key = Base64.encodeToString(generator.generateKey().encoded, Base64.NO_WRAP);
            sharedPreferences.edit().putString("AES_128", key).apply()
        }
        return SimpleCache(File(downloadDirectory, DOWNLOAD_CONTENT_DIRECTORY), NoOpCacheEvictor(), databaseProvider,
                Base64.decode(key, Base64.NO_WRAP), true, true)
    }

    @Provides
    @Singleton
    fun provideDataSourceFactory(downloadCache: Cache, httpDataSourceFactory: HttpDataSource.Factory): DataSource.Factory {
        val upstreamFactory = DefaultDataSourceFactory(context, httpDataSourceFactory)
        return buildReadOnlyCacheDataSource(upstreamFactory, downloadCache)
    }

    @Provides
    @Singleton
    fun provideHttpDataSourceFactory(@Named("UserAgent") userAgent: String): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(userAgent)
    }

    private fun buildReadOnlyCacheDataSource(
            upstreamFactory: DataSource.Factory?, cache: Cache?): CacheDataSourceFactory {
        return CacheDataSourceFactory(
                cache,
                upstreamFactory,
                FileDataSource.Factory(),  /* cacheWriteDataSinkFactory= */
                null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,  /* eventListener= */
                null)
    }

    @Provides
    @Singleton
    fun provideDownloadNotificationHelper(): DownloadNotificationHelper {
        return DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
    }

    @Provides
    @Singleton
    fun provideDownloadManager(downloadDirectory: File, databaseProvider: ExoDatabaseProvider, downloadCache: Cache, httpDataSourceFactory: HttpDataSource.Factory): DownloadManager {
        val downloadIndex = DefaultDownloadIndex(databaseProvider)
        upgradeActionFile(downloadDirectory,
                DOWNLOAD_ACTION_FILE, downloadIndex,  /* addNewDownloadsAsCompleted= */false)
        upgradeActionFile(downloadDirectory,
                DOWNLOAD_TRACKER_ACTION_FILE, downloadIndex,  /* addNewDownloadsAsCompleted= */true)
        val downloaderConstructorHelper = DownloaderConstructorHelper(downloadCache, httpDataSourceFactory)
        return DownloadManager(
                context, downloadIndex, DefaultDownloaderFactory(downloaderConstructorHelper))
    }

    @Provides
    @Singleton
    fun provideDownloadTracker(databaseProvider: DataSource.Factory, downloadManager: DownloadManager): DownloadTracker {
        return DownloadTracker(context, databaseProvider, downloadManager)
    }

    private fun upgradeActionFile(downloadDirectory: File,
                                  fileName: String, downloadIndex: DefaultDownloadIndex, addNewDownloadsAsCompleted: Boolean) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                    File(downloadDirectory, fileName),  /* downloadIdProvider= */
                    null,
                    downloadIndex,  /* deleteOnFailure= */
                    true,
                    addNewDownloadsAsCompleted)
        } catch (e: IOException) {
            AppLog.e(TAG, "Failed to upgrade action file: $fileName", e)
        }
    }

    @Provides
    @Singleton
    fun provideCookieManager(): CookieManager {
        return CookieManager().apply { setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER) }
    }

    @Provides
    @Singleton
    fun buildRenderersFactory(): RenderersFactory {
        return DefaultRenderersFactory( /* context= */context)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
    }
}