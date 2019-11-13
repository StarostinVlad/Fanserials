package com.example.fan;



import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import static com.example.fan.Video.nextSeria;
import static com.example.fan.Video.previusSeria;

public class VideoFragment extends Fragment {
    public static String uri;
    public static boolean starting = false;
    static VideoView video;
    static SeekBar video_seek;
    static TextView durationTextView;
    static TextView current_time;
    static int currentPos = 0;
    static boolean alive = true;
    static View v;
    static InputStream iss;
    static boolean end = false;
    public Button btn, Next, Prev, btnWindow;
    public ProgressBar pr;
    public ImageView top_gradient;
    public ImageView bottom_gradient;
    int duration = 0;


    public static void seturi(String uri, final int currentPos) {
        video.setVideoURI(Uri.parse(uri));
        video.seekTo(currentPos);
        //getTempFile(v.getContext(),uri);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        Log.d("sa", "onCreate fragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("sa", "onCreateView fragment");
        v = inflater.inflate(R.layout.video_fragment, container, false);
        video = (VideoView) v.findViewById(R.id.videoV);
        pr = (ProgressBar) v.findViewById(R.id.progressBar2);

        video_seek = (SeekBar) v.findViewById(R.id.seekBar);

        top_gradient = (ImageView) v.findViewById(R.id.top_gradient);
        bottom_gradient = (ImageView) v.findViewById(R.id.bottom_gradient);


        btn = (Button) v.findViewById(R.id.buttonPlay);
        Next = (Button) v.findViewById(R.id.button3);
        Prev = (Button) v.findViewById(R.id.button2);

        durationTextView = (TextView) v.findViewById(R.id.duration);
        current_time = (TextView) v.findViewById(R.id.current_time);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                @Override
//                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
//                    if (i == mediaPlayer.MEDIA_INFO_BUFFERING_START) {
//                        pr.setVisibility(View.GONE);
//                    } else if (i == mediaPlayer.MEDIA_INFO_BUFFERING_END) {
//                        pr.setVisibility(View.VISIBLE);
//                    }
//                    return false;
//                }
//            });
//        }

        v.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (starting) {
                        if (e.getRawX() > (v.getWidth() / 2)) {
                            video.seekTo(video.getCurrentPosition() + 10_000);
                        } else if (e.getRawX() < (v.getWidth() / 2)) {
                            video.seekTo(video.getCurrentPosition() - 10_000);
                        }
                        Log.d("TEST", "onDoubleTap");
                    }
                    return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (btn.getVisibility() == View.VISIBLE) {
                    hide();
                } else {
                    show();
                }
                Log.d("TEST", "Raw event: " + v.getWidth() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");

                gestureDetector.onTouchEvent(event);
                return false;
            }
        });


        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (TimeUnit.MILLISECONDS.toHours(video.getDuration()) > 0)
                    current_time.setText("0:00:00");
                else
                    current_time.setText("00:00");

                duration = mediaPlayer.getDuration();
                video_seek.setMax(duration);
                durationTextView.setText(getFormat(duration));

            }
        });

        alive = true;

        btn.setVisibility(View.GONE);
        Prev.setVisibility(View.GONE);
        Next.setVisibility(View.GONE);

