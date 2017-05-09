package cn.edu.cuit.wsy.travelnote.note;

import android.databinding.ObservableBoolean;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by jianglei on 2017/5/7.
 */

public class AddItemViewModel extends ListItemViewModel {
    private NewNoteActivity activity;
    public ObservableBoolean onFocus = new ObservableBoolean(false);
    public ReplyCommand onFocusChangeCommand=new ReplyCommand(new Action0() {
        @Override
        public void call() {
            onFocus.set(true);
        }
    });
    public ReplyCommand addImageClick=new ReplyCommand(new Action0() {
        @Override
        public void call() {
            onFocus.set(false);
            activity.selectPicture(activity.binding.getNoteViewModel().viewModels.indexOf(AddItemViewModel.this));
        }
    });
    public ReplyCommand addTextClick=new ReplyCommand(new Action0() {
        @Override
        public void call() {
            onFocus.set(false);
            activity.editText(activity.binding.getNoteViewModel().viewModels.indexOf(AddItemViewModel.this));
        }
    });

    public AddItemViewModel(NewNoteActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_ADD;
    }
}
