package cn.edu.cuit.wsy.travelnote.data.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.avos.avoscloud.AVFile;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import cn.edu.cuit.wsy.travelnote.data.entity.NoteContentItem;
import cn.edu.cuit.wsy.travelnote.utils.BitmapUtil;

/**
 * Created by jianglei on 2017/5/4.
 */

public class FileRepository {
    private static FileRepository instance;
    private static final String TAG = "FileRepository";

    public static FileRepository getInstance() {
        if (instance == null)
            instance = new FileRepository();
        return instance;
    }

    public String save(File file) {
        try {
            AVFile avFile = AVFile.withFile(file.getName(), file);
            avFile.save();
            return avFile.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void saveAllImage(final List<NoteContentItem> items, Context context, final UploadCallBack callBack) {
        UploadCallBack myUploadCallBack = new UploadCallBack() {
            int completNum;

            @Override
            public void onNext(String url, int index) {
                if (callBack != null)
                    callBack.onNext(url, index);
                completNum++;
                checkComplete();
            }

            @Override
            public void onComple() {
                if (callBack != null)
                    callBack.onComple();
            }

            @Override
            public void onFailed(String localUrl, int index) {
                if (callBack != null)
                    callBack.onFailed(localUrl, index);
                completNum++;
                checkComplete();
            }

            public void checkComplete() {
                if (completNum == items.size()) {
                    onComple();
                }
            }
        };
        for (int i = 0; i < items.size(); i++) {
            saveImage(items.get(i).getContent(), context, myUploadCallBack, i);
        }
    }

    public void saveImage(final String uri, final Context context, final UploadCallBack callBack, final int index) {
        ResizeOptions resizeOptions;
        try {
            resizeOptions = BitmapUtil.getBestOptions(context, Uri.parse(uri));
        } catch (Exception e) {
            e.printStackTrace();
            resizeOptions = new ResizeOptions(980, 540);
        }
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.width = resizeOptions.width;
        options.height = resizeOptions.height;
        Tiny.getInstance().source(Uri.parse(uri)).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile) {
                //return the compressed file path
                File file = new File(outfile);
                Log.d(TAG, "file size: " + file.length() / 1024 + "kb");
                if (outfile != null) {
                    callBack.onNext(outfile, index);
                } else {
                    callBack.onFailed(outfile, index);
                }
            }
        });
    }


    public interface UploadCallBack {
        public void onNext(String url, int index);

        public void onComple();

        public void onFailed(String localUrl, int index);
    }

    public String save(byte[] data) {
        try {
            String name = String.valueOf(System.currentTimeMillis());
            AVFile avFile = new AVFile(name, data);
            avFile.save();
            return avFile.getUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
