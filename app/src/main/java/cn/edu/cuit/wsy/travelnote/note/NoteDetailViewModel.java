package cn.edu.cuit.wsy.travelnote.note;

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
import cn.edu.cuit.wsy.travelnote.data.entity.Comment;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.entity.NoteContentItem;
import cn.edu.cuit.wsy.travelnote.data.repository.CommentRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.NoteRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
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

public class NoteDetailViewModel extends DetailViewModel implements Refreshable {
    public NoteDetailActivity activity;
    private Note note;
    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_TEXT:
                    itemView.set(BR.itemViewModel, R.layout.list_note_text);
                    break;
                case ListItemViewModel.VIEW_TYPE_IMAGE:
                    itemView.set(BR.itemViewModel, R.layout.list_note_image);
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
    public ObservableField<String> commentContent = new ObservableField<>();
    public ObservableBoolean editble=new ObservableBoolean(false);
    public ReplyCommand commentClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            TextEditActivity.startEditActivity(activity, commentContent.get());
        }
    });
    public ReplyCommand eidtClick=new ReplyCommand(new Action0() {
        @Override
        public void call() {
            Intent intent=new Intent(activity,NewNoteActivity.class);
            intent.putExtra("objectId",note.getObjectId());
            activity.startActivity(intent);
        }
    });

    public ReplyCommand deleteClick=new ReplyCommand(new Action0() {
        @Override
        public void call() {
            deleteNote();
        }
    });
    public void deleteNote(){
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                return NoteRepository.getInstance().deleteNote(note);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                if (s){
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                    activity.noteUpdate();
                    activity.finish();
                }else {
                    Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public NoteDetailViewModel(NoteDetailActivity activity, String objectId) {
        this.activity = activity;
        note = NoteRepository.getInstance().findFromCache(objectId);
        title.set(note.getTitle());
        initByNote(note);
        initComment(note);

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
                return CommentRepository.getInstance().commentNote(note, comment);
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

    public void initComment(Note note) {
        List<Comment> comments = note.getComments();
        viewModels.add(new CommentBannerViewModel(this));
        for (Comment comment : comments) {
            CommentItemViewModel itemViewModel = new CommentItemViewModel(this, comment);
            viewModels.add(itemViewModel);
        }
    }

    public void initByNote(Note note) {
        viewModels.clear();
        String content = note.getContent();
        try {
            if (LeanEngine.equeal(note.getSender(),UserRepository.getInstance().getMyInfo())){
                editble.set(true);
            }else {
                editble.set(false);
            }
        }catch (Exception e){
            editble.set(false);
        }
        List<NoteContentItem> items = NoteContentParser.items(content);
        for (NoteContentItem item : items) {
            if (item.getType().equals(NoteContentItem.TYPE_TEXT)) {
                TextItemViewModel textItemViewModel = new TextItemViewModel(this);
                textItemViewModel.content.set(item.getContent());
                viewModels.add(textItemViewModel);
            } else if (item.getType().equals(NoteContentItem.TYPE_IMAGE)) {
                if (TextUtils.isEmpty(item.getContent()))
                    continue;
                ImageItemViewModel imageItemViewModel = new ImageItemViewModel(this);
                imageItemViewModel.url.set(item.getContent());
                viewModels.add(imageItemViewModel);
            }
        }
    }

    @Override
    public void onBack() {
        activity.finish();
    }

    @Override
    public void onLoadMore(OnComplete complete) {

    }

    @Override
    public void onRefresh(final OnComplete complete) {
        loadding.set(true);
        RxUtil.execute(new IOTask<Note>() {
            @Override
            public Note run() {
                return NoteRepository.getInstance().find(note.getObjectId());
            }
        }, new UIAction<Note>() {
            @Override
            public void onComplete(Note note) {
                loadding.set(false);
                complete.onComplete();
                NoteDetailViewModel.this.note = note;
                initByNote(note);
                initComment(note);
            }
        });
    }
}
