package cn.edu.cuit.wsy.travelnote.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityUserInfoBinding;
import cn.edu.cuit.wsy.travelnote.utils.BitmapUtil;

public class UserInfoActivity extends BaseActivity {
    ActivityUserInfoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        UserInfoViewModel viewModel = new UserInfoViewModel(this);
        binding.setEditViewModel(viewModel);
    }
    public void selectPicture() {
        String[] items = getResources().getStringArray(R.array.pic_items);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.select_pic))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectFrom(which);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private File tempFile;
    public static final int REQUEST_GALLERY = 0;
    public static final int REQUEST_CAMMERA = 1;
    public static final int REQUEST_CUT = 2;
    private byte[] imageData;

    public void selectFrom(int which) {
        if (which == 0) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, REQUEST_GALLERY);
        } else if (which == 1) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            String fileName = "/pic_" + System.currentTimeMillis() + ".jpg";
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    fileName);
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CAMMERA);
        }
    }

    private static int MAX_LENGTH = 720;
    Uri notCorpUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                notCorpUri = data.getData();
                crop(notCorpUri);
            }
        } else if (requestCode == REQUEST_CAMMERA) {
            // 从相机返回的数据
            notCorpUri = Uri.fromFile(tempFile);
            crop(notCorpUri);
        } else if (requestCode == REQUEST_CUT) {
            Uri uri;
            if (data != null) {
                try {
                    if (data.getData() == null)
                        uri = notCorpUri;
                    else
                        uri = data.getData();
                    imageData = BitmapUtil.getBitmapFormUri(this, uri);
                    binding.getEditViewModel().onImageSelected(imageData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (tempFile != null)
                    // 将临时文件删除
                    tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /*
             * 剪切图片
             */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 720);
//        intent.putExtra("outputY", 720);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", false);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, REQUEST_CUT);
    }
}
