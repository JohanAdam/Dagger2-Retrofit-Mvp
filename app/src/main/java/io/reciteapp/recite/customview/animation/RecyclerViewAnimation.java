package io.reciteapp.recite.customview.animation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import io.reciteapp.recite.R;

public class RecyclerViewAnimation {

  /**
   * @param recyclerView pass RecyclerView here to run animation
   * when start/resume activity/fragment.
   *
   * You must call holder.itemView.clearAnimation(); for avoid fastscrolling animation bug
   * in onViewDetachedFromWindow in Recyclerview adapter.
   */
  public void rerunLayoutAnimation(final RecyclerView recyclerView) {
    final Context context = recyclerView.getContext();
    final LayoutAnimationController controller =
        AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

    recyclerView.setLayoutAnimation(controller);
    recyclerView.getAdapter().notifyDataSetChanged();
    recyclerView.scheduleLayoutAnimation();
  }



}
