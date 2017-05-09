package cn.edu.cuit.wsy.travelnote.note;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.data.repository.FileRepository;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityNewNoteBinding;
import cn.edu.cuit.wsy.travelnote.target.CityActivity;
import cn.edu.cuit.wsy.travelnote.utils.BitmapUtil;

public class NewNoteActivity extends BaseActivity {
    private static final String TAG = "NewNoteActivity";
    public static final int REQUEST_GALLERY = 0;
    ActivityNewNoteBinding binding;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String objectId = getIntent().getStringExtra("objectId");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_note);
        binding.setNoteViewModel(new NewNoteViewModel(this, objectId));
        registeEventAction(TextEditActivity.ACTION_EDIT_FINISH);
        registeEventAction(CityActivity.ACTION_CITY_FINISH);
    }

    @Override
    protected void onEvent(Intent intent) {
        super.onEvent(intent);
        if (intent.getAction().equals(TextEditActivity.ACTION_EDIT_FINISH)) {
            if (intent.getIntExtra(TextEditActivity.INDEX, -1) >= 0) {
                index = intent.getIntExtra(TextEditActivity.INDEX, -1);
                binding.getNoteViewModel().onTextChange(TextEditActivity.getEditContent(intent), index);
            } else {
                binding.getNoteViewModel().onTextAdd(TextEditActivity.getEditContent(intent), index);
            }
        }else if (intent.getAction().equals(CityActivity.ACTION_CITY_FINISH)){
            binding.getNoteViewModel().onCityChange(CityActivity.getCity(intent));
        }
    }

    public void noteEdit(){
        publishEvent(NoteDetailActivity.ACTION_NOTE_EDIT);
    }
    public void editText(int index) {
        this.index = index;
        Intent intent = new Intent(this, TextEditActivity.class);
        startActivity(intent);
    }

    public void selectPicture(int index) {
        this.index = index;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                Uri notCorpUri = data.getData();
                saveImage(notCorpUri);
            }
        }
    }

    public void saveImage(Uri uri) {
        ResizeOptions resizeOptions;
        try {
            resizeOptions = BitmapUtil.getBestOptions(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
            resizeOptions = new ResizeOptions(980, 540);
        }
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.width = resizeOptions.width;
        options.height = resizeOptions.height;
        Log.d(TAG, "saveImage:width: " + options.width + "height:" + options.height);
        Tiny.getInstance().source(uri).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile) {
                Log.d(TAG, "callback: success:" + isSuccess + "path:" + outfile);
                binding.getNoteViewModel().onImageAdd(Uri.fromFile(new File(outfile)).toString(), index);
            }
        });
    }
}
