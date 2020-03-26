package jp.tarako.hmd.tarakotter

import android.app.Application
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig

class TarakotterApplication : Application() {

    companion object {
        const val TAG = "TarakotterApplication"
    }

    override fun onCreate() {
        super.onCreate()

        val config : TwitterConfig = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(getConsumerKey(), getConsumerSecret()))
            .debug(true)
            .build()
        Twitter.initialize(config)

        initFirebase()
    }

    private fun getConsumerKey() : String {
        return getString(R.string.consumer_key)
    }

    private fun getConsumerSecret() : String {
        return getString(R.string.consumer_secret)
    }

    private fun initFirebase() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isComplete) {
                Log.d(TAG, "initFirebase: token=${it.result?.token}")
            }
        }
    }
}