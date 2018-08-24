package com.scinc.sczbar;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

class ScanResultDealer {

    static void deal(Activity context, String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        if (ScanConfig.JUST_RETURN_RESULT) {
            Intent intent = new Intent();
            intent.putExtra(ScanConfig.SCAN_RESULT, result);
            context.setResult(ScanConfig.RESPONSE_CODE, intent);
            context.finish();
        }
        if (result.startsWith("http://") || result.startsWith("https://")) {
            Uri uri = Uri.parse(result);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            context.finish();
        }
    }
}
