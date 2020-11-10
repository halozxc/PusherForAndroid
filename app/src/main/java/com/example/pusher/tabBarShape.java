package com.example.pusher;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import static java.lang.Math.PI;

public class tabBarShape extends View {
    float rpos = 0.5f;
    float rtpos = 0.5f;
    float holerate = 0.4f;

    public tabBarShape(Context context) {
        super(context);
    }
    public tabBarShape(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public tabBarShape(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
//
        float holewidth = (float) (holerate*getHeight());

//        this.setLayerType(LAYER_TYPE_SOFTWARE, null);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(15,0,-3,Color.GRAY);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        final float itembarheight = holerate*getHeight();
        final float angle = 89.50f;
        final float cornerradious = 15.0f;
        final float mx = rpos * getWidth();
        final float my = (float) (itembarheight + (1 - holewidth / Math.tan(angle / 180 * PI)+Math.cos(angle / 180 * PI)) * cornerradious);
        final double cornerWidth = cornerradious * Math.sin(angle / 180 * PI);
        final float rx = (float) (mx + holewidth + cornerWidth);
        final float ry = itembarheight+cornerradious;
        final float lx = (float) (mx - holewidth - cornerWidth);
        final float ly = ry;
        RectF leftRect = new RectF(lx-cornerradious, itembarheight,  lx + cornerradious, itembarheight+2*cornerradious);
        RectF rightRect = new RectF((float) (rx - cornerradious), itembarheight, rx+cornerradious, itembarheight+2*cornerradious);
        RectF middleRect = new RectF(mx-holewidth, my-holewidth, mx+holewidth, my +holewidth);


        Path mpath = new Path();
        mpath.moveTo(0,itembarheight);

        mpath.lineTo(lx, itembarheight);

        mpath.arcTo(leftRect, -90, angle);
        mpath.arcTo(middleRect, 90 + angle, -2f * angle);
        mpath.arcTo(rightRect, -90 - angle, angle);
        mpath.lineTo( getWidth(), itembarheight);
        mpath.lineTo( getWidth(), getWidth());
        mpath.lineTo(0,getHeight());
        mpath.lineTo(0,itembarheight);

       canvas.drawPath(mpath,paint);
       paint.setColor(Color.parseColor("#F4A7B9"));

       canvas.drawCircle(rpos*getWidth(),itembarheight,holewidth*0.8f,paint);


    }
}

