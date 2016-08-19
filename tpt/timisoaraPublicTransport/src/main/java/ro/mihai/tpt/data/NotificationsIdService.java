package ro.mihai.tpt.data;

import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import ro.mihai.tpt.utils.AppPreferences;

/**
 * Created by Mihai Balint on 8/19/16.
 */
public class NotificationsIdService extends FirebaseInstanceIdService {

    private static final String GLOBAL = "tpt.global";

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        // TODO: send token to server

        AppPreferences pref = new AppPreferences(this);
        String name = pref.getAchieveUserName();
        if (!name.isEmpty())
            FirebaseMessaging.getInstance().subscribeToTopic(pref.getAchieveUserName());
        FirebaseMessaging.getInstance().subscribeToTopic(GLOBAL);
    }
}