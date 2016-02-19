package com.adupp.administrator.fawazmoviedb.api;

import com.adupp.administrator.fawazmoviedb.Review;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fawaz on 2/19/2016.
 */
public interface IApiReview {
    @GET("movie/{id}/reviews")
    Call<Review> getReview(@Path("id") int Id, @Query("api_key") String key);
}
