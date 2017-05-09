package cn.edu.cuit.wsy.travelnote.comment;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.TextUtils;
import android.widget.Toast;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.ask.AskDetailActivity;
import cn.edu.cuit.wsy.travelnote.ask.AskDetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Comment;
import cn.edu.cuit.wsy.travelnote.data.repository.CommentRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.note.NoteDetailActivity;
import cn.edu.cuit.wsy.travelnote.note.NoteDetailViewModel;
import cn.edu.cuit.wsy.travelnote.note.TextEditActivity;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.StringUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class CommentItemViewModel extends ListItemViewModel {
    public Comment comment;
    private BaseViewModel parent;
    public ObservableField<String> sender = new ObservableField<>();
    public ObservableField<String> date = new ObservableField<>();
    public ObservableField<String> userHead = new ObservableField<>();
    public ObservableField<String> content = new ObservableField<>();
    public ObservableField<String> childContent1 = new ObservableField<>();
    public ObservableField<String> childContent2 = new ObservableField<>();
    public ObservableField<String> more = new ObservableField<>();
    public ObservableBoolean childVisible = new ObservableBoolean(false);
    public ReplyCommand itemClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (parent instanceof MyCommentViewModel){
                int index=((MyCommentViewModel) parent).viewModels.indexOf(CommentItemViewModel.this);
                ((MyCommentViewModel) parent).activity.editComment(index);
            }
        }
    });
    public ReplyCommand moreClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            if (parent instanceof NoteDetailViewModel) {
                NoteDetailActivity activity = ((NoteDetailViewModel) parent).activity;
                Intent intent = new Intent(activity, CommentListActivity.class);
                intent.putExtra("objectId", comment.getObjectId());
                activity.startActivity(intent);
            }

        }
    });

    public CommentItemViewModel(BaseViewModel parent, Comment comment) {
        this.comment = comment;
        this.parent = parent;
        sender.set(comment.getSender().getNick());
        userHead.set(comment.getSender().getUrl());
        content.set(comment.getContent());
        initDate();
        initChildComment();
    }

    public CommentItemViewModel(BaseViewModel parent, Comment comment, Comment beComment) {
        this.comment = comment;
        this.parent = parent;
        sender.set(comment.getSender().getNick());
        userHead.set(comment.getSender().getUrl());
        content.set("回复@" + beComment.getSender().getNick() + ":" + comment.getContent());
        initDate();
    }

    public void initDate() {
        long time = Long.valueOf(comment.getCreatedAt());
        date.set(StringUtil.getTimeString(time));
    }

    public ReplyCommand comentComentClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            comment();
        }
    });

    public void comment() {
        if (parent instanceof NoteDetailViewModel) {
            NoteDetailActivity activity = ((NoteDetailViewModel) parent).activity;
            int index = ((NoteDetailViewModel) parent).viewModels.indexOf(this);
            TextEditActivity.startEditActivity(activity, "", index);
        } else if (parent instanceof CommentListViewModel) {
            CommentListActivity activity = ((CommentListViewModel) parent).activity;
            int index = ((CommentListViewModel) parent).viewModels.indexOf(this);
            TextEditActivity.startEditActivity(activity, "", index);
        }else if (parent instanceof AskDetailViewModel){
            AskDetailActivity activity = ((AskDetailViewModel) parent).activity;
            int index = ((AskDetailViewModel) parent).viewModels.indexOf(this);
            TextEditActivity.startEditActivity(activity, "", index);
        }
    }

    public void onComment(final String content) {
        if (TextUtils.isEmpty(content))
            return;
        final Comment to = new Comment();
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                to.setSender(UserRepository.getInstance().getMyInfo());
                to.setContent(content);
                return CommentRepository.getInstance().commentComment(comment, to);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                if (s) {
                    if (parent instanceof NoteDetailViewModel) {
                        initChildComment();
                    } else if (parent instanceof CommentListViewModel) {
                        ((CommentListViewModel) parent).insert(to,comment);
                    }
                } else {
                    if (parent instanceof NoteDetailViewModel) {
                        NoteDetailActivity activity = ((NoteDetailViewModel) parent).activity;
                        Toast.makeText(activity, "评论失败", Toast.LENGTH_SHORT).show();
                    }else if (parent instanceof CommentListViewModel) {
                        CommentListActivity activity = ((CommentListViewModel) parent).activity;
                        Toast.makeText(activity, "评论失败", Toast.LENGTH_SHORT).show();
                    }else if (parent instanceof AskDetailViewModel){
                        AskDetailActivity activity = ((AskDetailViewModel) parent).activity;
                        Toast.makeText(activity, "评论失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void initChildComment() {
        int size = comment.getComments().size();
        if (size > 0) {
            childVisible.set(true);
            Comment comment1 = comment.getComments().get(0);
            childContent1.set(comment1.getSender().getNick() + "：" + comment1.getContent());
        }
        if (size > 1) {
            Comment comment2 = comment.getComments().get(1);
            childContent2.set(comment2.getSender().getNick() + "：" + comment2.getContent());
        }
        if (size > 2) {
            more.set("共" + size + "条回复 >");
        }

    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_COMMENT;
    }
}
