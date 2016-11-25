package chuang.karote.actionslistgenerator;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import chuang.karote.actionslistgenerator.adapter.ActionsListAdapter;
import chuang.karote.actionslistgenerator.model.TabataAction;
import chuang.karote.actionslistgenerator.model.TabataConfig;
import utility.Util;

/**
 * Created by karot.chuang on 2016/11/18.
 */

public class EditModeActivity extends AppCompatActivity {
    private final static String APP_FOLDER_NAME = "ALG";
    private final static String CONFIG_FILE_NAME = "config.json";

    private StringBuilder rootPath;

    private List<TabataAction> resourceList;
    private ArrayList<File> musicFiles;
    private String[] songList;
    private String selectMusic;
    private ActionsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.edit_mode_title);
        setSupportActionBar(toolbar);


        StringBuilder stringBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        rootPath = stringBuilder.append(File.separatorChar).append(APP_FOLDER_NAME);

        readSourceFile();

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_music);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(EditModeActivity.this, android.R.layout.simple_spinner_item, songList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        for (int i = 0; i < songList.length; i++) {
            if (selectMusic.equals(songList[i])) {
                spinner.setSelection(i);
                break;
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectMusic = "";
                } else {
                    selectMusic = songList[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (resourceList == null) {
            resourceList = new ArrayList<>();
        }

        RecyclerView sourceRecyclerView = (RecyclerView) findViewById(R.id.source_list);
        adapter = new ActionsListAdapter(resourceList);
        adapter.setOnItemClickListener(new ActionsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemDeleteClick(int position) {
                resourceList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        sourceRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        sourceRecyclerView.setAdapter(adapter);

        findViewById(R.id.add_new_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabataConfig config = new TabataConfig(selectMusic, resourceList);
                Gson gson = new Gson();
                String json = gson.toJson(config);
                Util.saveFile(rootPath.toString(), CONFIG_FILE_NAME, json);
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent editIntent = new Intent();
        editIntent.setClass(EditModeActivity.this, MainActivity.class);
        startActivity(editIntent);
        EditModeActivity.this.finish();
    }

    private void readSourceFile() {
        musicFiles = findSong(new File(rootPath.toString()));
        songList = new String[musicFiles.size() + 1];
        songList[0] = "<無>";
        for (int i = 0; i < musicFiles.size(); i++) {
            songList[1 + i] = musicFiles.get(i).getName();
        }

        String json = getJson(rootPath.toString(), CONFIG_FILE_NAME);
        try {
            if (json != null) {
                Gson gson = new Gson();
                TabataConfig config = gson.fromJson(json, TabataConfig.class);
                selectMusic = config.getMusic();
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

    private void showAddDialog() {
        final Dialog addDialog = new Dialog(this);
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addDialog.setCanceledOnTouchOutside(true);
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addDialog.setContentView(R.layout.popupdialog_add_action_item);
        addDialog.show();

        final EditText actionName = (EditText) addDialog.findViewById(R.id.edit_text_action_name);
        final EditText actionDescription = (EditText) addDialog.findViewById(R.id.edit_text_action_description);
        addDialog.findViewById(R.id.button_add_action_item_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String actionNameString = actionName.getText().toString();
                if (actionNameString.length() != 0) {
                    if (!checkInputActionName(actionNameString)) {
                        Util.showShortToast(EditModeActivity.this, "動作名稱重覆");
                    } else {
                        TabataAction newAction = new TabataAction(actionNameString, actionDescription.getText().toString());
                        resourceList.add(newAction);
                        adapter.notifyDataSetChanged();
                        addDialog.dismiss();
                    }
                } else {
                    Util.showShortToast(EditModeActivity.this, "名稱不得為空");
                }
            }
        });
    }

    private ArrayList<File> findSong(File root) {

        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                al.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".m4a")) {
                    al.add(singleFile);
                }
            }
        }
        return al;
    }

    private boolean checkInputActionName(String actionName) {
        for (TabataAction tabataAction : resourceList) {
            if (tabataAction.getName().equals(actionName)) {
                return false;
            }
        }
        return true;
    }
}
