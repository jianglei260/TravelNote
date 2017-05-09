package cn.edu.cuit.wsy.travelnote.note;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;

import com.kelin.mvvmlight.command.ReplyCommand;

import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.utils.NoteContentParser;
import cn.edu.cuit.wsy.travelnote.utils.StringUtil;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NoteItemViewModel extends ListItemViewModel {
    private Note note;
    private Context context;
    public ObservableField<String> commentNum = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> sender = new ObservableField<>();
    public ObservableField<String> date = new ObservableField<>();
    public ObservableField<String> userHead = new ObservableField<>();
    public ObservableField<String> image = new ObservableField<>();
    public ObservableField<String> city = new ObservableField<>();

    public ReplyCommand itemClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            Intent intent = new Intent(context, NoteDetailActivity.class);
            intent.putExtra("objectId", note.getObjectId());
            context.startActivity(intent);
        }
    });

    public NoteItemViewModel(Context context, Note note) {
        this.note = note;
        this.context = context;
        commentNum.set(String.valueOf(note.getComments().size()));
        title.set(note.getTitle());
        sender.set(note.getSender().getNick());
        date.set(StringUtil.getFormatDate(Long.valueOf(note.getCreatedAt())));
        userHead.set(note.getSender().getUrl());
        image.set(NoteContentParser.firstImage(note.getContent()));
        city.set(note.getCity());
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_NORMAL;
    }
}
