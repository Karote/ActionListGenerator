package chuang.karote.actionslistgenerator.adapter;

import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

/**
 * Created by karot.chuang on 2017/1/5.
 */

public class PictureGirdViewAdapter extends BaseAdapter {
    private final static String APP_FOLDER_NAME = "ALG";
    private final static String rootPath =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separatorChar
                    + APP_FOLDER_NAME;

    private List<String> pictureList;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(parent.getContext());
        simpleDraweeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(parent.getResources());
        builder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        simpleDraweeView.setHierarchy(builder.build());
        simpleDraweeView.setImageURI(Uri.fromFile(new File(rootPath, pictureList.get(position))));
        parent.addView(simpleDraweeView);
        return simpleDraweeView;
    }
}
