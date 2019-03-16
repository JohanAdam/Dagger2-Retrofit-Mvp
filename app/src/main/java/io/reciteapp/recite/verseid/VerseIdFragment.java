package io.reciteapp.recite.verseid;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
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
import com.crashlytics.android.Crashlytics;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;
import io.reciteapp.recite.R;
import io.reciteapp.recite.data.networking.NetworkCallWrapper;
import io.reciteapp.recite.di.component.VerseIdFragmentComponent;
import io.reciteapp.recite.di.module.VerseIdModule;
import io.reciteapp.recite.main.MainActivity;
import io.reciteapp.recite.data.model.QuranSearchResultResponse;
import io.reciteapp.recite.root.App;
import io.reciteapp.recite.verseid.VerseIdContract.Presenter;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;
//TODO Translation for verse id
/**
 * A simple {@link Fragment} subclass.
 */
public class VerseIdFragment extends Fragment implements VerseIdContract.View {

  @Inject
  Presenter presenter;

  @Inject
  @Named("service")
  NetworkCallWrapper service;
  @BindView(R.id.tv_title)
  TextView tvTitle;
  @BindView(R.id.hint_text)
  TextView tvHint;
  @BindView(R.id.btn_mic)
  ImageButton btnMic;
  @BindView(R.id.demo_mpc)
  MagicProgressCircle progressBar;
  @BindView(R.id.status_text)
  TextView tvStatus;
  Unbinder unbinder;

  private MainActivity activity;
  private VerseIdFragmentComponent component;
  private SpeechRecognizer speechRecognizer;
  private boolean isListening = false;
  private Animation anim = null;
  private Animation anim_fade_in = null;

