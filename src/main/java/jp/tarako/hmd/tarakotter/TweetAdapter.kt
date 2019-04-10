package jp.tarako.hmd.tarakotter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
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
        val (viewHolder, view) = if(convertView == null) {
            val tweetView = inflater.inflate(R.layout.row_tweet, parent, false)
            val screenName = tweetView.screen_name
            val tweetBody = tweetView.tweet_body
            val recentMark = tweetView.recent_mark
            val icon = tweetView.icon
            val viewHolder = ViewHolder(screenName, tweetBody, recentMark, icon)
            tweetView.tag = viewHolder
            Pair(viewHolder, tweetView)
        } else {
            Pair(convertView.tag as ViewHolder, convertView)
        }

        viewHolder.screenName.text = "${tweetList[position].user.name}@${tweetList[position].user.screenName}"
        viewHolder.tweetBody.text = tweetList[position].text

        val dateTweet = dateFormat.parse(tweetList[position].createdAt)
        val dateCurrentLimit = Date(System.currentTimeMillis() - CURRENT_LIMIT_DELAY)
        val isRecent = dateTweet.after(dateCurrentLimit)
        viewHolder.recentMark.visibility = if (isRecent) View.VISIBLE else View.GONE

        val task = ImageDownloadTask(viewHolder.icon)
        val url = URL(tweetList[position].user.profileImageUrl)
        task.execute(url)

        return view
    }

    class ViewHolder(
        val screenName: TextView,
        val tweetBody: TextView,
        val recentMark: TextView,
        val icon: ImageView)
}