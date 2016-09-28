package jirayu.pond.liveat500px.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import jirayu.pond.liveat500px.R;
import jirayu.pond.liveat500px.adapter.PhotoListAdapter;
import jirayu.pond.liveat500px.dao.PhotoItemCollectionDao;
import jirayu.pond.liveat500px.manager.HttpManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    ListView listView;
    PhotoListAdapter listAdapter;

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
        // Init 'View' instance(s) with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new PhotoListAdapter(); // สร้าง Adapter
        listView.setAdapter(listAdapter); // เอา ListView มาผูกกับ Adapter (สั่งให้ทำงานคู่กัน)

        // เชื่อมต่อกับ Server
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getService().loadPhotoList();
        call.enqueue(new Callback<PhotoItemCollectionDao>() {
            @Override // ถูกเรียกเมือมีการติดต่อกับ server สำเร็จ
            public void onResponse(Call<PhotoItemCollectionDao> call,
                                   Response<PhotoItemCollectionDao> response) {
                if (response.isSuccessful()){
                    PhotoItemCollectionDao dao = response.body();
                    Toast.makeText(getActivity(),
                            dao.getData().get(0).getCaption(),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Handle
                    try {
                        Toast.makeText(getActivity(),
                                response.errorBody().string(),
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override // ติดต่อ server ไม่ได้
            public void onFailure(Call<PhotoItemCollectionDao> call, Throwable t) {
                // Handle
                Toast.makeText(getActivity(),
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
