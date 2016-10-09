package ro.mihai.tpt.data;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Mihai Balint on 8/19/16.
 */
public class NotificationsService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        NotificationCompat nc;


        // Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        // Log.d(TAG, "FCM Notification Message: " +
        //        remoteMessage.getNotification());
        // Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        // TODO: store message in database for sunsequent display in list
    }
}
