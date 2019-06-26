package top.xxytime.prettyball.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import top.xxytime.prettyball.main.Main;
import top.xxytime.prettyball.main.StateCallBack;
import top.xxytime.prettyball.utils.Tools;

/**
 * 玩家实体类
 *
 * @author ayuan
 */
public class Player {
    private static final String TAG = "Player";

    /**
     * 生命条最大宽度
     */
    public static final int NI_HPBAR_WIDTH = 240;

    /**
     * 生命条高度
     */
    private final int NI_HPBAR_HEIGHT = 10;

    /**
     * 绘制文本：等级
     */
    private final String STR_TEXT_LV = "lv:";

    /**
     * 绘制文本：分值
     */
    private final String STR_TEXT_SCORE = "score:";

    /**
     * 生命自消减线程频率（毫秒）
     */
    private final int NI_HP_AUTO_HURT_ROTATE = 150;

    /**
     * 基础的移动速度
     */
    private final int NI_SPEED_BASIC = 4;

    /**
     * 角色帧宽度
     */
    private final int NI_WIDTH = 42;

    /**
     * 角色帧高度
     */
    private final int NI_HEIGHT = 46;

    /**
     * 生命等级最大上限
     */
    private final int NI_LV_MAX = 3;

    /**
     * 动画最大帧
     */
    private final int NI_FRAME_MAX = 6;

    /**
     * 动画组：玩家角色动画
     */
    private Bitmap[][] arrBmpAnimation;

    /**
     * 区域：玩家的角色位置
     */
    private Rect rectPosition;

    /**
     * 区域：玩家生命条
     */
    private Rect rectHpBar;

    /**
     * 笔刷：玩家生命条
     */
    private Paint paintHpBar;

    /**
     * 笔刷：玩家生命条框
     */
    private Paint paintHpBarBound;

    /**
     * 笔刷：玩家文本数据
     */
    private Paint paintText;

    /**
     * 线程：生命条自动消减控制器
     */
    private Timer timerHpAutoHurt;

    /**
     * 位置：分数
     */
    private Point pScore;

    /**
     * 位置：等级位置
     */
    private Point pLv;

    /**
     * 文本：等级
     */
    private String strLv;

    /**
     * 文本：分数
     */
    private String strScore;

    /**
     * 颜色组：生命条
     */
    private int[] arrNiHpColor = {
            Color.RED,
            Color.BLUE,
            Color.MAGENTA
    };

    /**
     * 当前帧
     */
    private int niFrame;

    /**
     * 等级下标
     */
    private int niLV;

    /**
     * 角色生命值
     */
    private int niHP;

    /**
     * 分数
     */
    private int niScore;

    /**
     * 🤚停止自动消减生命值计数亮（0代表非停止状态，非0代表停止自消减）
     */
    private int niStopAutoHurtValue;

    /**
     * 是否移动中🏍
     */
    private boolean isMoving;

    /**
     * 移动方向
     * true--->左👈
     * false--->右👉
     */
    private boolean isLeft;

    /**
     * 线程开关
     */
    private boolean isThread;

    /**
     * 回调对象
     */
    private StateCallBack callBack;

    /**
     * 在构造方法中赋值
     */
    public Player() {
        arrBmpAnimation = new Bitmap[NI_LV_MAX][NI_FRAME_MAX];
        // 获取角色图片
        for (int i = 0; i < arrBmpAnimation.length; i++) {
            for (int j = 0; j < arrBmpAnimation[i].length; j++) {
                arrBmpAnimation[i][j] = Tools.readBitmapFromAssets("image/player/p" + (i + 1) + "_" + j + ".png");
            }
        }
        rectPosition = new Rect();
        // 定位玩家的区域位置
        rectPosition.left = Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - NI_WIDTH) / 2;
        rectPosition.top = Main.getRECT_GANESCREEN_Y() + (Main.getRECT_GANESCREEN_HEIGHT() - (int) (NI_HEIGHT * 1.5));
        rectPosition.right = rectPosition.left + NI_WIDTH;
        rectPosition.bottom = rectPosition.top + NI_HEIGHT;

