package jirayu.pond.liveat500px.manager.http;

import jirayu.pond.liveat500px.dao.PhotoItemCollectionDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by lp700 on 28/9/2559.
 */

public interface ApiService  {

    @POST("list")
    Call<PhotoItemCollectionDao> loadPhotoList();

    @POST("list/after/{id}")
    Call<PhotoItemCollectionDao> loadPhotoListAfterId(@Path("id") int id);

    @POST("list/before/{id}")
    Call<PhotoItemCollectionDao> loadPhotoListBeforeId(@Path("id") int id);

}
