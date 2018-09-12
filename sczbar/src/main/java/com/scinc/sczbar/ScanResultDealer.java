package com.scinc.sczbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/*** 这个类处理扫描结果
 *
 * @author Zhang Chao
 * @date 2018-9-12 17:00
 * @version 1
 */
class ScanResultDealer {

    /**
     * 处理扫描结果
     * @param context 处理结果的上下文
     * @param result 扫描的结果
     */
    static void deal(Activity context, String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        if (ScanConfig.JUST_RETURN_RESULT) {
            Intent intent = new Intent();
            intent.putExtra(ScanConfig.SCAN_RESULT, result);
            context.setResult(ScanConfig.RESPONSE_CODE, intent);
            context.finish();
        } else if (result.startsWith("http://") || result.startsWith("https://")) {
            Uri uri = Uri.parse(result);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            context.finish();
        } else {
            Intent intent = new Intent(context, ScanResultActivity.class);
            intent.putExtra(ScanConfig.SCAN_RESULT, result);
            context.startActivity(intent);
        }
    }
}
