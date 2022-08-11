package com.cbi.app.trs.core.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.BuildConfig
import com.cbi.app.trs.R
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.domain.network.ServiceInterceptor
import com.cbi.app.trs.domain.repositories.*
import com.cbi.app.trs.features.utils.AppConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: AndroidApplication) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideAndroidApplication(): AndroidApplication = application


    @Provides
    @Singleton
    fun provideRetrofit(userDataCache: UserDataCache, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(createClient(userDataCache, gson))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideSharePreference(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    private fun createClient(userDataCache: UserDataCache, gson: Gson): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor(ServiceInterceptor(userDataCache, gson))
        okHttpClientBuilder.addInterceptor(ChuckInterceptor(application))
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
            okHttpClientBuilder.addInterceptor(OkHttpProfilerInterceptor())
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInOption(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestIdToken(application.resources.getString(R.string.server_client_id)).build()

    @Provides
    @Singleton
    fun provideGoogleSignInClient(gso: GoogleSignInOptions) =
        GoogleSignIn.getClient(application, gso)


    @Provides
    @Singleton
    fun provideSystemDataRepository(dataSource: SystemDataRepository.Network): SystemDataRepository =
        dataSource

    @Provides
    @Singleton
    fun provideAuthenticateRepository(dataSource: AuthenticateRepository.Network): AuthenticateRepository =
        dataSource

    @Provides
    @Singleton
    fun provideMovieRepository(dataSource: MovieRepository.Network): MovieRepository = dataSource

    @Provides
    @Singleton
    fun provideQuizRepository(dataSource: QuizRepository.Network): QuizRepository = dataSource

    @Provides
    @Singleton
    fun provideMobilityRepository(dataSource: MobilityRepository.Network): MobilityRepository =
        dataSource

    @Provides
    @Singleton
    fun provideWorkoutRepository(dataSource: WorkoutRepository.Network): WorkoutRepository =
        dataSource

    @Provides
    @Singleton
    fun providePainRepository(dataSource: PainRepository.Network): PainRepository = dataSource

    @Provides
    @Singleton
    fun provideNotificationRepository(dataSource: NotificationRepository.Network): NotificationRepository =
        dataSource

    @Provides
    @Singleton
    fun providePaymentRepository(dataSource: PaymentRepository.Network): PaymentRepository =
        dataSource

    @Provides
    @Singleton
    fun provideActivityRepository(dataSource: ActivityRepository.Network): ActivityRepository =
        dataSource
}
