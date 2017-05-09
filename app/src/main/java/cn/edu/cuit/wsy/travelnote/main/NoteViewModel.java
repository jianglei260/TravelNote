package cn.edu.cuit.wsy.travelnote.main;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.BR;
import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.base.Refreshable;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.repository.NoteRepository;
import cn.edu.cuit.wsy.travelnote.note.NoteItemViewModel;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NoteViewModel extends DetailViewModel implements Refreshable {
    private NoteFragment fragment;
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

    public NoteViewModel(NoteFragment fragment) {
        this.fragment = fragment;
        initNotes();
    }

    public void initNotes() {
        loadding.set(true);
        RxUtil.execute(new IOTask<List<Note>>() {
            @Override
            public List<Note> run() {
                return NoteRepository.getInstance().findAllNote();
            }
        }, new UIAction<List<Note>>() {
            @Override
            public void onComplete(List<Note> notes) {
                loadding.set(false);
                viewModels.clear();
                for (Note note : notes) {
                    NoteItemViewModel itemViewModel = new NoteItemViewModel(fragment.getActivity(), note);
                    viewModels.add(itemViewModel);
                }
                if (onComplete!=null)
                    onComplete.onComplete();
            }
        });
    }

    @Override
    public void onLoadMore(OnComplete complete) {

    }

    private OnComplete onComplete;

    @Override
    public void onRefresh(OnComplete complete) {
        this.onComplete = complete;
        initNotes();
    }
}
