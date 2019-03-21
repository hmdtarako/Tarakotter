package jp.tarako.hmd.tarakotter

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class TarakotterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config : TwitterConfig = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(getConsumerKey(), getConsumerSecret()))
            .debug(true)
            .build()
        Twitter.initialize(config)
    }

    private fun getConsumerKey() : String {
        return getString(R.string.consumer_key)
    }

    private fun getConsumerSecret() : String {
        return getString(R.string.consumer_secret)
    }
}