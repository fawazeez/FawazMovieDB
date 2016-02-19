package com.adupp.administrator.fawazmoviedb.api;
import com.adupp.administrator.fawazmoviedb.Trailer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fawaz on 2/19/2016.
 */
public interface IApiTrailer {
    @GET("movie/{id}/videos")
    Call<Trailer> getTrailer(@Path("id") int Id, @Query("api_key") String key);
}

