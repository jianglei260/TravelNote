package cn.edu.cuit.wsy.travelnote.base;

public abstract class ListItemViewModel {
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_LOAD_MORE = 1;
    public static final int VIEW_TYPE_LOAD_FINISH = 2;
    public static final int VIEW_TYPE_IMAGE = 3;
    public static final int VIEW_TYPE_TEXT = 4;
    public static final int VIEW_TYPE_ADD = 5;
    public static final int VIEW_TYPE_COMMENT = 6;
    public static final int VIEW_TYPE_COMMENT_BANNER = 7;
    public static final int VIEW_TYPE_NOTE_HEAD = 8;
    public static final int VIEW_TYPE_CITY = 9;

    public abstract int getViewType();
}