package jirayu.pond.liveat500px.manager;

import android.content.Context;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import jirayu.pond.liveat500px.dao.PhotoItemCollectionDao;

/**
 * Created by nuuneoi on 11/16/2014.
 * PhotoListManager ก็คือ Singleton Model ที่มีหน้าที่ถือครองตัวแปร dao ให้ตัวอื่นๆ เรียกใช้งานผ่าน getter
 */
public class PhotoListManager {

    private Context mContext;
    private PhotoItemCollectionDao dao;

    public PhotoListManager() {
        mContext = Contextor.getInstance().getContext();
    }

    public PhotoItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    public int getMaximumId() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        if (dao.getData().size() == 0) {
            return 0;
        }

        int maxId = dao.getData().get(0).getId();
        for (int i = 1 ; i < dao.getData().size() ; i++) {
            maxId = Math.max(maxId, dao.getData().get(i).getId());
        }
        return maxId;
    }

    public int getCount() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        return dao.getData().size();
    }
}
