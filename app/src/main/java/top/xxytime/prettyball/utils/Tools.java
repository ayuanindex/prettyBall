package top.xxytime.prettyball.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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

    /**
     * 获取assets中指定文件夹中的所有资源图片
     *
     * @param strDir 文件夹名称
     * @return 返回资源图片数组
     */
    public static final Bitmap[] readBitmapFolderFromAssets(String strDir) {
        try {
            // 获取指定文件夹下的资源图片所有的资源图片名称
            String[] arrStrFileName = null;
            arrStrFileName = Main.getAsset().list(strDir);
            Tools.LogInfo(Arrays.toString(arrStrFileName));

            //判断当前文件夹中是否有文件
            if (arrStrFileName.length == 0) {
                return null;
            }
            //通过循环将每一张图片获取，添加到数组中
            Bitmap[] arrBmp = new Bitmap[arrStrFileName.length];
            for (int i = 0; i < arrBmp.length; i++) {
                //获取指定路径下的图片放到bitmap数组中
                arrBmp[i] = readBitmapFromAssets(strDir + "/" + arrStrFileName[i]);
            }
            return arrBmp;
        } catch (IOException e) {
            Tools.LogError(strDir + "error------->" + e.getMessage());
        }
        return null;
    }

    /**
     * 打印错误
     *
     * @param strMsg
     */
    private static void LogError(String strMsg) {
        Log.e(Info.STR_LOG_TAG, strMsg);
    }

    /**
     * 打印调试信息
     *
     * @param strMsg
     */
    public static void LogInfo(String strMsg) {
        Log.e(Info.STR_LOG_TAG, strMsg);
    }
}
