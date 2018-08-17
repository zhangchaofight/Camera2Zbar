package com.scinc.Camera2Zbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.scinc.sczbar.DecodeImageUtil;
import com.scinc.sczbar.ScanActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }
        DecodeImageUtil.decode(getResources());
    }

    public void skip(View view) {
        File file = new File(getExternalFilesDir(null).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivity(intent);
    }
}
