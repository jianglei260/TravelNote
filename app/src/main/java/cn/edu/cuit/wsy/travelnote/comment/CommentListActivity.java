package cn.edu.cuit.wsy.travelnote.comment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityCommentListBinding;
import cn.edu.cuit.wsy.travelnote.note.TextEditActivity;

public class CommentListActivity extends BaseActivity {
    ActivityCommentListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String objectId = getIntent().getStringExtra("objectId");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment_list);
        binding.setCommentViewModel(new CommentListViewModel(this, objectId));
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
}