        // 定位玩家生命条的区域位置
        rectHpBar = new Rect();
        rectHpBar.left = Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - NI_HPBAR_WIDTH) / 2;
        rectHpBar.right = rectHpBar.left + NI_HPBAR_WIDTH;
        rectHpBar.top = Main.getRECT_GANESCREEN_Y() + Main.getRECT_GANESCREEN_HEIGHT() - NI_HPBAR_HEIGHT;
        rectHpBar.bottom = rectHpBar.top + NI_HPBAR_HEIGHT;
        niFrame = isLeft ? 1 : 4;

        paintHpBar = new Paint();
        paintHpBarBound = new Paint();
        paintHpBarBound.setStyle(Paint.Style.STROKE);
        paintHpBarBound.setStrokeWidth(1);
        paintText = new Paint();
        paintText.setTextSize(15);

        pLv = new Point(
                Main.getRECT_GANESCREEN_X() + 5,
                Main.getRECT_GANESCREEN_Y() + Main.getRECT_GANESCREEN_HEIGHT() - 5
        );

        pScore = new Point(
                Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() - 5 - (int) paintText.measureText(STR_TEXT_SCORE + niScore),
                pLv.y
        );
        niHP = NI_HPBAR_WIDTH;
        strLv = STR_TEXT_LV + (niLV + 1);
        strScore = STR_TEXT_SCORE + niScore;
    }

    /**
     * 重新开始功能（重置，初始化）
     */
    public void reset() {
        // 位置重置
        rectPosition.left = Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - NI_WIDTH) / 2;
        rectPosition.right = rectPosition.left + NI_WIDTH;
        // 等级重置
        niLV = 0;
        strLv = STR_TEXT_LV + (niLV + 1);

        // 分数重置
        niScore = 0;
        strScore = STR_TEXT_SCORE + niScore;
        pScore.x = Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() - 5 - (int) paintText.measureText(STR_TEXT_SCORE + niScore);

        // 重置状态

        // 重置生命值
        niHP = NI_FRAME_MAX;
    }

    /**
     * 添加回调功能
     *
     * @param callBack--->回调对象
     */
    public void addStateListener(StateCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 删除回调对象
     */
    public void removeStateListener() {
        if (callBack != null) {
            callBack = null;
        }
    }

    /**
     * 绘制方法
     */
    public void onDraw(Canvas canvas) {
        // 绘制角色
        canvas.drawBitmap(arrBmpAnimation[niLV][niFrame], rectPosition.left, rectPosition.top, null);
        // 绘制上一级生命条
        if (niLV > 0 && niHP != NI_HPBAR_WIDTH) {
            paintHpBarBound.setColor(arrNiHpColor[niLV - 1]);
            canvas.drawRect(rectHpBar, paintHpBar);

        }
        // 绘制当前生命条
        paintHpBar.setColor(arrNiHpColor[niLV]);
        canvas.drawRect(rectHpBar, paintHpBar);
        /*canvas.drawRect(
                rectHpBar.left,
                rectHpBar.top,
                rectHpBar.right,
                rectHpBar.left + niHP + 10,
                paintHpBar
        );*/
        // 绘制生命条边框
        canvas.drawRect(rectHpBar, paintHpBarBound);
        // 绘制文本等级
        canvas.drawText(strLv, pLv.x, pLv.y, paintText);
        // 绘制文本分数
        canvas.drawText(strScore, pScore.x, pScore.y, paintText);
    }

    public Rect getRectPosition() {
        return rectPosition;
    }

    public int getNiHP() {
        return niHP;
    }

    /**
     * 更新分数
     */
    public void updateScore(int niValue) {
        niScore += niValue;
        if (0 > niScore) {
            niScore = 0;
        }
        strScore = STR_TEXT_SCORE + niScore;
        pScore.x = Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() - 5 - (int) paintText.measureText(strScore);
    }

    /**
     * 升级功能
     *
     * @return retu true:升级成功，false:升级失败
     */
    public boolean lvUp() {
        if (niLV < NI_LV_MAX - 1) {
            niLV++;
            strLv = STR_TEXT_LV + (niLV + 1);
            return true;
        }
        return false;
    }

    /**
     * 增加生命值
     */
    public void addHp(int niValue) {
        niHP += niValue;
        if (niHP > NI_HPBAR_WIDTH) {
            if (lvUp()) {
                niHP = niHP - NI_HPBAR_WIDTH;
            } else {
                niHP = NI_HPBAR_WIDTH;
            }
        }
    }

    /**
     * 受伤
     */
    public void hurt(int niValue) {
        niHP -= niValue;
        if (niHP <= 0) {
            if (niLV > 0) {
                niLV--;
                strLv = STR_TEXT_LV + (niLV + 1);
                niHP = NI_LV_MAX;
            } else {
                niHP = 0;
                //结束游戏
                gameOver();
            }
        }
    }

    /**
     * 开始自动减少血量
     */

    /**
     * 开始停止自动减血
     *
     * @param niValue:停止时常
     * @return true:表示成功启动停止减血,false:表示处于减血状态
     */
    public boolean stopAutoHurt(int niValue) {
        if (niStopAutoHurtValue != 0) {
            return false;
        }
        niStopAutoHurtValue = 10 * niValue;
        return true;
    }

    /**
     * 左移动方法
     */
    public void moveLeft() {
        niFrame++;
        niFrame = niFrame == 3 ? 0 : niFrame;
        int niSpeed = NI_SPEED_BASIC + niLV;
        //区域:位置设置
        rectPosition.left -= niSpeed;
        rectPosition.right -= niSpeed;

        if (rectPosition.left <= Main.getRECT_GANESCREEN_X()) {
            rectPosition.left = Main.getRECT_GANESCREEN_X();
            rectPosition.right = rectPosition.left + NI_WIDTH;
        }
    }

    /**
     * 右移动方法
     */
    public void moveRight() {
        niFrame++;
        niFrame = niFrame == 6 ? 3 : niFrame;
        int niSpeed = NI_SPEED_BASIC + niLV;
        //区域:位置设置
        rectPosition.left += niSpeed;
        rectPosition.right += niSpeed;

        if (rectPosition.left >= Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH()) {
            rectPosition.right = Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH();
            rectPosition.left = rectPosition.right - NI_WIDTH;
        }
    }


    /**
     * 核心逻辑方法
     */
    public void logic() {
        if (isMoving) {
            if (isLeft) {
                moveLeft();
            } else {
                moveRight();
            }
        }
    }

    /**
     * 处理小球对玩家角色的功能
     *
     * @param niBallType:小球类型
     */
    public void ballDeal(int niBallType) {
        switch (niBallType) {
            case 0:
                //增加生命值
                addHp(25);
                break;
            case 1:
                //加50分
                updateScore(50);
                break;
            case 2:
                if (!stopAutoHurt(5)) {
                    addHp(50);
                }
                break;
            case 3:
                if (!lvUp()) {
                    addHp(Player.NI_HPBAR_WIDTH);
                }
                break;
            case 4:
                hurt(50);
                break;
            case 5:
                updateScore(-200);
                break;
            case 6:
                hurt(getNiHP());
                break;
        }
    }

    /**
     * 结束逻辑
     */
    private void gameOver() {
        if (callBack != null) {
            callBack.notifyGameOver();
        }
    }

    /**
     * 线程启动方法
     */
    public void start() {
        if (!isThread) {
            isThread = true;
            //玩家角色的动画线程启动
            new Thread(new LogicMonitor()).start();
            //生命条自动消减的线程
            timerHpAutoHurt = new Timer();
            timerHpAutoHurt.schedule(new HpAutoHurtMonitor(), 4000, NI_HP_AUTO_HURT_ROTATE);
        }
    }

    /**
     * 线程关闭方法
     */
    public void close() {
        isThread = false;
        if (timerHpAutoHurt != null) {
            timerHpAutoHurt.cancel();
            timerHpAutoHurt = null;
        }
    }

    /**
     * 设置玩家状态功能
     */
    public void setState(boolean isMoving, boolean isLeft) {
        this.isMoving = isMoving;
        this.isLeft = isLeft;
        niFrame = isMoving ? (isLeft ? 0 : 3) : (isLeft ? 1 : 4);
    }

    private class LogicMonitor implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logic();
            }
        }
    }

    /**
     * 生命自减
     */
    private class HpAutoHurtMonitor extends TimerTask {
        @Override
        public void run() {
            if (niStopAutoHurtValue == 0) {
                hurt(1);
            } else {
                niStopAutoHurtValue--;
            }
        }
    }
}
