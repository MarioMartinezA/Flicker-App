package lonestarapp.com.flicker.ui.litho;

import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.facebook.litho.Component;

import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.sections.Children;
import com.facebook.litho.sections.LoadingEvent;
import com.facebook.litho.sections.SectionContext;
import com.facebook.litho.sections.SectionLifecycle;
import com.facebook.litho.sections.annotations.GroupSectionSpec;
import com.facebook.litho.sections.annotations.OnBindService;
import com.facebook.litho.sections.annotations.OnCreateChildren;
import com.facebook.litho.sections.annotations.OnCreateService;
import com.facebook.litho.sections.annotations.OnUnbindService;
import com.facebook.litho.sections.annotations.OnViewportChanged;
import com.facebook.litho.sections.common.DataDiffSection;
import com.facebook.litho.sections.common.RenderEvent;
import com.facebook.litho.sections.common.SingleComponentSection;
import com.facebook.litho.widget.ComponentRenderInfo;
import com.facebook.litho.widget.EditText;
import com.facebook.litho.widget.RenderInfo;

import java.util.ArrayList;
import java.util.List;

import lonestarapp.com.flicker.ui.litho.components.ProgressLayout;
import lonestarapp.com.flicker.ui.litho.events.PhotoFeedModel;
import lonestarapp.com.flicker.ui.litho.components.ListItem;
import lonestarapp.com.flicker.data.model.Flicker.Photo;
import lonestarapp.com.flicker.viewmodels.MainActivityViewModel;

@GroupSectionSpec
public class ListSectionSpec {

    private final static String HINT = "Search Photos";

    @OnCreateInitialState
    static void createInitialState(
            final SectionContext c,
            StateValue<List<Photo>> photoFeed,
            StateValue<Boolean> isFetching,
            @Prop List<Photo> dataModel

    ) {
        Log.d("Feed", "Initializing feed with: " + dataModel.size() + " Photos");
        photoFeed.set(new ArrayList<>(dataModel));
        isFetching.set(false);
    }

    @OnCreateChildren
    static Children onCreateChildren(
            SectionContext c,
            @Prop MainActivityViewModel viewModel,
            @Prop List<Photo> dataModel,
            @State List<Photo> photoFeed) {

        return Children.create()
                .child(
                        SingleComponentSection.create(c)
                                .key("enterText")
                            .component(
                                    getEditTextComp(c, HINT, viewModel)
                            )
                        .isFullSpan(true)
                        .sticky(true)
                        .build()
                )
                .child(DataDiffSection.<Photo>create(c)
                        .data(photoFeed)
                        .renderEventHandler(ListSection.onRender(c))
                        .build()
                )
                .child(
                        SingleComponentSection.create(c).key("loadingBar")
                                .component(
                                        ProgressLayout.create(c)
                                )
                        .isFullSpan(true)
                        .build()
                )
                .build();
    }

    private static Component getEditTextComp(final SectionContext c, String hint, @Prop final MainActivityViewModel viewModel) {
        return EditText.create(c)
                .textSizeDip(16)
                .hint(hint)
                .isSingleLine(true)
                .backgroundColor(Color.WHITE)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .imeOptions(EditorInfo.IME_ACTION_SEARCH)
                .editorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                            Log.d("onEditorAction", "Value: " + textView.getText());
                            ListSection.setFetching(c, true);
                            viewModel.searchWithText(textView.getText().toString());
                            return false;
                        }
                        return false;
                    }
                })
                .build();
    }

    @OnEvent(RenderEvent.class)
    static RenderInfo onRender(final SectionContext c, @FromEvent Photo model) {
        return ComponentRenderInfo.create()
                .component(
                        ListItem.create(c)
                                .color(Color.WHITE)
                                .photo(model)
                                .build()
                )
                .build();
    }

    /**
     * photoFeed needs to be included as a parameter when creating the service that will fetch
     * more data. It's needed so that any changes made to photoFeed will reflect on the screen.
     */
    @OnCreateService
    static MainActivityViewModel onCreateService(
            final SectionContext c,
            @Prop MainActivityViewModel viewModel,
            @State List<Photo> photoFeed
    ) {
        Log.d("Feed", "Creating service is view model null? " + (viewModel == null));
        return viewModel;
    }

    @OnBindService
    static void onBindService(final SectionContext c, final MainActivityViewModel service) {
        service.registerLoadingEvent(ListSection.onDataLoaded(c));
        Log.d("Bind", "Binding service");
    }

    @OnUnbindService
    static void onUnbindService(final SectionContext c, final MainActivityViewModel service) {
        service.unregisterLoadingEvent();
        Log.d("Bind", "Unbinding service");
    }

    @OnEvent(PhotoFeedModel.class)
    static void onDataLoaded(final SectionContext c, @FromEvent List<Photo> photos) {
        ListSection.updateData(c, photos);
        ListSection.setFetching(c, false);
        SectionLifecycle.dispatchLoadingEvent(c, false, LoadingEvent.LoadingState.SUCCEEDED, null);
        Log.d("Feed", "Data has been loaded, current size: " + photos.size());

    }

    @OnUpdateState
    static void updateData(
            final StateValue<List<Photo>> photoFeed,
            @Param List<Photo> photos
    ) {
        photoFeed.set(photos);
        Log.d("Feed", "Data has been loaded, photoFeed size: " + photoFeed.get().size());
    }

    @OnUpdateState
    static void setFetching(final StateValue<Boolean> isFetching, @Param boolean fetch) {
        isFetching.set(fetch);
    }

//    @OnRefresh
//    static void onRefresh(SectionContext c, @Prop @Prop MainActivityViewModel viewModel) {
//        // Handle your refresh request
//        Log.d("Refresh", "Refreshing data");
//        if (listener != null) {
//            listener.onQueryUpdated(textSubmitted.getText());
//        }
//    }

    @OnViewportChanged
    static void onViewportChanged(
            SectionContext c,
            int firstVisiblePosition,
            int lastVisiblePosition,
            int totalCount,
            int firstFullyVisibleIndex,
            int lastFullyVisibleIndex,
            @Prop MainActivityViewModel viewModel,
            @State List<Photo> photoFeed,
            @State boolean isFetching) {

        Log.d("ViewPort", "Total count " + (totalCount - 1) + "\nLast Visible Position " + lastFullyVisibleIndex);
        if(totalCount - 1 <= lastFullyVisibleIndex && !isFetching) {
            Log.d("ViewPort", "Fetch more data");
            ListSection.setFetching(c, true);
            viewModel.fetchNextPage();
        }
    }

}

