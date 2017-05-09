package cn.edu.cuit.wsy.travelnote.note;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class ImageItemViewModel extends ListItemViewModel {
    private BaseViewModel parent;
    public ObservableBoolean editMode = new ObservableBoolean(false);
    public ObservableField<String> url = new ObservableField<>();
    public ReplyCommand itemClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {

        }
    });
    public ReplyCommand deleteClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (parent instanceof NewNoteViewModel) {
                ((NewNoteViewModel) parent).removeItem(ImageItemViewModel.this);
            }
        }
    });

    public ImageItemViewModel(BaseViewModel parent) {
        this.parent = parent;
        if (parent instanceof NewNoteViewModel) {
            editMode.set(true);
        }
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_IMAGE;
    }
}
