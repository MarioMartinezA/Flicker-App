package lonestarapp.com.flicker.data.model.Flicker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlickerResponse {

    @SerializedName("photos")
    @Expose
    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }
}
