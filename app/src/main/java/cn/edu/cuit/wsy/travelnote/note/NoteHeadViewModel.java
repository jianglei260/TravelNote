package cn.edu.cuit.wsy.travelnote.note;

import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;

/**
 * Created by jianglei on 2017/5/8.
 */

public class NoteHeadViewModel extends ListItemViewModel {
    private Note note;

    public NoteHeadViewModel(Note note) {
        this.note = note;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE_NOTE_HEAD;
    }
}
