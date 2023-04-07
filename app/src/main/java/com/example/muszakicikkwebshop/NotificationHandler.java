package com.example.muszakicikkwebshop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {

    private static final String CHNNEL_ID = "shop_notification_channel";
    private NotificationManager mManager;
    private final int NOTIFICATION_ID = 0;
    private Context context;
    public NotificationHandler(Context context) {
        this.context = context;
        this.mManager =  (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        createChannel();
    }

    public void send(String message){
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHNNEL_ID).
                setContentTitle("Műszaki Cikk Webshop").setContentText(message).setSmallIcon(R.drawable.
                        baseline_shopping_cart_24).setContentIntent(pendingIntent);
        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createChannel(){

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }
        NotificationChannel channel = new NotificationChannel(CHNNEL_ID, "Shop Notification", NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.RED);
        channel.setDescription("Valami történt a Műszaki cikk webshopp-ban");

        mManager.createNotificationChannel(channel);
    }
}