  public VerseIdFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof MainActivity) {
      this.activity = (MainActivity) context;
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_verse_id, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    getVerseIdComponent().inject(this);
    init();
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    speechRecognizer.stopListening();
    speechRecognizer.destroy();
    presenter.unSubscribe();
    isListening = false;
    stopListening();
    unbinder.unbind();
  }

  private VerseIdFragmentComponent getVerseIdComponent() {
    if (component == null) {
      component = App.getApp().getApplicationComponent()
          .newVerseIdComponent(new VerseIdModule());
    }
    return component;
  }

  private void init() {
    presenter.setView(this, service);
    setupView();
  }

  private void setupView() {
    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
    speechRecognizer.setRecognitionListener(new listener());

    tvTitle.setText(getResources().getString(R.string.msg_tap_identify_verse));
    tvStatus.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showWait() {
    if (isListening) {
      //play
      anim = AnimationUtils.loadAnimation(activity, R.anim.anim_fade_out);
      anim.setDuration(800);
      anim.setFillAfter(true);

      progressBar.startAnimation(anim);

      anim.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

          if (isListening) {
            //play
            anim_fade_in = AnimationUtils.loadAnimation(activity, R.anim.anim_fade_in);
            anim_fade_in.setDuration(800);
            anim_fade_in.setFillAfter(true);

            tvTitle.startAnimation(anim_fade_in);
            progressBar.startAnimation(anim_fade_in);

            anim_fade_in.setAnimationListener(new Animation.AnimationListener() {
              @Override
              public void onAnimationStart(Animation animation) {

              }

              @Override
              public void onAnimationEnd(Animation animation) {

                tvTitle.startAnimation(anim);
                progressBar.startAnimation(anim);
              }

              @Override
              public void onAnimationRepeat(Animation animation) {

              }
            });
          } else {
            //stopAndRelease
            tvTitle.clearAnimation();
            tvTitle.setAlpha(1f);
            progressBar.clearAnimation();
            progressBar.setAlpha(1f);
          }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
    }
  }

  @Override
  public void removeWait() {
    progressBar.clearAnimation();
    tvTitle.clearAnimation();
    if (anim != null) {
      anim.reset();
      anim.cancel();
    }

    if (anim_fade_in != null) {
      anim_fade_in.reset();
      anim_fade_in.cancel();
    }
  }

  @Override
  public void onSuccess(QuranSearchResultResponse result) {
    removeWait();
    if (result.getVerseName() == null) {
      tvStatus.setText("No match found");
    } else {
      tvHint.setVisibility(View.INVISIBLE);
      tvStatus.setVisibility(View.VISIBLE);
      tvStatus.setText(String.format("%s\n%s", result.getVerseName(), result.getAyah()));
    }
  }

  void startListening() {
    isListening = true;
    tvTitle.setVisibility(View.VISIBLE);
    tvTitle.setText(R.string.action_listening);
    tvHint.setVisibility(View.INVISIBLE);
    tvStatus.setVisibility(View.INVISIBLE);
    showWait();
  }

  void stopListening() {
    tvTitle.setVisibility(View.INVISIBLE);
    isListening = false;
  }

  @Override
  public void showErrorView(int error) {
    tvStatus.setVisibility(View.VISIBLE);
    String errorMessage;
    int colorResources = R.color.pinkish_grey;
    switch (error) {
      case SpeechRecognizer.ERROR_CLIENT:
        errorMessage = "Error Client";
        colorResources = R.color.md_red_400;
        break;
      case SpeechRecognizer.ERROR_NO_MATCH:
        errorMessage = "No Match found";
        break;
      case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
        errorMessage = "Speech Timeout";
        colorResources = R.color.md_red_400;
        break;
      case SpeechRecognizer.ERROR_NETWORK:
        errorMessage = "Network Error";
        colorResources = R.color.md_red_400;
        break;
      case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
        errorMessage = "Please enable permission first!";
        colorResources = R.color.md_red_400;
        break;
      case SpeechRecognizer.ERROR_SERVER:
        errorMessage = "Server Error";
        colorResources = R.color.md_red_400;
        break;
      default:
        errorMessage = getString(R.string.error_default);
        colorResources = R.color.md_red_400;
        break;
    }
    tvStatus.setText(errorMessage);
    tvStatus.setTextColor(ContextCompat.getColor(activity, colorResources));
  }

  private class listener implements RecognitionListener {

    @Override
    public void onReadyForSpeech(Bundle params) {
      startListening();
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
      stopListening();
    }

    @Override
    public void onError(int error) {
      Timber.e("onError error" + error);
      stopListening();
      showErrorView(error);
    }

    @Override
    public void onResults(Bundle results) {
      tvStatus.setVisibility(View.VISIBLE);
      tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.pinkish_grey));

      if (results != null) {
        Timber.d("onResult " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        assert data != null;
        Timber.d("data size " + data.size());
        Timber.d("data is " + data.get(0).toString());
        if (!data.isEmpty()) {
          tvStatus.setText(R.string.msg_analyzing);
          presenter.sendResult(data.get(0).toString());
        } else {
          tvStatus.setText(R.string.msg_result_empty);
        }
      } else {
        tvStatus.setText(R.string.msg_result_empty);
      }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
  }

  @Override
  public void logout() {
    activity.logout();
  }

  @OnClick(R.id.btn_mic)
  public void onClick() {

    //TODO Check permission

    if (!isListening) {
      Timber.d("!isListening");

      try {
        //isListening is false, so start listen
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-JO");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "io.recite.audiorecognitiontest");
        intent.putExtra(RecognizerIntent
            .EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000);
        intent.putExtra(RecognizerIntent
            .EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 10000);
        // value to wait
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,
            1);  // 1 is the maximum number of results to be returned.
        speechRecognizer.startListening(intent);
      } catch (SecurityException e) {
        e.printStackTrace();
        Crashlytics.logException(e);
        Crashlytics.log("Exception while using verse id, because of google typing input is disable");
        Toast.makeText(activity, "Google voice typing must be enabled!", Toast.LENGTH_LONG).show();
      }

    } else {
      Timber.d("isListening");
      speechRecognizer.stopListening();
    }

  }
}
