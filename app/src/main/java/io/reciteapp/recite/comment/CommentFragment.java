package io.reciteapp.recite.comment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import io.reciteapp.recite.R;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.utils.RecitePlayerContract.playerErrorStateCallback;
import io.reciteapp.recite.utils.RecitePlayerContract.playerStateCallback;
import io.reciteapp.recite.utils.Rplayer;
import io.reciteapp.recite.utils.Utils;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment implements OnClickListener {

  @BindView(R.id.tv_duration)
  TextView tvDuration;
  @BindView(R.id.tv_comment)
  TextView tvComment;
  @BindView(R.id.btn_your_recital)
  ImageButton btnYourRecital;
  @BindView(R.id.tv_your_recital)
  TextView tvYourRecital;
  @BindView(R.id.tv_correction)
  TextView tvCorrection;
  @BindView(R.id.btn_corection)
  ImageButton btnCorection;
  Unbinder unbinder;

  private MainActivity activity;
  private String durationString;

  //User currently audio duration in millisecond
  private long duration;
  private String comment;
  private String audioUri;
  private String audioUriCorrection;
  private Rplayer playerYourRecital;
  private Rplayer playerCorrection;
  private boolean isErrorUser = false;
  private boolean isErrorCs = false;

  public CommentFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      activity = (MainActivity) context;
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View root_view = inflater.inflate(R.layout.fragment_comment, container, false);
    unbinder = ButterKnife.bind(this, root_view);

    //Load all data from History Detail
    Bundle bundle = this.getArguments();
    if (bundle != null) {

      durationString = bundle.getString("duration","0:00");
      comment = bundle.getString("comment",getResources().getString(R.string.msg_voice_note_template));
      audioUri = bundle.getString("audiouriuser");
      audioUriCorrection = bundle.getString("audiouricorrection");

      Timber.d("durationString " + durationString + " comment " + comment + " audiouriuser " + audioUri + " audiouricorrection "  +audioUriCorrection);
      setupView();
    } else {
      //if no data from History Detail, exit
      activity.onBackPressed();
    }

    return root_view;
  }

  private void setupView() {

    tvDuration.setText(durationString);
    tvComment.setText(comment);

    btnYourRecital.setOnClickListener(this);
    btnCorection.setOnClickListener(this);

    //Convert time format MM:SS string to millisecond
    this.duration = TimeUnit.SECONDS.toMillis(new Utils().convertTimerStringFormatToSeconds(durationString));
    Timber.d("durationInMillis is %s", duration);
//    String[] allValue = durationString.split(":");
//    String duration = convertTimeToMilli(allValue);
//    this.duration = Long.parseLong(duration);

    if (!TextUtils.isEmpty(audioUri)) {
      //If there is audioUri, enable user play button and initialized player
      //Initialized player
      btnYourRecital.setEnabled(true);
      initUserPlayer();
    } else {
      //if there is not audioUri, disable button
      btnYourRecital.setEnabled(false);
    }

    if (!TextUtils.isEmpty(audioUriCorrection) && !audioUriCorrection.equals("null")) {
      //If there is audioUriCorrection, enable Correction play button and initialized player correction
      //Initialized player
      btnCorection.setEnabled(true);
      initCorrectionPlayer();
    } else {
      //if there is not audioUriCorrection, disable button Correction
      btnCorection.setEnabled(false);
      btnCorection.setBackground(getResources().getDrawable(R.drawable.correction_pressed));
    }

  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();

    if (playerYourRecital != null) {
      playerYourRecital.stopAndRelease();
    }

    if (playerCorrection != null) {
      playerCorrection.stopAndRelease();
    }

  }

  private void initUserPlayer() {
    if (!TextUtils.isEmpty(audioUri)) {
      isErrorUser = false;
      playerYourRecital = new Rplayer();
      playerYourRecital.initPlayer(activity, audioUri,
          new playerStateCallback() {
            @Override
            public void state(boolean playWhenReady, int state) {
              switch (state) {
                case Player.STATE_IDLE:
                  Timber.d("STATE IDLE");
                  if (isAdded()) {
                    if (btnYourRecital != null) {
                      isErrorUser = true;
                      btnYourRecital.clearAnimation();
                      btnYourRecital.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_white_24dp));
                    }
                  }
                  break;
                case Player.STATE_BUFFERING:
                  Timber.d("STATE BUFFERING");
                  if (btnYourRecital != null) {
                    btnYourRecital.setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh));
                    Animation rotateAnim = AnimationUtils.loadAnimation(activity, R.anim.anim_rotate);
                    rotateAnim.setRepeatCount(Animation.INFINITE);
                    btnYourRecital.startAnimation(rotateAnim);
                  }
                  break;
                case Player.STATE_READY:
                  Timber.d("STATE READY");
                  if (btnYourRecital != null) {
                    btnYourRecital.clearAnimation();
                    if (playWhenReady) {
                      btnYourRecital.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_24dp));
                    } else {
                      btnYourRecital.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                    }
                  }
                  break;
                case Player.STATE_ENDED:
                  Timber.d("STATE ENDED");
                  if (btnYourRecital != null) {
                    btnYourRecital.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_24dp));
                    playerYourRecital.seekTo(CommentFragment.this.duration);
                  }
                  break;
              }
            }
          },
          new playerErrorStateCallback() {
            @Override
            public void error_state(ExoPlaybackException exception) {
              switch (exception.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                  Timber.d("TYPE SOURCE");
                  Toast.makeText(activity, getResources().getString(R.string.error_stream_audio_general), Toast.LENGTH_SHORT).show();
                  break;

                case ExoPlaybackException.TYPE_RENDERER:
                  Timber.d("TYPE_RENDERER: %s", exception.getRendererException().getMessage());
                  break;

                case ExoPlaybackException.TYPE_UNEXPECTED:
                  Timber.d("TYPE_UNEXPECTED: %s", exception.getUnexpectedException().getMessage());
                  break;
              }
            }
          });
    }
  }

  private void initCorrectionPlayer() {
    if (!TextUtils.isEmpty(audioUriCorrection)) {
      isErrorCs = false;
      playerCorrection = new Rplayer();
      playerCorrection.initPlayer(activity, audioUriCorrection,
          new playerStateCallback() {
            @Override
            public void state(boolean playWhenReady, int state) {
              switch (state) {
                case Player.STATE_IDLE:
                  Timber.d("STATE IDLE");
                  if (btnCorection != null) {
                    isErrorCs = true;
                    btnCorection.clearAnimation();
                    btnCorection.setBackground(activity.getResources().getDrawable(R.drawable.correction_pressed));
                  }
                  break;
                case Player.STATE_BUFFERING:
                  Timber.d("STATE BUFFERING");
                  if (btnCorection != null) {
                    Animation rotate_anim = AnimationUtils.loadAnimation(activity, R.anim.fade_in_out_loop);
                    rotate_anim.setRepeatCount(-1);
                    rotate_anim.setRepeatMode(2);
                    btnCorection.startAnimation(rotate_anim);
                  }
                  break;
                case Player.STATE_READY:
                  Timber.d("STATE READY");
                  if (btnCorection != null) {
                    btnCorection.clearAnimation();
                    if (playWhenReady) {
                      btnCorection.setBackground(getResources().getDrawable(R.drawable.correction_pressed));
                    } else {
                      btnCorection.setBackground(getResources().getDrawable(R.drawable.correction));
                    }
                  }
                  break;
                case Player.STATE_ENDED:
                  Timber.d("STATE ENDED");
                  if (btnCorection != null) {
                    btnCorection.setBackground(getResources().getDrawable(R.drawable.correction));
                    playerCorrection.seekTo(0);
                  }
                  break;
              }
            }
          },
          new playerErrorStateCallback() {
            @Override
            public void error_state(ExoPlaybackException exception) {
              switch (exception.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                  Timber.d("TYPE SOURCE");
                  Toast.makeText(activity, getResources().getString(R.string.error_stream_audio_general), Toast.LENGTH_SHORT).show();
                  break;

                case ExoPlaybackException.TYPE_RENDERER:
                  Timber.d("TYPE_RENDERER: %s", exception.getRendererException().getMessage());
                  break;

                case ExoPlaybackException.TYPE_UNEXPECTED:
                  Timber.d("TYPE_UNEXPECTED: %s", exception.getUnexpectedException().getMessage());
                  break;
              }
            }
          });
    }
  }

  @OnClick({R.id.btn_your_recital, R.id.btn_corection})
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_your_recital:

        if (!TextUtils.isEmpty(durationString)) {
          Timber.d("durationString in milli is %s", duration);
          Timber.d("audioUriUser is %s", audioUri);

          if (!TextUtils.isEmpty(audioUri)) {
            if (playerYourRecital != null) {
              if (isErrorUser) {
                //if true, init back
                initUserPlayer();
              } else {
                // false, proceed
                if (playerYourRecital.checkPlaying()) {
                  playerYourRecital.pause();
                } else {
                  playerYourRecital.seekTo(duration);
                  Timber.d("Seek to duration %s", duration);
                  playerYourRecital.play();
                }
              }
            }
          } else {
            Timber.e("audioUri is null");
          }
        } else {
          Timber.e("durationString is null");
        }

        break;
      case R.id.btn_corection:
        Timber.d("audioUriCorrection is %s", audioUriCorrection);

        if (!TextUtils.isEmpty(audioUriCorrection)) {
          if (playerCorrection != null) {
            if (isErrorCs) {
              //if true, init back
              initCorrectionPlayer();
            } else {
              // false, proceed
              if (playerCorrection.checkPlaying()) {
                playerCorrection.pause();
                playerCorrection.seekTo(0);
              } else {
                playerCorrection.play();
              }
            }
          }
        } else {
          Timber.e("audioUriCorrection is null");
        }
        break;
    }
  }
}
