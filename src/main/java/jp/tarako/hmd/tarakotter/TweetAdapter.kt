package jp.tarako.hmd.tarakotter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.twitter.sdk.android.core.models.Tweet
import kotlinx.android.synthetic.main.tweet_row.view.*

class TweetAdapter(context: Context, val tweetList: List<Tweet>) : BaseAdapter() {
    private val inflater = context.getSystemService(LayoutInflater::class.java)

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
        val view = p1 ?: inflater.inflate(R.layout.tweet_row, p2, false)

        view.screen_name.text = "${tweetList[p0].user.name}@${tweetList[p0].user.screenName}"
        view.tweet_body.text = tweetList[p0].text

        return view
    }
}