package cn.edu.cuit.wsy.travelnote.note;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityNoteDetailBinding;

public class NoteDetailActivity extends BaseActivity {
    public static final String ACTION_NOTE_EDIT="ACTION_NOTE_EDIT";
    ActivityNoteDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String objectId = getIntent().getStringExtra("objectId");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_detail);
        binding.setNoteViewModel(new NoteDetailViewModel(this, objectId));
        registeEventAction(TextEditActivity.ACTION_EDIT_FINISH);
        registeEventAction(ACTION_NOTE_EDIT);
    }

    @Override
    protected void onEvent(String action) {
        super.onEvent(action);
        if (action.equals(ACTION_NOTE_EDIT)){
            binding.getNoteViewModel().onRefresh(null);
        }
    }

    public void noteUpdate(){
        publishEvent(NoteListActivity.ACTION_NOTE_UPDATE);
    }
    @Override
    protected void onEvent(Intent intent) {
        super.onEvent(intent);
        if (intent.getAction().equals(TextEditActivity.ACTION_EDIT_FINISH)) {
            if (intent.getIntExtra(TextEditActivity.INDEX, -1) >= 0) {
                int index = intent.getIntExtra(TextEditActivity.INDEX, -1);
                binding.getNoteViewModel().commentItemEdit(TextEditActivity.getEditContent(intent), index);
            } else {
                binding.getNoteViewModel().commentEdit(TextEditActivity.getEditContent(intent));
            }
        }
    }
}
