package jirayu.pond.liveat500px.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

    ListView listView;
    PhotoListAdapter listAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;

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
        // Init 'View' instance(s) with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new PhotoListAdapter(); // สร้าง Adapter
        listView.setAdapter(listAdapter); // เอา ListView มาผูกกับ Adapter (สั่งให้ทำงานคู่กัน)

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // เชื่อมต่อกับ Server
                refreshData();
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });

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
        call.enqueue(new Callback<PhotoItemCollectionDao>() {
            @Override
            public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
                swipeRefreshLayout.setRefreshing(false); // สั่งให้ Pull to Refresh หยุดหมุน
                if (response.isSuccessful()) { // response.isSuccessful คือ ได้ข้อมูลกลับมาสมบูรณ์
                    PhotoItemCollectionDao dao = response.body(); // แงะข้อมูลจาก response.body เก็บไว้ที่ dao
                    photoListManager.setDao(dao);
                    listAdapter.setDao(dao); // โยน dao ให้ Adapter
//                    PhotoListManager.getInstance().setDao(dao); // เอา dao ไปฝากไว้ที่ global variable(PhotoListManager) เพื่อแชร์ให้ระบบอื่นๆใช้งานข้อมูลได้
                    listAdapter.notifyDataSetChanged(); // adapter สั่งให้ listView refresh ตัวเอง

                    Toast.makeText(Contextor.getInstance().getContext(), // Use Application Context
                            "Load Completed",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Handle
                    try {
                        Toast.makeText(Contextor.getInstance().getContext(),
                                response.errorBody().string(),
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
                // Handle
                swipeRefreshLayout.setRefreshing(false); // สั่งให้ Pull to Refresh หยุดหมุน
                Toast.makeText(Contextor.getInstance().getContext(),
                        t.toString(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void reloadData() {
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new Callback<PhotoItemCollectionDao>() {
            @Override // onResponse ถูกเรียกเมือมีการติดต่อกับ server สำเร็จ
            public void onResponse(Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
                swipeRefreshLayout.setRefreshing(false); // สั่งให้ Pull to Refresh หยุดหมุน
                if (response.isSuccessful()) { // response.isSuccessful คือ ได้ข้อมูลกลับมาสมบูรณ์
                    PhotoItemCollectionDao dao = response.body(); // แงะข้อมูลจาก response.body เก็บไว้ที่ dao
                    photoListManager.setDao(dao);
                    listAdapter.setDao(dao); // โยน dao ให้ Adapter
//                    PhotoListManager.getInstance().setDao(dao); // เอา dao ไปฝากไว้ที่ global variable(PhotoListManager) เพื่อแชร์ให้ระบบอื่นๆใช้งานข้อมูลได้
                    listAdapter.notifyDataSetChanged(); // adapter สั่งให้ listView refresh ตัวเอง

                    Toast.makeText(Contextor.getInstance().getContext(), // Use Application Context
                            dao.getData().get(0).getCaption(),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Handle
                    try {
                        Toast.makeText(Contextor.getInstance().getContext(),
                                response.errorBody().string(),
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override // ติดต่อ server ไม่ได้ [ แงะ Error Message จาก Throwable t ได้ ]
            public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
                // Handle
                swipeRefreshLayout.setRefreshing(false); // สั่งให้ Pull to Refresh หยุดหมุน
                Toast.makeText(Contextor.getInstance().getContext(),
                        t.toString(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
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
}
