package com.example.fan;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class top_layout extends Service {
    private RelativeLayout rootView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams topParams;
    VideoView topVideo;
    private Button btn;
    static boolean starting=false;

    public top_layout() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String uri=intent.getStringExtra("currentUri");
        if(uri!=null) {
            topVideo.setVideoURI(Uri.parse(uri));
            topVideo.seekTo(intent.getIntExtra("currentPos", 0));
            topVideo.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void Pause(){
        btn.setBackground(getResources().getDrawable(R.drawable.play));
        topVideo.pause();
        Log.d("window","paused");
    }
    void Play(){
        btn.setBackground(getResources().getDrawable(R.drawable.pause));
        topVideo.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        topParams = new WindowManager.LayoutParams(
                200, // Ширина экрана
                200, // Высота экрана
                WindowManager.LayoutParams.TYPE_PHONE, // Говорим, что приложение будет поверх других. В поздних API > 26, данный флаг перенесен на TYPE_APPLICATION_OVERLAY
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
                PixelFormat.TRANSPARENT); // Само окно прозрачное
        Log.d("window","started");
        // Задаем позиции для нашего Layout
        topParams.gravity = Gravity.TOP|Gravity.END;
        topParams.x = 0;
        topParams.y = 0;

        // Отображаем наш Layout
        rootView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.top_layout, null);
        windowManager.addView(rootView, topParams);
        rootView.findViewById(R.id.topVideo).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    // Обрабатываем позицию касания и обноваляем размер Layout'а
                    topParams.height = (int) motionEvent.getRawY();
                    windowManager.updateViewLayout( rootView, topParams);
                }
                return true;
            }
        });
        topVideo= (VideoView) rootView.findViewById(R.id.topVideo);

        btn= (Button) rootView.findViewById(R.id.top_PlayPause);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!starting) {
                    topVideo.start();
                    starting=true;
                }
                if(topVideo.isPlaying()){
                    Pause();
                }
                else {
                    Play();
                }
            }
        });
    }
}
