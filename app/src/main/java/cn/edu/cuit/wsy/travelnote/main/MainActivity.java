package cn.edu.cuit.wsy.travelnote.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.lang.reflect.Field;
import java.util.HashMap;

import cn.edu.cuit.wsy.travelnote.R;
import cn.edu.cuit.wsy.travelnote.ask.NewAskActivity;
import cn.edu.cuit.wsy.travelnote.base.BaseActivity;
import cn.edu.cuit.wsy.travelnote.note.NewNoteActivity;
import cn.edu.cuit.wsy.travelnote.target.CityActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String ACTION_USER_LOGIN = "ACTION_USER_LOGIN";
    public static final String ACTION_USER_LOGOUT = "ACTION_USER_LOGOUT";
    private static final String TAG = "MainActivity";
    private BottomNavigationBar bottomNavigationView;
    private HashMap<Integer, Fragment> menuMap;
    private Fragment selectd;
    private FrameLayout container;
    private NoteFragment noteFragment;
    private TargetFragment targetFragment;
    private AskFragment askFragment;
    private MeFragment meFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (FrameLayout) findViewById(R.id.container);
        bottomNavigationView = (BottomNavigationBar) findViewById(R.id.bottom_navigation);
        initFragment(savedInstanceState);
        menuMap = new HashMap<>();
        initBottomNavigation();
        registeEventAction(ACTION_USER_LOGIN);
        registeEventAction(CityActivity.ACTION_CITY_FINISH);
    }

    @Override
    protected void onEvent(String action) {
        super.onEvent(action);
        if (action.equals(ACTION_USER_LOGIN)) {
            if (meFragment != null)
                meFragment.binding.getMeViewModel().initUserInfo();
        }
        if (action.equals(ACTION_USER_LOGOUT)) {
            if (meFragment != null)
                meFragment.binding.getMeViewModel().initUserInfo();
        }

    }

    @Override
    protected void onEvent(Intent intent) {
        super.onEvent(intent);
        if (intent.getAction().equals(CityActivity.ACTION_CITY_FINISH)) {
            if (targetFragment != null)
                targetFragment.binding.getTargetViewModel().onLocation(CityActivity.getCity(intent));
        }
    }

    /*
           *初始化底部导航栏
           */
    public void initBottomNavigation() {
        bottomNavigationView.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationView.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationView.setBarBackgroundColor(R.color.white);
        bottomNavigationView.addItem(initItem(R.drawable.ic_note, R.string.note, 0, noteFragment))
                .addItem(initItem(R.drawable.ic_location, R.string.target, 1, targetFragment))
                .addItem(initItem(R.drawable.ic_ask, R.string.ask, 2, askFragment))
                .addItem(initItem(R.drawable.ic_me, R.string.me, 3, meFragment)).initialise();
        initBottomBar(bottomNavigationView);
        bottomNavigationView.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                show(menuMap.get(position));
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }

    private void initBottomBar(BottomNavigationBar navigationBar) {
        try {
            Field field = BottomNavigationBar.class.getDeclaredField("mTabContainer");
            field.setAccessible(true);
            LinearLayout tabContainer = (LinearLayout) field.get(navigationBar);
            ImageView addImageView = new ImageView(this);
            addImageView.setImageResource(R.drawable.add);
            int padding = getResources().getDimensionPixelSize(R.dimen.badge_top_margin);
            addImageView.setPadding(padding, 0, padding, padding);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.weight = 1;
            tabContainer.setPadding(0, getResources().getDimensionPixelSize(R.dimen.dp_6), 0, 0);
            tabContainer.addView(addImageView, 2);
            for (int i = 0; i < tabContainer.getChildCount(); i++) {
                View child = tabContainer.getChildAt(i);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.weight = 1;
                child.setLayoutParams(params);
            }
            addImageView.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (!getFragmentManager().isDestroyed()) {
                NoteFragment note = (NoteFragment) getFragmentManager().findFragmentByTag("note");
                if (note != null)
                    noteFragment = note;
                else
                    initNote();
                TargetFragment target = (TargetFragment) getFragmentManager().findFragmentByTag("target");
                if (target != null)
                    targetFragment = target;
                else
                    initTarget();
                AskFragment ask = (AskFragment) getFragmentManager().findFragmentByTag("ask");
                if (ask != null)
                    askFragment = ask;
                else
                    initAsk();
                MeFragment me = (MeFragment) getFragmentManager().findFragmentByTag("me");
                if (me != null)
                    meFragment = me;
                else
                    initMe();
            }
        } else {
            initNote();
            initTarget();
            initAsk();
            initMe();
        }
        getFragmentManager().beginTransaction().show(noteFragment).hide(targetFragment).hide(askFragment).hide(meFragment).commit();
        selectd = noteFragment;
    }

    public void initNote() {
        noteFragment = new NoteFragment();
        getFragmentManager().beginTransaction().add(R.id.container, noteFragment, "note").commit();
    }

    public void initTarget() {
        targetFragment = new TargetFragment();
        getFragmentManager().beginTransaction().add(R.id.container, targetFragment, "target").hide(targetFragment).commit();
    }

    public void initAsk() {
        askFragment = new AskFragment();
        getFragmentManager().beginTransaction().add(R.id.container, askFragment, "ask").hide(askFragment).commit();
    }

    public void initMe() {
        meFragment = new MeFragment();
        getFragmentManager().beginTransaction().add(R.id.container, meFragment, "me").hide(meFragment).commit();
    }

    public BottomNavigationItem initItem(@DrawableRes int icon, @StringRes int title, int position, Fragment fragment) {
        BottomNavigationItem item = new BottomNavigationItem(icon, getString(title));
        item.setActiveColorResource(R.color.colorPrimary);
        item.setInActiveColorResource(R.color.deepGray);
        menuMap.put(position, fragment);
        return item;
    }

    private void show(Fragment fragment) {
        Log.d(TAG, "show: " + fragment.getClass().getName());
        if (fragment == selectd)
            return;
        if (selectd != null)
            getFragmentManager().beginTransaction().hide(selectd).commit();
        getFragmentManager().beginTransaction().show(fragment).commit();
        selectd = fragment;
    }

    public void startSendActivity(Class<? extends Activity> actvity) {
        Intent intent = new Intent(this, actvity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String[] items = getResources().getStringArray(R.array.send_items);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择类型")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startSendActivity(NewNoteActivity.class);
                                break;
                            case 1:
                                startSendActivity(NewAskActivity.class);
                                break;
                        }
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

}
