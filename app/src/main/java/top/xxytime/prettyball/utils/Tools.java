package top.xxytime.prettyball.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import top.xxytime.prettyball.main.Info;
import top.xxytime.prettyball.main.Main;

/**
 * 工具类
 */
public final class Tools {
    /**
     * 获取asset文件夹底下的资源图片
     *
     * @return 返回bitmap格式的资源图片
     */
    public static final Bitmap readBitmapFromAssets(String strFileName) {
        InputStream is = null;
        Bitmap bmp = null;
        try {
            is = Main.getAsset().open(strFileName);
            bmp = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            // 打印错误信息
            Tools.LogError("read Bitmap error:" + strFileName);
        }
        return bmp;
    }

    // 打印错误
    private static void LogError(String strMsg) {
        Log.e(Info.STR_LOG_TAG, strMsg);
    }

    // 打印调试信息
    public static void LogInfo(String strMsg) {
        Log.e(Info.STR_LOG_TAG, strMsg);
    }
}
