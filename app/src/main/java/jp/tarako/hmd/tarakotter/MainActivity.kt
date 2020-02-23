package jp.tarako.hmd.tarakotter

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : Activity() {

    private var timer : Timer? = null
    private val tweetList: MutableList<Tweet> = ArrayList<Tweet>()
    private var tweetAdapter: TweetAdapter? = null

    companion object {
        private const val TAG = "Tarakotter@MainActivity"
        private const val TIMER_PERIOD = 60000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tweetAdapter = TweetAdapter(this, tweetList)
        timeline.adapter = tweetAdapter

        debug_button.setOnClickListener {
            val sessionManager = TwitterCore.getInstance().sessionManager
            val userId = sessionManager.activeSession.userId

            val apiClient = TwitterCore.getInstance().apiClient
            val statusesService = apiClient.statusesService

            val call = apiClient.statusesService.show(1108974005602516992, null, null, null)

            timer?.cancel()
            Log.d(TAG, "Timer is canceled.")

            call.enqueue(object : Callback<Tweet>() {
                override fun success(result: Result<Tweet>?) {
                    Log.d(TAG, "" + result?.data?.user?.id + "/$userId")
                }

                override fun failure(exception: TwitterException?) {
                    Log.d(TAG, "enqueue failure")
                }
            })
        }
        login_button.setOnClickListener {
            tryLogin()
        }
        post_button.setOnClickListener {
            post(edit_text.text.toString())
        }
        account_button.setOnClickListener {
            clearActiveSession()
            tryLogin()
        }
        update_button.setOnClickListener {
            updateTimeline()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isSessionActive()) {
            login_button.visibility = View.GONE
            account_button.visibility = View.VISIBLE

            timer = Timer()
            timer?.schedule(0, TIMER_PERIOD) {
                Log.d(TAG, "TimerAction")
                updateTimeline()
            }
        } else {
            login_button.visibility = View.VISIBLE
            account_button.visibility = View.GONE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
        timer = null
    }

    private fun isSessionActive() : Boolean {
        val sessionManager = TwitterCore.getInstance().sessionManager
        return sessionManager.activeSession != null
    }

    private fun clearActiveSession() {
        val sessionManager = TwitterCore.getInstance().sessionManager
        sessionManager.clearActiveSession()
    }

    private fun tryLogin() {
        Log.d(TAG, "ログイン試行")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun post(str: String) {
        Log.d(TAG, "tweet:$str")

        val apiClient = TwitterCore.getInstance().apiClient
        val statusesService = apiClient.statusesService
        val call = statusesService.update(str, null,null,null,null,null,null,null,null)

        call.enqueue(object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>?) {
                Toast.makeText(applicationContext, getString(R.string.toast_success), Toast.LENGTH_SHORT).show()
                edit_text.text.clear()
            }

            override fun failure(exception: TwitterException?) {
                Toast.makeText(applicationContext, getString(R.string.toast_failure), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTimeline() {
        val apiClient = TwitterCore.getInstance().apiClient
        val statusesService = apiClient.statusesService
        val count = 50
        val call = statusesService.homeTimeline(count, null, null, null, null, null, null)

        call.enqueue(object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>?) {
                result?.let {
                    tweetList.removeAll { true }
                    tweetList.addAll(it.data)
                    tweetAdapter?.notifyDataSetChanged()

                    val msg = "タイムライン取得成功:${it.data.size}件"
                    Log.d(TAG, msg)
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun failure(exception: TwitterException?) {
                val msg = "タイムライン取得失敗"
                Log.d(TAG, msg)
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
