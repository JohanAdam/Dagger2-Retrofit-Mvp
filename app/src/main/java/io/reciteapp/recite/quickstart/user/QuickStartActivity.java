package io.reciteapp.recite.quickstart.user;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.sharedpref.SharedPreferencesManager;
import io.reciteapp.recite.root.BaseActivity;
import timber.log.Timber;

//TODO change to Mvp
/**
 * Not MVP architecture
 */


public class QuickStartActivity extends BaseActivity {

  @BindView(R.id.container)
  ViewPager container;
  @BindView(R.id.view)
  View view;
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
  @BindView(R.id.intro_btn_finish)
  Button btn_finish;
  @BindView(R.id.intro_btn_next)
  ImageButton btn_next;
  @BindView(R.id.frameLayout2)
  FrameLayout frameLayout;
  @BindView(R.id.main_content)
  ConstraintLayout mainContent;

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
    Timber.d("FIRST_USER is %s", sharedPref.loadBoolean(Constants.PREF_FIRST_USER));
    if (sharedPref.loadBoolean(Constants.PREF_FIRST_USER)) {
      //First run
      firstUserView();
    } else {
      //Quick Start
      quickStartView();
    }

    SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

    //Color resource and color list
    final int color = ContextCompat.getColor(this, R.color.disable_item_text);
    final int[] colorList = new int[]{color, color, color, color, color};

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
//        switch (position) {
//          case 0:
//            mViewPager.setBackgroundColor(color);
//            break;
//          case 1:
//            mViewPager.setBackgroundColor(color);
//            break;
//          case 2:
//            mViewPager.setBackgroundColor(color);
//            break;
//          case 3:
//            mViewPager.setBackgroundColor(color);
//            break;
//          case 4:
//            mViewPager.setBackgroundColor(color);
//            break;
//        }
        btn_next.setVisibility(position == totalPage - 1 ? View.GONE : View.VISIBLE);
        btn_finish.setVisibility(position == totalPage - 1 ? View.VISIBLE : View.GONE);

        if (position == totalPage - 1) {
          //If first user true, finish button not available at last page to avoid user skip
          btn_finish.setVisibility(sharedPref.loadBoolean(Constants.PREF_FIRST_USER) ? View.GONE : View.VISIBLE);
        } else {
          //If not at the last page, set btn finish to Gone
          btn_finish.setVisibility(View.GONE);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  @Override
  protected int getLayoutResourceId() {
    return R.layout.activity_quick_start;
  }


  private void updateIndicators(int currentPage) {
    for (int i = 0; i < indicators.length; i++) {
      indicators[i].setBackgroundResource(
          i == currentPage ? R.drawable.indicator_selected : R.drawable.indicator_unselected
      );
    }
  }

  public void firstUserView() {
    indicators = new ImageView[]{ivZero, ivOne, ivTwo, ivThree, ivFour};
    btn_skip.setVisibility(View.GONE);
    totalPage = 5;
  }


  public void quickStartView() {
    indicators = new ImageView[]{ivZero, ivOne, ivTwo, ivThree};
    ivFour.setVisibility(View.GONE);
    btn_skip.setVisibility(View.GONE);
    totalPage = 4;
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
      return ContentFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
      //Show 5 total page
      return totalPage;
    }
  }

  public void saveCountryAndFinish(String country) {
    sharedPref.saveString(Constants.PREF_COUNTRY, country);
    sharedPref.saveBoolean(Constants.PREF_FIRST_USER, false);
    Timber.d("setCountry %s", country);
    finish();
    Timber.e("Finish!");
  }

  @Override
  public void onBackPressed() {
    if (!sharedPref.loadBoolean(Constants.PREF_FIRST_USER)) {
      super.onBackPressed();
    }
  }
}
