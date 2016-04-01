package com.y2w.uikit.customcontrols.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class RoundProgressBar extends View {

    Paint paint,textpaint;
    RectF area;
    int width = 50;
    int value = 100;
    LinearGradient shader;

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }

    public RoundProgressBar(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }

    public void setProgress(int value){
        this.value = value;
        invalidate();
    }
    
    public void init() {
        paint = new Paint();
        paint.setStrokeWidth(15f);
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        textpaint = new Paint();
        textpaint.setTextSize(25f);
        textpaint.setColor(Color.WHITE);
        area = new RectF(10, 10, width + 40, width + 40);
        
        shader =new LinearGradient(0, 0, 400, 0, new int[] {
                 Color.parseColor("#5f5f5f"), Color.WHITE}, null,    
                Shader.TileMode.CLAMP); 
        paint.setShader(shader);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        //canvas.drawColor(Color.GRAY);
        canvas.drawArc(area, 120, 360*value/100 , false, paint);
        canvas.drawText(value+"%", 25, 60, textpaint);
    }

}

