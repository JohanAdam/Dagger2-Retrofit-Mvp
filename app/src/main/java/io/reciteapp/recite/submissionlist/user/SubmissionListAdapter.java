package io.reciteapp.recite.submissionlist.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.DebouncedOnClickListener;
import io.reciteapp.recite.data.model.SubmissionListResponse;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.submissiondetail.user.SubmissionDetailUFragment;
import java.util.List;

class SubmissionListAdapter extends RecyclerView.Adapter<SubmissionListAdapter.ViewHolder> {

  private final MainActivity mContext;
  private List<SubmissionListResponse> submissionList;

  SubmissionListAdapter(MainActivity activity, List<SubmissionListResponse> responses) {
    this.mContext = activity;
    this.submissionList = responses;
  }

  @NonNull
  @Override
  public SubmissionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_submission_list, parent, false);
    return new SubmissionListAdapter.ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull SubmissionListAdapter.ViewHolder holder, int position) {
    final SubmissionListResponse data = submissionList.get(holder.getAdapterPosition());
    holder.bind(data);

    holder.itemView.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        openSubmissionDetail(data);
      }
    });
  }

  /**
   * Open submission detail
   * @param data submission detail data
   */
  private void openSubmissionDetail(SubmissionListResponse data) {
    Fragment mFragment = new SubmissionDetailUFragment();
    Bundle mBundle = new Bundle();
    mBundle.putSerializable("data", data);
    mFragment.setArguments(mBundle);

    mContext.switchFragmentAddBackstack(mFragment, Constants.TAG_SUBMISSION_DETAIL_USER);
  }

  /**
   * Item list count
   */
  @Override
  public int getItemCount() {
    if (submissionList != null) {
      return submissionList.size();
    }
    return 0;
  }

  /**
   * Remove animation when remove recyclerview
   */
  @Override
  public void onViewDetachedFromWindow(@NonNull SubmissionListAdapter.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fastscrolling animation
    holder.itemView.clearAnimation();
  }

  /**
   * Bind view
   */
  public class ViewHolder extends RecyclerView.ViewHolder {

    final TextView tvSurahName;
    final TextView tvSurahAyat;
    final TextView tvTimeStamp;
    final CardView cardView;

    ViewHolder(View itemView) {
      super(itemView);
      cardView = itemView.findViewById(R.id.list_item_submission);
      tvSurahName = itemView.findViewById(R.id.tv_surah_name);
      tvSurahAyat = itemView.findViewById(R.id.tv_surah_ayat);
      tvTimeStamp = itemView.findViewById(R.id.tv_timestamp);
    }

    public void bind(SubmissionListResponse data) {
      tvSurahName.setText(data.getSurahName());
      tvSurahAyat.setText(data.getAyat());
      tvTimeStamp.setText(data.getSubmitted());

      tvTimeStamp.setVisibility(View.INVISIBLE);
    }
  }
}
