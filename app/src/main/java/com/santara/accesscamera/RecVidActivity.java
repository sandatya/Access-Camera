package com.santara.accesscamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.Calendar;

public class RecVidActivity extends AppCompatActivity {

    VideoView myViewVid;
    Button btnRecord;
    Uri movUri;
    MediaController mdc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_vid);
        myViewVid =(VideoView) findViewById(R.id.videoView);
        btnRecord=(Button)findViewById(R.id.buttonRec);

        mdc =new MediaController(this);
        myViewVid.setMediaController(mdc);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordVideo();
            }
        });


    }

    void RecordVideo(){
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File folder = new File(dir + File.separator + "TRESNAVID");
        boolean success = true;

        if (!folder.exists()){
            try{
                success=folder.mkdirs();
                Toast.makeText(getApplicationContext(), "Folder Create Successfull", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        if (success){
            dir=dir+File.separator+"TRESNAVID";
        }

        Calendar calendar = Calendar.getInstance();
        File file = new File(dir,"TRESNAVID"+calendar.getTimeInMillis()+".mp4");
        if (!file.exists()){
            try{
                file.createNewFile();
                movUri= FileProvider.getUriForFile(RecVidActivity.this,getPackageName(),file);
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,movUri);
                startActivityForResult(intent,2000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            try{
                file.delete();
                file.createNewFile();
                movUri= FileProvider.getUriForFile(RecVidActivity.this,getPackageName(),file);
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,movUri);
                startActivityForResult(intent,2000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2000){
            Uri vidUri=data.getData();
            myViewVid.setVideoURI(vidUri);
            myViewVid.start();
        }

    }
}