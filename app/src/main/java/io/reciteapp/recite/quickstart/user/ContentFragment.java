package io.reciteapp.recite.quickstart.user;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reciteapp.recite.R;
import io.reciteapp.recite.constants.Constants;

/**
 * A Placeholder fragment contained the content
 */
public class ContentFragment extends Fragment {

  @BindView(R.id.iv_main)
  ImageView ivMain;
  @BindView(R.id.main_frame_layout)
  FrameLayout mainFrameLayout;
  @BindView(R.id.tv_main)
  TextView tvMain;
  @BindView(R.id.tv_choose_country)
  TextView tvChooseCountry;
  @BindView(R.id.btn_malaysia)
  Button btnMalaysia;
  @BindView(R.id.btn_indonesia)
  Button btnIndonesia;
  Unbinder unbinder;

  private static final String ARG_SECTION_NUMBER = "section_number";
  final int[] contentImageList = new int[]{R.drawable.onboard_dashboard,
      R.drawable.onboard_pickayat,
      R.drawable.onboard_recorder,
      R.drawable.onboard_history,
      R.drawable.onboard_dashboard};

  final int[] contextTextList = new int[]{
      R.string.msg_track_progress,
      R.string.msg_choose_recording,
      R.string.msg_record_submit,
      R.string.msg_listen_recital_other,
      R.string.msg_choose_country};
  private QuickStartActivity activity;

  public ContentFragment() {
    //public constructor
  }

  public static ContentFragment newInstance(int sectionNumber) {
    ContentFragment fragment = new ContentFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(ARG_SECTION_NUMBER, sectionNumber);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof QuickStartActivity) {
      this.activity = (QuickStartActivity) context;
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_content, container, false);
    unbinder = ButterKnife.bind(this, rootView);

    ivMain.setImageResource(contentImageList[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
    tvMain.setText(contextTextList[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);


    ivMain.setVisibility(getArguments().getInt(ARG_SECTION_NUMBER) == 5 ? View.GONE : View.VISIBLE);
    tvMain.setVisibility(getArguments().getInt(ARG_SECTION_NUMBER) == 5 ? View.GONE : View.VISIBLE);
    btnMalaysia.setVisibility(getArguments().getInt(ARG_SECTION_NUMBER) == 5 ? View.VISIBLE : View.GONE);
    btnIndonesia.setVisibility(getArguments().getInt(ARG_SECTION_NUMBER) == 5 ? View.VISIBLE : View.GONE);
    tvChooseCountry.setVisibility(getArguments().getInt(ARG_SECTION_NUMBER) == 5 ? View.VISIBLE : View.GONE);

    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({R.id.btn_malaysia, R.id.btn_indonesia})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_malaysia:
        setCountry(Constants.my);
        break;
      case R.id.btn_indonesia:
        setCountry(Constants.in);
        break;
    }
  }

  public void setCountry(String country) {
    activity.saveCountryAndFinish(country);
  }
}
