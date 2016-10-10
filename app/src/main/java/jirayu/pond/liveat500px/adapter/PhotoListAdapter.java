package jirayu.pond.liveat500px.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import jirayu.pond.liveat500px.dao.PhotoItemDao;
import jirayu.pond.liveat500px.manager.PhotoListManager;
import jirayu.pond.liveat500px.view.PhotoListItem;

/**
 * Created by lp700 on 19/9/2559.
 */
public class PhotoListAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        if (PhotoListManager.getInstance().getDao() == null) {
            return 0;
        }
        if (PhotoListManager.getInstance().getDao().getData() == null) {
            return 0;
        }
        return PhotoListManager.getInstance().getDao().getData().size();
    }

    @Override
    public Object getItem(int position) {
        return PhotoListManager.getInstance().getDao().getData().get(position);
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
        item.setNameText(dao.getCaption()); // set ค่าให้ view ใน customViewGroup
        item.setDescriptionText(dao.getUsername() + "\n" + dao.getCamera());
        item.setImageUrl(dao.getImageUrl());

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
