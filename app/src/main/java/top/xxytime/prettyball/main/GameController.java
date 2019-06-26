package top.xxytime.prettyball.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.ExecutionException;

import top.xxytime.prettyball.game.Background;
import top.xxytime.prettyball.game.LoadResourceListener;
import top.xxytime.prettyball.game.Player;
import top.xxytime.prettyball.game.StartView;
import top.xxytime.prettyball.utils.Tools;

/**
 * 画布类
 * 项目核心逻辑类
 * 1.6创建GameController画布类
 */
public class GameController extends View implements Runnable {
    private static final String TAG = "GameController";

    /**
     * 声明background对象
     */
    private Background back;

    /**
     * 声明StartView对象(开始界面的对象)
     */
    private StartView startView;

    /**
     * 是否是开始界面
     */
    private boolean isStartView;

    /**
     * 图片:GameOver
     */
    private Bitmap bmpGameOver;

    /**
     * 玩家对象
     */
    private Player player;
    /**
     * 游戏结束
     */
    private boolean isGameOver;

    /**
     * 创建构造方法
     *
     * @param context
     */
    public GameController(Context context) {
        super(context);
        back = new Background();
        startView = new StartView();
        isStartView = true;
        startView.addLoadResourceListener(new LoadResourceMonitor());
        startView.start();
        new Thread(this).start();
        back.start();
    }

    /**
     * 程序启动方法
     */
    private void startGame() {
        isStartView = false;
        back.start();
        player.start();//开启玩家线程
        //开启小球管理器线程
    }

    /**
     * 触屏操作
     *
     * @param event
     * @return
     */
    public boolean onTouch(MotionEvent event) {
        //获取触屏位置的坐标
        int niTouchX = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://检测屏幕第一个触电，按下去执行的操作
                //非开始，非结束，
                if (!isStartView && !isGameOver) {
                    player.setState(true, niTouchX < Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() / 2);
                }
                break;
            case MotionEvent.ACTION_UP://松开屏幕触电是执行的操作
                if (isStartView) {
                    if (startView.isStartGame()) {
                        //启动游戏
                        startGame();
                    }
                } else {
                    //游戏进行时状态
                    if (!isGameOver) {
                        player.setState(false, niTouchX < Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() / 2);
                    } else {
                        //当前游戏是GameOver状态
                        //重新启动，重置restart();
                    }
                }
                break;
        }
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
        if (isStartView) {
            //绘制开始界面
            startView.onDraw(canvas);
        } else {
            if (isGameOver) {
                //游戏结束
                if (bmpGameOver != null) {
                    canvas.drawBitmap(bmpGameOver, Main.getRECT_GANESCREEN_X(), Main.getRECT_GANESCREEN_Y(), null);
                }
            } else {
                //游戏进行中
                back.onDraw(canvas);
                //绘制玩家
                player.onDraw(canvas);
                //绘制小球
            }
        }
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

    /**
     * 资源内部类
     */
    private class LoadResourceMonitor implements LoadResourceListener {

        @Override
        public boolean loadResource() {
            back = new Background();
            player = new Player();
            player.addStateListener(new stateMonitor());
            //创建小球对象

            return true;
        }
    }

    private class stateMonitor implements StateCallBack {
        @Override
        public void notifyGameOver() {
            //结束逻辑
            //状态改变
            isGameOver = true;
            back.close();
            player.close();
            //加载GameOver背景图
            bmpGameOver = Tools.readBitmapFromAssets("image/system/gameover.png");
        }
    }
}
