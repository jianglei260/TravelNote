package cn.edu.cuit.wsy.travelnote.note;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.kelin.mvvmlight.command.ReplyCommand;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.BR;
import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.base.Refreshable;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.data.repository.NoteRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.main.NoteFragment;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NoteListViewModel extends DetailViewModel implements Refreshable{
    NoteListActivity activity;
    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_NORMAL:
                    itemView.set(BR.itemViewModel, R.layout.list_note);
                    break;
            }
        }

        @Override
        public int viewTypeCount() {
            return 1;
        }
    };

    public NoteListViewModel(NoteListActivity activity) {
        this.activity = activity;
        initNotes();
    }

    public void initNotes() {
        loadding.set(true);
        RxUtil.execute(new IOTask<List<Note>>() {
            @Override
            public List<Note> run() {
                UserInfo myInfo= UserRepository.getInstance().getMyInfo();
                return NoteRepository.getInstance().findNoteByUser(myInfo);
            }
        }, new UIAction<List<Note>>() {
            @Override
            public void onComplete(List<Note> notes) {
                loadding.set(false);
                viewModels.clear();
                for (Note note : notes) {
                    NoteItemViewModel itemViewModel = new NoteItemViewModel(activity, note);
                    viewModels.add(itemViewModel);
                }
                if (onComplete!=null)
                    onComplete.onComplete();
            }
        });
    }

    @Override
    public void onLoadMore(Refreshable.OnComplete complete) {

    }

    private Refreshable.OnComplete onComplete;

    @Override
    public void onRefresh(Refreshable.OnComplete complete) {
        this.onComplete = complete;
        initNotes();
    }
}
