package io.reciteapp.recite.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * TextView with icon font support
 */
public class CustomTextView extends android.support.v7.widget.AppCompatTextView {


  public CustomTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

    this.setTypeface(Typeface.createFromAsset(context.getAssets()
        , "fonts/fontawesome-webfont.ttf"));
  }
}
