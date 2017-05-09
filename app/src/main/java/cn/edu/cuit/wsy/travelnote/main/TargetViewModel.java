package cn.edu.cuit.wsy.travelnote.main;

import android.content.Intent;
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
import cn.edu.cuit.wsy.travelnote.target.CityActivity;
import cn.edu.cuit.wsy.travelnote.target.CityItemViewModel;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;

/**
 * Created by jianglei on 2017/5/7.
 */

public class TargetViewModel extends DetailViewModel implements Refreshable {
    private TargetFragment fragment;
    private String city = "成都";
    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_NORMAL:
                    itemView.set(BR.itemViewModel, R.layout.list_note);
                    break;
                case ListItemViewModel.VIEW_TYPE_CITY:
                    itemView.set(BR.itemViewModel, R.layout.list_target);
                    break;
            }
        }

        @Override
        public int viewTypeCount() {
            return 1;
        }
    };

    public TargetViewModel(TargetFragment fragment) {
        this.fragment = fragment;
        title.set("目的地");
    }

    public void initNotes(final String city) {
        loadding.set(true);
        viewModels.clear();
        viewModels.add(new CityItemViewModel(this,city));
        RxUtil.execute(new IOTask<List<Note>>() {
            @Override
            public List<Note> run() {
                return NoteRepository.getInstance().findNoteByCity(city);
            }
        }, new UIAction<List<Note>>() {
            @Override
            public void onComplete(List<Note> notes) {
                loadding.set(false);
                for (Note note : notes) {
                    NoteItemViewModel itemViewModel = new NoteItemViewModel(fragment.getActivity(), note);
                    viewModels.add(itemViewModel);
                }
                if (onComplete != null)
                    onComplete.onComplete();
            }
        });
    }
    public void selectCity(){
        Intent intent=new Intent(fragment.getActivity(), CityActivity.class);
        fragment.startActivity(intent);
    }

    public void onLocation(String city) {
        this.city = city;
        initNotes(city);
    }

    @Override
    public void onLoadMore(Refreshable.OnComplete complete) {

    }

    private Refreshable.OnComplete onComplete;

    @Override
    public void onRefresh(Refreshable.OnComplete complete) {
        this.onComplete = complete;
        initNotes(city);
    }
}
