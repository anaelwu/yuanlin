package com.ninetowns.tootooplus.photoview;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

import com.ninetowns.tootooplus.R;

public class PerInfoVipView extends View {
	/** 该控件的宽度 **/
	private int vip_view_width = 0;
	/** 该控件的高度 **/
	private int vip_view_height = 0;
	/** vip刻度图 **/
	private Bitmap line_vip_bmp;
	/** vip刻度图的宽度 **/
	private int line_vip_bmp_width = 0;
	/** vip刻度图的高度 **/
	private int line_vip_bmp_height = 0;
	/** 刻度和vip图片的画笔 **/
	private Paint bmpPaint;
	/** vip进度的画笔 **/
	private Paint vipPerPaint;

	/** vip比率 **/
	private String vip_grade_percent;
	/** 白色竖线 **/
	private Paint linePaint;

	public PerInfoVipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public PerInfoVipView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public void setGradePercent(String vip_grade_percent) {
		this.vip_grade_percent = vip_grade_percent;
		postInvalidate();
//		invalidate();
	}

	private void init() {

		line_vip_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.line_per_info_vip);

		line_vip_bmp_width = line_vip_bmp.getWidth();

		line_vip_bmp_height = line_vip_bmp.getHeight();

		bmpPaint = new Paint();

		vipPerPaint = new Paint();
		vipPerPaint.setAntiAlias(true);
		vipPerPaint.setStrokeWidth(1);
		vipPerPaint.setStyle(Style.FILL);

		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(0.2f);
		linePaint.setStyle(Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Resources resources = getResources();
		vip_view_width = getWidth();

		vip_view_height = getHeight();

		float draw_vip_bmp_x = (vip_view_width - line_vip_bmp_width) / 2;

		float draw_vip_bmp_y = vip_view_height * 3 / 4 - line_vip_bmp_height
				/ 2;
		// 画vip刻度图
		canvas.drawBitmap(line_vip_bmp, draw_vip_bmp_x, draw_vip_bmp_y, bmpPaint);

		Bitmap icon_vip_1_bmp = BitmapFactory.decodeResource(resources, R.drawable.icon_vip_1);
		// vip等级图画的竖直位置都一样。刻度图的开始画的竖直位置减去vip等级图的高度(由于等级图V下面还有点空白，所以看着V离刻度图有点距离)
		float draw_icon_vip_y = draw_vip_bmp_y - icon_vip_1_bmp.getWidth();
		// vip一等级图横向位置
		float draw_icon_vip_1_x = draw_vip_bmp_x - icon_vip_1_bmp.getWidth() / 2;
		canvas.drawBitmap(icon_vip_1_bmp, draw_icon_vip_1_x, draw_icon_vip_y, bmpPaint);

		Bitmap icon_vip_2_bmp = BitmapFactory.decodeResource(resources,
				R.drawable.icon_vip_2);
		// vip二等级图横向位置
		float draw_icon_vip_2_x = draw_vip_bmp_x + line_vip_bmp_width / 4 - icon_vip_1_bmp.getWidth() / 2;
		canvas.drawBitmap(icon_vip_2_bmp, draw_icon_vip_2_x, draw_icon_vip_y,
				bmpPaint);

		Bitmap icon_vip_3_bmp = BitmapFactory.decodeResource(resources,
				R.drawable.icon_vip_3);
		// vip三等级图横向位置
		float draw_icon_vip_3_x = draw_vip_bmp_x + line_vip_bmp_width * 2 / 4
				- icon_vip_1_bmp.getWidth() / 2;
		canvas.drawBitmap(icon_vip_3_bmp, draw_icon_vip_3_x, draw_icon_vip_y,
				bmpPaint);

		Bitmap icon_vip_4_bmp = BitmapFactory.decodeResource(resources,
				R.drawable.icon_vip_4);
		// vip四等级图横向位置
		float draw_icon_vip_4_x = draw_vip_bmp_x + line_vip_bmp_width * 3 / 4
				- icon_vip_1_bmp.getWidth() / 2;
		canvas.drawBitmap(icon_vip_4_bmp, draw_icon_vip_4_x, draw_icon_vip_y,
				bmpPaint);

		Bitmap icon_vip_5_bmp = BitmapFactory.decodeResource(resources,
				R.drawable.icon_vip_5);
		// vip五等级图横向位置
		float draw_icon_vip_5_x = draw_vip_bmp_x + line_vip_bmp_width * 4 / 4
				- icon_vip_1_bmp.getWidth() / 2;
		canvas.drawBitmap(icon_vip_5_bmp, draw_icon_vip_5_x, draw_icon_vip_y,
				bmpPaint);

		// 画vip进度
		float vip_percent = 0.0f;

		if (vip_grade_percent != null) {
			vip_percent = Float.parseFloat(vip_grade_percent);
			RectF vip_precent_rect = new RectF(draw_vip_bmp_x + resources.getDimension(R.dimen.per_info_vip_precent_start_x),
					draw_vip_bmp_y + getResources().getDimension(R.dimen.per_info_vip_precent_start_y),
					draw_vip_bmp_x + line_vip_bmp_width * vip_percent / 4,
					draw_vip_bmp_y
							+ line_vip_bmp_height
							- getResources().getDimension(
									R.dimen.per_info_vip_precent_end_y));
			// 根据颜色渐变
			Shader mLinearGradientClamp = new LinearGradient(draw_vip_bmp_x
					+ getResources().getDimension(
							R.dimen.per_info_vip_precent_start_x),
					draw_vip_bmp_y
							+ getResources().getDimension(
									R.dimen.per_info_vip_precent_start_y)
							+ vip_precent_rect.height() / 6, draw_vip_bmp_x
							+ getResources().getDimension(
									R.dimen.per_info_vip_precent_start_x),
					draw_vip_bmp_y
							+ getResources().getDimension(
									R.dimen.per_info_vip_precent_start_y)
							+ vip_precent_rect.height() * 5 / 6, new int[] {
							getResources().getColor(
									R.color.vip_pro_dark_green_color),
							getResources().getColor(
									R.color.vip_pro_light_green_color),
							getResources().getColor(
									R.color.vip_pro_dark_green_color) }, null,
					TileMode.CLAMP);
			vipPerPaint.setShader(mLinearGradientClamp);
			// 画圆角矩形
			canvas.drawRoundRect(vip_precent_rect, 6, 1000, vipPerPaint);

			// 画白色竖线
			linePaint.setColor(getResources().getColor(R.color.white));

			for (int i = 1; i <= vip_percent; i++) {

				canvas.drawLine(draw_vip_bmp_x + line_vip_bmp_width * i / 4,
						draw_vip_bmp_y + 4, draw_vip_bmp_x + line_vip_bmp_width
								* i / 4,
						draw_vip_bmp_y + 4 + vip_precent_rect.height(),
						linePaint);
			}

		}

	}

}