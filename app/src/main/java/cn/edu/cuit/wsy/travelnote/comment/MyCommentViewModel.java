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
import cn.edu.cuit.wsy.travelnote.data.entity.UserInfo;
import cn.edu.cuit.wsy.travelnote.data.repository.CommentRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.note.TextEditActivity;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;

/**
 * Created by jianglei on 2017/5/10.
 */

public class MyCommentViewModel extends DetailViewModel implements Refreshable {
    public MyCommentActivity activity;
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

    public MyCommentViewModel(MyCommentActivity activity) {
        this.activity = activity;
        initComment();
    }

    public void deleteItem(final int index) {
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                CommentItemViewModel viewModel = (CommentItemViewModel) viewModels.get(index);
                UserInfo myInfo = UserRepository.getInstance().getMyInfo();
                return CommentRepository.getInstance().deleteComment(viewModel.comment, myInfo);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                if (s) {
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                    viewModels.remove(index);
                } else {
                    Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void editItem(int index) {
        CommentItemViewModel viewModel = (CommentItemViewModel) viewModels.get(index);
        TextEditActivity.startEditActivity(activity,viewModel.comment.getContent(),index);
    }

    public void initComment() {
        loadding.set(true);
        RxUtil.execute(new IOTask<List<Comment>>() {
            @Override
            public List<Comment> run() {
                UserInfo myInfo = UserRepository.getInstance().getMyInfo();
                return CommentRepository.getInstance().findCommentByUser(myInfo);
            }
        }, new UIAction<List<Comment>>() {
            @Override
            public void onComplete(List<Comment> comments) {
                loadding.set(false);
                viewModels.clear();
                initComments(comments);
                if (onComplete != null)
                    onComplete.onComplete();
            }
        });
    }

    public void commentItemEdit(final String content, int index) {
        final ListItemViewModel itemViewModel = viewModels.get(index);
        if (itemViewModel instanceof CommentItemViewModel) {
            RxUtil.execute(new IOTask<Boolean>() {
                @Override
                public Boolean run() {
                    return CommentRepository.getInstance().edit(((CommentItemViewModel) itemViewModel).comment,content);
                }
            }, new UIAction<Boolean>() {
                @Override
                public void onComplete(Boolean s) {
                    if (s) {
                        Toast.makeText(activity, "编辑成功", Toast.LENGTH_SHORT).show();
                        ((CommentItemViewModel) itemViewModel).content.set(content);
                    } else {
                        Toast.makeText(activity, "编辑失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void initComments(List<Comment> comments) {
        for (Comment comment : comments) {
            CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment);
            viewModels.add(itemViewModel);
        }
    }

    public void insert(Comment comment, Comment parent) {
        CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment, parent);
        viewModels.add(itemViewModel);
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
