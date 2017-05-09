package cn.edu.cuit.wsy.travelnote.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseFragment;
import cn.edu.cuit.wsy.travelnote.databinding.FragmentTargetBinding;

/**
 * Created by jianglei on 2017/4/27.
 */

public class TargetFragment extends BaseFragment {
    FragmentTargetBinding binding;
    private AMapLocationClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_target, container, false);
        TargetViewModel viewModel = new TargetViewModel(this);
        binding.setTargetViewModel(viewModel);
        initLocation();
        return binding.getRoot();
    }

    public void initLocation() {
        client = new AMapLocationClient(getActivity().getApplicationContext());
        client.setLocationListener(locationListener);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setOnceLocation(true);
        option.setNeedAddress(true);
        client.setLocationOption(option);
        client.startLocation();
    }

    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            String city = "成都";
            if (aMapLocation.getErrorCode() == 0) {
                city = aMapLocation.getCity();
            } else {
                Toast.makeText(getActivity(), "定位失败", Toast.LENGTH_SHORT).show();
            }
            binding.getTargetViewModel().onLocation(city);
        }
    };
}
