package io.reciteapp.recite.customview;

import android.os.SystemClock;
import android.view.View;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class DebouncedOnClickListener implements View.OnClickListener {

  private final long minimumInterval;
  private final Map<View, Long> lastClickMap;

  /**
   * The one and only constructor
   *
   * @param minimumIntervalMsec The minimum allowed time between clicks - any click sooner than this
   * after a previous click will be rejected
   */
  public DebouncedOnClickListener(long minimumIntervalMsec) {
    this.minimumInterval = minimumIntervalMsec;
    this.lastClickMap = new WeakHashMap<>();
  }

  /**
   * Implement this in your subclass instead of onClick
   *
   * @param v The view that was clicked
   */
  public abstract void onDebouncedClick(View v);

  @Override
  public void onClick(View clickedView) {
    Long previousClickTimestamp = lastClickMap.get(clickedView);
    long currentTimestamp = SystemClock.uptimeMillis();

    lastClickMap.put(clickedView, currentTimestamp);
    if (previousClickTimestamp == null
        || Math.abs(currentTimestamp - previousClickTimestamp) > minimumInterval) {
      onDebouncedClick(clickedView);
    }
  }
}
