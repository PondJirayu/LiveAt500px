package jirayu.pond.liveat500px.manager.http;


import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by lp700 on 28/9/2559.
 */

public interface ApiService  {

    @POST("list")
    Call<Object> loadPhotoList();
}
