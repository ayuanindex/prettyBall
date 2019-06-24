package top.xxytime.prettyball.game;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Timer;

import top.xxytime.prettyball.main.Main;
import top.xxytime.prettyball.utils.Tools;

/**
 * 玩家实体类
 *
 * @author ayuan
 */
public class Player {
    /**
     * 生命条最大宽度
     */
    public static final int NI_HPBAR_WIDTH = 240;

    /**
     * 生命条高度
     */
    private final int NI_HPBAR_HEIGHT = 6;

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
     * 在构造方法中赋值
     */
    public Player() {
        arrBmpAnimation = new Bitmap[NI_LV_MAX][NI_FRAME_MAX];
        //获取角色图片
        for (int i = 0; i < arrBmpAnimation.length; i++) {
            for (int j = 0; j < arrBmpAnimation[i].length; j++) {
                arrBmpAnimation[i][j] = Tools.readBitmapFromAssets("image/player/p" + i + "_" + j + ".png");
            }
        }
        rectPosition = new Rect();
        //定位玩家的区域位置
        rectPosition.left = Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - NI_WIDTH) / 2;
        rectPosition.top = Main.getRECT_GANESCREEN_Y() + (Main.getRECT_GANESCREEN_HEIGHT() - (int) (NI_HEIGHT * 1.5));
        rectPosition.right = rectPosition.left + NI_WIDTH;
        rectPosition.bottom = rectPosition.top + NI_HEIGHT;

        // 定位玩家生命条的区域位置
        rectHpBar = new Rect();
        paintHpBarBound = new Paint();
        paintHpBarBound.setStyle(Paint.Style.STROKE);
        paintHpBarBound.setStrokeWidth(1);

        paintText = new Paint();
        paintText.setTextSize(11);

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
        //位置重置
        rectPosition.left = Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - NI_WIDTH) / 2;
        rectPosition.right = rectPosition.left + NI_WIDTH;
        //等级重置
        niLV = 0;
        strLv = STR_TEXT_LV + (niLV + 1);

        //分数重置
        niScore = 0;
        strScore = STR_TEXT_SCORE + niScore;
        pScore.x = Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() - 5 - (int) paintText.measureText(STR_TEXT_SCORE + niScore);

        //重置状态

        //重置生命值
        niHP = NI_FRAME_MAX;


    }

    /**
     * 添加回调功能
     */

    /**
     * 删除回调功能
     */

    /**
     * 绘制方法
     */
    public void onDraw(Canvas canvas) {
        //绘制角色
        canvas.drawBitmap(arrBmpAnimation[niLV][niFrame], rectPosition.left, rectPosition.top, null);

    }

    /**
     * 设置玩家状态功能
     */
    public void setState(boolean isMoving, boolean isLeft) {
        this.isMoving = isMoving;
        this.isLeft = isLeft;
        niFrame = isMoving ? (isLeft ? 0 : 3) : (isLeft ? 1 : 4);
        /*if (isMoving) {
            niFrame = isLeft ? 0 : 3;
        } else {
            niFrame = isLeft ? 1 : 4;
        }*/
    }
}
