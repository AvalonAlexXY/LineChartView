package com.example.chartviewlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.Map;


public class BarChartView extends View {

    private final String TAG = "chartView";
    private Paint mPaint;
    private Paint textPaint;
    //条状图间隔距离
    private int marginLength = 120;
    //字体大小
    private int textSize = 45 ;
    //数据值
    private Map<String,Integer> dataMap;
    //横坐标数值
    private String[] Xnames = {"姓名1","姓名2","姓名3","姓名4","姓名11","姓名12","姓名13","姓名14"};
    //纵坐标数值
    private Integer[] YNums = {56,12,42,5,24,55,18,53};
    //条形图颜色
    private Integer[] chartColors = {Color.RED,Color.YELLOW,Color.BLUE,Color.GREEN,Color.RED,Color.YELLOW,Color.BLUE,Color.GREEN};
    //条形图高度
    private int chartHeight = 0;
    private int mSpeed=1;
    private heightPlus t;
    private Thread t1;
    private Scroller scroller;

    int downx = 0 ;
    int downy = 0 ;

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        textPaint = new Paint();
        textPaint.setTextSize(textSize);

        scroller = new Scroller(getContext());

        if(t==null) {
            t = new heightPlus();
        }
        if(t1==null) {
            t1 = new Thread(t);
            t1.start();
        }
    }


    //    重写之前先了解MeasureSpec的specMode,一共三种类型：
//    EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
//    AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
//    UNSPECIFIED：表示子布局想要多大就多大，很少使用
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.e(TAG, "onMeasure--widthMode-->" + widthMode);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:

                break;
            case MeasureSpec.AT_MOST:

                break;
            case MeasureSpec.UNSPECIFIED:

                break;
        }
        Log.e(TAG, "onMeasure--widthSize-->" + widthSize);
        Log.e(TAG, "onMeasure--heightMode-->" + heightMode);
        Log.e(TAG, "onMeasure--heightSize-->" + heightSize);
    }

    class heightPlus implements Runnable {
        public synchronized void  run() {
            while (true) {
                try {
                    Thread.sleep(mSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                chartHeight ++ ;
                postInvalidate();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();

        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.FILL); // 设置实心
        mPaint.setColor(Color.BLACK);
        //绘制横纵坐标
        canvas.drawLine(marginLength, height - marginLength,
                marginLength,marginLength,mPaint);
        canvas.drawLine(marginLength, height - marginLength,
                width-marginLength,  height-marginLength,mPaint);

        //绘制图标的横线：
        int itemHeight = (height - 3*marginLength) / 4;
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(marginLength,height-marginLength - itemHeight,
                width - marginLength,height - marginLength - itemHeight,mPaint);
        canvas.drawLine(marginLength,height- marginLength - 2*itemHeight,
                width - marginLength,height - marginLength - 2*itemHeight,mPaint);
        canvas.drawLine(marginLength,height- marginLength - 3*itemHeight,
                width - marginLength,height - marginLength - 3*itemHeight,mPaint);
        canvas.drawLine(marginLength,height- marginLength - 4*itemHeight,
                width - marginLength,height - marginLength - 4*itemHeight,mPaint);

        canvas.translate(marginLength, height - marginLength);
        //绘制刻度
        int maxY = handleIntsMax(YNums);
        int xItem = (width-2*marginLength)/2/YNums.length;
        for (int i = 0; i < YNums.length; i++) {
            //绘制纵坐标刻度
            textPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(maxY/4*(i+1)+" ",0, -itemHeight*(i+1), textPaint);
            //绘制横坐标刻度
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(Xnames[i],xItem*i*2+ xItem,marginLength/2,textPaint);
        }

        //绘制横坐标
        for (int i = 0; i < YNums.length; i++) {
            mPaint.setColor(chartColors[i]);
            float margintop = (float)(YNums[i])/maxY;
            int topHeight = (int) (margintop*(height-3*marginLength));
            if(chartHeight <= topHeight) {
                canvas.drawRect(xItem * i * 2 + xItem, -chartHeight,
                        xItem * i * 2 + 2 * xItem, 0, mPaint);
            }else {
                canvas.drawRect(xItem * i * 2 + xItem, -topHeight,
                        xItem * i * 2 + 2 * xItem, 0, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downx = (int) event.getX();
                downy = (int) event.getY();
                Log.e(TAG,"touchx:" + downx + "touchY" + downy);
                break;
            case MotionEvent.ACTION_MOVE:
                int movex = (int) event.getX();
                int moveY = (int) event.getY();
                scroller.startScroll(downx,0,downx-movex,0);
                Log.e(TAG,"movex:" + movex + "moveY" + moveY);

                if(movex!=0 && moveY!=0)
                    scrollTo(downx-movex,0);
                break;
            case MotionEvent.ACTION_UP:
                downx = (int) event.getX();
                downy = (int) event.getY();
                scrollTo(0,0);
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        // 先推断mScroller滚动是否完毕
//        if (scroller.computeScrollOffset()) {
//            // 这里调用View的scrollTo()完毕实际的滚动
//            scrollTo( scroller.getCurrX(), scroller .getCurrY());
//            // 必须调用该方法，否则不一定能看到滚动效果
//            invalidate();
//        }
        super.computeScroll();
    }

    //获取几个数中的最大值
    private Integer handleIntsMax(Integer[] num){
        int maxY = 0;
        for(int i=0;i<num.length;i++){
            if(num[i]>maxY){
                maxY=num[i];
            }
        }

        if(maxY % 10 != 0){
            maxY = (maxY/10 + 1) * 10;
        }
        return maxY;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }


    // TODO: 2018/4/9
    public void setDataMap(Map<String, Integer> dataMap) {
        //通过传递过来的dataMap,获取到横纵坐标的数组
        // YNums =
        // Xnames =
        this.dataMap = dataMap;
    }
}
