package com.scinc.sczbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivity extends AppCompatActivity {

    private ScanView scanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        scanView = findViewById(R.id.sv_scan_view);
        scanView.setScanRegion(100, 100, 600, 800);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanView.startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scanView.stopScan();
    }
}
