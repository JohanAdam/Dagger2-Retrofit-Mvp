package io.reciteapp.recite.surahlist.SurahListRecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import io.reciteapp.recite.constants.Constants;
import io.reciteapp.recite.R;
import io.reciteapp.recite.customview.DebouncedOnClickListener;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.AyatListsResponse;
import io.reciteapp.recite.data.model.SurahListResponse;
import io.reciteapp.recite.recite.ReciteFragment;
import java.util.List;
import timber.log.Timber;

public class SurahListAdapter extends ExpandableRecyclerAdapter<SurahParentViewHolder,
    SurahChildViewHolder> {

  private final Context mcontext;
  private final LayoutInflater mInflator;
  private String surahName;
  private long mLastClickTime = 0;

  public SurahListAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
    super(parentItemList);
    mInflator = LayoutInflater.from(context);
    mcontext = context;
  }

  //attach layout parent
  @Override
  public SurahParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
    View recipeView = mInflator.inflate(R.layout.surah_list_item, parentViewGroup, false);
    return new SurahParentViewHolder(recipeView);
  }

  //attach layout child
  @Override
  public SurahChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
    View ingredientView = mInflator.inflate(R.layout.sub_ayat_list_item, childViewGroup, false);
    return new SurahChildViewHolder(ingredientView);
  }

  //bind data to parent layout
  @Override
  public void onBindParentViewHolder(final SurahParentViewHolder viewHolder, final int position,
      ParentListItem parentListItem) {
    final SurahListResponse data = (SurahListResponse) parentListItem;
    viewHolder.bind(data);

    viewHolder.full_layout.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (viewHolder.mArrowExpandImageView.getVisibility() == View.VISIBLE) {
          surahName = data.getSurahName();
          if (viewHolder.isExpanded()) {
            viewHolder.onClick(v);
          } else {
            viewHolder.onClick(v);
          }
        } else if (viewHolder.mArrowExpandImageView.getVisibility() == View.GONE) {
          Timber.d("click parent %s", data.getSurahName());

          //TODO check permission
          if (mcontext instanceof MainActivity) {
//          MainActivity pickayat = (MainActivity) mcontext;
//            boolean stateMic = pickayat.checkRecordingPermission();
//            boolean stateFile = pickayat.checkFilePermission();

//            if (stateMic && stateFile) {
            //permission for mic is true
            openReciteFragment(data.getSurahName(), data.getAyat(), data.getListayat().get(0).getAyatId()
            );

//            } else {

            //ask for permission
//              pickayat.getPermission();
//              Toast.makeText(mcontext, mcontext.getResources().getString(R
//                      .string.permission_is_needed),
//                  Toast.LENGTH_SHORT).show();

//            }
          }
        }
      }
    });

  }

  //bind data to child layout
  @Override
  public void onBindChildViewHolder(SurahChildViewHolder viewHolder, final int position,
      Object childListItem) {
    final AyatListsResponse ayatListsResponse = (AyatListsResponse) childListItem;
    viewHolder.bind(ayatListsResponse);
    viewHolder.layout.setOnClickListener(new DebouncedOnClickListener(Constants.defaultClickValue) {
      @Override
      public void onDebouncedClick(View v) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
          return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        Timber.d("Clicked child %s", ayatListsResponse.getSubAyat());
        if (mcontext instanceof MainActivity) {
//        MainActivity pickayat = (MainActivity) mcontext;
//        boolean stateMic = pickayat.checkRecordingPermission();
//        boolean stateFile = pickayat.checkFilePermission();

//        if (stateMic && stateFile) {
          //permission for mic is true
          openReciteFragment(surahName, ayatListsResponse.getSubAyat(), ayatListsResponse.getAyatId());

//        } else {
          //ask for permission
//          pickayat.getPermission();
//          Toast.makeText(mcontext, mcontext.getResources().getString(R
//                  .string.permission_is_needed),
//              Toast.LENGTH_SHORT).show();
//
//        }
        }
      }
    });

  }

  @Override
  public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    //for avoid fastscrolling animation
    holder.itemView.clearAnimation();
  }

  private void openReciteFragment(String surahName, String subAyat, String surahId) {
    Fragment fragment = new ReciteFragment();
    Bundle bundle = new Bundle();
    bundle.putString(Constants.AYAT_SURAHNAME, surahName);
    bundle.putString(Constants.AYAT_SUBAYAT, subAyat);
    bundle.putString(Constants.AYAT_ID, surahId);
    fragment.setArguments(bundle);
    switchContent(fragment);
  }

  private void switchContent(Fragment mFragment) {
    if (mcontext == null) {
      //if context is null return
      return;
    }
    if (mcontext instanceof MainActivity) {
      //switch fragment by calling method in MainActivity
      MainActivity activity = (MainActivity) mcontext;
      activity.switchFragmentAddBackstack(mFragment, Constants.TAG_OTHERS_FRAGMENT);
    }
  }

}