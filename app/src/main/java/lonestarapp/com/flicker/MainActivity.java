package lonestarapp.com.flicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import lonestarapp.com.flicker.data.model.Flicker.Photo;
import lonestarapp.com.flicker.ui.litho.ListSection;
import lonestarapp.com.flicker.viewmodels.MainActivityViewModel;

import android.os.Bundle;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.sections.SectionContext;
import com.facebook.litho.sections.widget.GridRecyclerConfiguration;
import com.facebook.litho.sections.widget.RecyclerCollectionComponent;
import com.facebook.litho.sections.widget.RecyclerConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Photo> photoList = new ArrayList<>();
    private MainActivityViewModel mMainActivityViewModel;
    private ComponentContext context;
    private Component component;
    private LithoView lithoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = new ComponentContext(this);

        // **********************************************************

        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.getObservable().
                observe(this, new Observer<List<Photo>>() {
                    @Override
                    public void onChanged(List<Photo> photos) {
                        if(photos != null && photoList.isEmpty()) {
                            photoList.clear();
                            photoList.addAll(photos);
                            createLithoUI();
                        }
                    }
                });
        // **********************************************************
    }

    private void createLithoUI() {
        int NUMBER_OF_COL = 3;

        final RecyclerConfiguration
                recyclerConfiguration = GridRecyclerConfiguration.create()
                .orientation(LinearLayoutManager.VERTICAL)
                .numColumns(NUMBER_OF_COL)
                .build();


        this.component = RecyclerCollectionComponent.create(context)
                .disablePTR(true)
                .section(ListSection.create(new SectionContext(context))
                        .viewModel(mMainActivityViewModel)
                        .dataModel(photoList)
                        .build())
                .recyclerConfiguration(recyclerConfiguration)
                .build();
        lithoView = LithoView.create(context, component);
        setContentView(lithoView);
    }
}
