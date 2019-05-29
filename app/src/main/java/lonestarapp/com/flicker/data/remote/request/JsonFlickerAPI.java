package lonestarapp.com.flicker.data.remote.request;

import java.util.Map;

import io.reactivex.Single;
import lonestarapp.com.flicker.data.model.Flicker.FlickerResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface JsonFlickerAPI {

    String QUERY_METHOD = "method";
    String QUERY_API_KEY = "api_key";
    String QUERY_FORMAT = "format";
    String QUERY_NOJSONCALLBACK = "nojsoncallback";
    String QUERY_TEXT = "text";
    String QUERY_PAGE = "page";

    //TODO: Implementing rxjava
    @GET("/services/rest/")
    Single<FlickerResponse> getPosts(
            @Query(QUERY_METHOD) String method,
            @Query(QUERY_API_KEY) String api_key,
            @Query(QUERY_FORMAT) String format,
            @Query(QUERY_NOJSONCALLBACK) String nojsoncallback,
            @Query(QUERY_TEXT) String text
    );

//  Whenever you want to use a hashmap to pass more than one query
    @GET("/services/rest/")
    Single<FlickerResponse> getPosts(
            @QueryMap Map<String, String> options
    );

}
