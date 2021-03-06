package com.creative.draw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @deprecated
 */
public abstract class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	//private static final String TAG ="BaseSurfaceView";
	
	/**
	 * 设置波形平滑
	 */
	protected CornerPathEffect mCornerPathEffect = new CornerPathEffect(20);

	protected SurfaceHolder mHolder;

	/**
	 * 绘制波形的画笔
	 */
	protected Paint mWavePaint;

	/**
	 * view宽
	 */
	protected int mSurfaceViewWidth;

	/**
	 * view高
	 */
	protected int mSurfaceViewHeight;

	protected DisplayMetrics mDisplayMetrics;

	/**
	 * 每mm所占的像素 单位：像素点/mm
	 */
	protected float mResx = 0.0f;

	/**
	 * 像素转毫米的单位
	 */
	protected float mPx2MmUnit = 0.0f;

	/**
	 * 绘制背景网格的画笔
	 */
	private Paint mGridPaint;

	/**
	 * 绘制心电增益的画笔
	 */
	private Paint mGainPaint;

	/**
	 * 背景颜色
	 */
	public static int mBackgroundColor = Color.WHITE;

	/**
	 * 是否绘制背景网格
	 */
	private boolean bDrawGrid = true;

	/**
	 * 背景网格一格的高度(5mm对应的像素值),1mV对应5mm。
	 */
	private float mGridHeight = 0.0f;

	/**
	 * 是否绘制增益标尺
	 */
	private boolean bDrawGain = false;

	/**
	 * 心电波形增益
	 */
	protected int mGain = 2; 	

	/**
	 * 两点之间的步长 由它控制波形走速(血氧spo2用到) 
	 */
	protected float step = 0.0f;
	
	public BaseSurfaceView(Context context) {
		super(context);
		init(context);
	}

	public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public BaseSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	@SuppressLint("NewApi")
	private void init(Context context) {
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mRect = new Rect();
		
		//加速Surface 
		//mHolder.setType(SurfaceHolder.SURFACE_TYPE_HARDWARE); 
		
		mDisplayMetrics = context.getResources().getDisplayMetrics();

		//波形画笔
		mWavePaint = new Paint();
		mWavePaint.setAntiAlias(true);
		mWavePaint.setStyle(Style.STROKE);
		mWavePaint.setColor(Color.RED);
		mWavePaint.setStrokeWidth(mDisplayMetrics.density * 2 ); 
		mWavePaint.setPathEffect(mCornerPathEffect);
 		
		
		mPx2MmUnit = 25.4f / mDisplayMetrics.densityDpi;
		mResx = mDisplayMetrics.densityDpi / 25.4f;
				
		//网格画笔
		mGridPaint = new Paint();
		mGridPaint.setAntiAlias(true);
		mGridPaint.setColor(Color.GRAY);
		mGridPaint.setStrokeWidth(mDisplayMetrics.density);
		mGridHeight = fMMgetPx(5);
		
//		//波形增益
//		mGainPaint = new Paint(mGridPaint);
//		mGainPaint.setColor(Color.BLUE);
				
	}


	/**
	 * 在Surface格式和大小发生变化时会被立即调用，可以在这个方法中更新Surface
	 *
	 * @param holder 持有当前Surface的SurfaceHolder对象
	 * @param format surface的新格式
	 * @param width surface的新宽度
	 * @param height surface的新高度
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * 在Surface首次创建时被立即调用：获得焦点时。一般在这里开启画图的线程。
	 * @param holder 持有当前Surface的SurfaceHolder对象
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {		
        Canvas canvas = holder.lockCanvas();
		drawBackground(canvas);       
        holder.unlockCanvasAndPost(canvas);       
	}

	/**
	 * 在Surface被销毁时立即调用:失去焦点时。一般在这将画图的线程停止销毁
	 * @param holder 持有当前Surface的SurfaceHolder对象
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mSurfaceViewWidth= w;
		mSurfaceViewHeight = h ;
	}
	
	/**
	 * 将毫米转成像素
	 */
	protected float fMMgetPx(float mm) {
		return mm / mPx2MmUnit;
	}

	/**
	 * 将像素转换为毫米
	 */
	protected float fPXgetMM(int px) {
		return px * mPx2MmUnit;
	}


	protected Path mPath = new Path();
	protected int mCount = 0; //x轴步数
	protected int mScanLineWidth = 6;//右侧空白点的宽度
	protected Rect mRect;	
	private int preX =0;
	private int curX =0;
	private int preY =0;
	private int curY =0;
	
	
	public void addData(int data) {
		synchronized (this) {		
			int nextX = (int) getX(mCount);
			int nextY = (int) getY(data);
			mCount++;
			
			mRect.set(preX, 0, (nextX + mScanLineWidth), mSurfaceViewHeight);		
		
			//获得Surface的画布对象
			Canvas canvas = mHolder.lockCanvas(mRect);
			if (canvas == null) 
				return;

			drawBackground(canvas);

			if (nextX < mSurfaceViewWidth) {
//				mPath.reset();
//				mPath.moveTo(preX, preY);
//				mPath.quadTo((preX + curX) / 2f, (preY + curY) / 2f, curX, curY);
//				canvas.drawPath(mPath, mWavePaint);
				
//				mPath.reset();
//				mPath.moveTo(curX, curY);
//				mPath.quadTo((curX + nextX) / 2f, (curY + nextY) / 2f, nextX, nextY);
//				canvas.drawPath(mPath, mWavePaint);
								
				canvas.drawLine(preX, preY, curX, curY, mWavePaint);
				canvas.drawLine(curX, curY, nextX, nextY, mWavePaint);
				
			} else {
				mCount = 0;
				curX = 0;
			}
					
			preX = curX;
			preY = curY;
			curX = nextX;
			curY = nextY;		

			mHolder.unlockCanvasAndPost(canvas);			
		}	
	}
	
	//--2
