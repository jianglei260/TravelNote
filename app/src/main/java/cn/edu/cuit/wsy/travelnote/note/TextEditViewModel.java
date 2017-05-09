package cn.edu.cuit.wsy.travelnote.note;

import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class TextEditViewModel extends DetailViewModel {
    private TextEditActivity activity;
    public ObservableField<String> content = new ObservableField<>();
    public ReplyCommand finishClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            activity.editFinish(content.get());
        }
    });

    public TextEditViewModel(TextEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onBack() {
        activity.finish();
    }
}
