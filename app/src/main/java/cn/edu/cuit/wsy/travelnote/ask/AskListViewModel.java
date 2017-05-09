package cn.edu.cuit.wsy.travelnote.ask;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.BR;
import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.base.Refreshable;
import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.data.repository.AskRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.main.AskFragment;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;

/**
 * Created by jianglei on 2017/5/7.
 */

public class AskListViewModel extends DetailViewModel implements Refreshable{
    public AskListActivity activity;
    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_NORMAL:
                    itemView.set(BR.itemViewModel, R.layout.list_ask);
                    break;
            }
        }

        @Override
        public int viewTypeCount() {
            return 1;
        }
    };

    public AskListViewModel(AskListActivity activity) {
        this.activity = activity;
        initAsks();
    }

    public void initAsks() {
        loadding.set(true);
        RxUtil.execute(new IOTask<List<Ask>>() {
            @Override
            public List<Ask> run() {
                UserInfo myInfo= UserRepository.getInstance().getMyInfo();
                return AskRepository.getInstance().findAskByUser(myInfo);
            }
        }, new UIAction<List<Ask>>() {
            @Override
            public void onComplete(List<Ask> asks) {
                loadding.set(false);
                bindAsks(asks);
                if (onComplete != null)
                    onComplete.onComplete();
            }
        });
    }

    public void bindAsks(List<Ask> asks) {
        viewModels.clear();
        for (Ask ask : asks) {
            AskItemViewModel itemViewModel = new AskItemViewModel(this, ask);
            viewModels.add(itemViewModel);
        }
    }

    @Override
    public void onLoadMore(Refreshable.OnComplete complete) {

    }

    private Refreshable.OnComplete onComplete;

    @Override
    public void onRefresh(Refreshable.OnComplete complete) {
        this.onComplete = complete;
        initAsks();
    }

    @Override
    public void onBack() {
        activity.finish();
    }
}
