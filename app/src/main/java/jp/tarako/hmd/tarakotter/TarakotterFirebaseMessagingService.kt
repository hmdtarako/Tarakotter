package jp.tarako.hmd.tarakotter

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TarakotterFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TAG = "TarakotterFirebaseMessagingService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "onMessageReceived: notification=(${remoteMessage.notification?.title}, ${remoteMessage.notification?.body})")
        Log.d(TAG, "onMessageReceived: data=${remoteMessage.data}")
    }
}