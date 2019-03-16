package io.reciteapp.recite.customview;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.widget.ImageView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.root.App;
import timber.log.Timber;

public class ProgressBarAnimation {

  private AnimatedVectorDrawableCompat animatedVector;


  public ProgressBarAnimation() {
    animatedVector = AnimatedVectorDrawableCompat.create(App.getApp(), R.drawable.anim_loading);
  }

  /**
   * Start the animation.
   * @param imageView  pass the view for the animation to run.
   */
  public void startAnimation(ImageView imageView) {
    imageView.setImageDrawable(animatedVector);
    final Handler mainHandler = new Handler(Looper.getMainLooper());
    assert animatedVector != null;
    animatedVector.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
      @Override
      public void onAnimationEnd(final Drawable drawable) {
        mainHandler.post(animatedVector::start);
      }
    });

    animatedVector.start();
  }

  /**
   * End the animation.
   */
  public void stopAnimation() {
    Timber.e("ANIMATION STOP");
    if (animatedVector != null) {
      animatedVector.clearAnimationCallbacks();
      animatedVector.stop();
    }
  }
}
