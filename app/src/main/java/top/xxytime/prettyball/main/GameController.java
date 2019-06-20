package top.xxytime.prettyball.main;

import android.content.Context;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import top.xxytime.prettyball.game.Background;
import top.xxytime.prettyball.game.StartView;

/**
 * 画布类
 * 项目核心逻辑类
 * 1.6创建GameController画布类
 */
public class GameController extends View implements Runnable {
    private static final String TAG = "GameController";
    /**
     * 声明StartView对象
     */
    private StartView startView;

    /**
     * 声明background对象
     */
    private Background background;

    /**
     * 创建构造方法
     *
     * @param context
     */
    public GameController(Context context) {
        super(context);
        background = new Background();
        startView = new StartView();
        new Thread(this).start();
        background.start();
        startView.start();
    }

    /**
     * 触屏操作
     *
     * @param event
     * @return
     */
    public boolean onTouch(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 测试按键操作
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 绘制方法
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        background.onDraw(canvas);
        startView.onDraw(canvas);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 重绘制方法 不断的调用onDraw()方法
            this.postInvalidate();
        }

    }
}
