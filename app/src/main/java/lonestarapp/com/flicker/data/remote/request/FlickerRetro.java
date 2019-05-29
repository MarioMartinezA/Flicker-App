package lonestarapp.com.flicker.data.remote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

import lonestarapp.com.flicker.data.model.Flicker.FlickerResponse;
import lonestarapp.com.flicker.data.model.Flicker.Photo;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickerRetro {

    private static Retrofit instance;

    public static final String BASE_IMG_URL = "https://farm{farm}.staticflickr.com/{server}/{id}_{secret}.jpg";
    private static final String BASE_URL = "https://api.flickr.com/";
    private final String METHOD_SEARCH = "flickr.photos.search";
    private final String METHOD_RECENT = "flickr.photos.getRecent";
    private final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";
    private final String JSON_FORMAT = "json";
    private final String NOJSONCALLBACK_VAL = "1";

    private Map<String, String> optionsMethodRecent;
    private Map<String, String> optionsMethodSearch;

    private static synchronized Retrofit getRetroInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    public static JsonFlickerAPI getAPIService() {
        return getRetroInstance().create(JsonFlickerAPI.class);
    }

    public FlickerRetro() {
        optionsMethodRecent = new HashMap<>();
        optionsMethodRecent.put(JsonFlickerAPI.QUERY_METHOD, METHOD_RECENT);
        optionsMethodRecent.put(JsonFlickerAPI.QUERY_API_KEY, API_KEY);
        optionsMethodRecent.put(JsonFlickerAPI.QUERY_FORMAT, JSON_FORMAT);
        optionsMethodRecent.put(JsonFlickerAPI.QUERY_NOJSONCALLBACK, NOJSONCALLBACK_VAL);
    }

    public Single<FlickerResponse> searchWithText(String text) {
        return getAPIService()
                .getPosts(METHOD_SEARCH, API_KEY, JSON_FORMAT, NOJSONCALLBACK_VAL, text);
    }

    public Single<FlickerResponse> getPhotosObservable() {
        Map<String, String> options = new HashMap<>();
        options.put(JsonFlickerAPI.QUERY_METHOD, METHOD_RECENT);
        options.put(JsonFlickerAPI.QUERY_API_KEY, API_KEY);
        options.put(JsonFlickerAPI.QUERY_FORMAT, JSON_FORMAT);
        options.put(JsonFlickerAPI.QUERY_NOJSONCALLBACK, NOJSONCALLBACK_VAL);
        return getAPIService()
                .getPosts(options);
    }

    public Single<FlickerResponse> getPhotosNextPage(String pageNumber) {
        Map<String, String> options = new HashMap<>();
        options.put(JsonFlickerAPI.QUERY_PAGE, pageNumber);
        options.put(JsonFlickerAPI.QUERY_METHOD, METHOD_RECENT);
        options.put(JsonFlickerAPI.QUERY_API_KEY, API_KEY);
        options.put(JsonFlickerAPI.QUERY_FORMAT, JSON_FORMAT);
        options.put(JsonFlickerAPI.QUERY_NOJSONCALLBACK, NOJSONCALLBACK_VAL);

        return getAPIService()
                .getPosts(options);
    }

    public Single<FlickerResponse> getPhotosNextPage(String pageNumber, String text) {
        if(text == null) {
            return getPhotosNextPage(pageNumber);
        }
        Map<String, String> options = new HashMap<>();
        options.put(JsonFlickerAPI.QUERY_TEXT, text);
        options.put(JsonFlickerAPI.QUERY_PAGE, pageNumber);
        options.put(JsonFlickerAPI.QUERY_METHOD, METHOD_SEARCH);
        options.put(JsonFlickerAPI.QUERY_API_KEY, API_KEY);
        options.put(JsonFlickerAPI.QUERY_FORMAT, JSON_FORMAT);
        options.put(JsonFlickerAPI.QUERY_NOJSONCALLBACK, NOJSONCALLBACK_VAL);

        return getAPIService()
                .getPosts(options);
    }
}
