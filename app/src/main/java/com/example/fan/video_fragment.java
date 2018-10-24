package com.example.fan;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

public class video_fragment extends Fragment{
    static VideoView video;
    Button btn,Next,Prev,btnWindow;
    ProgressBar pr;
    static SeekBar video_seek;
    public static String uri;
    static int currentPos=0;
    public static boolean starting=false;
    static boolean alive=true;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //setRetainInstance(true);

        Log.d("sa","onCreate fragment");
        super.onCreate(savedInstanceState);
    }
    static View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        v = inflater.inflate(R.layout.video_fragment, container, false);
        video = (VideoView) v.findViewById(R.id.videoV);
        pr = (ProgressBar) v.findViewById(R.id.progressBar2);
        video_seek=(SeekBar)v.findViewById(R.id.seekBar);
        btn = (Button) v.findViewById(R.id.button);
        Next= (Button) v.findViewById(R.id.button3);
        Prev= (Button) v.findViewById(R.id.button2);
        alive=true;
        if(savedInstanceState!=null){
            uri = savedInstanceState.getString("current uri");
            video.seekTo(currentPos =  savedInstanceState.getInt("current position"));
            video.start();
        }



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread Seeker=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int max = video.getDuration();
                        video_seek.setMax(max);
                        while (alive) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            currentPos = video.getCurrentPosition();
                            video_seek.setProgress(video.getCurrentPosition());
                            if (video.getDuration() > 0)
                                max = video.getDuration();
                            else max = 10000;
                            video_seek.setMax(max);
                            Log.d("window", "cur: " + video.getCurrentPosition() + " / " + video.getDuration());
                        }
                    }
                });
                if(!Seeker.isAlive()){
                    Seeker.start();
                }
                video_seek.setMax(video.getDuration());
                if(!starting) {
                    video.start();
                    starting=true;
                }
                if(video.isPlaying()){
                    Pause();
                }
                else {
                    Play();
                }
            }
        });
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Video.class);
                intent.putExtra("Seria",new Seria("",Video.nextSeria,"",""));
                getActivity().finish();
                startActivity(intent);
            }
        });
        Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Video.class);
                intent.putExtra("Seria",new Seria("",Video.previusSeria,"",""));
                getActivity().finish();
                startActivity(intent);

            }
        });
        video_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                video_seek.setMax(video.getDuration());
                if(b) {
                    alive=true;
                    Log.d("window","time: "+i+" / "+video.getDuration());
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
        return v;
    }
    public static File getTempFile(Context context, String url) {
        File file;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
            Log.d("window","file saved");
            return file;
            }
        catch (IOException e) {
            return null;   // Error while creating file
            }
        }


    public static void seturi(String uri, final int currentPos){
        video.setVideoURI(Uri.parse(uri));
        video.seekTo(currentPos);
        //getTempFile(v.getContext(),uri);
    }
    static InputStream iss;
    static boolean end=false;
    public static void setSub(final String subtitleUri) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count;
                try {
                    URL url = new URL(subtitleUri);
                    InputStream is = url.openStream();
                    File f = getExternalFile();
                    FileOutputStream fos = new FileOutputStream(f);
                    byte data[] = new byte[1024];
                    while ((count = is.read(data)) != -1) {
                        fos.write(data, 0, count);
                    }
                    is.close();
                    fos.close();
                }catch (Exception e){
                    e.printStackTrace();
                    end=true;
                }
                Log.d("subs", "file downloaded");
                end=true;
            }
        }).start();
        while(!end)alive=true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                MediaController mc=new MediaController(v.getContext());
                video.setMediaController(mc);
                video.addSubtitleSource(getSubtitleSource(v.getContext().getExternalFilesDir(null)
                        .getPath() + "/sample.vtt"), MediaFormat.createSubtitleFormat(MediaFormat.MIMETYPE_TEXT_VTT, Locale.ENGLISH.getLanguage()));
                Log.d("subs", "subs added");
            }
    }
    public static File getExternalFile() {
        File srt = null;
        try {
                srt = new File(v.getContext().getExternalFilesDir(null)
                        .getPath() + "/sample.vtt");
            srt.createNewFile();
            return srt;
        } catch (Exception e) {
            Log.e("subs", "exception in file creation");
        }
        return null;
    }

    private static InputStream getSubtitleSource(String filepath) {
        InputStream ins = null;
        File file = new File(filepath);
        if (file.exists() == false)
        {
            Log.e("subs","no close caption file " + filepath);
            return null;
        }
        FileInputStream fins = null;
        try {
            fins = new FileInputStream(file);
        }catch (Exception e) {
            Log.e("subs","exception " + e);
        }
        ins = (InputStream)fins;
        return ins;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("current position",video.getCurrentPosition() );
        outState.putString("current uri", uri);
        super.onSaveInstanceState(outState);
        Log.d("sa", "saved fragment");
    }

    @Override
    public void onDestroy() {
        alive=false;
        Log.d("window","destroy");
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
        Pause();
        super.onPause();
    }

    void Pause(){
        btn.setBackground(getResources().getDrawable(R.drawable.play));
        video.pause();
    }
    void Play(){
        btn.setBackground(getResources().getDrawable(R.drawable.pause));
        video.start();
    }
}
