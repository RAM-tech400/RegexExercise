package com.ramApps.RegexExercise;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ColorSelectionView extends LinearLayout {
    
    private Context context;
	private RectF rectBounds = new RectF();
	private int defaultColor;
	private int selectedColor;
	
	private HeuSaturationBox mHeuSaturationBox;
	private BrightnessSlider mBrightnessSlider;

	public ColorSelectionView(Context context) {
		super(context);
		this.context = context;
		defaultColor = 0xFFE62243;
		init();
	}

	public ColorSelectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		defaultColor = 0xFFFF8227;
		init();
	}

	public ColorSelectionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		defaultColor = 0xFFACCAFF;
		init();
	}
	
	private void init() {
		setOrientation(VERTICAL);
		setClipToOutline(false);
		setClipChildren(false);
		
		selectedColor = defaultColor;
		
		float[] hsv = new float[] {0, 0, 0};
		Color.colorToHSV(defaultColor, hsv);
		
		mHeuSaturationBox = new HeuSaturationBox(context, hsv[0], hsv[1]);
		mHeuSaturationBox.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) getDp(200)));
		addView(mHeuSaturationBox);
		
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, (int) getDp(24));
		lp.setMargins(0, (int) getDp(24), 0, 0);
		mBrightnessSlider = new BrightnessSlider(context, selectedColor);
		mBrightnessSlider.setLayoutParams(lp);
		addView(mBrightnessSlider);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		rectBounds.set(0, 0, w, h);
	}
    
	private float getDp(int dp) {return dp * getResources().getDisplayMetrics().density;}
	
	private class HeuSaturationBox extends FrameLayout {
		
		private float heu = 0;
		private float saturation = 0;
		
		private PointF ratio = new PointF(1, 1);
		private RectF rfBounds = new RectF();
		
		private Handle handle;
		
		public HeuSaturationBox(final Context context, float heu, float saturation) {
			super(context);
			this.heu = heu;
			this.saturation = saturation;
			setClipToOutline(false);
			setClipChildren(false);
			drawBackground();
			setOnTouchListener(new OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getX() < rfBounds.right && event.getX() > rfBounds.left && event.getY() < rfBounds.bottom && event.getY() > rfBounds.top) {
							if(event.getAction() == MotionEvent.ACTION_MOVE) {
								setHeu(event.getX() / ratio.x);
								setSaturation((event.getY() / ratio.y) / 100);
								
								float[] hsv = new float[] {0, 0, 0};
								Color.colorToHSV(selectedColor, hsv);
								hsv[0] = getHeu();
								hsv[1] = getSaturation();
								selectedColor = Color.HSVToColor(hsv);
								hsv[2] = 1;
								handle.setX(event.getX() - handle.getHandleSize() / 2);
								handle.setY(event.getY() - handle.getHandleSize() / 2);
								handle.setHandleColor(Color.HSVToColor(hsv));
								mBrightnessSlider.setColor(Color.HSVToColor(hsv));
							}
						}
						return true;
					}
				});
		}

		private void drawBackground() {
			GradientDrawable gdH = new GradientDrawable();
			gdH.setColors(new int[] {0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000});
			gdH.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
			gdH.setCornerRadius(getDp(5));
			
			GradientDrawable gdS = new GradientDrawable();
			gdS.setColors(new int[] {0xFFFFFFFF, 0x00000000});
			gdS.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
			gdS.setCornerRadius(getDp(5));
			
			LayerDrawable ld = new LayerDrawable(new Drawable[] {gdH, gdS});
			
			setBackgroundDrawable(ld);
		}

		private void addHandle() {
			LayoutParams lp = new LayoutParams((int) getDp(24), (int) getDp(24));
			
			handle = new Handle(context);
			handle.setHandleColor(Color.HSVToColor(new float[] {heu, saturation, 1}));
			handle.setElevation(getDp(6));
			handle.setLayoutParams(lp);
			addView(handle);
			handle.setX(heu * ratio.x);
			handle.setY(saturation * 100 * ratio.y);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			rfBounds.set(0, 0, w, h);
			ratio.set(w / 360f, h / 100f);
			addHandle();
		}
		
		public void setHeu(float heu) {
			this.heu = heu;
		}

		public float getHeu() {
			return heu;
		}

		public void setSaturation(float saturation) {
			this.saturation = saturation;
		}

		public float getSaturation() {
			return saturation;
		}
		
	}
	
	private class Handle extends View {
		
		private int handleSize;
		private int handleColor;
		
		public Handle(Context context) {
			super(context);
			handleSize = (int) getDp(36);
			handleColor = 0xFF000000;
		}

		public void setHandleSize(int handleSize) {
			this.handleSize = handleSize;
			invalidate();
		}

		public int getHandleSize() {
			return handleSize;
		}

		public void setHandleColor(int handleColor) {
			this.handleColor = handleColor;
			invalidate();
		}

		public int getHandleColor() {
			return handleColor;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Paint p = new Paint();
			p.setAntiAlias(true);
			p.setStyle(Paint.Style.FILL);
			
			p.setColor(0xFFFFFFFF);
			int radius = handleSize / 2;
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, p);
			
			p.setColor(handleColor);
			radius = radius - (int) getDp(3);
			canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, p);
		}
		
	}
	
	private class BrightnessSlider extends FrameLayout {
		
		private Handle handle;
		private float ratio = 1;
		private RectF rfBounds = new RectF();
		private int color;
		private Handle h;
		
		public BrightnessSlider(final Context context, int color) {
			super(context);
			float[] hsv = new float[] {0, 0, 0};
			Color.colorToHSV(color, hsv);
			hsv[2] = 1;
			this.color = Color.HSVToColor(hsv);
			setClipToOutline(false);
			setClipChildren(false);
			drawBackground();
			setOnTouchListener(new OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getX() < rfBounds.right && event.getX() > rfBounds.left) {
							if(event.getAction() == MotionEvent.ACTION_MOVE) {
								float[] hsv = new float[] {0, 0, 0};
								Color.colorToHSV(selectedColor, hsv);
								hsv[2] = (event.getX() / ratio) / 100f;
								selectedColor = Color.HSVToColor(hsv);
								h.setX(event.getX() - h.getHandleSize() / 2);
								h.setHandleColor(selectedColor);
								h.invalidate();
							}
						}
						return true;
					}
				});
		}

		private void drawBackground() {
			GradientDrawable gd = new GradientDrawable();
			gd.setColors(new int[] {0xFF000000, color});
			gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
			gd.setCornerRadius(getDp(24));
			setBackgroundDrawable(gd);
		}

		private void addHandle() {
			if(getChildCount() > 0) removeView(h);
			
			h = new Handle(context);
			h.setHandleColor(selectedColor);
			h.setLayoutParams(new LayoutParams((int) getDp(36), (int) getDp(36)));
			addView(h);
			
			float[] hsv = new float[] {0, 0, 0};
			Color.colorToHSV(selectedColor, hsv);
			h.setX((hsv[2] * ratio * 100f)  - h.getHandleSize() / 2);
			h.setY(-((h.getHandleSize() - getDp(24)) / 2));
		}

		private void setColor(int color) {
			float[] hsv = new float[] {0, 0, 0};
			Color.colorToHSV(color, hsv);
			hsv[2] = 1;
			this.color = Color.HSVToColor(hsv);
			drawBackground();
			addHandle();
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			rfBounds.set(0, 0, w, h);
			ratio = w / 100f;
			addHandle();
		}
		
	}
	
	public int getSelectedColor() {
		return selectedColor;
	}
	
	public void setDefaultColor(int color) {
		defaultColor = color;
		removeAllViews();
		init();
	}
}
