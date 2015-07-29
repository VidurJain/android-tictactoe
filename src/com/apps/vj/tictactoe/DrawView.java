package com.apps.vj.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

public class DrawView extends View {
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	PointF start;
	PointF end;

	public DrawView(Context context) {
		super(context);
		paint.setColor(Color.parseColor("#00ff7f"));
		paint.setStrokeWidth(8);
	}

	public DrawView(Context context, PointF start, PointF end) {
		this(context);
		setPoints(start, end);
	}

	void setPoints(PointF start, PointF end) {
		this.start = start;
		this.end = end;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(start.x, start.y, end.x, end.y, paint);
	}

}