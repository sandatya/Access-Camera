package com.santara.accesscamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class playMovActivity extends AppCompatActivity {
    VideoView vidPlay;
    Button btPlay;
    MediaController mdc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mov);

        vidPlay=(VideoView) findViewById(R.id.videoView2);
        btPlay=(Button) findViewById(R.id.buttonFile);

        mdc=new MediaController(this);
        vidPlay.setMediaController(mdc);

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("video/mp4");
                fileIntent=Intent.createChooser(fileIntent,"Choose Video");
                startActivityForResult(fileIntent,4000);
            }
        });
    }

    String getRealPathUri(Uri contentUri){
        String result="";
        Cursor cursor=getContentResolver().query(contentUri,null,null,null,null);
        if (cursor==null){
            result=contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int idx=cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            result=cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data==null){
            return;
        }

        if (requestCode==4000){
            try{
                Uri vid=data.getData();
                String path=getRealPathUri(vid);
                vidPlay.setVideoPath(path);
                vidPlay.start();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}