package ru.ruslan.showgifs;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuestApi {

    @GET("/{category}/{page_num}?json=true")
    Call<List<PostModel>> getData(@Path("category") String resourceName, @Path("page_num") int count);

    @GET("/{category}?json=true")
    Single<PostModel> getData(@Path("category") String resourceName);
}
