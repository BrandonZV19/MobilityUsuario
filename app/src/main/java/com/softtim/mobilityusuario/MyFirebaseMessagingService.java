package com.softtim.mobilityusuario;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by softtim on 8/24/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Realm realm;
    private SharedPreferences preferences;
    private static final String TAG = "MyFirebaseMsgService";
    MediaPlayer mp;
    Uri soundConfirmado, soundAlerta;
    WakeLocker wakeLocker;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getBaseContext().getApplicationContext());

        soundConfirmado = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.confirmado);
        soundAlerta = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.confirmado);

    }

    /**
     * Called when message is received.
     *
     *      *
     *
     *
     *     Savar cualquier notificacion que no se haya completado su fucnion correctamente para
     *     recuperarla en cualquier estado de la aplicacion    *********************************
     *
     *
     *      *
     *
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */



    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        preferences=getApplicationContext().getSharedPreferences(getString(R.string.preferences),MODE_PRIVATE);

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());


            if (remoteMessage.getData().get("message").contains("servicio_cancelado")){

                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm=Realm.getDefaultInstance();
                //realm.setAutoRefresh(true);

                final int idd=Integer.parseInt(remoteMessage.getData().get("idServicio"));
                final int stat=Integer.parseInt(remoteMessage.getData().get("idStatus"));

                // All changes to data must happen in a transaction
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //Se guarda como cancelado
                        Servicio result = realm.where(Servicio.class).equalTo("id",idd).findFirst();
                        result.setCancelado(1);
                        realm.insertOrUpdate(result);

                    }
                });
                realm.close();

                SharedPreferences.Editor editorC=preferences.edit();
                editorC.putInt(getString(R.string.current_cancelado),1);
                editorC.putInt(getString(R.string.current_cancelado_v),0);
                editorC.commit();

                whatToDo("servicio_cancelado");
                sendNotification("servicio_cancelado");


            }

            if (remoteMessage.getData().get("message").contains("servicio_compartido")){


                whatToDo("servicio_compartido");
                sendNotification("servicio_compartido");


            }


            if (remoteMessage.getData().get("message").contains("status_servicio")){

                RealmConfiguration config = new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm.setDefaultConfiguration(config);

                realm=Realm.getDefaultInstance();

                final int idd=Integer.parseInt(remoteMessage.getData().get("idServicio"));
                final int stat=Integer.parseInt(remoteMessage.getData().get("idStatus"));
                final float costo_c=Float.parseFloat(remoteMessage.getData().get("dCostoConductor"));
                final int costo_u=Integer.parseInt(remoteMessage.getData().get("dCostoUsuario"));
                final int costo_s=Integer.parseInt(remoteMessage.getData().get("dCostoSugerido"));
                final int llegada_origen=Integer.parseInt(remoteMessage.getData().get("lTaxiLlegaOrigen"));
                final int ppCosto=        Integer.parseInt(remoteMessage.getData().get("dCostoPaypal"));
                final int ppAcreditado=   Integer.parseInt(remoteMessage.getData().get("idStatusPaypal"));
                final int ppSolicitado=   Integer.parseInt(remoteMessage.getData().get("lSolicitudPaypal"));

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //Se guarda como cancelado
                        Servicio result = realm.where(Servicio.class).equalTo("id",idd).findFirst();
                        result.setStatus(stat);
                        realm.insertOrUpdate(result);

                    }
                });
                realm.close();

                SharedPreferences.Editor editor=preferences.edit();
                editor.putInt(getString(R.string.current_service),idd);
                editor.putInt(getString(R.string.current_status),stat);
                editor.putFloat(getString(R.string.current_costo_conductor),costo_c);
                editor.putInt(getString(R.string.current_costo_usuario),costo_u);
                editor.putInt(getString(R.string.current_costo_sugerido),costo_s);
                editor.putInt(getString(R.string.current_llego_origen),llegada_origen);
                editor.putInt(getString(R.string.current_acreditado_pp),ppAcreditado);
                editor.putInt(getString(R.string.current_costo_pp),ppCosto);
                editor.putInt(getString(R.string.current_solicitado_pp),ppSolicitado);
                if (stat==2){
                    editor.putInt(getString(R.string.current_confirmado),1);
                }
                editor.commit();

                if (stat==2){
                    whatToDo("confirmado");
                    sendNotification("confirmado");
                }else {
                    whatToDo("status_servicio");
                    sendNotification("status_servicio");
                }
            }

            if (remoteMessage.getData().get("message").contains("test")){
                whatToDo("test");
                sendNotification("test");
            }

        }

        // Check if message contains a notification payload.
        //Aqui estoy recibiendo de consola
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getBody());
            whatToDo(remoteMessage.getNotification().getBody());
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody FCM message body received
     */




    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (messageBody.contains("status_servicio")){
            //Uri sound = Uri.parse("android.resource://com.brandonzamudio.softtim.mobilityusuario/" + R.raw.taximine);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Actualizacion de status")
                    .setAutoCancel(true)
                    //.setSound(sound)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

        if (messageBody.contains("confirmado")){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Conductor confirmado")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

        if (messageBody.contains("test")){
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.premier)
                    .setContentTitle("Notificación de testeo")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }


    }

    private void whatToDo(String messageBody){
        wakeLocker=new WakeLocker();
        wakeLocker.acquire(getApplicationContext());

        if (messageBody.contains("test")){
            Intent intent = new Intent(getBaseContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("FMS");
            intent.putExtra("whatToDo",messageBody);
            getApplication().startActivity(intent);
        }
        if (messageBody.contains("confirmado")){

            if (mp!=null){
                mp.reset();
            }
            mp = new MediaPlayer();
            try {
                mp.setDataSource(this, soundConfirmado);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);

            mp.prepareAsync();

            mp.setVolume(1.0f, 1.0f);
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);
            mp.setAudioSessionId(1); //manually assign an ID here

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            Intent intent = new Intent(getBaseContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("FMS");
            intent.putExtra("whatToDo",messageBody);
            getApplication().startActivity(intent);
        }
        if (messageBody.contains("status_servicio")){

            AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0 /*flags*/);

            if (mp!=null){
                mp.reset();
            }
            mp = new MediaPlayer();
            try {
                mp.setDataSource(this, soundConfirmado);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);

            mp.prepareAsync();

            mp.setVolume(1.0f, 1.0f);
            mp.setAudioStreamType(AudioManager.STREAM_ALARM);
            mp.setAudioSessionId(1); //manually assign an ID here

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });


            Intent intent = new Intent(getBaseContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory("FMS");
            intent.putExtra("whatToDo",messageBody);
            getApplication().startActivity(intent);
        }

        wakeLocker.release();

    }

}
