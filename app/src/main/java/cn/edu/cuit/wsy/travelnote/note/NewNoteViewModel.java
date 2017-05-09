package cn.edu.cuit.wsy.travelnote.note;

import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.kelin.mvvmlight.command.ReplyCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.cuit.wsy.travelnote.BR;
import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.base.DetailViewModel;
import cn.edu.cuit.wsy.travelnote.base.ListItemViewModel;
import cn.edu.cuit.wsy.travelnote.data.entity.Note;
import cn.edu.cuit.wsy.travelnote.data.entity.NoteContentItem;
import cn.edu.cuit.wsy.travelnote.data.repository.FileRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.NoteRepository;
import cn.edu.cuit.wsy.travelnote.data.repository.UserRepository;
import cn.edu.cuit.wsy.travelnote.target.CityActivity;
import cn.edu.cuit.wsy.travelnote.utils.BitmapUtil;
import cn.edu.cuit.wsy.travelnote.utils.IOTask;
import cn.edu.cuit.wsy.travelnote.utils.NoteContentParser;
import cn.edu.cuit.wsy.travelnote.utils.RxUtil;
import cn.edu.cuit.wsy.travelnote.utils.StringUtil;
import cn.edu.cuit.wsy.travelnote.utils.UIAction;
import me.tatarka.bindingcollectionadapter.ItemView;
import me.tatarka.bindingcollectionadapter.ItemViewSelector;
import rx.functions.Action0;

/**
 * Created by jianglei on 2017/5/7.
 */

