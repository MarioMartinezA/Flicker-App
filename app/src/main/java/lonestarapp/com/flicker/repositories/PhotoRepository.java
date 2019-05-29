package lonestarapp.com.flicker.repositories;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import lonestarapp.com.flicker.data.model.Flicker.FlickerResponse;
import lonestarapp.com.flicker.data.model.Flicker.Photo;
import lonestarapp.com.flicker.data.remote.request.FlickerRetro;

public class PhotoRepository {

    private static PhotoRepository instance;
    private static FlickerRetro flickerAPI;

    private MutableLiveData<List<Photo>> liveData;
    private Observable<List<Photo>> observableData;

    public static PhotoRepository getInstance() {
        if(instance == null) {
            instance = new PhotoRepository();
            flickerAPI = new FlickerRetro();
        }
        return instance;
    }

//    public MutableLiveData<List<Photo>> getLiveData() {
//        if (liveData == null) {
//            liveData = flickerAPI.getPhotosLiveData();
//        }
//        return liveData;
//    }

    public Single<FlickerResponse> getObservableData() {
        return flickerAPI.getPhotosObservable();
    }

    public Single<FlickerResponse> searchWithText(String text) {
        return flickerAPI.searchWithText(text);
    }

    public Single<FlickerResponse> getNextObservablePage(String pageNumber, String text) {
        return flickerAPI.getPhotosNextPage(pageNumber, text);
    }

    public List<Photo> getDummyData() {
        List<Photo> list = new ArrayList<>();
        Photo tempPhoto;
        for (int i = 0; i < 30; i++) {
            tempPhoto = new Photo();
            tempPhoto.setImgUrl("https://farm1.staticflickr.com/578/23451156376_8983a8ebc7.jpg");
            list.add(tempPhoto);
        }
        return list;
    }
}
