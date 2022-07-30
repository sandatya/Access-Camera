package com.santara.accesscamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class playMusicActivity extends AppCompatActivity {
Button btChoose,btStart,btPause,btStop;
TextView tvProgress,tvDuartion,tvTitle;
SeekBar sbAudio;
MediaPlayer mdPlayerAudio;
int startTime=0, endTime=0;
Handler hdlr=new Handler();
Uri backupUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        btStop=(Button) findViewById(R.id.buttonStop);
        btPause=(Button) findViewById(R.id.buttonPause);
        btStart=(Button)findViewById(R.id.buttonStart);
        btChoose=(Button) findViewById(R.id.buttonChooseFileAudio);

        btStop.setEnabled(false);
        btPause.setEnabled(false);
        btStart.setEnabled(false);



        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                try{
                    startActivityForResult(intent,1002);
                }catch(ActivityNotFoundException e){
                    Log.e("ErrorLoad","No Activity can handle picking file");
                }
            }
        });

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdPlayerAudio.start();
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (mdPlayerAudio!=null){
                        mdPlayerAudio.stop();
                    }
                    mdPlayerAudio=new MediaPlayer();
                    mdPlayerAudio.setDataSource(getBaseContext(),backupUri);
                    mdPlayerAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mdPlayerAudio.prepare();
                }catch(Exception e){
                    Log.e("Error Stop",e.getMessage());
                }
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdPlayerAudio.pause();
            }
        });


    }

    private Runnable updateSongTime=new Runnable() {
        @Override
        public void run() {
            startTime=mdPlayerAudio.getCurrentPosition();
            tvProgress.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
            sbAudio.setProgress(startTime);
            hdlr.postDelayed(this,1000);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1002:
                if (resultCode==RESULT_OK){
                    try{
                        if (mdPlayerAudio!=null){
                            mdPlayerAudio.stop();
                        }

                        mdPlayerAudio=new MediaPlayer();
                        backupUri=data.getData();
                        mdPlayerAudio.setDataSource(this,data.getData());
                        mdPlayerAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mdPlayerAudio.prepare();
                        mdPlayerAudio.start();
                        ContentResolver contentResolver=getContentResolver();
                        Cursor cursor = contentResolver.query(backupUri,null,null,null,null);

                        if (cursor!=null){
                            cursor.moveToFirst();
                            do{
                                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                                tvTitle.setText(title);
                            }while(cursor.moveToNext());
                        }

                        endTime=mdPlayerAudio.getDuration();
                        startTime=mdPlayerAudio.getCurrentPosition();
                        sbAudio.setMax(endTime);
                        tvDuartion.setText(String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes(endTime),
                                TimeUnit.MILLISECONDS.toSeconds(endTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime))));

                        tvProgress.setText(String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes(startTime),
                                TimeUnit.MILLISECONDS.toSeconds(startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));

                        sbAudio.setProgress(startTime);
                        hdlr.postDelayed(updateSongTime,100);
                        btStop.setEnabled(true);
                        btPause.setEnabled(true);
                        btStart.setEnabled(true);


                    }catch(Exception e){
                        Log.e("Error Pilih File",e.getMessage());
                    }
                }
                break;
        }
    }
}