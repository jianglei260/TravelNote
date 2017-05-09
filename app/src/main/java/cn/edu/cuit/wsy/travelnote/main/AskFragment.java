package cn.edu.cuit.wsy.travelnote.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseFragment;
import cn.edu.cuit.wsy.travelnote.databinding.FragmentAskBinding;

/**
 * Created by jianglei on 2017/4/27.
 */

public class AskFragment extends BaseFragment {
    FragmentAskBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ask, container, false);
        AskViewModel viewModel = new AskViewModel(this);
        binding.setAskViewModel(viewModel);
        return binding.getRoot();
    }
}
