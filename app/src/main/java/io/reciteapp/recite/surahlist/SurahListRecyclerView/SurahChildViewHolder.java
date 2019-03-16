package io.reciteapp.recite.surahlist.SurahListRecyclerView;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.model.AyatListsResponse;

public class SurahChildViewHolder extends ChildViewHolder {

  public final ConstraintLayout layout;
  private final TextView ayat;
  private final TextView tv_submission_count;

  /**
   * Default constructor.
   *
   * @param itemView The {@link View} being hosted in this ViewHolder
   */
  SurahChildViewHolder(View itemView) {
    super(itemView);
    ayat = itemView.findViewById(R.id.tv_surah_ayat);
    layout = itemView.findViewById(R.id.child_layout_main);
    tv_submission_count = itemView.findViewById(R.id.tv_submission_count);
  }

  public void bind(AyatListsResponse subList) {
    ayat.setText(String.format("Verse %s", subList.getSubAyat()));
    tv_submission_count.setText(String.valueOf(subList.getTotalSubmission()));
  }
}
