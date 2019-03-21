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

    companion object {
        private const val TAG = "Tarakotter/MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_logout -> {
                clearActiveSession()
                tryLogin()
                return true
            }
            else -> {
                return super.onMenuItemSelected(featureId, item)
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
}
