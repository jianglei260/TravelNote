package cn.edu.cuit.wsy.travelnote.note;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class TextItemViewModel extends ListItemViewModel {
    private BaseViewModel parent;
    public ObservableField<String> content = new ObservableField<>();
    public ObservableBoolean editMode = new ObservableBoolean(false);
    public ReplyCommand itemClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (parent instanceof NewNoteViewModel) {
                int index = ((NewNoteViewModel) parent).viewModels.indexOf(TextItemViewModel.this);
                ((NewNoteViewModel) parent).startEdit(content.get(), index);
            }
        }
    });

    public TextItemViewModel(BaseViewModel parent) {
        this.parent = parent;
        if (parent instanceof NewNoteViewModel){
            editMode.set(true);
        }
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_TEXT;
    }
}
