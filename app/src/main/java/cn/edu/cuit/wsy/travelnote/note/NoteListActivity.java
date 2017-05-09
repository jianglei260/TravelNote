package cn.edu.cuit.wsy.travelnote.note;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityNoteListBinding;

public class NoteListActivity extends BaseActivity {
    public static final String ACTION_NOTE_UPDATE="ACTION_NOTE_UPDATE";
    ActivityNoteListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_note_list);
        binding.setNoteViewModel(new NoteListViewModel(this));
        registeEventAction(ACTION_NOTE_UPDATE);
        registeEventAction(NoteDetailActivity.ACTION_NOTE_EDIT);
    }
    @Override
    protected void onEvent(String action) {
        super.onEvent(action);
        if (action.equals(ACTION_NOTE_UPDATE)||action.equals(NoteDetailActivity.ACTION_NOTE_EDIT)){
            binding.getNoteViewModel().onRefresh(null);
        }
    }

}