//	public void addData(int data) {
//		synchronized (this) {
//			int newX = (int) getX(mCount);
//			int newY = (int) getY(data);
//			
//			mCount++;	
//			
//			mRect.set(curX, 0, (newX + mScanLineWidth), mSurfaceViewHeight);		
//									
//			//获得Surface的画布对象
//			Canvas canvas = mHolder.lockCanvas(mRect);
//			if (canvas == null) 
//				return;
//
//			drawBackground(canvas);				  			 
//			
//			if(newX < mSurfaceViewWidth){
//		        mPath.reset();
//				mPath.moveTo(curX, curY);
//				mPath.quadTo((curX + newX) / 2f, (curY + newY) / 2f, newX, newY);
//				canvas.drawPath(mPath, mWavePaint);	
//				
//				//canvas.drawLine(mStartPoint.x, mStartPoint.y, newX, newY, mWavePaint);
//			}else {
//				mCount = 0;
//				newX=0;
//			}
//			
//			curX = newX;
//			curY = newY;		
//			
//			mHolder.unlockCanvasAndPost(canvas);		
//		}			
//	}
	
    //--3
//	private int offSet=0;
//	private int startY=0;
//	public void addData(int data) {
//		int stepX; //x轴每次行进步长
//		int gain = (int) mDisplayMetrics.density;
//		
//		//int newX = (int) getX(offset);
//		int newY = (int) getY(data);
//		
//		if(offSet + 1 >= mSurfaceViewWidth)
//			stepX = mSurfaceViewWidth - offSet;
//		else
//			stepX = 1;
//		
//		stepX = stepX * gain;
//		
//		Rect rect = new Rect((offSet>0)?(offSet - 1):0, 0, offSet + stepX + 2, mSurfaceViewHeight);
//		Canvas canvas = mHolder.lockCanvas(rect);	
//			
//		drawBackground(canvas);
//				
//		mWavePaint.setColor(Color.TRANSPARENT);
//		canvas.drawLine(offSet + stepX + 1, 0, offSet + stepX + 1, mSurfaceViewHeight, mWavePaint);
//		
//		mWavePaint.setColor(Color.RED);
//		if(offSet > 0){
//			mPath.reset();
//			mPath.moveTo(offSet-1, startY);
//			mPath.quadTo((offSet-1 + offSet) / 2f, (startY + newY) / 2f, offSet, newY);
//			canvas.drawPath(mPath, mWavePaint);	
//			
//			canvas.drawLine(offSet-1, startY, offSet, newY, mWavePaint);
//		}
//		mHolder.unlockCanvasAndPost(canvas);
//		startY = newY;
//		offSet = (offSet + stepX)% mSurfaceViewWidth;
//	}
	

	/**
	 * 清空画布
	 */
	public void clean() {
		synchronized (this) {
			mCount = 0;
			curX = 0;
			curY=0;
			
			Canvas canvas = mHolder.lockCanvas();			
			drawBackground(canvas);
			mHolder.unlockCanvasAndPost(canvas);	
			
		}
	}
	

	/**
	 * 计算Y点离基线的偏移距离
	 * @return
	 */
	public abstract float getY(int data);

	/**
	 * 计算x点的距离,即新的x轴坐标
	 * @param data
	 * @return
	 */
	public abstract float getX(int data);
		

	/**
	 * 是否绘制背景网格
	 */
	public boolean isDrawGrid() {
		return bDrawGrid;
	}

	/**
	 * 设置是否绘制背景上的网格
	 */
	public void setDrawGrid(boolean drawGrid) {
		bDrawGrid = drawGrid;
	}

	/**
	 * 是否绘制增益标尺
	 */
	public boolean isDrawGain() {
		return bDrawGain;
	}

	/**
	 * 设置是否绘制增益标尺
	 */
	public void setDrawGain(boolean isDrawGain) {
		this.bDrawGain = isDrawGain;
	}

	/**
	 * 获取心电增益
	 */
	public int getGain() {
		return mGain;
	}

	/**
	 * 设置心电增益
	 */
	public void setGain(int gain) {
		this.mGain = gain;
	}

	/**
	 * 返回绘制背景网格的画笔
	 */
	public Paint getmGridPaint() {
		return mGridPaint;
	}

	/**
	 * 设置绘制背景网格的画笔
	 */
	public void setmGridPaint(Paint mGridPaint) {
		this.mGridPaint = mGridPaint;
	}

	/**
	 * 获取增益的画笔
	 */
	public Paint getmGainPaint() {
		return mGainPaint;
	}

	/**
	 * 设置增益的画笔
	 */
	public void setmGainPaint(Paint mGainPaint) {
		this.mGainPaint = mGainPaint;
	}

	/**
	 * 获取背景颜色
	 */
	public int getmBackgroundColor() {
		return mBackgroundColor;
	}
	
	/**
	 * 绘制背景网格
	 */
	protected void drawBackground(Canvas canvas) {
		canvas.drawColor(mBackgroundColor);
		if (!bDrawGrid)
			return;

		// 绘制竖线
		for (float i = 0f; i < mSurfaceViewWidth; i += mGridHeight) {
			canvas.drawLine(i, 0, i, mSurfaceViewHeight, mGridPaint);
		}

		// 绘制横线
		for (float i = mSurfaceViewHeight / 2f; i >= 0; i -= mGridHeight) {
			canvas.drawLine(0, i, mSurfaceViewWidth, i, mGridPaint);
		}

		for (float i = mSurfaceViewHeight / 2f; i <= mSurfaceViewHeight; i += mGridHeight) {
			canvas.drawLine(0, i, mSurfaceViewWidth, i, mGridPaint);
		}

		if (bDrawGain) {
			// 绘制增益
			float i = (mGridHeight * mGain) / 2f;
			canvas.drawLine(0, mSurfaceViewHeight / 2 - i, mGridHeight / 2, mSurfaceViewHeight / 2 - i, mGainPaint);
			canvas.drawLine(0, mSurfaceViewHeight / 2 + i, mGridHeight / 2, mSurfaceViewHeight / 2 + i, mGainPaint);
			canvas.drawLine(mGridHeight / 4, mSurfaceViewHeight / 2 - i, mGridHeight / 4, mSurfaceViewHeight / 2 + i,
					mGainPaint);
		}
	}
	
}
