package lonestarapp.com.flicker.ui.litho.components;

import android.graphics.Color;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.fresco.FrescoImage;

import lonestarapp.com.flicker.R;
import lonestarapp.com.flicker.data.model.Flicker.Photo;

import static com.facebook.yoga.YogaEdge.ALL;

@LayoutSpec
public class ListItemSpec {
    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop int color,
                                    @Prop Photo photo) {

        final DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(photo.getImgUrl())
                .build();

        return Column.create(c)
                .paddingDip(ALL, 1)
                .backgroundColor(color)
                .child(
                        FrescoImage.create(c)
                                .controller(controller)
                                .failureImageRes(R.drawable.ic_broken_image)
                                .placeholderImageRes(R.drawable.ic_image)
                                .backgroundColor(Color.GRAY)
                                .aspectRatio(1)
                )
                .build();
    }
}
