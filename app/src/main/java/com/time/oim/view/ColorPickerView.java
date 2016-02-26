package com.time.oim.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPickerView extends View {

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int w = getWidth();  
	    int h = getHeight();  
	    RectF clientRect = new RectF(0,0,w,h);
	    
	    Paint mPaint = new Paint(); 
        mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(10);
	     
        int colors[] = new int[8];  
        float positions[] = new float[8];  
          
        colors[0] = 0xFF000000;  
        positions[0] = 0;  
          
        colors[1] = 0xFFFF0000;  
        positions[1] = 0.14f;  
          
        colors[2] = 0xFFFFFF00;  
        positions[2] = 0.28f;
        
        colors[3] = 0xFF00FF00;  
        positions[3] = 0.42f;
        
        colors[4] = 0xFF00FFFF;  
        positions[4] = 0.56f;
        
        colors[5] = 0xFF0000FF;  
        positions[5] = 0.70f;

        colors[6] = 0xFFFF00FF;  
        positions[6] = 0.85f;

        colors[7] = 0xFFFFFFFF;  
        positions[7] = 1;
          
        LinearGradient shader = new LinearGradient(  
                0, 0,  
                0, h,  
                colors,  
                positions,  
                TileMode.MIRROR);  
        mPaint.setShader(shader); 
		canvas.drawRoundRect(clientRect, w/2, w/2, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);
//		return super.onTouchEvent(event);
		return true;
	}
	
	

}
