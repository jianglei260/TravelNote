package cn.edu.cuit.wsy.travelnote.adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.InputStream;

import cn.edu.cuit.wsy.travelnote.utils.RxUtil;

public class SimpleDraweeAdapteer {
    private static final String TAG = "SimpleDraweeAdapteer";

    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"imageUri", "loadding", "loadfailed"}, requireAll = false)
    public static void setImageUri(final SimpleDraweeView simpleDraweeView, final String uri, final boolean loadding, final boolean failed) {
        if (simpleDraweeView.getContext() instanceof Activity) {
            ((Activity) simpleDraweeView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(uri)) {
                        return;
                    }
                    simpleDraweeView.setImageURI(Uri.parse(uri));
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"resizeUri"})
    public static void setresizeUri(final SimpleDraweeView simpleDraweeView, final String uri) {
        if (simpleDraweeView.getContext() instanceof Activity) {
            ((Activity) simpleDraweeView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(uri)) {
                        return;
                    }
                    if (uri.startsWith("content://") || uri.startsWith("file://")) {
                        int width = 540, height = 980;
                        ResizeOptions options = null;
                        try {
                            options = getBestOptions(simpleDraweeView.getContext(), Uri.parse(uri));
                        } catch (Exception e) {
                            e.printStackTrace();
                            options = new ResizeOptions(height, width);
                        }
                        Log.d(TAG, "option: " + options.width + " " + options.height);
                        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                                .setResizeOptions(options)
                                .setAutoRotateEnabled(true)
                                .build();
                        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                .setOldController(simpleDraweeView.getController())
                                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                                    @Override
                                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                                        ViewGroup.LayoutParams params = simpleDraweeView.getLayoutParams();
                                        params.height = imageInfo.getHeight();
                                        simpleDraweeView.setLayoutParams(params);
                                    }
                                })
                                .setImageRequest(request)
                                .build();
                        simpleDraweeView.setController(controller);
                    } else {
                        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                                .setAutoRotateEnabled(true)
                                .build();
                        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                .setOldController(simpleDraweeView.getController())
                                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                                    @Override
                                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                                        ViewGroup.LayoutParams params = simpleDraweeView.getLayoutParams();
                                        params.height = imageInfo.getHeight();
                                        simpleDraweeView.setLayoutParams(params);
                                    }
                                })
                                .setImageRequest(request)
                                .build();
                        simpleDraweeView.setController(controller);
                    }
                }
            });
        }
    }

    public static ResizeOptions getBestOptions(Context context, Uri uri) throws Exception {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        Log.d(TAG, "getBestOptions: " + originalWidth + " " + originalHeight);
        int be = 1;//be=1表示不缩放
        be = originalWidth / 1000;
        if (be <= 0)
            be = 1;
        ResizeOptions options = new ResizeOptions(originalWidth / be, originalHeight / be);
        return options;
    }
}
