package cn.edu.cuit.wsy.travelnote.ask;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.text.TextUtils;
import android.widget.Toast;

import com.kelin.mvvmlight.command.ReplyCommand;

import java.util.List;

import cn.edu.cuit.wsy.travelnote.BR;
import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.base.Refreshable;
import cn.edu.cuit.wsy.travelnote.comment.CommentBannerViewModel;
import cn.edu.cuit.wsy.travelnote.comment.CommentItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Ask;
import cn.edu.cuit.wsy.travelnote.data.entity.Comment;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.entity.NoteContentItem;
import cn.edu.cuit.wsy.travelnote.data.repository.AskRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.CommentRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.NoteRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.note.ImageItemViewModel;
import cn.edu.cuit.wsy.travelnote.note.NewNoteActivity;
import cn.edu.cuit.wsy.travelnote.note.NoteDetailViewModel;
import cn.edu.cuit.wsy.travelnote.note.TextEditActivity;
import cn.edu.cuit.wsy.travelnote.note.TextItemViewModel;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.LeanEngine;
import cn.edu.cuit.wsy.travelnote.utils.NoteContentParser;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class AskDetailViewModel extends DetailViewModel implements Refreshable {
    public AskDetailActivity activity;
    private String objectId;
    private Ask ask;
    public ObservableField<String> commentContent = new ObservableField<>();

    public ReplyCommand commentClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            TextEditActivity.startEditActivity(activity, commentContent.get());
        }
    });

    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_TEXT:
                    itemView.set(BR.itemViewModel, R.layout.list_note_text);
                    break;
                case ListItemViewModel.VIEW_TYPE_COMMENT:
                    itemView.set(BR.itemViewModel, R.layout.list_comment);
                    break;
                case ListItemViewModel.VIEW_TYPE_COMMENT_BANNER:
                    itemView.set(BR.itemViewModel, R.layout.list_comment_banner);
                    break;
            }
        }

        @Override
        public int viewTypeCount() {
            return 1;
        }
    };
    public ObservableBoolean editble = new ObservableBoolean(false);
    public ReplyCommand eidtClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            Intent intent = new Intent(activity, NewAskActivity.class);
            intent.putExtra("objectId", ask.getObjectId());
            activity.startActivity(intent);
        }
    });

    public ReplyCommand deleteClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            deleteAsk();
        }
    });

    public void deleteAsk() {
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                return AskRepository.getInstance().deleteAsk(ask);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                if (s) {
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                    activity.askUpdate();
                    activity.finish();
                } else {
                    Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public AskDetailViewModel(AskDetailActivity activity, String objectId) {
        this.activity = activity;
        this.objectId = objectId;
        ask = AskRepository.getInstance().findFromCahe(objectId);
        title.set(ask.getTitle());
        initContent(ask.getContent());
        initComment(ask);
    }

    public void commentEdit(String content) {
        commentContent.set(content);
        comment();
    }

    public void commentItemEdit(String content, int index) {
        ListItemViewModel itemViewModel = viewModels.get(index);
        if (itemViewModel instanceof CommentItemViewModel) {
            ((CommentItemViewModel) itemViewModel).onComment(content);
        }
    }


    public void comment() {
        if (!UserRepository.getInstance().isLogin()) {
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(commentContent.get())) {
            Toast.makeText(activity, "没有评论哦", Toast.LENGTH_SHORT).show();
            return;
        }
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                Comment comment = new Comment();
                comment.setSender(UserRepository.getInstance().getMyInfo());
                comment.setContent(commentContent.get());
                return CommentRepository.getInstance().commentAsk(ask, comment);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                if (s) {
                    Toast.makeText(activity, "评论成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "评论失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initComment(Ask ask) {
        List<Comment> comments = ask.getComments();
        viewModels.add(new CommentBannerViewModel(this));
        for (Comment comment : comments) {
            CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment);
            viewModels.add(itemViewModel);
        }
    }

    public void initContent(String content) {
       try {
           if (LeanEngine.equeal(ask.getSender(), UserRepository.getInstance().getMyInfo())) {
               editble.set(true);
           } else {
               editble.set(false);
           }
       }catch (Exception e){
           editble.set(false);
       }
        viewModels.clear();
        TextItemViewModel itemViewModel = new TextItemViewModel(this);
        itemViewModel.content.set(content);
        viewModels.add(itemViewModel);
    }

    @Override
    public void onBack() {
        activity.finish();
    }

    @Override
    public void onLoadMore(Refreshable.OnComplete complete) {

    }

    @Override
    public void onRefresh(final Refreshable.OnComplete complete) {
        loadding.set(true);
        RxUtil.execute(new IOTask<Ask>() {
            @Override
            public Ask run() {
                return AskRepository.getInstance().find(objectId);
            }
        }, new UIAction<Ask>() {
            @Override
            public void onComplete(Ask ask) {
                loadding.set(false);
                AskDetailViewModel.this.ask = ask;
                initContent(ask.getContent());
                initComment(ask);
                if (complete != null)
                    complete.onComplete();
            }
        });
    }
}
