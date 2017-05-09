package cn.edu.cuit.wsy.travelnote.comment;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.ask.NewAskActivity;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityMyCommentBinding;
import cn.edu.cuit.wsy.travelnote.note.NewNoteActivity;
import cn.edu.cuit.wsy.travelnote.note.TextEditActivity;

public class MyCommentActivity extends BaseActivity {
    ActivityMyCommentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_my_comment);
        binding.setCommentViewModel(new MyCommentViewModel(this));
        registeEventAction(TextEditActivity.ACTION_EDIT_FINISH);
    }
    @Override
    protected void onEvent(Intent intent) {
        super.onEvent(intent);
        if (intent.getAction().equals(TextEditActivity.ACTION_EDIT_FINISH)) {
            if (intent.getIntExtra(TextEditActivity.INDEX, -1) >= 0) {
                int index = intent.getIntExtra(TextEditActivity.INDEX, -1);
                binding.getCommentViewModel().commentItemEdit(TextEditActivity.getEditContent(intent), index);
            }
        }
    }
    public void editComment(final int index) {
        String[] items = getResources().getStringArray(R.array.edit_items);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择操作")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                binding.getCommentViewModel().deleteItem(index);
                                break;
                            case 1:
                                binding.getCommentViewModel().editItem(index);
                                break;
                        }
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

}
