package io.reciteapp.recite.surahlist.SurahListRecyclerView;

import android.annotation.SuppressLint;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.SurahListResponse;

public class SurahParentViewHolder extends
    com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {

  private static final float INITIAL_POSITION = 0.0f;
  private static final float ROTATED_POSITION = 180f;
  final ImageView mArrowExpandImageView;
  final CardView full_layout;
  private final TextView tv_surahname;
  private final TextView tv_ayat, tv_submission_count;
  private final TextView tv_submission_title;


  /**
   * Default constructor.
   *
   * @param itemView The {@link View} being hosted in this ViewHolder
   */
  public SurahParentViewHolder(View itemView) {
    super(itemView);
    tv_surahname = itemView.findViewById(R.id.tv_surah_name);
    tv_ayat = itemView.findViewById(R.id.tv_surah_ayat);
    mArrowExpandImageView = itemView.findViewById(R.id.arrow_expand_imageview);
    full_layout = itemView.findViewById(R.id.main_card_view);
    tv_submission_count = itemView.findViewById(R.id.tv_submission_count);
    tv_submission_title = itemView.findViewById(R.id.tv_title_submission);
  }

  public void bind(SurahListResponse data) {

    tv_surahname.setText(data.getSurahName());
    tv_ayat.setText(data.getAyat());

    if (data.getListayat() != null) {
      if (data.getListayat().size() < 2) {
        mArrowExpandImageView.setVisibility(View.GONE);
        tv_submission_count.setVisibility(View.VISIBLE);
        tv_submission_title.setVisibility(View.VISIBLE);
        tv_submission_count.setText(String.valueOf(data.getListayat().get(0).getTotalSubmission()));
      } else {
        mArrowExpandImageView.setVisibility(View.VISIBLE);
        tv_submission_count.setVisibility(View.GONE);
        tv_submission_title.setVisibility(View.INVISIBLE);
      }
    } else {
      mArrowExpandImageView.setVisibility(View.GONE);
    }

  }

  @SuppressLint("NewApi")
  @Override
  public void setExpanded(boolean expanded) {
    super.setExpanded(expanded);
    if (expanded) {
      mArrowExpandImageView.setRotation(ROTATED_POSITION);
    } else {
      mArrowExpandImageView.setRotation(INITIAL_POSITION);
    }
  }

  @Override
  public void onExpansionToggled(boolean expanded) {
    super.onExpansionToggled(expanded);
    RotateAnimation rotateAnimation;
    if (expanded) {
      // rotate clockwise
      rotateAnimation = new RotateAnimation(ROTATED_POSITION,
          INITIAL_POSITION,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    } else {
      // rotate counterclockwise
      rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
          INITIAL_POSITION,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f,
          RotateAnimation.RELATIVE_TO_SELF, 0.5f);
    }

    rotateAnimation.setDuration(200);
    rotateAnimation.setFillAfter(true);
    mArrowExpandImageView.startAnimation(rotateAnimation);
  }

}
