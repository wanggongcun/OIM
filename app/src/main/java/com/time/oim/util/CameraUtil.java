package com.time.oim.util;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CameraUtil {

	public static WindowManager manager;
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	public static CameraUtil cameraUtil;
	public static Camera getCameraInstance() {
		// TODO Auto-generated method stub
		Camera c = null;
		try{
			c = Camera.open(0);
		}catch(Exception e){
			
		}
		return c;
	}
	
	public static CameraUtil getInstance(){
		
		return cameraUtil;
	}
	
	public boolean checkCameraHardware(Context context){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			return true;
		}else{
			return false;
		}
	}
	
	public void updateCameraParameters(Context context, Camera mCamera,Camera.Parameters p,SurfaceView mSur) {
        if (mCamera != null) {
                
            // Set the preview frame aspect ratio according to the picture size.
                
            Size previewSize = findBestPreviewSize(context,mCamera,p);
                
                
            int supportPreviewWidth = previewSize.width;
            int supportPreviewHeight = previewSize.height;
                
            int srcWidth = getScreenWH(context).widthPixels;
            int srcHeight = getScreenWH(context).heightPixels;
                
            int width = Math.min(srcWidth, srcHeight);
            int height = width * supportPreviewWidth / supportPreviewHeight ;
               
//            mSur.setLayoutParams(new FrameLayout.LayoutParams(height, width));//
        }
    }
	
	public Size findBestPictureSize(Context context, Camera mCamera, Camera.Parameters parameters) {
        int  diff = Integer.MIN_VALUE;
        String pictureSizeValueString = parameters.get("picture-size-values");
             
         // saw this on Xperia
         if (pictureSizeValueString == null) {
             pictureSizeValueString = parameters.get("picture-size-value");
         }
             
         if(pictureSizeValueString == null) {
             return  mCamera.new Size(getScreenWH(context).widthPixels,getScreenWH(context).heightPixels);
         }
             
         int bestX = 0;
         int bestY = 0;
            
            
         for(String pictureSizeString : COMMA_PATTERN.split(pictureSizeValueString))
         {
             pictureSizeString = pictureSizeString.trim();
                 
             int dimPosition = pictureSizeString.indexOf('x');
             if(dimPosition == -1){
                 continue;
             }
                 
             int newX = 0;
             int newY = 0;
                 
             try{
                 newX = Integer.parseInt(pictureSizeString.substring(0, dimPosition));
                 newY = Integer.parseInt(pictureSizeString.substring(dimPosition+1));
             }catch(NumberFormatException e){
                 continue;
             }
                
             Point screenResolution = new Point (getScreenWH(context).widthPixels,getScreenWH(context).heightPixels);
                 
             int newDiff = Math.abs(newX - screenResolution.x)+Math.abs(newY- screenResolution.y);
                 if(newDiff == diff)
                 {
                     bestX = newX;
                     bestY = newY;
                     break;
                 } else if(newDiff > diff){
                     if((3 * newX) == (4 * newY)) {
                         bestX = newX;
                         bestY = newY;
                         diff = newDiff;
                     }
                 }
             }
                 
         if (bestX > 0 && bestY > 0) {
            return mCamera.new Size(bestX, bestY);
         }
        return null;
    }
        
    public Size findBestPreviewSize(Context context, Camera mCamera, Camera.Parameters parameters) {
            
         String previewSizeValueString = null;
         int diff = Integer.MAX_VALUE;
         previewSizeValueString = parameters.get("preview-size-values");
             
         if (previewSizeValueString == null) {
             previewSizeValueString = parameters.get("preview-size-value");
         }
            
         if(previewSizeValueString == null) {  // 有些手机例如m9获取不到支持的预览大小   就直接返回屏幕大小
             return  mCamera.new Size(getScreenWH(context).widthPixels,getScreenWH(context).heightPixels);
         }
         int bestX = 0;
         int bestY = 0;
            
         for(String prewsizeString : COMMA_PATTERN.split(previewSizeValueString))
         {
             prewsizeString = prewsizeString.trim();
                 
             int dimPosition = prewsizeString.indexOf('x');
             if(dimPosition == -1){
                 continue;
             }
                 
             int newX = 0;
             int newY = 0;
                 
             try{
                 newX = Integer.parseInt(prewsizeString.substring(0, dimPosition));
                 newY = Integer.parseInt(prewsizeString.substring(dimPosition+1));
             }catch(NumberFormatException e){
                 continue;
             }
                
             Point screenResolution = new Point (getScreenWH(context).widthPixels,getScreenWH(context).heightPixels);
                 
             int newDiff = Math.abs(newX - screenResolution.x)+Math.abs(newY- screenResolution.y);
                 
             if(newDiff == diff)
             {
                 bestX = newX;
                 bestY = newY;
                 break;
             } else if(newDiff < diff){
                 if((3 * newX) == (4 * newY)) {
                     bestX = newX;
                     bestY = newY;
                     diff = newDiff;
                 }
             }
         }
         if (bestX > 0 && bestY > 0) {
            return mCamera.new Size(bestX, bestY);
         }
        return null;
    }
        
     protected DisplayMetrics getScreenWH(Context context) {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = context.getResources().getDisplayMetrics();
        return dMetrics;
     }
	
	
}
