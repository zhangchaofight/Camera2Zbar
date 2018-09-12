package com.scinc.sczbar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/*** 这个类扫描bitmap
 *
 * @author Zhang Chao
 * @date 2018-9-12 17:00
 * @version 1
 */
public class DecodeImageUtil {

    private static final String TAG = "DecodeImageUtil";

    public static void decode(Resources res) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.zbar);
        int W = bitmap.getWidth();
        int H = bitmap.getHeight();
        int[] photodata = new int[W * H];
        bitmap.getPixels(photodata, 0, W, 0, 0, W, H); //获取图片原始ARGB数据
        //将RGB转为灰度数据。
        byte[] greyData = new byte[W * H];
        for (int i = 0; i < greyData.length; i++) {
            greyData[i] = (byte) ((((photodata[i] & 0x00ff0000) >> 16)
                    * 19595 + ((photodata[i] & 0x0000ff00) >> 8)
                    * 38469 + ((photodata[i] & 0x000000ff)) * 7472) >> 16);
        }

        Image barcode = new Image(W, H, "GREY");
        barcode.setData(greyData);
        ImageScanner scanner = new ImageScanner();
        int ret = scanner.scanImage(barcode);

        if (ret != 0) {
            SymbolSet syms = scanner.getResults();
            String resultString = "";
            for (Symbol sym : syms) {
                resultString = "" + sym.getData();
            }
            if (resultString.equals("")) {
                Log.d(TAG, "decode: empty");
            } else {
                Log.d(TAG, "decode: " + resultString);
            }
        } else {
            Log.d(TAG, "decode: failed");
        }
    }
}