package io.reciteapp.recite.submissiondetail.cs;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.DebouncedOnClickListener;
import io.reciteapp.recite.data.model.UstazReviewCommentResponse;
import io.reciteapp.recite.submissiondetail.cs.SubmissionDetailCommentAdapter.ViewHolder;
import io.reciteapp.recite.utils.FileManager;
import java.util.List;

class SubmissionDetailCommentAdapter extends RecyclerView.Adapter<ViewHolder> {

  private final SubmissionDetailCsFragment fragment;
  private List<UstazReviewCommentResponse> commentList;
  private String userAudioUri;
  private String audioAttachmentFileName;

  SubmissionDetailCommentAdapter(SubmissionDetailCsFragment activity, String userAudioUri,
      String audioAttachmentFileName, List<UstazReviewCommentResponse> responses) {
    this.fragment = activity;
    this.userAudioUri = userAudioUri;
    this.audioAttachmentFileName = audioAttachmentFileName;
    this.commentList = responses;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cs_comment, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    final UstazReviewCommentResponse data = commentList.get(holder.getAdapterPosition());
    holder.bind(data);

    holder.itemView.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        fragment.openCommentDialog(userAudioUri, audioAttachmentFileName, data);
      }
    });

    holder.itemView.setOnLongClickListener(v -> {
      if (position != -1) {
        String uri = commentList.get(holder.getAdapterPosition()).getAudioUri();
        new FileManager().deleteFilebyUri(uri);
        commentList.remove(holder.getAdapterPosition());
        notifyItemRemoved(holder.getAdapterPosition());
      }
      return false;
    });
  }

  /**
   * Item list count
   */
  @Override
  public int getItemCount() {
    if (commentList != null) {
      return commentList.size();
    }
    return 0;
  }

  /**
   * Remove animation when remove recyclerview
   */
  @Override
  public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fastscrolling animation
    holder.itemView.clearAnimation();
  }

  /**
   * Bind view
   */
  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView tv_duration, tv_comment;
    AppCompatImageView iv_audio;

    ViewHolder(View itemView) {
      super(itemView);
      tv_duration = itemView.findViewById(R.id.tv_duration);
      tv_comment = itemView.findViewById(R.id.tv_comment);
      iv_audio = itemView.findViewById(R.id.iv_icon_audio);
    }

    public void bind(UstazReviewCommentResponse data) {
      tv_duration.setText(data.getAudioDuration());
      tv_comment.setText(data.getComment());

      if (data.isAttachmentAvailable()) {
        ImageViewCompat.setImageTintList(iv_audio, ColorStateList.valueOf
            (ContextCompat.getColor(fragment.activity, R.color.pinkTheme)));
      } else {
        ImageViewCompat.setImageTintList(iv_audio, ColorStateList.valueOf
            (ContextCompat.getColor(fragment.activity, R.color.disable_item_text)));
      }

    }
  }
}
