package lonestarapp.com.flicker.viewmodels;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.facebook.litho.EventHandler;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lonestarapp.com.flicker.data.model.Flicker.FlickerResponse;
import lonestarapp.com.flicker.data.model.Flicker.Photo;
import lonestarapp.com.flicker.data.model.Flicker.Photos;
import lonestarapp.com.flicker.data.remote.request.FlickerRetro;
import lonestarapp.com.flicker.repositories.PhotoRepository;
import lonestarapp.com.flicker.ui.litho.events.PhotoFeedModel;

public class MainActivityViewModel extends AndroidViewModel {

    private CompositeDisposable disposable;
    private MutableLiveData<List<Photo>> mPhotos = new MutableLiveData<>();
    private PhotoRepository mPhotoRepo;
    private Photos currentPhotoList;

    private EventHandler<PhotoFeedModel> mDataModelEventHandler;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        if(mPhotoRepo != null) {
            return;
        }

        mPhotoRepo = PhotoRepository.getInstance();
        disposable = new CompositeDisposable();
        //getDummyData();
        fetchPhotos();
    }

    public MutableLiveData<List<Photo>> getObservable() {
        if(mPhotos == null) {
            fetchPhotos();
        }
        return mPhotos;
    }

     public void searchWithText(final String text) {
        if(text == null || text.equals("")){
            return;
        }
        Log.d("RxJava", "About to search with given text " + text);
        disposable.add(mPhotoRepo.searchWithText(text)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<FlickerResponse>() {
                        @Override
                        public void onSuccess(FlickerResponse response) {
                            Log.d("RxJava", "Finished fetching searched photos");

                            if(response == null) {
                                Log.d("RxJava", "Photo list is empty");
                                return;
                            }

                            currentPhotoList = response.getPhotos();
                            currentPhotoList.setSearchedText(text);
                            List<Photo> list = addImageURL(response.getPhotos().getPhotoList());

                            if(list == null) {
                                return;
                            }

                            Log.d("RxJava", "Photo List text " + currentPhotoList.getSearchedText());

                            mPhotos.setValue(list);
                            dispatchEvent(new ArrayList<>(mPhotos.getValue()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Log.d("RxJava" ,"An error occurred while trying to search for photos " + e.getLocalizedMessage());
                            Toast.makeText(getApplication().getApplicationContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
        );
     }

    public void fetchPhotos() {
        Log.d("RxJava", "About to fetch photos");

        disposable.add(mPhotoRepo.getObservableData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<FlickerResponse>() {

                    @Override
                    public void onSuccess(FlickerResponse response) {
                        if(response == null) {
                            Log.d("RxJava", "Photo list is empty");
                            return;
                        }

                        currentPhotoList = response.getPhotos();
                        List<Photo> list = addImageURL(response.getPhotos().getPhotoList());
                        mPhotos.setValue(list);
                        currentPhotoList.setPhotoList(mPhotos.getValue());
                        dispatchEvent(mPhotos.getValue());
                        Log.d("RxJava", "Finished fetching photos");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("RxJava", "An error occurred");
                        Log.d("RxJava", "" + e.getLocalizedMessage());
                        Toast.makeText(getApplication().getApplicationContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    public void fetchNextPage() {
        if(currentPhotoList == null) {
            Log.d("RxJava", "current Photo list is empty");
            fetchPhotos();
        } else {
            nextPage();
        }
    }

    private void nextPage() {
        Log.d("RxJava", "About to fetch next page");
        if(currentPhotoList.getPage() >= currentPhotoList.getPages()) {
            return;
        }

        String text = currentPhotoList.getSearchedText();
        int nextPageNumber = currentPhotoList.getPage() + 1;

        disposable.add(mPhotoRepo.getNextObservablePage(nextPageNumber + "", text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<FlickerResponse>() {
                    @Override
                    public void onSuccess(FlickerResponse response) {
                        if(response == null) {
                            return;
                        }
                        String searchedText = null;
                        if(currentPhotoList != null) {
                            searchedText = currentPhotoList.getSearchedText();
                            Log.d("FeedText" , "searched text " + searchedText);
                        }
                        currentPhotoList = response.getPhotos();
                        List<Photo> list = response.getPhotos().getPhotoList();
                        if(list == null) {
                            Log.d("RxJava", "Photo list is empty");
                            return;
                        }
                        list = addImageURL(list);
                        currentPhotoList.setPhotoList(list);
                        currentPhotoList.setSearchedText(searchedText);
                        if(mPhotos.getValue() == null) {
                            mPhotos.setValue(list);
                        } else {
                            List<Photo> tempList = mPhotos.getValue();
                            tempList.addAll(list);
                            mPhotos.setValue(tempList);
                            Log.d("RxJava", "Appending new photos fetched size: " + mPhotos.getValue().size());
                        }

                        dispatchEvent(new ArrayList<>(mPhotos.getValue()));
                        Log.d("RxJava", "Finished fetching photos");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("RxJava", "Some error occurred");
                        Log.d("RxJava", "" + e.getLocalizedMessage());
                        Toast.makeText(getApplication().getApplicationContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private List<Photo> addImageURL(List<Photo> photos) {
        for(Photo photo: photos) {
            photo.setImgUrl(parseImageUrl(photo));
        }
        return photos;
    }

    // constructing image url
    private String parseImageUrl(Photo photo) {
        List<String> imgQuery = new ArrayList<>(3);

        imgQuery.add(Integer.toString(photo.getFarm()));
        imgQuery.add(photo.getServer());
        imgQuery.add(photo.getId());
        imgQuery.add(photo.getSecret());

        String link = FlickerRetro.BASE_IMG_URL;
        for (String param : imgQuery) {
            link = link.replaceFirst("\\{[a-zA-Z]+\\}", param);
        }
        Log.i("ImgLink", "" + link);
        return link;
    }

    // Register lithos handler so the new data is sent to litho to display.
    public void registerLoadingEvent(EventHandler<PhotoFeedModel> dataModelEventHandler) {
        this.mDataModelEventHandler = dataModelEventHandler;
    }

    public void unregisterLoadingEvent() {
        this.mDataModelEventHandler = null;
    }

    // Send the data to litho
    public void dispatchEvent(List<Photo> photos) {
        if(mDataModelEventHandler != null) {
            PhotoFeedModel photoFeedModel = new PhotoFeedModel();
            photoFeedModel.photos = photos;
            mDataModelEventHandler.dispatchEvent(photoFeedModel);
            Log.d("RxJava", "Dispatching event");
        }
    }

    public void getDummyData() {
        mPhotos.setValue(mPhotoRepo.getDummyData());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null) {
            disposable.clear();
            disposable = null;
        }
    }
}

