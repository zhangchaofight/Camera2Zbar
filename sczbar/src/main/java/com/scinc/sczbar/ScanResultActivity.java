package com.scinc.sczbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import static com.scinc.sczbar.ScanConfig.SCAN_RESULT;

/*** 这个类展示配置之外的扫描结果
 *
 * @author Zhang Chao
 * @date 2018-9-12 17:00
 * @version 1
 */
public class ScanResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        if (getIntent() == null || getIntent().getExtras() == null) {
            return;
        }
        String result = getIntent().getStringExtra(SCAN_RESULT);
        if (TextUtils.isEmpty(result)) {
            return;
        }
        TextView tvResult = findViewById(R.id.tv_scan_result);
        tvResult.setText(String.format("%s%s", tvResult.getText(), result));
    }
}
