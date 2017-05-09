package cn.edu.cuit.wsy.travelnote.comment;

import android.databinding.ObservableField;

import cn.edu.cuit.wsy.travelnote.ask.AskDetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.BaseViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.main.AskViewModel;
import cn.edu.cuit.wsy.travelnote.note.NoteDetailViewModel;

/**
 * Created by jianglei on 2017/5/8.
 */

public class CommentBannerViewModel extends ListItemViewModel {
    public BaseViewModel parent;
    public ObservableField<String> text=new ObservableField<>();

    public CommentBannerViewModel(BaseViewModel parent) {
        this.parent = parent;
        if (parent instanceof AskDetailViewModel){
            text.set("热门回复");
        }else if (parent instanceof NoteDetailViewModel){
            text.set("热门评论");
        }
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_COMMENT_BANNER;
    }
}
