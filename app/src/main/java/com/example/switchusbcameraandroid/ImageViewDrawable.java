package com.example.switchusbcameraandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class ImageViewDrawable extends androidx.appcompat.widget.AppCompatImageView {
    private Paint currentPaint, transparentPaint, textPaint, pointPaint;
    private int index = -1;
    private int width = 100;
    private int height = 100;
    private float stPx, edPx, stPy, edPy, w116Px, h116Py;
    private static char[] alphatable = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    public ImageViewDrawable(Context context) {
        super(context);
    }

    public ImageViewDrawable(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(this.getResources().getColor(R.color.gridline));  // alpha.r.g.b
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(1);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.WHITE));
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(1);

        pointPaint = new Paint();
        pointPaint.setColor(getResources().getColor(R.color.RED));
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(2);

        transparentPaint = new Paint();
        transparentPaint.setDither(true);
        transparentPaint.setColor(this.getResources().getColor(R.color.TRANSPARENT));  // alpha.r.g.b
        transparentPaint.setStyle(Paint.Style.STROKE);
        transparentPaint.setStrokeJoin(Paint.Join.ROUND);
        transparentPaint.setStrokeCap(Paint.Cap.ROUND);
        transparentPaint.setStrokeWidth(1);


    }

    public ImageViewDrawable(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
        Log.d("onDraw", "canvas:"+width + " * "+height);
        drawGrid1(canvas, width, height);
        switch (index){
            case 2:
                drawFront(canvas);
                break;
            case 3:
                drawBack(canvas);
                break;
            case 4:
                drawLeft(canvas);
                break;
            case 5:
                drawRight(canvas);
                break;
        }
    }

    private void drawGrid1(Canvas canvas, int w, int h){
        Log.d("drawGrid", " index="+index);
        stPx = (float) (w/6.0);
        stPy = (float) (h/12.0);
        edPx = w - stPx;
        edPy = h - stPy;
        w116Px = (float) ((edPx-stPx)/16.0);
        h116Py = (float) ((edPy-stPy)/16.0);

        for (int i=0;i<=16;i++){
            canvas.drawLine(stPx, stPy+i*h116Py, edPx, stPy+i*h116Py, currentPaint);
            canvas.drawLine(stPx+i*w116Px, stPy, stPx+i*w116Px, edPy, currentPaint);
            canvas.drawText(getAlphabet(i), stPx-12, stPy + i*h116Py +5, textPaint);
            canvas.drawText(String.valueOf((i+1)), stPx + i*w116Px - 3, stPy-5, textPaint);
        }

    }

    public void drawFront(Canvas canvas){
        Log.d("drawFront", " index="+index);
        canvas.drawCircle(stPx+8*w116Px, stPy+14*h116Py, 2.5f, pointPaint);
        canvas.drawCircle(stPx+4*w116Px, stPy+10*h116Py, 2.5f, pointPaint);
        canvas.drawCircle(stPx+12*w116Px, stPy+10*h116Py, 2.5f, pointPaint);
    }

    public void drawBack(Canvas canvas){
        Log.d("drawBack", " index="+index);
        canvas.drawCircle(stPx + 3*w116Px, stPy + 7*h116Py, 2.5f, pointPaint);
        canvas.drawCircle(stPx + 13*w116Px, stPy + 7*h116Py, 2.5f, pointPaint);
        canvas.drawCircle(stPx + 8*w116Px, stPy + 12*h116Py, 2.5f, pointPaint);
        canvas.drawLine(stPx+5*w116Px, stPy+10*h116Py, stPx+5*w116Px, stPy+13*h116Py, pointPaint);
        canvas.drawLine(stPx+11*w116Px, stPy+10*h116Py, stPx+11*w116Px, stPy+13*h116Py, pointPaint);
    }

    public void drawLeft(Canvas canvas){
        canvas.drawCircle(stPx+3*w116Px, stPy+9*h116Py, 2.5f, pointPaint);      //J4
        canvas.drawCircle(stPx+5*w116Px, stPy+5*h116Py, 2.5f, pointPaint);      //F5
        canvas.drawCircle(stPx+10*w116Px, stPy+5*h116Py, 2.5f, pointPaint);     //F11
    }

    private void drawRight(Canvas canvas) {
        canvas.drawCircle(stPx+13*w116Px, stPy+9*h116Py, 2.5f, pointPaint);     //J14
        canvas.drawCircle(stPx+12*w116Px, stPy+5*h116Py, 2.5f, pointPaint);     //F13
        canvas.drawCircle(stPx+6*w116Px, stPy+5*h116Py, 2.5f, pointPaint);     //F7
    }

    public void setShow(int num){
        index = num;
    }

    public String getAlphabet(int n){
        return String.valueOf(alphatable[n]);
    }
}
