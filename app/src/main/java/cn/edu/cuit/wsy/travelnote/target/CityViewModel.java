package cn.edu.cuit.wsy.travelnote.target;

import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/9.
 */

public class CityViewModel extends DetailViewModel {
    CityActivity activity;
    public ObservableField<String> city = new ObservableField<>();
    public ObservableField<String> location = new ObservableField<>();

    public ReplyCommand locationClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            activity.editFinish(location.get());
        }
    });

    public ReplyCommand finishClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            activity.editFinish(city.get());
        }
    });

    public CityViewModel(CityActivity activity) {
        loadding.set(true);
        this.activity = activity;
    }

    public void onLocation(String city) {
        loadding.set(false);
        location.set(city);
    }

    @Override
    public void onBack() {
        activity.finish();
    }
}
