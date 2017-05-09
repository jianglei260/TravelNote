package cn.edu.cuit.wsy.travelnote.comment;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.Toast;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.BR;
import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.base.Refreshable;
import cn.edu.cuit.wsy.travelnote.data.entity.Comment;
import cn.edu.cuit.wsy.travelnote.data.repository.CommentRepository;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;

/**
 * Created by jianglei on 2017/5/9.
 */

public class CommentListViewModel extends DetailViewModel implements Refreshable {
    private Comment comment;
    public CommentListActivity activity;
    private String objectId;
    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_COMMENT:
                    itemView.set(BR.itemViewModel, R.layout.list_comment);
                    break;
            }
        }

        @Override
        public int viewTypeCount() {
            return 1;
        }
    };

    public CommentListViewModel(CommentListActivity activity, String objectId) {
        this.activity = activity;
        this.objectId = objectId;
        title.set("所有评论");
        initComment();
    }

    public void initComment() {
        loadding.set(true);
        RxUtil.execute(new IOTask<Comment>() {
            @Override
            public Comment run() {
                return CommentRepository.getInstance().find(objectId);
            }
        }, new UIAction<Comment>() {
            @Override
            public void onComplete(Comment comment) {
                loadding.set(false);
                if (comment == null) {
                    Toast.makeText(activity, "加载失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                CommentListViewModel.this.comment = comment;
                viewModels.clear();
                initChilds(comment);
                if (onComplete != null)
                    onComplete.onComplete();
            }
        });
    }
    public void commentItemEdit(String content, int index) {
        ListItemViewModel itemViewModel = viewModels.get(index);
        if (itemViewModel instanceof CommentItemViewModel) {
            ((CommentItemViewModel) itemViewModel).onComment(content);
        }
    }

    public void initChilds(Comment parent) {
        List<Comment> comments = parent.getComments();
        for (Comment comment : comments) {
            CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment,parent);
            viewModels.add(itemViewModel);
            initChildComentChilds(comment);
        }
    }

    public void insert(Comment comment,Comment parent){
        CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment, parent);
        viewModels.add(itemViewModel);
    }

    public void initChildComentChilds(Comment child) {
        List<Comment> comments = child.getComments();
        for (Comment comment : comments) {
            CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment, child);
            viewModels.add(itemViewModel);
            initChildComentChilds(comment);
        }
    }

    @Override
    public void onBack() {
        activity.finish();
    }

    @Override
    public void onLoadMore(OnComplete complete) {

    }

    private OnComplete onComplete;

    @Override
    public void onRefresh(OnComplete complete) {
        this.onComplete = complete;
        initComment();
    }
}
