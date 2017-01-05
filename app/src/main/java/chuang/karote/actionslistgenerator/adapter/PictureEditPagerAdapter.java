package chuang.karote.actionslistgenerator.adapter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

import chuang.karote.actionslistgenerator.R;

/**
 * Created by karot.chuang on 2017/1/3.
 */

public class PictureEditPagerAdapter extends PagerAdapter {
    private final static String APP_FOLDER_NAME = "ALG";
    private final static String rootPath =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separatorChar
                    + APP_FOLDER_NAME;

    private List<String> pictureList;

    public PictureEditPagerAdapter(List<String> pictureList) {
        this.pictureList = pictureList;
    }

    public interface OnPictureClickListener {
        void onAddPictureClicked();

        void onDeletePictureClicked();
    }

    private OnPictureClickListener onPictureClickListener;

    public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(container.getContext());
        simpleDraweeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        GenericDraweeHierarchyBuilder builder = GenericDraweeHierarchyBuilder.newInstance(container.getResources());
        builder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        if (position == getCount() - 1) {
            builder.setPlaceholderImage(R.drawable.ic_add_picture);
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPictureClickListener.onAddPictureClicked();
                }
            });
        } else {
            simpleDraweeView.setImageURI(Uri.fromFile(new File(rootPath, pictureList.get(position))));
            Drawable deleteDrawable = ContextCompat.getDrawable(container.getContext(), R.drawable.ic_delete_picture);
            builder.setOverlay(deleteDrawable);
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPictureClickListener.onDeletePictureClicked();
                }
            });
        }
        simpleDraweeView.setHierarchy(builder.build());
        container.addView(simpleDraweeView);
        return simpleDraweeView;
    }

    @Override
    public int getCount() {
        return pictureList.size() + 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void addView() {

    }

    private void removeView() {

    }
}
