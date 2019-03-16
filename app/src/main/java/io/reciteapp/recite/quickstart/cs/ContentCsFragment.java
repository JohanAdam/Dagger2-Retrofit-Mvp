package io.reciteapp.recite.quickstart.cs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reciteapp.recite.R;

/**
 * A Placeholder fragment contained the content
 */
public class ContentCsFragment extends Fragment {

  @BindView(R.id.iv_main)
  ImageView ivMain;
  @BindView(R.id.main_frame_layout)
  FrameLayout mainFrameLayout;
  @BindView(R.id.tv_main)
  TextView tvMain;
  Unbinder unbinder;

  private static final String ARG_SECTION_NUMBER = "section_number";
  final int[] contentImageList = new int[]{
      R.drawable.onboard_cs_dashboard,
      R.drawable.onboard_cs_submission,
      R.drawable.onboard_cs_player,
      R.drawable.onboard_cs_pause,
      R.drawable.onboard_cs_dialog,
      R.drawable.onboard_cs_submit,
      R.drawable.onboard_cs_success};

  final int[] contextTextList = new int[]{
      R.string.msg_onboard_dashboard,
      R.string.msg_onboard_submission_list,
      R.string.msg_onboard_start_reviewing,
      R.string.msg_onboard_reviewing_pause,
      R.string.msg_onboard_dialog_comment,
      R.string.msg_onboard_submit_review,
      R.string.msg_onboard_success_review};
  private QuickStartCsctivity activity;

  public ContentCsFragment() {
    //public constructor
  }

  public static ContentCsFragment newInstance(int sectionNumber) {
    ContentCsFragment fragment = new ContentCsFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_SECTION_NUMBER, sectionNumber);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof QuickStartCsctivity) {
      this.activity = (QuickStartCsctivity) context;
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_content_cs, container, false);
    unbinder = ButterKnife.bind(this, rootView);

    ivMain.setImageResource(contentImageList[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
    tvMain.setText(contextTextList[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

}
