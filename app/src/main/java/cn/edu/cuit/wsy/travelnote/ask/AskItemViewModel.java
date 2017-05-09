package cn.edu.cuit.wsy.travelnote.ask;

import android.content.Intent;
import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.main.AskViewModel;
import cn.edu.cuit.wsy.travelnote.utils.StringUtil;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class AskItemViewModel extends ListItemViewModel {
    public ObservableField<String> content = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> sender = new ObservableField<>();
    public ObservableField<String> date = new ObservableField<>();
    public ObservableField<String> userHead = new ObservableField<>();
    private BaseViewModel parent;
    private Ask ask;
    public ReplyCommand itemClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (parent instanceof AskViewModel) {
                Intent intent = new Intent(((AskViewModel) parent).fragment.getActivity(), AskDetailActivity.class);
                intent.putExtra("objectId", ask.getObjectId());
                ((AskViewModel) parent).fragment.startActivity(intent);
            } else if (parent instanceof AskListViewModel) {
                Intent intent = new Intent(((AskListViewModel) parent).activity, AskDetailActivity.class);
                intent.putExtra("objectId", ask.getObjectId());
                ((AskListViewModel) parent).activity.startActivity(intent);
            }
        }
    });

    public AskItemViewModel(BaseViewModel parent, Ask ask) {
        this.parent = parent;
        this.ask = ask;
        title.set(ask.getTitle());
        content.set(ask.getContent());
        date.set(StringUtil.getTimeString(Long.valueOf(ask.getCreatedAt())));
        sender.set(ask.getSender().getNick());
        userHead.set(ask.getSender().getUrl());
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_NORMAL;
    }
}
