package cn.edu.cuit.wsy.travelnote.base;

import android.databinding.ObservableBoolean;

import com.kelin.mvvmlight.command.ReplyCommand;

import rx.functions.Action0;

public class LoaddingViewModel extends BaseViewModel{
    public ObservableBoolean loadding = new ObservableBoolean(false);
    public ObservableBoolean emptyContent = new ObservableBoolean(false);
    public ReplyCommand onBackClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            LoaddingViewModel.this.onBack();
        }
    });
    public void onBack(){
    }}
