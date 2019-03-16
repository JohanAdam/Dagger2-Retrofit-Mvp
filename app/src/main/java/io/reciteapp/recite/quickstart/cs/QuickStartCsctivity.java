package io.reciteapp.recite.quickstart.cs;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.root.BaseActivity;

//TODO change to Mvp

/**
 * Not MVP architecture
 */


public class QuickStartCsctivity extends BaseActivity {

  @BindView(R.id.intro_btn_skip)
  Button btn_skip;
  @BindView(R.id.intro_indicator_0)
  ImageView ivZero;
  @BindView(R.id.intro_indicator_1)
  ImageView ivOne;
  @BindView(R.id.intro_indicator_2)
  ImageView ivTwo;
  @BindView(R.id.intro_indicator_3)
  ImageView ivThree;
  @BindView(R.id.intro_indicator_4)
  ImageView ivFour;
  @BindView(R.id.intro_indicator_5)
  ImageView ivFive;
  @BindView(R.id.intro_indicator_6)
  ImageView ivSix;
  @BindView(R.id.intro_btn_finish)
  Button btn_finish;
  @BindView(R.id.intro_btn_next)
  ImageButton btn_next;

  ImageView[] indicators;
  int totalPage = 0;
  int currentPage = 0;
  ViewPager mViewPager;
  SharedPreferencesManager sharedPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ButterKnife.bind(this);

    sharedPref = new SharedPreferencesManager(this);
    setupIndicator();

    SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

    //Color resource and color list
    final int color = ContextCompat.getColor(this, R.color.disable_item_text);
    final int[] colorList = new int[]{color, color, color, color, color, color, color};

    updateIndicators(currentPage);
    //Set up the ViewPager with the sections adapter.
    mViewPager = findViewById(R.id.container);
    mViewPager.setAdapter(sectionPagerAdapter);

    //Color changer
    final ArgbEvaluator evaluator = new ArgbEvaluator();
    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Color Update for every page
        int colorUpdate = (Integer) evaluator.evaluate(positionOffset,
            colorList[position], colorList[position == totalPage - 1 ? position : position + 1]);
        mViewPager.setBackgroundColor(colorUpdate);
      }

      @Override
      public void onPageSelected(int position) {
        currentPage = position;
        updateIndicators(currentPage);

        mViewPager.setBackgroundColor(color);
        btn_next.setVisibility(position == totalPage - 1 ? View.GONE : View.VISIBLE);

        //Show finish button at the end of page
        btn_finish.setVisibility(position == totalPage - 1 ? View.VISIBLE : View.GONE);

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override
  protected int getLayoutResourceId() {
    return R.layout.activity_quick_start_cs;
  }


  private void updateIndicators(int currentPage) {
    for (int i = 0; i < indicators.length; i++) {
      indicators[i].setBackgroundResource(
          i == currentPage ? R.drawable.indicator_selected : R.drawable.indicator_unselected
      );
    }
  }


  public void setupIndicator() {
    indicators = new ImageView[]{ivZero, ivOne, ivTwo, ivThree, ivFour, ivFive, ivSix};
    btn_skip.setVisibility(View.GONE);
    totalPage = 7;
  }

  @OnClick({R.id.intro_btn_skip, R.id.intro_btn_finish, R.id.intro_btn_next})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.intro_btn_skip:
        sharedPref.saveBoolean(Constants.PREF_FIRST_USER, false);
        finish();
        break;
      case R.id.intro_btn_finish:
        sharedPref.saveBoolean(Constants.PREF_FIRST_USER, false);
        finish();
        break;
      case R.id.intro_btn_next:
        currentPage += 1;
        mViewPager.setCurrentItem(currentPage, true);
        break;
    }
  }

  private class SectionPagerAdapter extends FragmentPagerAdapter {

    SectionPagerAdapter(FragmentManager supportFragmentManager) {
      super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return ContentCsFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
      //Show total page
      return totalPage;
    }
  }
}