//        if(savedInstanceState!=null){
//            uri = savedInstanceState.getString("current uri");
//            video.seekTo(currentPos =  savedInstanceState.getInt("current position"));
//            video.start();
//        }
        if (uri != null) {
            seturi(uri, currentPos);
            video.start();
        }


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Thread Seeker = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        long last = 0;
//                        int max = video.getDuration();
//                        video_seek.setMax(max);
//                        while (alive) {
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            currentPos = video.getCurrentPosition();
//
//                            if (currentPos - last > 1000) {
//                                last = currentPos;
//                                current_time.setText(getFormat(currentPos));
//                            }
//                            video_seek.setProgress(video.getCurrentPosition());
//                            if (video.getDuration() > 0) {
//                                max = video.getDuration();
//                            } else max = 10000;
//                            video_seek.setMax(max);
//                            Log.d("window", "cur: " + video.getCurrentPosition() + " / " + video.getDuration());
//                        }
//                    }
//                });
//                if (!Seeker.isAlive()) {
//                    Seeker.start();
//                }
//                video_seek.setMax(video.getDuration());
//
//                durationTextView.setText(getFormat(video.getDuration()));
                new VideoProgress().execute();

                if (!starting) {
                    Play();
                    starting = true;
                } else if (video.isPlaying()) {
                    Pause();
                } else {
                    Play();
                }
            }
        });
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Video.class);
                intent.putExtra("Seria", new Seria("", nextSeria, "", ""));
                getActivity().finish();
                startActivity(intent);
            }
        });
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Video.class);
                intent.putExtra("Seria", new Seria("", previusSeria, "", ""));
                getActivity().finish();
                startActivity(intent);

            }
        });
        video_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                video_seek.setMax(video.getDuration());
                if (b) {
                    alive = true;
                    Log.d("window", "time: " + i + " / " + video.getDuration());
                    video.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Log.d("sa", "onCreateView end fragment");
        return v;
    }

    void hide() {
        ActionBar actionBar = ((Video)getActivity()).getSupportActionBar();
        actionBar.hide();

        durationTextView.setVisibility(View.GONE);
        current_time.setVisibility(View.GONE);

        top_gradient.setVisibility(View.GONE);
        bottom_gradient.setVisibility(View.GONE);
//        play_pause.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
//        seek_video.setVisibility(View.GONE);
        video_seek.setVisibility(View.GONE);
//        Prevbut.setVisibility(View.GONE);
        Prev.setVisibility(View.GONE);
//        Nextbut.setVisibility(View.GONE);
        Next.setVisibility(View.GONE);
    }

    void show() {
        ActionBar actionBar = ((Video)getActivity()).getSupportActionBar();
        actionBar.show();

        durationTextView.setVisibility(View.VISIBLE);
        current_time.setVisibility(View.VISIBLE);

        top_gradient.setVisibility(View.VISIBLE);
        bottom_gradient.setVisibility(View.VISIBLE);

//        play_pause.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
//        seek_video.setVisibility(View.VISIBLE);
        video_seek.setVisibility(View.VISIBLE);
        if (nextSeria != "") Next.setVisibility(View.VISIBLE);
        if (previusSeria != "") Prev.setVisibility(View.VISIBLE);
    }

    String getFormat(long time) {
        String hms = "";
        if (TimeUnit.MILLISECONDS.toHours(video.getDuration()) > 0)
            hms = String.format("%01d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        else
            hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        return hms;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("current position", video.getCurrentPosition());
        outState.putString("current uri", uri);
        super.onSaveInstanceState(outState);
        Log.d("sa", "saved fragment");
    }

    @Override
    public void onDestroy() {
        alive = false;
        Log.d("window", "destroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        btn.setEnabled(false);
        video.seekTo(currentPos);
        btn.setEnabled(true);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!getActivity().isInMultiWindowMode())
                Pause();
        } else

            Pause();
        super.onPause();
    }

    void Pause() {
        btn.setBackground(getResources().getDrawable(R.drawable.ic_action_name));
        video.pause();
    }

    void Play() {
        btn.setBackground(getResources().getDrawable(R.drawable.ic_action_pause));
        video.start();
    }

    class VideoProgress extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            long lastPos = System.currentTimeMillis();

            do {
                if (System.currentTimeMillis() - lastPos > 200) {
                    publishProgress(currentPos);
                    lastPos = System.currentTimeMillis();
                }
            } while (currentPos <= duration && alive);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("progress", values[0] + " %");
            video_seek.setProgress(values[0]);
            currentPos = video.getCurrentPosition();
            current_time.setText(getFormat(video.getCurrentPosition()));
        }
    }
}
