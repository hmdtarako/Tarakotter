package jp.tarako.hmd.tarakotter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : Activity() {

    companion object {
        private const val TAG = "Tarakotter/LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                Log.d(TAG, "login success")
            }

            override fun failure(exception: TwitterException?) {
                Log.d(TAG, "login failure")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val sessionManager = TwitterCore.getInstance().sessionManager
        if (sessionManager.activeSession == null) {
            status_text.text = getString(R.string.status_logout)
        } else {
            status_text.text = getString(R.string.status_login)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        login_button.onActivityResult(requestCode, resultCode, data)
    }
}