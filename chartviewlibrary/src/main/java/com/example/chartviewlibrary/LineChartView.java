package com.example.chartviewlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import java.util.Map;

public class LineChartView extends View {

    private Paint mPaint;
    private Paint textPaint;
    //字体大小
    private int textSize = 35 ;
    private Scroller scroller;
    //条状图间隔距离
    private int marginLength = 120;
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

    //小圆半径
    private int radius = 10;

    private int mSpeed=1;
    private heightPlus t;
    private Thread t1;

    public LineChartView(Context context) {
        super(context);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
            canvas.rotate(45, xItem*i*2+ xItem, 0);
            canvas.drawText(Xnames[i],xItem*i*2+ xItem,marginLength/2,textPaint);
            canvas.rotate(-45, xItem*i*2+ xItem, 0);
        }

        //绘制坐标
        float lastPointX = 0 ,lastPointY = 0;
        for (int i = 0; i < YNums.length; i++) {
            mPaint.setColor(chartColors[i]);
            float margintop = (float)(YNums[i])/maxY;
            float topHeight = (int) (margintop*(height-3*marginLength));

            if(chartHeight <= topHeight) {
                canvas.drawCircle((float) (xItem * i * 2 + 1.5 * xItem), -chartHeight,radius, mPaint);
                if (i != 0) {
                    mPaint.setColor(Color.BLACK);
                    canvas.drawLine(lastPointX,lastPointY, (float) (xItem * i * 2 + 1.5 * xItem),-chartHeight,mPaint);
                }
                lastPointX = (float) (xItem * i * 2 + 1.5 * xItem);
                lastPointY = -chartHeight;
            }else {
                canvas.drawCircle((float) (xItem * i * 2 + 1.5 * xItem), -topHeight,radius, mPaint);
                if (i != 0) {
                    mPaint.setColor(Color.BLACK);
                    canvas.drawLine(lastPointX,lastPointY, (float) (xItem * i * 2 + 1.5 * xItem),-topHeight,mPaint);
                }
                lastPointX = (float) (xItem * i * 2 + 1.5 * xItem);
                lastPointY = -topHeight;
            }


        }
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setMarginLength(int marginLength) {
        this.marginLength = marginLength;
    }

    public void setDataMap(Map<String, Integer> dataMap) {
        this.dataMap = dataMap;
    }

    public void setXnames(String[] xnames) {
        Xnames = xnames;
    }

    public void setYNums(Integer[] YNums) {
        this.YNums = YNums;
    }

    public void setChartColors(Integer[] chartColors) {
        this.chartColors = chartColors;
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
}