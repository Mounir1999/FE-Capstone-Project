package music.saidweb.playlist.offline;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

import java.io.IOException;

import music.saidweb.playlist.offline.dummy.DummyContent;

public class MyForeGroundService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIES = "ACTION_PREVIES";
    public static final String ACTION_SEEK = "ACTION_SEEK";
    // keys
    public static final String SONG_NUMBER_KEY = "SONG_NUMBER_KEY";
    public static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    private static final String TAG = "%%FOREGROUND_SERVICE%%";
    private final IBinder iBinder = new LocalBinder();
    //mediaPlayer
    public MediaPlayer mediaPlayer;
    public int curentSongNumber = 0;
    public int INDEX = 0;
    public int PLAY_STATE = 0;
    public int PAUSE_STATE = 1;
    public int CURRENT_STATE = 1;
    public CallBacks activity;
    private AudioManager audioManager;
    private NotificationManager manager;
    private NotificationCompat.Builder notificationBuilder;
    private PendingIntent pausePendingIntent;
    private PendingIntent nextPendingIntent;
    private PendingIntent previousPendingIntent;
    private RemoteViews contentView;
//    private Object DummyContent;


    public MyForeGroundService() {
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "My foreground service onCreate().");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            requestAudioFocus();
            if (intent.getAction() != null && !intent.getAction().matches("")) {
                String action = intent.getAction();

                switch (action) {
                    case ACTION_START_FOREGROUND_SERVICE:
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            startForegroundService();
//                        else
//                            startForeground(1, new Notification());
                        break;
                    case ACTION_STOP_FOREGROUND_SERVICE:
                        stopForegroundService();
                        break;
                    case ACTION_START:
                        int songIndex = intent.getIntExtra(SONG_NUMBER_KEY, 0);
                        int seekPositon = intent.getIntExtra(SEEK_POSITION_KEY, 0);
                        start(songIndex);
                        break;
                    case ACTION_PLAY:
                        playOrPause();
                        break;
                    case ACTION_PAUSE:
                        playOrPause();
                        break;
                    case ACTION_NEXT:
                        next();
                        break;
                    case ACTION_PREVIES:
                        privies();
                        break;
                    case ACTION_SEEK:
                        int seekPositons = intent.getIntExtra(SEEK_POSITION_KEY, 0);
                        seek(seekPositons);
                }
            }
            Log.e(TAG, "onStartCommand: " + " intent don't contain Action!?");
        }
        return START_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        activity = null;
        return super.onUnbind(intent);
    }


    public void playOrPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                CURRENT_STATE = PLAY_STATE;
                pauseAndPlayNotify();
            } else {
                mediaPlayer.start();
                CURRENT_STATE = PAUSE_STATE;
                pauseAndPlayNotify();
            }
        }
    }

    private void pauseAndPlayNotify(){
        notificationBuilder.clearActions()
                .addAction(R.drawable.previous_icon, "previous", previousPendingIntent)
                .addAction(pauseOrPlay(CURRENT_STATE), "pause/play",pausePendingIntent )
                .addAction(R.drawable.next_icon, "next", nextPendingIntent);

        contentView.setImageViewResource(R.id.pause, pauseOrPlay(CURRENT_STATE));

        //contentView.setOnClickPendingIntent(R.id.pause, pausePendingIntent);

        manager.notify(2,notificationBuilder.build());
    }

    public void start(int songIndex) {
        curentSongNumber = songIndex;
        //manager.notify();
        System.out.println("Current Song: "+DummyContent.ITEMS.get(songIndex));
        initMediaPlayer(curentSongNumber);

        changeTitle();

    }

    private void changeTitle() {

        Log.i(TAG, "changeTitle: " );
        try{
            notificationBuilder.setContentTitle(DummyContent.ITEMS.get(curentSongNumber).content);
        }
        catch (Exception e){
//            notificationBuilder.setContentTitle("Track");
        }
//        contentView.setTextViewText(R.id.trackTitle, DummyContent.ITEMS.get(curentSongNumber).content);
//        manager.notify(2,notificationBuilder.build());
    }

    private int pauseOrPlay(int state){
        if (state == PLAY_STATE){
            return R.drawable.play_icon;
        }else {
            return R.drawable.pause_icon;
        }
    }

    public boolean next() {
        if (curentSongNumber < (DummyContent.ITEMS.size() - 1)) {
            curentSongNumber++;
            initMediaPlayer(curentSongNumber);
            changeTitle();
            return true;
        } else {
            return false;
        }
    }

    public boolean privies() {
        if (curentSongNumber > 0) {
            curentSongNumber--;
            initMediaPlayer(curentSongNumber);
            changeTitle();
            return true;
        } else {
            return false;
        }
    }

    private void seek(int seekPosition) {
        if (mediaPlayer != null)
            mediaPlayer.seekTo(seekPosition);
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {


//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "11")
//                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
//                .setContentTitle("My MUSIC APP")
//                // .setContentText("you can set this text as you need")
////                .setStyle(new NotificationCompat.BigTextStyle()
////                        .bigText("you can set this text as you need....."))
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .addAction(android.R.drawable.ic_delete, "stop", stopPendingIntent)
//                .addAction(android.R.drawable.ic_media_pause, "pause/play", pusePendingIntent);
//        Notification notification = mBuilder.build();
//        startForeground(11, notification);




        Log.d(TAG, "Start foreground service.");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // close intent
        Intent stopIntent = new Intent(this, MyForeGroundService.class);
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        Intent puseIntent = new Intent(this, MyForeGroundService.class);
        puseIntent.setAction(ACTION_PAUSE);
        pausePendingIntent = PendingIntent.getService(this, 0, puseIntent, PendingIntent.FLAG_IMMUTABLE);
        //Next
        Intent nextIntent = new Intent(this, MyForeGroundService.class);
        nextIntent.setAction(ACTION_NEXT);
        nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);
        //Previous
        Intent previousIntent = new Intent(this, MyForeGroundService.class);
        previousIntent.setAction(ACTION_PREVIES);
        previousPendingIntent = PendingIntent.getService(this, 0, previousIntent, PendingIntent.FLAG_IMMUTABLE);

        String NOTIFICATION_CHANNEL_ID = "xom.music.test.offline";
        String channelName = "My Background Service";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        contentView = new RemoteViews(getPackageName() , R.layout.notification );

        contentView.setOnClickPendingIntent(R.id.previous, previousPendingIntent);
        contentView.setOnClickPendingIntent(R.id.pause, pausePendingIntent);
        contentView.setOnClickPendingIntent(R.id.next, nextPendingIntent);
        contentView.setTextViewText(R.id.trackTitle, DummyContent.ITEMS.get(curentSongNumber).content);

        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .setLargeIcon(largeIcon)
                //.setContent(contentView)
                //.setContentTitle("My MUSIC APP")
                .setContentTitle(DummyContent.ITEMS.get(curentSongNumber).content)
                //.addAction(android.R.drawable.ic_delete, "stop", stopPendingIntent)
                //.addAction(android.R.drawable.ic_media_pause, "pause/play", pusePendingIntent)
                .addAction(R.drawable.previous_icon, "previous", previousPendingIntent)
                .addAction(pauseOrPlay(CURRENT_STATE), "pause/play", pausePendingIntent)
                .addAction(R.drawable.next_icon, "next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2))
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)

                .build();

        startForeground(2, notification);
    }

    private void stopForegroundService() {
        Log.e(TAG, "Stop foreground service.");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Stop foreground service and remove the notification.
        if (!MainActivity.active){
            stopForeground(true);
            // Stop the foreground service.
            stopSelf();
        }
    }

    private void initMediaPlayer(int songIndex) {
        INDEX = songIndex;
        DummyContent.DummyItem item = DummyContent.ITEMS.get(songIndex);
        System.out.println("++++++++++++++ DummyItem: "+item);
        String mediaFile = item.content + "." + item.details;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);

        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            AssetFileDescriptor descriptor = getAssets().openFd(mediaFile);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {

            switch (focusState) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // resume playback
                    if (mediaPlayer == null) initMediaPlayer(curentSongNumber);
                    else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Lost focus for an unbounded amount of time: stop playback and release media tokmusic
                    if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Lost focus for a short time, but we have to stop
                    // playback. We don't release the media tokmusic because playback
                    // is likely to resume
                    if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lost focus for a short time, but it's ok to keep playing
                    // at an attenuated level
                    if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                    break;
            }
        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        this.mediaPlayer.start();
        if (activity != null) {
            activity.onMediaStart();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    public void registerClient(Activity activity) {
        this.activity = (CallBacks) activity;
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "chanel name";
            String description = "chanel discription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    interface CallBacks {
        void onMediaStart();

        void onMediaPause();

        void onMediaStop();

    }

    public class LocalBinder extends Binder {
        public MyForeGroundService getService() {
            return MyForeGroundService.this;
        }
    }

}