package top.ayuan.prettyball.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import top.ayuan.prettyball.game.Background;
import top.ayuan.prettyball.game.Ball;
import top.ayuan.prettyball.game.LoadResourceListener;
import top.ayuan.prettyball.game.Player;
import top.ayuan.prettyball.game.StartView;
import top.ayuan.prettyball.utils.Tools;

/**
 * @author ayuan
 * 画布类
 * 项目核心逻辑类
 * 1.6创建GameController画布类
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GameController extends View implements Runnable {
    /**
     * 声明BackGround对象
     */
    private Background background;

    /**
     * 声明StartView对象
     */
    private StartView startView;

    /**
     * 开始开始界面
     */
    private boolean isStartView;

    /**
     * 图片：gameover背景图
     */
    private Bitmap bmpGameOver;

    /**
     * 玩家对象
     */
    private Player player;

    /**
     * 是否结束游戏
     */
    private boolean isGameOver;

    /**
     * 随机出现小球的数量
     */
    private int NUM_RANDOM;

    /**
     * 绘制小球
     */
    private Ball[] balls;
    private boolean isLeft;


    /**
     * 创建构造方法
     *
     * @param context
     */
    public GameController(Context context) {
        super(context);
        //background = new Background();
        startView = new StartView();
        isStartView = true;
        startView.addLoadResourceListener(new LoadResourceMonitor());
        startView.start();
        new Thread(this).start();
        //background.start();
        NUM_RANDOM = Tools.getRandomInt(3, 5);
        balls = new Ball[NUM_RANDOM];
    }

    /**
     * 触屏操作
     *
     * @param event
     * @return
     */
    public boolean onTouch(MotionEvent event) {
        //触屏位置的坐标
        int niTouchX = (int) event.getX();
        //检测屏幕中第一个触点，按下去执行的操作
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //非开始，非结束
                if (!isStartView && !isGameOver) {
                    player.setState(true, niTouchX < Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() / 2);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isStartView) {
                    if (startView.isStartGame()) {
                        //启动游戏
                        startGame();
                    }
                } else {
                    //游戏进行状态
                    if (!isGameOver) {
                        //手指抬起，将任务设置为不移动的状态
                        player.setState(false, niTouchX < Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() / 2);

                    } else {
                        //当前游戏gemeover状态
                        //重新启动 重置restart（）
                        player.reset();
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
        isLeft = true;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                isLeft = true;
                player.setState(true, isLeft);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                isLeft = false;
                player.setState(true, isLeft);
                break;
        }
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
            startView.onDraw(canvas);
        } else {
            //游戏结束
            if (bmpGameOver != null) {
                canvas.drawBitmap(bmpGameOver, Main.getRECT_GANESCREEN_X(), Main.getRECT_GANESCREEN_Y(), null);
            } else {

                //游戏开始
                background.onDraw(canvas);
                player.onDraw(canvas);
                //绘制小球
                for (int i = 0; i < balls.length; i++) {
                    balls[i].onDraw(canvas);
                    balls[i].use();
                }
            }
        }
    }

    /**
     * 程序启动方法
     */
    private void startGame() {
        isStartView = false;
        background.start();
        player.start();//开启玩家线程
        //开启小球管理线程
        for (int i = 0; i < balls.length; i++) {
            balls[i].start();
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
            //重绘制方法 不断的调用onDraw()方法
            this.postInvalidate();
        }
    }

    /**
     * 资源内部类
     */
    private class LoadResourceMonitor implements LoadResourceListener {

        @Override
        public boolean loadResource() {
            background = new Background();

            player = new Player();
            player.addStateListener(new StateMonitor());
            //创建小球对象
            for (int i = 0; i < balls.length; i++) {
                balls[i] = new Ball();
                balls[i].addBallListener(new BallMonitor());
            }
            return true;
        }
    }

    private class StateMonitor implements StateCallBack {
        @Override
        public void notifyGameOver() {
            //结束逻辑
            //状态改变
            if (player.getNiHP() <= 0) {

                isGameOver = true;
                background.close();
                player.close();
                //加载gameOver的背景图
                bmpGameOver = Tools.readBitmapFromAssets("image/system/gameover.png");
            }
        }

        @Override
        public void addBallSpeed() {
            for (int i = 0; i < balls.length; i++) {
                int niSpeed = balls[i].getNiSpeed();
                niSpeed += 2;
                balls[i].setNiSpeed(niSpeed);
            }
        }

        @Override
        public void downBallSpeed() {
            for (int i = 0; i < balls.length; i++) {
                int niSpeed = balls[i].getNiSpeed();
                niSpeed -= 2;
                balls[i].setNiSpeed(niSpeed);
            }
        }
    }

    private class BallMonitor implements BallCallback {

        @Override
        public void collideCheck(Ball ball) {
            boolean collideWith = ball.isCollideWith(player);
            if (collideWith) {
                player.ballDeal(ball.getType());
                ball.reset();
            }
        }
    }
}
