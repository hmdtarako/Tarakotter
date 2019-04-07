package jp.tarako.hmd.tarakotter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.row_tweet.view.*
import java.text.SimpleDateFormat
import java.util.*

class TweetAdapter(context: Context, val tweetList: List<Tweet>) : BaseAdapter() {
    private val inflater = context.getSystemService(LayoutInflater::class.java)
    private val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH)

    companion object {
        private const val CURRENT_LIMIT_DELAY = 1000 * 60 * 3
    }

    override fun getCount(): Int {
        return tweetList.size
    }

    override fun getItem(p0: Int): Any {
        return tweetList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return tweetList[p0].getId()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = p1 ?: inflater.inflate(R.layout.row_tweet, p2, false)

        view.screen_name.text = "${tweetList[p0].user.name}@${tweetList[p0].user.screenName}"
        view.tweet_body.text = tweetList[p0].text

        val dateTweet = dateFormat.parse(tweetList[p0].createdAt)
        val dateCurrentLimit = Date(System.currentTimeMillis() - CURRENT_LIMIT_DELAY)

        if (dateTweet.after(dateCurrentLimit)) {
            view.recent_mark.visibility = View.VISIBLE
        } else {
            view.recent_mark.visibility = View.GONE
        }

        return view
    }
}