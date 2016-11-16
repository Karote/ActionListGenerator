package chuang.karote.actionslistgenerator;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import chuang.karote.actionslistgenerator.adapter.ActionsListAdapter;
import chuang.karote.actionslistgenerator.model.TabataAction;
import chuang.karote.actionslistgenerator.model.TabataConfig;
import utility.Util;

public class MainActivity extends AppCompatActivity {
    private final static String APP_FOLDER_NAME = "ALG";
    private final static String CONFIG_FILE_NAME = "config.json";

    private List<TabataAction> resourceList;
    private HashMap<String, Boolean> resourceMap;

    private String musicPath;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = new MediaPlayer();

        readConfig();

        final RecyclerView actionsListRecyclerView = (RecyclerView) findViewById(R.id.action_list);
        final Button addButton = (Button) findViewById(R.id.add_button);
        final Button add8Button = (Button) findViewById(R.id.add8_button);
        final Button clearButton = (Button) findViewById(R.id.clear_button);
        final Button playButton = (Button) findViewById(R.id.play_button);

        resourceMap = new HashMap<>();
        for (TabataAction listItem : resourceList) {
            resourceMap.put(listItem.getName(), false);
        }
        final List<TabataAction> actionList = new ArrayList<>();
        final ActionsListAdapter adapter = new ActionsListAdapter(actionList);
        adapter.setOnItemClickListener(new ActionsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showDetailDialog(actionList.get(position).getName(), actionList.get(position).getDescription());
            }

            @Override
            public void onItemDeleteClick(int position) {
                if (mp.isPlaying()) {
                    return;
                }
                resourceMap.put(actionList.get(position).getName(), false);
                actionList.remove(position);
                adapter.notifyDataSetChanged();
                if (actionList.size() < 8) {
                    addButton.setEnabled(true);
                    add8Button.setEnabled(true);
                }
                if (actionList.size() == 0) {
                    mp.seekTo(0);
                    clearButton.setEnabled(false);
                    playButton.setEnabled(false);
                }
            }
        });
        actionsListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        actionsListRecyclerView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionList.add(getActionItem());
                adapter.notifyDataSetChanged();
                actionsListRecyclerView.smoothScrollToPosition(actionList.size());
                if (actionList.size() > 7) {
                    addButton.setEnabled(false);
                    add8Button.setEnabled(false);
                }
                if (!clearButton.isEnabled()) {
                    clearButton.setEnabled(true);
                }
                if (!playButton.isEnabled()) {
                    playButton.setEnabled(true);
                }
            }
        });

        add8Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while (actionList.size() < 8) {
                    actionList.add(getActionItem());
                }
                adapter.notifyDataSetChanged();
                addButton.setEnabled(false);
                add8Button.setEnabled(false);
                if (!clearButton.isEnabled()) {
                    clearButton.setEnabled(true);
                }
                if (!playButton.isEnabled()) {
                    playButton.setEnabled(true);
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.seekTo(0);
                mp.pause();
                for (TabataAction listItem : resourceList) {
                    resourceMap.put(listItem.getName(), false);
                }
                actionList.clear();
                adapter.notifyDataSetChanged();
                clearButton.setEnabled(false);
                playButton.setEnabled(false);
                if (!addButton.isEnabled()) {
                    addButton.setEnabled(true);
                }
                if (!add8Button.isEnabled()) {
                    add8Button.setEnabled(true);
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mp.isPlaying()) {
                    addButton.setEnabled(false);
                    add8Button.setEnabled(false);
                    clearButton.setEnabled(false);
                    playButton.setText("Pause");
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    mp.start();
                } else {
                    if (actionList.size() < 8) {
                        addButton.setEnabled(true);
                        add8Button.setEnabled(true);
                    }
                    clearButton.setEnabled(true);
                    playButton.setText("Play");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    mp.pause();
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (actionList.size() < 8) {
                    addButton.setEnabled(true);
                    add8Button.setEnabled(true);
                }
                clearButton.setEnabled(true);
                playButton.setText("Play");
            }
        });

    }

    @Override
    protected void onDestroy() {
        if (mp != null) {
            mp.release();
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    private void readConfig() {
        StringBuilder stringBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());
        StringBuilder rootPath = stringBuilder.append(File.separatorChar).append(APP_FOLDER_NAME);
        String json = getJson(rootPath.toString(), CONFIG_FILE_NAME);
        try {
            if (json != null) {
                Gson gson = new Gson();
                TabataConfig config = gson.fromJson(json, TabataConfig.class);
                musicPath = rootPath.append(File.separator).append(config.getMusic()).toString();
                try {
                    mp.setDataSource(musicPath);
                    mp.setLooping(false);
                    mp.prepare();
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

    private TabataAction getActionItem() {
        Random random = new Random();
        int index = random.nextInt(resourceList.size());
        while (resourceMap.get(resourceList.get(index).getName())) {
            index = random.nextInt(resourceList.size());
        }
        resourceMap.put(resourceList.get(index).getName(), true);
        return resourceList.get(index);
    }

    private void showDetailDialog(String actionName, String description) {
        Dialog mDetailDialog = new Dialog(this);
        mDetailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDetailDialog.setCanceledOnTouchOutside(true);
        mDetailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDetailDialog.setContentView(R.layout.popupdialog_action_detail);
        ((TextView) mDetailDialog.findViewById(R.id.action_name)).setText(actionName);
        ((TextView) mDetailDialog.findViewById(R.id.description_text)).setText(description);
        mDetailDialog.show();
    }
}
