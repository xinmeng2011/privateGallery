package com.mm.utility;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class NotifyUtility {
	
    public static void Vibrate(final Activity activity, long milliseconds) {  
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);  
        vib.vibrate(milliseconds);  
    }
    
    public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {  
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);  
        vib.vibrate(pattern, isRepeat ? 1 : -1);  
    }
    
    public static void VibrateShort(final Activity activity){
    	Vibrate(activity, 200);
    }
}
