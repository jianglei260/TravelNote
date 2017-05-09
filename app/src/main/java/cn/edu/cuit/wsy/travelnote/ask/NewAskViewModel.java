package cn.edu.cuit.wsy.travelnote.ask;

import android.databinding.ObservableField;
import android.text.TextUtils;
import android.widget.Toast;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.data.repository.AskRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NewAskViewModel extends DetailViewModel {
    private NewAskActivity activity;
    private Ask ask;

    public NewAskViewModel(NewAskActivity activity,String objectId) {
        this.activity = activity;
        title.set("发表问题");
        if (!TextUtils.isEmpty(objectId)){
            ask=AskRepository.getInstance().findFromCahe(objectId);
            content.set(ask.getContent());
            askTitle.set(ask.getTitle());
        }
    }

    public ObservableField<String> content = new ObservableField<>();
    public ObservableField<String> askTitle = new ObservableField<>();
    public ReplyCommand sendClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            sendAsk();
        }
    });


    public void sendAsk() {
        if (TextUtils.isEmpty(askTitle.get())) {
            Toast.makeText(activity, R.string.title_empty_notiy, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content.get())) {
            Toast.makeText(activity, R.string.content_empty_notiy, Toast.LENGTH_SHORT).show();
            return;
        }
        loadding.set(true);
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                if (ask == null) {
                    ask = new Ask();
                }
                ask.setTitle(askTitle.get());
                ask.setContent(content.get());
                ask.setSender(UserRepository.getInstance().getMyInfo());
                return AskRepository.getInstance().sendAsk(ask);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean success) {
                loadding.set(false);
                if (success == true) {
                    Toast.makeText(activity, R.string.send_success, Toast.LENGTH_SHORT).show();

                    activity.finish();
                } else {
                    Toast.makeText(activity, R.string.send_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBack() {
        activity.finish();
    }
}
