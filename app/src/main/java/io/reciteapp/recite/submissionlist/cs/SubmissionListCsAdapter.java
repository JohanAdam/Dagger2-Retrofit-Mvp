package io.reciteapp.recite.submissionlist.cs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCsFragment;
import java.util.List;
import timber.log.Timber;

public class SubmissionListCsAdapter extends RecyclerView.Adapter<SubmissionListCsAdapter.ViewHolder> {

  private final MainActivity mContext;
  private List<SubmissionListResponse> submissionList;

  SubmissionListCsAdapter(MainActivity activity, List<SubmissionListResponse> responses) {
    this.mContext = activity;
    this.submissionList = responses;
  }

  @NonNull
  @Override
  public SubmissionListCsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater
        .from(parent.getContext()).inflate(R.layout.list_item_submission_list, parent, false);
    return new SubmissionListCsAdapter.ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull SubmissionListCsAdapter.ViewHolder holder, int position) {
    final SubmissionListResponse data = submissionList.get(holder.getAdapterPosition());
    holder.bind(data);

    holder.itemView.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        if (!data.isLock()) {
          openSubmissionDetail(data);
        } else {
          Timber.e("Post is locked!");
          //TODO add dialog post is locked
        }
      }
    });
  }

  private void openSubmissionDetail(SubmissionListResponse data) {
    Fragment mFragment = new SubmissionDetailCsFragment();
    Bundle mBundle = new Bundle();
    mBundle.putSerializable("data", data);
    mFragment.setArguments(mBundle);

    mContext.switchFragmentAddBackstack(mFragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public int getItemCount() {
    if (submissionList != null) {
      return submissionList.size();
    }
    return 0;
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull SubmissionListCsAdapter.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fastscrolling animation
    holder.itemView.clearAnimation();
  }

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

      tvTimeStamp.setVisibility(View.VISIBLE);

      //if submission is lock by other cs, show "Locked" at time stamp tv
      if (data.isLock()) {
        tvTimeStamp.setText(R.string.title_lock);
        tvTimeStamp.setTextColor(mContext.getResources().getColor(R.color.wheat));
      } else {
        tvTimeStamp.setText(data.getSubmitted());
        tvTimeStamp.setTextColor(ContextCompat.getColor(mContext, R.color.font_9b));
      }

      //If submission priority is 1, set the card background to Red
      if (data.getIsPriority()) {
        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.md_red_300));
      }

    }
  }
}
