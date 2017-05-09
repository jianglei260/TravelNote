package cn.edu.cuit.wsy.travelnote.target;

import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.City;
import cn.edu.cuit.wsy.travelnote.data.repository.CityRepository;
import cn.edu.cuit.wsy.travelnote.main.TargetViewModel;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/9.
 */

public class CityItemViewModel extends ListItemViewModel {
    public ObservableField<String> cityName = new ObservableField<>();
    public ObservableField<String> cityImage = new ObservableField<>();
    private BaseViewModel parent;

    public CityItemViewModel(BaseViewModel parent,String cityName) {
        this.parent=parent;
        this.cityName.set(cityName);
        initCity(cityName);
    }

    public ReplyCommand itemClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (parent instanceof TargetViewModel){
                ((TargetViewModel) parent).selectCity();
            }
        }
    });

    public void initCity(final String cityName) {
        RxUtil.execute(new IOTask<City>() {
            @Override
            public City run() {
                return CityRepository.getInstance().find(cityName);
            }
        }, new UIAction<City>() {
            @Override
            public void onComplete(City city) {
                if (city != null)
                    cityImage.set(city.getUrl());
                else {
                    cityImage.set("res:///"+ R.drawable.ic_default_city_bg);
                }
            }
        });
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_CITY;
    }
}
