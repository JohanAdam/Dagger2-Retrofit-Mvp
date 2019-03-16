package io.reciteapp.recite.progress;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.DebouncedOnClickListener;
import io.reciteapp.recite.historydetail.HistoryDetailFragment;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.HistoryResponse;
import java.util.ArrayList;
import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder>{

  private final MainActivity activity;
  private List<HistoryResponse> historyResponseList;

  ProgressAdapter(MainActivity activity, ArrayList<HistoryResponse> responses) {
    this.activity = activity;
    this.historyResponseList = responses;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater
        .from(parent.getContext()).inflate(R.layout.list_item_history_list, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final HistoryResponse data = historyResponseList.get(holder.getAdapterPosition());
    holder.bind(data);

    holder.itemView.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        openHistoryDetail(data);
      }
    });
  }

  private void openHistoryDetail(HistoryResponse data) {
    Fragment mFragment = new HistoryDetailFragment();
    Bundle mBundle = new Bundle();
    mBundle.putSerializable("data", data);
    mFragment.setArguments(mBundle);

    activity.switchFragmentAddBackstack(mFragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public int getItemCount() {
    if (historyResponseList != null) {
      return historyResponseList.size();
    }
    return 0;
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull ProgressAdapter.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fastscrolling animation
    holder.itemView.clearAnimation();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    final TextView tvSurahName;
    final TextView tvSurahAyat;
    final TextView tvTimeStamp;
    final TextView isReviewed;

    ViewHolder(View itemView) {
      super(itemView);
      tvSurahName = itemView.findViewById(R.id.tv_surah_name);
      tvSurahAyat = itemView.findViewById(R.id.tv_surah_ayat);
      tvTimeStamp = itemView.findViewById(R.id.tv_timestamp);
      isReviewed = itemView.findViewById(R.id.tv_is_review);
    }

    public void bind(HistoryResponse data) {
      tvSurahName.setText(data.getSurahName());
      tvSurahAyat.setText(data.getAyat());
      tvTimeStamp.setText(data.getSubmitted());

      if (data.isReviewed()) {
        //reviewed
        isReviewed.setText(activity.getResources().getString(R.string.msg_reviewed));
        isReviewed.setTextColor(ContextCompat.getColor(activity, R.color.pale_olive_green));
      } else {
        //unReviewed
        isReviewed.setText(activity.getResources().getString(R.string.msg_unreviewed));
        isReviewed.setTextColor(ContextCompat.getColor(activity, R.color.wheat));
      }
    }
  }
}
