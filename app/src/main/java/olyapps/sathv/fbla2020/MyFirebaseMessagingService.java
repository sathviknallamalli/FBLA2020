package olyapps.sathv.fbla2020;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.NotificationCompat;

/**
 * Created by sathv on 7/12/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String titles;

            //   body.add(remoteMessage.getNotification().getBody());
            // time.add(remoteMessage.getSentTime() + "");

            String bodys = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();

            long yourmilliseconds = remoteMessage.getSentTime();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date resultdate = new Date(yourmilliseconds);
            String times = sdf.format(resultdate);

            if (remoteMessage.getNotification().getTitle() != null) {
                titles = remoteMessage.getNotification().getTitle();
            } else {
                titles = "OHS FBLA";
            }


            //CUSTOM NOTIFICATION
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(titles)
                            .setContentText(bodys);
            mBuilder.setVibrate(new long[]{250, 250, 250, 250, 250});
            mBuilder.setAutoCancel(true);

            if (titles.contains("Activity Stream")) {
                Intent resultIntent = new Intent(click_action);
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }

            if (titles.contains("Message from")) {
                Intent resultIntent = new Intent(click_action);
                resultIntent.putExtra("viewpage","gotocombo");
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }

            if (titles.contains("Group Chat")) {
                Intent resultIntent = new Intent(click_action);
                resultIntent.putExtra("viewpage","gotocombo");
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }

            if (titles.contains("Group Message")) {
                Intent resultIntent = new Intent(click_action);
                resultIntent.putExtra("viewpage","gotocombo");
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }

            if (titles.contains("Team Event")) {
                Intent resultIntent = new Intent(click_action);
                resultIntent.putExtra("viewpage","gotomyfbla");
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }
            if (titles.contains("Status Update")) {
                Intent resultIntent = new Intent(click_action);
                resultIntent.putExtra("viewpage","gotomyfbla");
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }
            if (titles.contains("You have a reminder")) {
                Intent resultIntent = new Intent(click_action);
                resultIntent.putExtra("viewpage","gotocal");
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        );


                mBuilder.setContentIntent(resultPendingIntent);
            }

            int mNotificationid = (int) System.currentTimeMillis();

            NotificationManager mNotifymgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifymgr.notify(mNotificationid, mBuilder.build());
        }
    }

}
