package jp.tarako.hmd.tarakotter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.row_tweet.view.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class TweetAdapter(context: Context, val tweetList: List<Tweet>) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH)

    companion object {
        private const val CURRENT_LIMIT_DELAY = 1000 * 60 * 3
    }

    override fun getCount(): Int {
        return tweetList.size
    }

    override fun getItem(position: Int): Any {
        return tweetList[position]
    }

    override fun getItemId(position: Int): Long {
        return tweetList[position].getId()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.row_tweet, parent, false)

        view.screen_name.text = "${tweetList[position].user.name}@${tweetList[position].user.screenName}"
        view.tweet_body.text = tweetList[position].text

        val dateTweet = dateFormat.parse(tweetList[position].createdAt)
        val dateCurrentLimit = Date(System.currentTimeMillis() - CURRENT_LIMIT_DELAY)

        if (dateTweet.after(dateCurrentLimit)) {
            view.recent_mark.visibility = View.VISIBLE
        } else {
            view.recent_mark.visibility = View.GONE
        }

        val task = ImageDownloadTask(view.icon)
        val url = URL(tweetList[position].user.profileImageUrl)
        task.execute(url)

        return view
    }
}