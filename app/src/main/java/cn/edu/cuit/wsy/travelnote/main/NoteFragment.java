package cn.edu.cuit.wsy.travelnote.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.BaseFragment;
import cn.edu.cuit.wsy.travelnote.databinding.FragmentNoteBinding;

/**
 * Created by jianglei on 2017/4/27.
 */

public class NoteFragment extends BaseFragment {
    FragmentNoteBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false);
        NoteViewModel viewModel = new NoteViewModel(this);
        binding.setNoteViewModel(viewModel);
        return binding.getRoot();
    }
}
