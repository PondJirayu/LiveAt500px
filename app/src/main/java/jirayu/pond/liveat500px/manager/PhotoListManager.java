package jirayu.pond.liveat500px.manager;

import android.content.Context;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import jirayu.pond.liveat500px.dao.PhotoItemCollectionDao;

/**
 * Created by nuuneoi on 11/16/2014.
 * PhotoListManager ก็คือ Singleton Model ที่มีหน้าที่ถือครองตัวแปร dao ให้ตัวอื่นๆ เรียกใช้งานผ่าน getter
 */
public class PhotoListManager {

    private static PhotoListManager instance;

    public static PhotoListManager getInstance() {
        if (instance == null)
            instance = new PhotoListManager();
        return instance;
    }

    private Context mContext;
    private PhotoItemCollectionDao dao;

    private PhotoListManager() {
        mContext = Contextor.getInstance().getContext();
    }

    public PhotoItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }
}
