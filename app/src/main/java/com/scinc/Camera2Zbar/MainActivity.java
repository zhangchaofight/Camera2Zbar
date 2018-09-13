package com.scinc.Camera2Zbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.scinc.sczbar.DecodeImageUtil;
import com.scinc.sczbar.TestActivity;

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
        //noinspection ConstantConditions
        File file = new File(getExternalFilesDir(null).getAbsolutePath());
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
//        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        Intent intent = new Intent(MainActivity.this, TestActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 100) {
            if (data == null) {
                return;
            }
            String result = data.getStringExtra("scan_result");
            if (!TextUtils.isEmpty(result)) {
                ((TextView) findViewById(R.id.scan_result)).setText(String.format("扫描结果 : %s", result));
            }
        }
    }
}