public class NewNoteViewModel extends DetailViewModel {
    private NewNoteActivity activity;
    public ObservableField<String> noteTitle = new ObservableField<>();
    public ObservableField<String> city = new ObservableField<>();
    private Note note;
    private List<NoteContentItem> failedImage = new ArrayList<>();
    public ObservableList<ListItemViewModel> viewModels = new ObservableArrayList<>();
    public ItemViewSelector<ListItemViewModel> itemView = new ItemViewSelector<ListItemViewModel>() {
        @Override
        public void select(ItemView itemView, int position, ListItemViewModel item) {
            switch (item.getViewType()) {
                case ListItemViewModel.VIEW_TYPE_IMAGE:
                    itemView.set(BR.itemViewModel, R.layout.list_note_image);
                    break;
                case ListItemViewModel.VIEW_TYPE_TEXT:
                    itemView.set(BR.itemViewModel, R.layout.list_note_text);
                    break;
                case ListItemViewModel.VIEW_TYPE_ADD:
                    itemView.set(BR.itemViewModel, R.layout.list_note_add);
                    break;
            }
        }

        @Override
        public int viewTypeCount() {
            return 1;
        }
    };
    public ReplyCommand sendClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            sendNote();
        }
    });
    public ReplyCommand cityClick = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            Intent intent=new Intent(activity, CityActivity.class);
            intent.putExtra(CityActivity.CONTENT,city.get());
            activity.startActivity(intent);
        }
    });
    public void onImageAdd(String uri, int index) {
        ImageItemViewModel itemViewModel = new ImageItemViewModel(this);
        itemViewModel.url.set(uri);
        viewModels.add(index, itemViewModel);
        insertAddAt(index);
    }

    public void onTextAdd(String text, int index) {
        TextItemViewModel itemViewModel = new TextItemViewModel(this);
        itemViewModel.content.set(text);
        viewModels.add(index, itemViewModel);
        insertAddAt(index);
    }

    public void onCityChange(String newCity) {
        city.set(newCity);
    }

    public void onTextChange(String text, int index) {
        ListItemViewModel itemViewModel = viewModels.get(index);
        if (itemViewModel instanceof TextItemViewModel) {
            if (TextUtils.isEmpty(text)) {
                removeItem(itemViewModel);
            } else {
                ((TextItemViewModel) itemViewModel).content.set(text);
            }
        }
    }

    public void startEdit(String content, int index) {
        TextEditActivity.startEditActivity(activity, content, index);
    }

    public void removeEmpty() {
        Iterator<ListItemViewModel> iterator = viewModels.iterator();
        while (iterator.hasNext()) {
            ListItemViewModel viewModel = iterator.next();
            if (viewModel instanceof TextItemViewModel) {
                if (TextUtils.isEmpty(((TextItemViewModel) viewModel).content.get())) {
                    iterator.remove();
                }
            }
        }
    }

    public void removeItem(ListItemViewModel itemViewModel) {
        int index = viewModels.indexOf(itemViewModel);
        viewModels.remove(itemViewModel);
        viewModels.remove(index);
    }

    public void sendNote() {
        if (TextUtils.isEmpty(noteTitle.get())) {
            Toast.makeText(activity, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(city.get())) {
            Toast.makeText(activity, "请选择地点", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<NoteContentItem> items = getItems();
        if (items.size() <= 0) {
            Toast.makeText(activity, "请添加内容", Toast.LENGTH_SHORT).show();
            return;
        }
        loadding.set(true);
        if (note == null) {
            note = new Note();
        }
        note.setTitle(noteTitle.get());
        RxUtil.execute(new IOTask<Boolean>() {
            @Override
            public Boolean run() {
                failedImage.clear();
                note.setSender(UserRepository.getInstance().getMyInfo());
                note.setCity(city.get());
                saveImages(items);
                String content = NoteContentParser.toString(items);
                note.setContent(content);
                return NoteRepository.getInstance().sendNote(note);
            }
        }, new UIAction<Boolean>() {
            @Override
            public void onComplete(Boolean s) {
                loadding.set(false);
                if (s) {
                    Toast.makeText(activity, "发布成功", Toast.LENGTH_SHORT).show();
                    activity.noteEdit();
                    activity.finish();
                } else {
                    Toast.makeText(activity, "发布失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    boolean uploadFinish = false;
    int retryTmies = 0;

    public void saveImages(List<NoteContentItem> items) {
        failedImage.clear();
        for (NoteContentItem item : items) {
            try {
                if (item.getType().equals(NoteContentItem.TYPE_IMAGE) && isLocal(item.getContent())) {
                    Uri uri = Uri.parse(item.getContent());
                    String url = FileRepository.getInstance().save(new File(uri.getPath()));
                    if (url != null) {
                        item.setContent(url);
                    } else {
                        failedImage.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (NoteContentItem item : failedImage) {
            try {
                if (item.getType().equals(NoteContentItem.TYPE_IMAGE) && isLocal(item.getContent())) {
                    File file = new File(item.getContent());
                    String url = FileRepository.getInstance().save(file);
                    if (url != null) {
                        item.setContent(url);
                        failedImage.remove(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isLocal(String uri) {
        return !uri.startsWith("http://");
    }

    public void compressImages(List<NoteContentItem> items) {
        final List<NoteContentItem> images = new ArrayList<>();
        for (NoteContentItem item : items) {
            try {
                if (item.getType().equals(NoteContentItem.TYPE_IMAGE) && isLocal(item.getContent())) {
                    images.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        failedImage.clear();
        FileRepository.getInstance().saveAllImage(images, activity, new FileRepository.UploadCallBack() {

            @Override
            public void onNext(String url, int index) {
                images.get(index).setContent(url);
            }

            @Override
            public void onComple() {
                if (failedImage.size() > 0 && retryTmies < 3) {
                    compressImages(failedImage);
                    retryTmies++;
                } else {
                    uploadFinish = true;
                }
            }

            @Override
            public void onFailed(String localUrl, int index) {
                failedImage.add(images.get(index));
            }
        });
        while (!uploadFinish) {
            continue;
        }
    }

    private static final String TAG = "NewNoteViewModel";

    public List<NoteContentItem> getItems() {
        List<NoteContentItem> items = new ArrayList<>();
        for (ListItemViewModel viewModel : viewModels) {
            NoteContentItem item = new NoteContentItem();
            if (viewModel instanceof TextItemViewModel) {
                item.setType(NoteContentItem.TYPE_TEXT);
                item.setContent(((TextItemViewModel) viewModel).content.get());
                items.add(item);
            } else if (viewModel instanceof ImageItemViewModel) {
                item.setType(NoteContentItem.TYPE_IMAGE);
                item.setContent(((ImageItemViewModel) viewModel).url.get());
                items.add(item);
            }
        }
        return items;
    }

    public NewNoteViewModel(NewNoteActivity activity, String objectId) {
        this.activity = activity;
        init(objectId);
    }

    public void init(String objectId) {
        if (objectId == null) {
            initEmpty();
        } else {
            Note note = NoteRepository.getInstance().findFromCache(objectId);
            this.note = note;
            initByNote(note);
        }
    }

    public void initByNote(Note note) {
        String content = note.getContent();
        List<NoteContentItem> items = NoteContentParser.items(content);
        title.set(note.getTitle());
        noteTitle.set(note.getTitle());
        city.set(note.getCity());
        for (NoteContentItem item : items) {
            AddItemViewModel addItemViewModel = new AddItemViewModel(activity);
            viewModels.add(addItemViewModel);
            if (item.getType().equals(NoteContentItem.TYPE_TEXT)) {
                TextItemViewModel textItemViewModel = new TextItemViewModel(this);
                textItemViewModel.content.set(item.getContent());
                viewModels.add(textItemViewModel);
            } else if (item.getType().equals(NoteContentItem.TYPE_IMAGE)) {
                ImageItemViewModel imageItemViewModel = new ImageItemViewModel(this);
                imageItemViewModel.url.set(item.getContent());
                viewModels.add(imageItemViewModel);
            }
        }
        if (!(viewModels.get(viewModels.size() - 1) instanceof AddItemViewModel)) {
            AddItemViewModel addItemViewModel = new AddItemViewModel(activity);
            viewModels.add(addItemViewModel);
        }
    }

    public void initEmpty() {
        insertAddAt(0);
    }

    public void insertAddAt(int index) {
        AddItemViewModel itemViewModel = new AddItemViewModel(activity);
        viewModels.add(index, itemViewModel);
    }

    @Override
    public void onBack() {
        activity.finish();
    }
}
