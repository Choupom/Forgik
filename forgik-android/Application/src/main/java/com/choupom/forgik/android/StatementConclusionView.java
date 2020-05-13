/*
 * Java
 *
 * Copyright 2019 Andy Poudret. All rights reserved.
 */
package com.choupom.forgik.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class StatementConclusionView extends TextView {

    public static final int INDENT_WIDTH = 35;

    private int depth;

    public StatementConclusionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = getPaint();
        paint.setStrokeWidth(getResources().getDisplayMetrics().density);
        paint.setColor(Color.parseColor("#6F6F6F"));

        Rect rect = new Rect();
        getDrawingRect(rect);

        for (int i = 0; i < this.depth; i++) {
            int x = rect.left + i*INDENT_WIDTH + 14;
            canvas.drawLine(x, rect.top, x, rect.bottom, paint);
        }
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
