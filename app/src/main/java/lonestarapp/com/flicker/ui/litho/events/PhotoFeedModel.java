package lonestarapp.com.flicker.ui.litho.events;

import com.facebook.litho.annotations.Event;

import java.util.List;

import lonestarapp.com.flicker.data.model.Flicker.Photo;

@Event
public class PhotoFeedModel {
    public List<Photo> photos;
}
