package music.saidweb.playlist.offline;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import music.saidweb.playlist.offline.Ringtone.Operation;
import music.saidweb.playlist.offline.Ringtone.RingToneOperation;
import music.saidweb.playlist.offline.Ringtone.StoragePermission;
import music.saidweb.playlist.offline.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements SongFragment.OnListSongsFragmentInteractionListener, MyForeGroundService.CallBacks, StoragePermission {

    private static final String TAG = "##MainActivity##";
    static boolean active = false;
    boolean serviceBound = false;
    ImageView palyAndPuseIV;
    ImageView next;
    ImageView previose;
    TextView songName;
    SeekBar songSeekBar;
    TextView totalTimeTV;
    TextView leftTimeTV;
    private boolean setting = false;
    private boolean storage = false;
    private String fileName = "";
    private int type = 0;
    // service component
    MyForeGroundService mService;
    private final int STORAGE_PERMISSION_CODE = 1;
    private final StoragePermission storagePermission = (StoragePermission) this;

    private int counter = 0;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (serviceBound) {
                if (mService.mediaPlayer != null) {
                    updateProgresLayout(mService.mediaPlayer.getDuration(), mService.mediaPlayer.getCurrentPosition());
                } else {
                    updateProgresLayout(0, 0);

                }
            } leftTimeTV.postDelayed(this, 250);
        }
    };
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: ");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyForeGroundService.LocalBinder binder = (MyForeGroundService.LocalBinder) service; mService = binder.getService();
            mService.registerClient(MainActivity.this); serviceBound = true; leftTimeTV.post(runnable);
            MediaPlayer mediaPlayer = mService.mediaPlayer; boolean isPlaying = false; if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    isPlaying = true;
                } inithilizeLayout(mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition(), mService.curentSongNumber, isPlaying);
            } else {

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: "); serviceBound = false; leftTimeTV.removeCallbacks(runnable);

        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Toast.makeText(MainActivity.this, "cliccc item top", Toast.LENGTH_SHORT).show();

        if (item.getItemId() == R.id.more_app) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.privacy-policy.com/")); startActivity(browserIntent);
        } else if (item.getItemId() == R.id.rate_app) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=music.saidweb.playlist.offline"));
            startActivity(browserIntent);
        } else if (item.getItemId() == R.id.share_app) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND); i.setType("text/plain"); i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String sAux = "\n Hi! Try Off This Amazing App  \"  \" \n\n";
                sAux = sAux + "/https://play.google.com/store/apps/details?id=build.gradle \n\n"; i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        } return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); active = true; setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: ");

        // ads
        AdsManager.getInstance().init(this, Constants.ad_banner); AdsManager.getInstance().init(this, Constants.ad_inter);
        AdsManager.getInstance().init(this, Constants.ad_native); AdsManager.getInstance().loadInterstitialAd(this, Constants.ad_inter);
        AdsManager.getInstance().loadBannerAd(this, Constants.ad_banner);


        //        if (!isMyServiceRunning(MyForeGroundService.class)) {
        Intent intent = new Intent(MainActivity.this, MyForeGroundService.class);
        intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE); startService(intent);
        //        }

        setFragment(SongFragment.newInstance(1));
        //link component with XML
        songName = findViewById(R.id.song_name_tv); totalTimeTV = findViewById(R.id.total_time_tv); leftTimeTV = findViewById(R.id.left_time_tv);
        songSeekBar = findViewById(R.id.song_seek_bar);

        // set listeners
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seek(seekBar.getProgress() * 1000);
            }
        });

        palyAndPuseIV = findViewById(R.id.play_and_pause); palyAndPuseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playOrPause();
            }
        });

        next = findViewById(R.id.next); next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++; if (counter >= Constants.ad_interval) {
                    AdsManager.getInstance().showInterstitialAd(); counter = 0;
                } next();
            }
        });

        previose = findViewById(R.id.previous); previose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++; if (counter >= Constants.ad_interval) {
                    AdsManager.getInstance().showInterstitialAd(); counter = 0;
                } previose();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, MyForeGroundService.class), serviceConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop(); if (serviceBound) {
            unbindService(serviceConnection); serviceBound = false; leftTimeTV.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume(); if (serviceBound) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    // method to control music:

    private void next() {
        if (isMyServiceRunning(MyForeGroundService.class) && mService != null) {
            if (mService.next()) {
                palyAndPuseIV.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            }
        }
    }

    private void previose() {
        if (isMyServiceRunning(MyForeGroundService.class) && mService != null) {
            if (mService.privies()) {
                palyAndPuseIV.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            }
        }
    }

    private void playOrPause() {
        if (isMyServiceRunning(MyForeGroundService.class) && mService != null) {
            if (mService.mediaPlayer != null) {
                if (mService.mediaPlayer.isPlaying()) {
                    palyAndPuseIV.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));

                } else {
                    palyAndPuseIV.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                }
            } else {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            }

            mService.playOrPause();
        }
    }

    void seek(int seekpositon) {
        Intent intent = new Intent(MainActivity.this, MyForeGroundService.class); intent.setAction(MyForeGroundService.ACTION_SEEK);
        intent.putExtra(MyForeGroundService.SEEK_POSITION_KEY, seekpositon); startService(intent);
    }

    void startSong(int songIndex, int seekPosition) {
        Intent intent = new Intent(MainActivity.this, MyForeGroundService.class);
        intent.setAction(MyForeGroundService.ACTION_START);
        intent.putExtra(MyForeGroundService.SONG_NUMBER_KEY, songIndex);
        intent.putExtra(MyForeGroundService.SEEK_POSITION_KEY, seekPosition);
        startService(intent);
    }


    // this method triggered when user click on song from the list
    @Override
    public void onListSongsFragmentInteraction(int songIndex) {
        counter++; if (counter >= Constants.ad_interval) {
            AdsManager.getInstance().showInterstitialAd(); counter = 0;
        } startSong(songIndex, 0); print();
    }

    @Override
    public void optionButtonClicked(int position, int type) {
        //Handel Option Clicked
        System.out.println("------- Option Button Clicked");
        this.type = type;
        this.fileName = DummyContent.ITEMS.get(position).content;
        //First thing first, check the permissions
        checkPermissions();
        //Second set the ringtone
        //setRington()
    }

    private void checkPermissions() {
        requestStoragePermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                storage = true; storagePermission.storageValidation(true);
            } else {
                //Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                storage = false; storagePermission.storageValidation(false);
            }
        } super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void storageValidation(boolean isStorageGranted) {
        if (isStorageGranted) {
            //print("I'm Ready to do Next Step");
            System.out.println("I'm Ready to do Next Step"); storage = true; settingPermission();
        } else {
            System.out.println("I'm not Ready :("); storage = false;
        }
    }

    @Override
    public void setRingtone() {
        if (storage && setting) {
            System.out.println("--------------- I'm ready To Set The Ringtone"); System.out.println("--------------- File Name: " + fileName);

            RingToneOperation ringtone = new RingToneOperation(this); Operation file = ringtone.createRingToneFile(fileName);
            Log.i(TAG, "setRingtone :" + file.isSuccess() + file.getFile()); if (file.isSuccess()) {
                ringtone.SetAsRingtoneOrNotification(file.getFile(), type);
                //Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();

                switch (type) {
                    case RingtoneManager.TYPE_ALARM:
                        Toast.makeText(this, getString(R.string.add_to_alarem), Toast.LENGTH_SHORT).show(); break;
                    case RingtoneManager.TYPE_RINGTONE:
                        Toast.makeText(this, getString(R.string.add_to_rington), Toast.LENGTH_SHORT).show(); break;
                }
            } else {
                System.out.println("Something went Wrong: " + file.isSuccess() + " " + file);
            }
        } else {
            System.out.println("--------------- you don't have the permission To Set The Ringtone");
        }
    }


    private void settingPermission() {
        //Check Setting Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                setting = true; System.out.println("---------- I have The Permission :)"); setRingtone();
            } else {

                System.out.println("---------- I will ask for The Permission"); Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName())); resultLauncher.launch(intent);

            }
        } else {
            setting = true;
        }
    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            //                    if (result.getResultCode() == Activity.RESULT_OK) {
            //                        // There are no request codes
            //                        Intent data = result.getData();
            //                        doSomeOperations();
            //                    }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setting = Settings.System.canWrite(getApplicationContext()); if (setting) {
                    storagePermission.setRingtone();
                }
            }
        }
    });


    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // android 11
            if (Environment.isExternalStorageManager()) {
                storagePermission.storageValidation(true);
            } else {
                openManageAllPermissionSettings();
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this).setTitle(R.string.please_give_the_permission).setMessage(R.string.please_give_the_permission_message).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    void openManageAllPermissionSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION); intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(String.format("package:%s", this.getApplicationContext().getPackageName()))); this.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(); intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION); this.startActivity(intent);
        }
    }

    protected void setFragment(Fragment fragment) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction(); t.replace(R.id.songs_container, fragment); t.commit();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        } return false;
    }


    @Override
    public void onMediaStart() {
        int songnumber = mService.curentSongNumber; int songTime = mService.mediaPlayer.getDuration();
        inithilizeLayout(songTime, 0, songnumber, true);
    }

    void inithilizeLayout(int songTime, int leftTime, int songnumber, boolean isPlaying) {
        songName.setText("" + DummyContent.ITEMS.get(songnumber).content);

        int minuets = (songTime / 1000) / 60; int seconds = (songTime / 1000) % 60; totalTimeTV.setText("" + minuets + ":" + seconds);
        updateProgresLayout(songTime, leftTime); if (isPlaying) {
            palyAndPuseIV.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        } else {
            palyAndPuseIV.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
    }

    void updateProgresLayout(int toatalTime, int leftTime) {
        int secLeftTime = leftTime / 1000; int secTotalTime = toatalTime / 1000; songSeekBar.setMax(secTotalTime);
        songSeekBar.setProgress(secLeftTime); int minuets = secLeftTime / 60; int seconds = secLeftTime % 60;
        leftTimeTV.setText("" + minuets + ":" + seconds);
    }

    @Override
    public void onMediaPause() {

    }

    @Override
    public void onMediaStop() {

    }

    public void print() {
        System.out.println("----------- Hi There ------------");
    }


}
