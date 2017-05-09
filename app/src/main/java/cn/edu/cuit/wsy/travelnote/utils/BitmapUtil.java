package cn.edu.cuit.wsy.travelnote.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jianglei on 16/8/9.
 */

public class BitmapUtil {
    public static byte[] getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return compressImage(bitmap);//再进行质量压缩
    }

    public static ResizeOptions getBestOptions(Context context, Uri uri) throws Exception {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        ExifInterface exifInterface = new ExifInterface(getPath(context, uri));
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        int be = 1;//be=1表示不缩放
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            int tmp = originalWidth;
            originalWidth = originalHeight;
            originalHeight = tmp;
        }
        be = originalWidth / 1000;
        if (be <= 0)
            be = 1;
        ResizeOptions options = new ResizeOptions(originalWidth / be, originalHeight / be);
        return options;
    }

    public static String getPath(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int actual_image_column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String img_path = cursor.getString(actual_image_column_index);
            return img_path;
        } else if (uri.getScheme().equals("file")) {
            return uri.getPath();
        }
        return "";
    }
//    public static byte[] getOriginalBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
//        ResizeOptions resizeOptions;
//        try {
//           resizeOptions =getBestOptions(ac,uri);
//        } catch (Exception e) {
//            e.printStackTrace();
//            resizeOptions=new ResizeOptions(980,540);
//        }
//        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
//        options.width=resizeOptions.width;
//        options.height=resizeOptions.height;
//        Tiny.getInstance().source(uri).asFile().withOptions(options).compress(new FileCallback() {
//            @Override
//            public void callback(boolean isSuccess, String outfile) {
//                //return the compressed file path
//            }
//        });
//        return compressImage(bitmap);//再进行质量压缩
//    }

    public static byte[] compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 10, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 50;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options <= 0)
                break;
        }
        Log.d("out size(kb)", baos.toByteArray().length / 1024 + "kb");
        return baos.toByteArray();
    }

    public static Bitmap convertViewToBitmap(View view) {
        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public static String saveViewToStorage(View view) {
        Bitmap bitmap = convertViewToBitmap(view);
        float scaleWidth = 600f / view.getWidth();
        float scaleHeight = 800f / view.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap compressBitmap = Bitmap.createBitmap(bitmap, 0, 0, view.getWidth(), view.getHeight(), matrix, true);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(path + File.separator + "wenwo");
        if (!dir.exists())
            dir.mkdir();
        File out = new File(dir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(out);
            compressBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.getAbsolutePath();
    }
}
