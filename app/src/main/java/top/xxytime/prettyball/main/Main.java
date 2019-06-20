package top.xxytime.prettyball.main;

import android.content.res.AssetManager;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class Main extends AppCompatActivity {
    private static AssetManager asset;
    /**
     * 屏幕适配工作实施
     * 创建一个接口：Info
     * 创建一个游戏区域（Rect）
     */
    private GameController gameController;/*创建GameController的对象*/

    /**
     * 创建一个区域块Rect
     */
    public static final Rect RECT_GAMESCREEB = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1、去除窗体标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 2、任务栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 3、获取显示的对象
        Display display = getWindowManager().getDefaultDisplay();
        // 获取宽度和像素
        int width = display.getWidth();
        // 获取高度
        int height = display.getHeight();
        // 计算游戏界面位置及区域
        RECT_GAMESCREEB.left = (width - Info.NI_GAMESCREEN_WIDTH) / 2;

        /*
         * right
         * top
         * bottom
         */
        RECT_GAMESCREEB.right = RECT_GAMESCREEB.left + Info.NI_GAMESCREEN_WIDTH;// right

        RECT_GAMESCREEB.top = (height - Info.NI_GAMESCREEB_HEIGHT) / 2;// top

        RECT_GAMESCREEB.bottom = RECT_GAMESCREEB.top + Info.NI_GAMESCREEB_HEIGHT;// bottom

        asset = this.getAssets();

        gameController = new GameController(this);// 创建GameController对象

        setContentView(gameController);// 修改setContextView的参数
    }

    /**
     * 重写触屏操作方法onTouchEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gameController.onTouch(event);
        return super.onTouchEvent(event);
    }

    /**
     * 重写onKeyDown
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        gameController.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    public static final int getRECT_GANESCREEN_X(){
        return RECT_GAMESCREEB.left;
    }
    public static final int getRECT_GANESCREEN_WIDTH(){
        return RECT_GAMESCREEB.right;
    }
    public static final int getRECT_GANESCREEN_Y(){
        return RECT_GAMESCREEB.top;
    }
    public static final int getRECT_GANESCREEN_HEIGHT(){
        return RECT_GAMESCREEB.bottom;
    }

    /**
     * @return 返回assetManager
     */
    public static final AssetManager getAsset() {
        return asset;
    }
}
