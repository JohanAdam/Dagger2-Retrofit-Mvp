package io.reciteapp.recite.submissiondetail.user;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.comment.CommentFragment;
import io.reciteapp.recite.customview.DebouncedOnClickListener;
import io.reciteapp.recite.data.model.ReviewerCommentResponse;
import io.reciteapp.recite.main.MainActivity;
import java.util.List;

public class SubmissionDetailUCommentListAdapter extends RecyclerView.Adapter<SubmissionDetailUCommentListAdapter.ViewHolder> {
  
  private final MainActivity mContext;
  private List<ReviewerCommentResponse> commentLists;
  private String userAudioUri;

  SubmissionDetailUCommentListAdapter(MainActivity activity, List<ReviewerCommentResponse> responses,
      String userAudioUri) {
    this.mContext = activity;
    this.commentLists = responses;
    this.userAudioUri = userAudioUri;
  }

  @NonNull
  @Override
  public SubmissionDetailUCommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater
        .from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);
    return new SubmissionDetailUCommentListAdapter.ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull SubmissionDetailUCommentListAdapter.ViewHolder holder, int position) {
    final ReviewerCommentResponse data = commentLists.get(holder.getAdapterPosition());
    holder.bind(data);

    holder.itemView.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        openCommentFragment(data);
      }
    });
  }

  private void openCommentFragment(ReviewerCommentResponse data) {
    Fragment mFragment = new CommentFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString("duration", data.getAudioDuration());
    mBundle.putString("comment", data.getRemark());
    mBundle.putString("audiouriuser", userAudioUri);
    mBundle.putString("audiouricorrection", data.getReviewAudioUri());
    mFragment.setArguments(mBundle);

    mContext.addFragmentAddBackstack(mFragment, Constants.TAG_OTHERS_FRAGMENT);
  }

  @Override
  public int getItemCount() {
    if (commentLists != null) {
      return commentLists.size();
    }
    return 0;
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull SubmissionDetailUCommentListAdapter.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fastscrolling animation
    holder.itemView.clearAnimation();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    final TextView tvDuration;
    final ImageView iv_audio;
    final ImageView iv_comment;

    ViewHolder(View itemView) {
      super(itemView);
      tvDuration = itemView.findViewById(R.id.tv_duration);
      iv_audio = itemView.findViewById(R.id.iv_icon_audio);
      iv_comment = itemView.findViewById(R.id.iv_icon_comment);
    }

    public void bind(ReviewerCommentResponse data) {

      tvDuration.setText(data.getAudioDuration());

      if (data.isAttachmentAvailable()) {
        ImageViewCompat.setImageTintList(iv_audio, ColorStateList.valueOf
            (ContextCompat.getColor(mContext, R.color.pinkTheme)));
      } else {
        ImageViewCompat.setImageTintList(iv_audio, ColorStateList.valueOf
            (ContextCompat.getColor(mContext, R.color.disable_item_text)));
      }


      //TODO too complex, temporary fix
      //If not empty or not contain default text , light them up!
      if (!TextUtils.isEmpty(data.getRemark()) && !data.getRemark().trim().equalsIgnoreCase(mContext.getString(R.string.msg_voice_note_template))) {
        ImageViewCompat.setImageTintList(iv_comment, ColorStateList.valueOf
            (ContextCompat.getColor(mContext, R.color.pinkTheme)));
      } else {
        //if empty of contain default text, shut it down!
        ImageViewCompat.setImageTintList(iv_comment, ColorStateList.valueOf
            (ContextCompat.getColor(mContext, R.color.disable_item_text)));
      }

    }
  }
}
