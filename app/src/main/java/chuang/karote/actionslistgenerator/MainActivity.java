package chuang.karote.actionslistgenerator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import chuang.karote.actionslistgenerator.adapter.ActionsListAdapter;
import chuang.karote.actionslistgenerator.adapter.CheckListAdapter;
import chuang.karote.actionslistgenerator.adapter.ContainsFilterArrayAdapter;
import chuang.karote.actionslistgenerator.adapter.PictureViewPagerAdapter;
import chuang.karote.actionslistgenerator.adapter.SavedActionListAdapter;
import chuang.karote.actionslistgenerator.adapter.SelectedListAdapter;
import chuang.karote.actionslistgenerator.model.CalenderLog;
import chuang.karote.actionslistgenerator.model.SavedActionList;
import chuang.karote.actionslistgenerator.model.TabataAction;
import chuang.karote.actionslistgenerator.model.TabataConfig;
import chuang.karote.actionslistgenerator.sqlite.CalenderLogDataAccessObject;
import chuang.karote.actionslistgenerator.sqlite.SavedActionListDataAccessObject;
import chuang.karote.actionslistgenerator.ui.CalenderLogFragment;
import chuang.karote.actionslistgenerator.ui.ClearableAutoCompleteTextView;
import utility.Util;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String APP_FOLDER_NAME = "ALG";
    private final static String CONFIG_FILE_NAME = "config.json";

    private boolean canPlay = true;

    private List<TabataAction> resourceList;
    private HashMap<String, Boolean> resourceMap;

    private MediaPlayer mp;

    private RecyclerView actionsListRecyclerView;
    private Button add1Button;
    private Button add8Button;
    private Button clearButton;
    private Button playButton;
    private ClearableAutoCompleteTextView autoCompleteTextView;
    private ArrayList<TabataAction> actionList;
    private ActionsListAdapter actionsListAdapter;
    private Button addButton;
    private MenuItem menuSaveItem;
    private MenuItem menuLoadItem;
    private boolean menuSaveItemStatus = false;
    private boolean menuLoadItemStatus = true;
    private SavedActionListDataAccessObject savedActionListDAO;

    private RecyclerView selectedListRecyclerView;
    private SelectedListAdapter selectedListAdapter;
    private ListView checkListView;

    private CalenderLogDataAccessObject calenderLogDAO;
    private int counterOfToday = 0;
    private CalenderLogFragment dialogCaldroidFragment;
    private DotIndicator indicator;
    private CalenderLog todayLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = new MediaPlayer();

        savedActionListDAO = new SavedActionListDataAccessObject(MainActivity.this);
        calenderLogDAO = new CalenderLogDataAccessObject(MainActivity.this);
        todayLog = calenderLogDAO.getRecordByDate(getTodayDate());
        if (todayLog != null) {
            counterOfToday = todayLog.getCounter();
        }

        readConfig();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.calender_dialog:
                        showCalenderDialog();
                        break;
                    case R.id.action_edit:
                        Intent editIntent = new Intent();
                        editIntent.setClass(MainActivity.this, EditModeActivity.class);
                        startActivity(editIntent);
                        MainActivity.this.finish();
                        break;
                    case R.id.action_save:
                        showSaveListNameDialog();
                        break;
                    case R.id.action_load:
                        showLoadListDialog();
                        break;
                }
                return true;
            }
        });

        initView();

        if (resourceList == null) {
            add1Button.setEnabled(false);
            add8Button.setEnabled(false);
            clearButton.setEnabled(false);
            playButton.setEnabled(false);
            menuLoadItemStatus = false;
            autoCompleteTextView.setEnabled(false);
            Util.showLongToast(this, CONFIG_FILE_NAME + " open fail");
            return;
        }

        resourceMap = new HashMap<>();
        String[] autoCompleteResources = new String[resourceList.size()];
        for (int i = 0; i < resourceList.size(); i++) {
            resourceMap.put(resourceList.get(i).getName(), false);

            autoCompleteResources[i] = resourceList.get(i).getName();
        }

        ContainsFilterArrayAdapter<String> autoCompleteAdapter = new ContainsFilterArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompleteResources);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputString = editable.toString();
                if (checkInputInResourceList(inputString)) {
                    if (!checkInputInActionList(inputString)) {
                        addButton.setEnabled(true);
                    } else {
                        addButton.setEnabled(false);
                    }
                } else {
                    addButton.setEnabled(false);
                }
            }
        });

        actionList = new ArrayList<>();
        if (savedInstanceState != null) {
            actionList = savedInstanceState.getParcelableArrayList("actionList");
            resourceMap = (HashMap<String, Boolean>) savedInstanceState.getSerializable("resourceMap");
        }
        actionsListAdapter = new ActionsListAdapter(actionList, ActionsListAdapter.ADAPTER_MODE_LIST);
        actionsListAdapter.setOnItemClickListener(new ActionsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showDetailDialog(actionList.get(position).getName(), actionList.get(position).getPictures());
            }

            @Override
            public void onItemDeleteClick(int position) {
                if (mp.isPlaying()) {
                    return;
                }
                resourceMap.put(actionList.get(position).getName(), false);
                actionList.remove(position);
                actionsListAdapter.notifyDataSetChanged();
                selectedListAdapter.notifyDataSetChanged();

                if (actionList.size() == 0) {
                    mp.seekTo(0);
                }
                updateButtonStatus();
            }
        });
        actionsListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        actionsListRecyclerView.setAdapter(actionsListAdapter);

        selectedListAdapter = new SelectedListAdapter(actionList);
        selectedListAdapter.setOnItemClickListener(new SelectedListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showDetailDialog(actionList.get(position).getName(), actionList.get(position).getPictures());
            }
        });
        selectedListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        selectedListRecyclerView.setAdapter(selectedListAdapter);

        CheckListAdapter checkListAdapter = new CheckListAdapter(MainActivity.this, resourceList, resourceMap);
        checkListView.setAdapter(checkListAdapter);
        checkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.action_name);
                if (actionList.size() > 7 && !chkItem.isChecked()) {
                    return;
                }
                chkItem.setChecked(!chkItem.isChecked());
                resourceMap.put(resourceList.get(i).getName(), chkItem.isChecked());
                if (chkItem.isChecked()) {
                    addActionItemToActionList(resourceList.get(i));
                } else {
                    for (int i1 = 0; i1 < actionList.size(); i1++) {
                        if (actionList.get(i1).getName().equals(resourceList.get(i).getName())) {
                            actionList.remove(i1);
                            actionsListAdapter.notifyDataSetChanged();
                            selectedListAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                counterOfToday++;

                if (todayLog == null) {
                    CalenderLog.Builder calenderLogBuilder = new CalenderLog.Builder();
                    todayLog = calenderLogBuilder
                            .setCalenderDate(getTodayDate())
                            .setCounter(counterOfToday)
                            .create();
                    calenderLogDAO.insert(todayLog);
                } else {
                    todayLog.setCounter(counterOfToday);
                    calenderLogDAO.update(todayLog);
                }

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                playButton.setText("Play");
                updateButtonStatus();

                showCalenderDialog();
            }
        });

        updateButtonStatus();
    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.release();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        savedActionListDAO.close();
        calenderLogDAO.close();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("actionList", actionList);
        outState.putSerializable("resourceMap", resourceMap);
        if (dialogCaldroidFragment != null && dialogCaldroidFragment.isVisible()) {
            dialogCaldroidFragment.dismiss();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menuSaveItem = menu.findItem(R.id.action_save);
        menuLoadItem = menu.findItem(R.id.action_load);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuSaveItem.setEnabled(menuSaveItemStatus);
        menuLoadItem.setEnabled(menuLoadItemStatus);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_1_button:
                addActionItemToActionList(getActionItemInRandom());
                break;

            case R.id.add_8_button:
                while (actionList.size() < 8) {
                    actionList.add(getActionItemInRandom());
                }
                actionsListAdapter.notifyDataSetChanged();
                selectedListAdapter.notifyDataSetChanged();
                break;

            case R.id.clear_button:
                mp.seekTo(0);
                for (TabataAction listItem : resourceList) {
                    resourceMap.put(listItem.getName(), false);
                }
                actionList.clear();
                actionsListAdapter.notifyDataSetChanged();
                selectedListAdapter.notifyDataSetChanged();
                break;

            case R.id.play_button:
                if (!mp.isPlaying()) {
                    playButton.setText("Pause");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mp.start();
                } else {
                    playButton.setText("Play");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    mp.pause();
                }
                break;

            case R.id.add_button:
                if (actionList.size() > 7)
                    return;

                String inputString = autoCompleteTextView.getText().toString();
                autoCompleteTextView.setText("");
                resourceMap.put(inputString, true);
                for (int i = 0; i < resourceList.size(); i++) {
                    if (inputString.equals(resourceList.get(i).getName())) {
                        addActionItemToActionList(resourceList.get(i));
                        break;
                    }
                }

        }
        updateButtonStatus();
    }

    private void readConfig() {
        StringBuilder stringBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        StringBuilder rootPath = stringBuilder.append(File.separatorChar).append(APP_FOLDER_NAME);
        String json = getJson(rootPath.toString(), CONFIG_FILE_NAME);
        try {
            if (json != null) {
                Gson gson = new Gson();
                TabataConfig config = gson.fromJson(json, TabataConfig.class);
                String musicPath = rootPath.append(File.separator).append(config.getMusic()).toString();
                try {
                    mp.setDataSource(musicPath);
                    mp.setLooping(false);
                    mp.prepare();
                } catch (FileNotFoundException e) {
                    canPlay = false;
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                resourceList = config.getActions();
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            toastJsonFormatError(APP_FOLDER_NAME);
        }
    }

    private static String getJson(String dirPath, String fileName) {
        String json = Util.readFile(dirPath, fileName);
        return json.isEmpty() ? null : json;
    }

    private void toastJsonFormatError(String path) {
        final String message = path + "/" + CONFIG_FILE_NAME + " 檔案格式有誤，請檢查修正！";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Util.showLongToast(getBaseContext(), message);
            }
        });
    }

    private void initView() {
        autoCompleteTextView = (ClearableAutoCompleteTextView) findViewById(R.id.search_ac_textview);
        addButton = (Button) findViewById(R.id.add_button);
        actionsListRecyclerView = (RecyclerView) findViewById(R.id.action_list);
        selectedListRecyclerView = (RecyclerView) findViewById(R.id.selected_list);
        checkListView = (ListView) findViewById(R.id.check_list);
        add1Button = (Button) findViewById(R.id.add_1_button);
        add8Button = (Button) findViewById(R.id.add_8_button);
        clearButton = (Button) findViewById(R.id.clear_button);
        playButton = (Button) findViewById(R.id.play_button);

        addButton.setOnClickListener(this);
        add1Button.setOnClickListener(this);
        add8Button.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
    }

    private void addActionItemToActionList(TabataAction actionItem) {
        actionList.add(actionItem);
        actionsListAdapter.notifyDataSetChanged();
        actionsListRecyclerView.smoothScrollToPosition(actionList.size());
        selectedListAdapter.notifyDataSetChanged();
        selectedListRecyclerView.smoothScrollToPosition(actionList.size());
    }

    private TabataAction getActionItemInRandom() {
        Random random = new Random();
        int index = random.nextInt(resourceList.size());
        while (resourceMap.get(resourceList.get(index).getName())) {
            index = random.nextInt(resourceList.size());
        }
        resourceMap.put(resourceList.get(index).getName(), true);
        return resourceList.get(index);
    }

    private void showDetailDialog(String actionName, List<String> pictures) {
        final Dialog detailDialog = new Dialog(this);
        detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        detailDialog.setCanceledOnTouchOutside(true);
        detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        detailDialog.setContentView(R.layout.popupdialog_action_detail);
        ((TextView) detailDialog.findViewById(R.id.action_name)).setText(actionName);
        ViewPager viewPager = (ViewPager) detailDialog.findViewById(R.id.view_pager);
//        viewPager.setAdapter(new PictureViewPagerAdapter(pictures));
        List<String> pictureList = new ArrayList<>();
        pictureList.add("dgjpeg.jpg");
        pictureList.add("dgjpeg (1).jpg");
        viewPager.setAdapter(new PictureViewPagerAdapter(pictureList));
        viewPager.addOnPageChangeListener(onPageChangeListener);
        indicator = (DotIndicator) detailDialog.findViewById(R.id.dot_indicator);
        indicator.setNumberOfItems(pictureList.size());
        detailDialog.show();

        detailDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailDialog.dismiss();
            }
        });
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            indicator.setSelectedItem(position, true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void showSaveListNameDialog() {
        final View saveListNameView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupdialog_save_list, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.save_list_name_dialog_title)
                .setView(saveListNameView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String listName = ((EditText) saveListNameView.findViewById(R.id.save_list_name_edit_text)).getText().toString();
                        saveCurrentListToSqlite(listName);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void showLoadListDialog() {
        final Dialog loadListDialog = new Dialog(this);
        loadListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadListDialog.setCanceledOnTouchOutside(true);
        loadListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadListDialog.setContentView(R.layout.popupdialog_load_list);
        RecyclerView listRecyclerView = (RecyclerView) loadListDialog.findViewById(R.id.saved_list);
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        final SavedActionListAdapter listAdapter = new SavedActionListAdapter(savedActionListDAO.getAll());
        listAdapter.setOnSavedListItemClickListener(new SavedActionListAdapter.OnSavedListItemClickListener() {
            @Override
            public void onItemLayoutClick(String listJsonString) {
                actionList.clear();
                List<TabataAction> loadList = new Gson().fromJson(listJsonString, new TypeToken<List<TabataAction>>() {
                }.getType());
                for (TabataAction tabataAction : loadList) {
                    addActionItemToActionList(tabataAction);
                }
                add1Button.setEnabled(actionList.size() < 8);
                add8Button.setEnabled(actionList.size() < 8);
                autoCompleteTextView.setEnabled(actionList.size() < 8);
                if (!clearButton.isEnabled()) {
                    clearButton.setEnabled(true);
                }
                if (!playButton.isEnabled() && canPlay) {
                    playButton.setEnabled(true);
                }
                if (!menuSaveItemStatus) {
                    menuSaveItemStatus = true;
                    invalidateOptionsMenu();
                }
                loadListDialog.dismiss();
            }

            @Override
            public void onItemDeleteClick(long id) {
                savedActionListDAO.delete(id);
                listAdapter.update(savedActionListDAO.getAll());
            }
        });
        listRecyclerView.setAdapter(listAdapter);

        loadListDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadListDialog.dismiss();
            }
        });

        loadListDialog.show();
    }

    private boolean checkInputInResourceList(String input) {
        for (TabataAction tabataAction : resourceList) {
            if (input.equals(tabataAction.getName()))
                return true;
        }
        return false;
    }

    private boolean checkInputInActionList(String input) {
        return resourceMap.get(input);
    }

    private void saveCurrentListToSqlite(String listName) {
        String jsonString = new Gson().toJson(actionList);

        SavedActionList.Builder savedActionListBuilder = new SavedActionList.Builder();
        SavedActionList savedActionList = savedActionListBuilder.setListName(listName)
                .setTimeStamp(System.currentTimeMillis())
                .setJsonString(jsonString)
                .create();
        savedActionListDAO.insert(savedActionList);
    }

    private void updateButtonStatus() {
        boolean add1BtnStatus = (actionList.size() < 8) && (!mp.isPlaying());
        boolean add8BtnStatus = (actionList.size() < 8) && (!mp.isPlaying());
        boolean clearBtnStatus = (actionList.size() > 0) && (!mp.isPlaying());
        boolean playBtnStatus = (actionList.size() > 0) && canPlay;
        boolean autoCompleteTextStatus = (actionList.size() < 8) && (!mp.isPlaying());
        menuSaveItemStatus = (actionList.size() > 0);
        menuLoadItemStatus = !mp.isPlaying();

        add1Button.setEnabled(add1BtnStatus);
        add8Button.setEnabled(add8BtnStatus);
        clearButton.setEnabled(clearBtnStatus);
        playButton.setEnabled(playBtnStatus);
        autoCompleteTextView.setEnabled(autoCompleteTextStatus);

        invalidateOptionsMenu();
    }

    private void showCalenderDialog() {
        dialogCaldroidFragment = new CalenderLogFragment();
        Map<String, Object> extraData = dialogCaldroidFragment.getExtraData();
        extraData.put("calenderLogMap", calenderLogDAO.getAll());
//        dialogCaldroidFragment.setCaldroidListener(listener);

        final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
//        Bundle bundle = new Bundle();
//        dialogCaldroidFragment.setArguments(bundle);

        dialogCaldroidFragment.show(getSupportFragmentManager(), dialogTag);
    }

    private Date getTodayDate() {
        Date todayDate = new Date(System.currentTimeMillis());
        SimpleDateFormat justDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String justDateOfTodayString = justDateFormat.format(todayDate);
        Date justDateOfTodayDate = new Date();
        try {
            justDateOfTodayDate = justDateFormat.parse(justDateOfTodayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return justDateOfTodayDate;
    }
}
