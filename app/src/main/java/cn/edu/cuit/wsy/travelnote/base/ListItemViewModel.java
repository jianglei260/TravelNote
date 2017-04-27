package cn.edu.cuit.wsy.travelnote.base;
public abstract class ListItemViewModel {
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_LOAD_MORE = 1;
    public static final int VIEW_TYPE_LOAD_FINISH = 2;
    public static final int VIEW_TYPE_BANNER = 3;
    public static final int VIEW_TYPE_LINEAR= 4;
    public static final int VIEW_TYPE_ACTIVE= 5;
    public static final int VIEW_TYPE_GOODS_BANNER= 6;


    public abstract int getViewType();
}