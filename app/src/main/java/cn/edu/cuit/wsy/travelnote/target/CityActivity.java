package cn.edu.cuit.wsy.travelnote.target;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.databinding.ActivityCityBinding;

public class CityActivity extends BaseActivity {
    public static final String ACTION_CITY_FINISH = "ACTION_CITY_FINISH";
    public static final String CONTENT = "city";
    private AMapLocationClient client;
    ActivityCityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String content = getIntent().getStringExtra(CONTENT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_city);
        binding.setCityViewModel(new CityViewModel(this));
        if (!TextUtils.isEmpty(content)){
            binding.getCityViewModel().city.set(content);
        }
        initLocation();
    }

    public void editFinish(String city) {
        Intent intent = new Intent(ACTION_CITY_FINISH);
        intent.putExtra(CONTENT, city);
        publishEvent(intent);
        finish();
    }

    public void initLocation() {
        client = new AMapLocationClient(getApplicationContext());
        client.setLocationListener(locationListener);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setOnceLocation(true);
        option.setNeedAddress(true);
        client.setLocationOption(option);
        client.startLocation();
    }

    public static String getCity(Intent intent) {
        return intent.getStringExtra(CONTENT);
    }

    private AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            String city = "成都";
            if (aMapLocation.getErrorCode() == 0) {
                city = aMapLocation.getCity();
            } else {
                Toast.makeText(CityActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
            }
            binding.getCityViewModel().onLocation(city);
        }
    };
}
