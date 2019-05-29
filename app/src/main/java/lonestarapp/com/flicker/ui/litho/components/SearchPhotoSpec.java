package lonestarapp.com.flicker.ui.litho.components;

import android.graphics.Color;
import android.text.InputType;
import android.util.Log;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateInitialState;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.EditText;
import com.facebook.litho.widget.TextChangedEvent;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import lonestarapp.com.flicker.viewmodels.MainActivityViewModel;


@LayoutSpec
public class SearchPhotoSpec {

    @OnCreateInitialState
    static void createInitialState(
            ComponentContext context) {

    }

    @OnCreateLayout
    static Component onCreateLayout(final ComponentContext c,
                                    @Prop String hint,
                                    @Prop final MainActivityViewModel viewModel) {

        return EditText.create(c)
                .textSizeDip(16)
                .hint(hint)
                .isSingleLine(true)
                .backgroundColor(Color.WHITE)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .imeOptions(EditorInfo.IME_ACTION_DONE)
                //.textChangedEventHandler(SearchPhoto.onQueryChanged(c))
                .editorActionListener(new OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if(actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.d("onEditorAction", "Value: " + textView.getText());

                            viewModel.searchWithText(textView.getText().toString());
                            return true;
                        }
                        return false;
                    }
                })
                .build();
    }


    @OnEvent(TextChangedEvent.class)
    static void onQueryChanged(final ComponentContext c, @FromEvent String text) {
        if(text.length() > 10) {
        }
        Log.d("TextChangedEvent", "Value: " + text);
    }
}
