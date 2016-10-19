package jirayu.pond.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import jirayu.pond.liveat500px.R;
import jirayu.pond.liveat500px.dao.PhotoItemCollectionDao;
import jirayu.pond.liveat500px.dao.PhotoItemDao;
import jirayu.pond.liveat500px.manager.PhotoListManager;
import jirayu.pond.liveat500px.view.PhotoListItem;

/**
 * Created by lp700 on 19/9/2559.
 */
public class PhotoListAdapter extends BaseAdapter {

    PhotoItemCollectionDao dao;
    int lastPosition = -1;

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        return dao.getData().size();
    }

    @Override
    public Object getItem(int position) {
        return dao.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
//
//    @Override
//    public int getViewTypeCount() { // จำนวนประเภทของ View ใน ListView
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) { // ถ้า ListView ส่ง Position เป็นเลขคู่ให้ return 0(PhotoListItem) ถ้าไม่ใช่ให้ return 1(TextView)
//        return position % 2 == 0 ? 0 : 1;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (getItemViewType(position) == 0) {
        PhotoListItem item;
        if (convertView != null) {
            // Reuse
            item = (PhotoListItem) convertView;
        } else {
            // Create
            item = new PhotoListItem(parent.getContext());
        }
        // set value
        PhotoItemDao dao = (PhotoItemDao) getItem(position);
        // set ค่าให้ view ใน customViewGroup
        item.setNameText(dao.getCaption());
        item.setDescriptionText(dao.getUsername() + "\n" + dao.getCamera());
        item.setImageUrl(dao.getImageUrl());

        if (position > lastPosition){
            Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_bottom);
            item.startAnimation(anim);
            lastPosition = position;
        }

        return item;
//        } else {
//            TextView item;
//            if (convertView != null) {
//                item = (TextView) convertView;
//            } else {
//                item = new TextView(parent.getContext());
//            }
//            item.setText("Position : " + position);
//            return item;
//        }
    }
}
