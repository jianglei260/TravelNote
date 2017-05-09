package cn.edu.cuit.wsy.travelnote.note;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityTextEditBinding;

public class TextEditActivity extends BaseActivity {
    public static final String ACTION_EDIT_FINISH = "ACTION_EDIT_FINISH";
    public static final String CONTENT = "content";
    public static final String INDEX = "index";
    ActivityTextEditBinding binding;
    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String content = getIntent().getStringExtra(CONTENT);
        index = getIntent().getIntExtra(INDEX, -1);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_edit);
        binding.setEditViewModel(new TextEditViewModel(this));
        binding.getEditViewModel().content.set(content);
    }

    public void editFinish(String content) {
        Intent intent = new Intent(ACTION_EDIT_FINISH);
        intent.putExtra(CONTENT, content);
        if (index >= 0) {
            intent.putExtra(INDEX, index);
        }
        publishEvent(intent);
        finish();
    }

    public static void startEditActivity(Context context, String content) {
        Intent intent = new Intent(context, TextEditActivity.class);
        intent.putExtra(CONTENT, content);
        context.startActivity(intent);
    }

    public static void startEditActivity(Context context, String content, int index) {
        Intent intent = new Intent(context, TextEditActivity.class);
        intent.putExtra(CONTENT, content);
        intent.putExtra(INDEX, index);
        context.startActivity(intent);
    }

    public static String getEditContent(Intent intent) {
        return intent.getStringExtra(CONTENT);
    }
}
