package jirayu.pond.liveat500px.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import java.io.IOException;

import jirayu.pond.liveat500px.R;
import jirayu.pond.liveat500px.adapter.PhotoListAdapter;
import jirayu.pond.liveat500px.dao.PhotoItemCollectionDao;
import jirayu.pond.liveat500px.manager.HttpManager;
import jirayu.pond.liveat500px.manager.PhotoListManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    /***********
     * Variables
     ************/
    ListView listView;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    Button btnNewPhotos;
    Animation anim;

    /************
     * Functions
     *************/

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView);
        return rootView;
    }

    private void initInstances(View rootView) {
        photoListManager = new PhotoListManager();

        btnNewPhotos = (Button) rootView.findViewById(R.id.btnNewPhotos);
        btnNewPhotos.setOnClickListener(buttonClickListener);

        // Init 'View' instance(s) with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new PhotoListAdapter(); // สร้าง Adapter
        listView.setAdapter(listAdapter); // เอา ListView มาผูกกับ Adapter (สั่งให้ทำงานคู่กัน)

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        // Handle Pull to Refresh
        swipeRefreshLayout.setOnRefreshListener(pullToRefreshListener);

        listView.setOnScrollListener(listViewScrollListener);

        // เชื่อมต่อกับ Server
        refreshData();
    }

    private void refreshData() {
        if (photoListManager.getCount() == 0) {
            reloadData();
        } else {
            reloadDataNewer();
        }
    }


    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximumId();

        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoListAfterId(maxId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD_NEWER));
    }

    boolean isLoadingMore = false;

    private void loadMoreData() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        int minId = photoListManager.getMinimumId();

        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoListBeforeId(minId);
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_LOAD_MORE));
    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new PhotoListLoadCallback(PhotoListLoadCallback.MODE_RELOAD));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore Instance State here
        }
    }

    private void showButtonNewPhotos() {
        anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_fade_in);
        btnNewPhotos.startAnimation(anim);
        btnNewPhotos.setVisibility(Button.VISIBLE);
    }

    private void hideButtonNewPhotos() {
        anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_fade_out);
        btnNewPhotos.startAnimation(anim);
        btnNewPhotos.setVisibility(Button.GONE);
    }

    private void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();

    }

    /***************
    * Listener Zone
     ***************/

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnNewPhotos){
                listView.smoothScrollToPosition(0);
                hideButtonNewPhotos();
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener pullToRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() { // ถ้ามีการ pull to refresh คำสั่งนี้จะถูกเรียก
            // เชื่อมต่อกับ Server
            refreshData();
        }
    };

    AbsListView.OnScrollListener listViewScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override // ถ้ามีการ scroll ใน list view คำสั่ง onScroll จะถูกเรียก
        public void onScroll(AbsListView view,
                             int firstVisibleItem,  // ตำแหน่งแรกที่ถูกแสดงผล
                             int visibleItemCount,  // จำนวนไอเทมที่มองเห็นบนหน้าจอ
                             int totalItemCount) {  // จำนวนไอเทมทั้งหมด
            if (view == listView){
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0); // ให้ pull to refresh ทำงานเมื่อ scroll ที่ไอเทมตำแหน่งแรก
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (photoListManager.getCount() > 0) {
                        // Load More
                        loadMoreData();
                    }
                }
            }
        }
    };

    /**************
     * Inner Class
     **************/

    class PhotoListLoadCallback implements Callback<PhotoItemCollectionDao> {

        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWER = 2;
        public static final int MODE_LOAD_MORE = 3;

        int mode;

        public PhotoListLoadCallback(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {

            swipeRefreshLayout.setRefreshing(false); // สั่งให้ Pull to Refresh หยุดหมุน

            if (response.isSuccessful()) {  // response.isSuccessful คือ ได้ข้อมูลกลับมาสมบูรณ์
                PhotoItemCollectionDao dao = response.body();   // แงะข้อมูลจาก response.body เก็บไว้ที่ dao

                int firstVisiblePosition = listView.getFirstVisiblePosition(); // get ตำแหน่งแรกสุดที่ถูกแสดงผลบน list view
                View c = listView.getChildAt(0); // หาค่าตำแหน่งแรกว่า scroll พ้นขอบจอกี่ px
                int top = (c == null) ? 0 : c.getTop();

                if (mode == MODE_RELOAD_NEWER) {
                    photoListManager.insertDaoAtPosition(dao);   // ส่งข้อมูลไปวิเคราะห์ใน model
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appendDaoAtButtonPosition(dao);
                } else {
                    photoListManager.setDao(dao);
                }
                clearLoadingMoreFlagIfCapable(mode);
                listAdapter.setDao(photoListManager.getDao());    // โยน dao ให้ Adapter
//                    PhotoListManager.getInstance().setDao(dao); // เอา dao ไปฝากไว้ที่ global variable(PhotoListManager) เพื่อแชร์ให้ระบบอื่นๆใช้งานข้อมูลได้
                listAdapter.notifyDataSetChanged(); // adapter สั่งให้ listView refresh ตัวเอง

                if (mode == MODE_RELOAD_NEWER) {
                    // Maintain Scroll Position การ scroll ไปยังตำแหน่งที่ต้องการ
                    int additionalSize = (dao != null && dao.getData() != null) ? dao.getData().size() : 0;
                    listAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize,
                            top);

                    // Show Button NewPhotos
                    if (additionalSize > 0)
                        showButtonNewPhotos();
                } else {

                }

                showToast("Load Completed");

            } else {
                // Handle
                clearLoadingMoreFlagIfCapable(mode);
                try {
                    showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
            // Handle
            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false); // สั่งให้ Pull to Refresh หยุดหมุน
            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE) {
                isLoadingMore = false;
            }
        }
    }

}
