package com.time.oim.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.Surface;

public class SaveFileUtil {
	
	public static Bitmap getxtsldraw(Context c, String file) {
		File f = new File(file);
		Bitmap drawable = null;
		if (f.length() / 1024 < 100) {
			drawable = BitmapFactory.decodeFile(file);
		} else {
			Cursor cursor = c.getContentResolver().query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					new String[] { MediaStore.Images.Media._ID },
					MediaStore.Images.Media.DATA + " like ?",
					new String[] { "%" + file }, null);
			if (cursor == null || cursor.getCount() == 0) {
				drawable = getbitmap(file, 720 * 1280);
			} else {
				if (cursor.moveToFirst()) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPurgeable = true;
					options.inInputShareable = true;
					options.inPreferredConfig = Bitmap.Config.RGB_565;
					String videoId = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media._ID));
					long videoIdLong = Long.parseLong(videoId);
					Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
							c.getContentResolver(), videoIdLong,
							Thumbnails.MINI_KIND, options);
					return bitmap;
				} else {
					// drawable = BitmapFactory.decodeResource(c.getResources(),
					// R.drawable.ic_doctor);
				}
			}
		}
		int degree = 0;
		ExifInterface exifInterface;
		try {
			exifInterface = new android.media.ExifInterface(file);

			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
			if (degree != 0 && drawable != null) {
				Matrix m = new Matrix();
				m.setRotate(degree, (float) drawable.getWidth() / 2,
						(float) drawable.getHeight() / 2);
				drawable = Bitmap.createBitmap(drawable, 0, 0,
						drawable.getWidth(), drawable.getHeight(), m, true);
			}
		} catch (java.lang.OutOfMemoryError e) {
			// Toast.makeText(c, "牌照出错，请重新牌照", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	public static Bitmap getbitmap(String imageFile, int length) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(imageFile, opts);
		int ins = computeSampleSize(opts, -1, length);
		opts.inSampleSize = ins;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inJustDecodeBounds = false;
		try {
			Bitmap bmp = BitmapFactory.decodeFile(imageFile, opts);
			return bmp;
		} catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
		return null;
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	public static String saveToSDCard(byte[] data) throws IOException {  
        Date date = new Date();  
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
        String filename = format.format(date) + ".png";  
        File fileFolder = new File(Environment.getExternalStorageDirectory()  
                + "/aone/img/");  
        String path = Environment.getExternalStorageDirectory() + "/aone/img/" + filename;
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录  
            fileFolder.mkdir();  
        }  
        File jpgFile = new File(fileFolder, filename);  
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流  
        outputStream.write(data); // 写入sd卡中  
        outputStream.close(); // 关闭输出流  
        return path;
    }  
	
	public static String savebm(Bitmap bm){//保存图片、地图截图，可见
		Date date = new Date();  
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
        String filename = format.format(date) + ".png";  
        File fileFolder = new File(Environment.getExternalStorageDirectory()  
                + "/aone/img/");  
        String path = Environment.getExternalStorageDirectory() + "/aone/img/" + filename;
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个 
            if(!fileFolder.mkdir()){
            	fileFolder.mkdirs();
            }
        } 
        File jpgFile = new File(fileFolder, filename);
        
        FileOutputStream fout;
		try {
			fout = new FileOutputStream(jpgFile);
			BufferedOutputStream bos = new BufferedOutputStream(fout);  
			  
            //          //如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800  
//          Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);  
  
	        bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);  
	        bos.flush();  
	        bos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return path;
          
	}
	

	
	public static String savetempbm(Bitmap bm){//保存临时图片,聊天记录图片，不可见
		Date date = new Date();  
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
        String filename = format.format(date) + ".png";  
        File fileFolder = new File(Environment.getExternalStorageDirectory()  
                + "/aone/.tempimg/");  
        String path = Environment.getExternalStorageDirectory() + "/aone/.tempimg/" + filename;
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个 
        	if(!fileFolder.mkdir()){
            	fileFolder.mkdirs();
            } 
        } 
        File jpgFile = new File(fileFolder, filename);
        
        FileOutputStream fout;
		try {
			fout = new FileOutputStream(jpgFile);
			BufferedOutputStream bos = new BufferedOutputStream(fout);  
			  
            //          //如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800  
//          Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);  
  
	        bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);  
	        bos.flush();  
	        bos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return path;
          
	}
	
	public static String savemapbm(Bitmap bm){//保存地图图片，不可见
		Date date = new Date();  
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
        String filename = format.format(date) + ".png";  
        File fileFolder = new File(Environment.getExternalStorageDirectory()  
                + "/aone/.mapbm/");  
        String path = Environment.getExternalStorageDirectory() + "/aone/.mapbm/" + filename;
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个 
            fileFolder.mkdir();  
        } 
        File jpgFile = new File(fileFolder, filename);
        
        FileOutputStream fout;
		try {
			fout = new FileOutputStream(jpgFile);
			BufferedOutputStream bos = new BufferedOutputStream(fout);  
	        bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);  
	        bos.flush();  
	        bos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return path;
          
	}
	
	public static void deletefile(String path){
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
	}
	
	public static void clearfiles(){
		String path = Environment.getExternalStorageDirectory() + "/aone/.tempimg/";
		File file = new File(path);
		if(file.exists()){
			for (File item : file.listFiles()) {

				item.delete();

				}
		}
	}
	
	public static String savebmpng(Bitmap bm){
		Date date = new Date();  
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
        String filename = format.format(date) + ".png";  
        File fileFolder = new File(Environment.getExternalStorageDirectory()  
                + "/aone/.tempimg/");  
        String path = Environment.getExternalStorageDirectory() + "/aone/.tempimg/" + filename;
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个 
            fileFolder.mkdir();  
        } 
        File jpgFile = new File(fileFolder, filename);
        
        FileOutputStream fout;
		try {
			fout = new FileOutputStream(jpgFile);
			BufferedOutputStream bos = new BufferedOutputStream(fout);  
			  
            //          //如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800  
//          Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);  
  
	        bm.compress(Bitmap.CompressFormat.PNG, 60, bos);  
	        bos.flush();  
	        bos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return path;
          
	}

	public static int getPreviewDegree(Activity activity) {  
        // 获得手机的方向  
        int rotation = activity.getWindowManager().getDefaultDisplay()  
                .getRotation();  
        int degree = 0;  
        // 根据手机的方向计算相机预览画面应该选择的角度  
        switch (rotation) {  
        case Surface.ROTATION_0:  
            degree = 90;  
            break;  
        case Surface.ROTATION_90:  
            degree = 0;  
            break;  
        case Surface.ROTATION_180:  
            degree = 270;  
            break;  
        case Surface.ROTATION_270:  
            degree = 180;  
            break;  
        }  
        return degree;  
    } 
}
