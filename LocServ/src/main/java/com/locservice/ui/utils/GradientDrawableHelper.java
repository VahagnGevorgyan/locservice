package com.locservice.ui.utils;

import android.graphics.drawable.GradientDrawable;

public class GradientDrawableHelper {

	public GradientDrawableHelper() {
		
	}
	
	/**
	 * Method for getting gradient Drawable via colors
	 * @param colors
	 * @param orientation
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public static GradientDrawable getGradientDrawable(int[] colors,
            GradientDrawable.Orientation orientation,
            float x, float y, float radius) {

		GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
		gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		gradientDrawable.setGradientCenter(x, y);
		gradientDrawable.setGradientRadius(radius);
		
		return gradientDrawable;
	}
	
}
