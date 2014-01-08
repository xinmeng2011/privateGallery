package com.mm.utility;

import android.content.Context;

public class ScreenHelper {

	public static float density=0;
	public static float getDesity(Context context){
		if (density == 0) {
			// ����GOOGLE IO�ĵ����ᵽ�ģ���density������������ʡ�������
			density = context.getResources().getDisplayMetrics().density;
		}
		return density;
	}
}
