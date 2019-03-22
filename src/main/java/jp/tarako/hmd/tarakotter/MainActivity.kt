package jp.tarako.hmd.tarakotter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private val tweetList: MutableList<Tweet> = ArrayList<Tweet>()
    private var tweetAdapter: TweetAdapter? = null

    companion object {
        private const val TAG = "Tarakotter/MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tweetAdapter = TweetAdapter(this, tweetList)
        timeline.adapter = tweetAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_logout -> {
                clearActiveSession()
                tryLogin()
                true
            }
            R.id.menu_debug -> {
                updateTimeline()
                true
            }
            else -> {
                super.onMenuItemSelected(featureId, item)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (isSessionActive()) {
            login_button.visibility = View.GONE
            post_button.setOnClickListener {
                val str = edit_text.text.toString()
                post(str)
            }
        } else {
            login_button.visibility = View.VISIBLE
            login_button.setOnClickListener {
                tryLogin()
            }
        }
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
        val count = 20
        val call = statusesService.homeTimeline(count, null, null, null, null, null, null)

        call.enqueue(object : Callback<List<Tweet>>() {
            override fun success(result: Result<List<Tweet>>?) {
                result?.let {
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
