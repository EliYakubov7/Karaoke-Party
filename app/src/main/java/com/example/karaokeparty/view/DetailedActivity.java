package com.example.karaokeparty.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.karaokeparty.R;
import com.example.karaokeparty.model.SingerModel;

import com.example.karaokeparty.youtube.PlayerConfig;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


import static com.example.karaokeparty.view.MainActivity.DETAILED_SINGER_TAG;

@SuppressLint("Registered")
public class DetailedActivity extends YouTubeBaseActivity /*implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener*/{


    YouTubePlayerView youTubePlayerView;
    Button play;
    YouTubePlayer.OnInitializedListener onInitializedListener;


//    private ImageButton btn_play_pause;
//    private SeekBar seekBar;
//    private TextView textView;
//
//    private MediaPlayer mediaPlayer;
//    private int mediaFileLength;
//    private int realtimeLength;
//    final Handler handler = new Handler();

//    VusikView v;

//    int x;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        initViews();

        youTubePlayerView = findViewById(R.id.youtube_player);
        play = findViewById(R.id.play_video_button);

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                SingerModel singerModel = (SingerModel) getIntent().getSerializableExtra(DETAILED_SINGER_TAG);
                assert singerModel != null;
                youTubePlayer.loadVideo(singerModel.getUrlYoutube());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                youTubePlayerView.initialize(PlayerConfig.MY_API_KEY,onInitializedListener);
            }
        });

    }

        //        x=-1;
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(x<0){
//                    v.stopNotesFall();
//                }
//                else
//                    v.startNotesFall();
//                x=x*-1;
//            }
//        });


//        v = findViewById(R.id.vusik);
//        int[]  myImageList = new int[]{R.drawable.note1,R.drawable.note2,R.drawable.note4};
//        v.setImages(myImageList).start();
//        v.startNotesFall();

//        seekBar = findViewById(R.id.seekbar);
//        seekBar.setMax(99); // 100% (0~99)
//        seekBar.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(mediaPlayer.isPlaying())
//                {
//                    SeekBar seekBar = (SeekBar)v;
//                    int playPosition = (mediaFileLength/100)*seekBar.getProgress();
//                    mediaPlayer.seekTo(playPosition);
//                }
//                return false;
//            }
//        });
//
//        textView = findViewById(R.id.textTimer);
//
//        btn_play_pause = findViewById(R.id.btn_play_pause);
//        btn_play_pause.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                final ProgressDialog mDialog = new ProgressDialog(DetailedActivity.this);
//
//                @SuppressLint("StaticFieldLeak") AsyncTask<String,String,String> mp3Play = new AsyncTask<String, String, String>() {
//
//                    @Override
//                    protected void onPreExecute() {
//                        mDialog.setMessage("Please wait");
//                        mDialog.show();
//                    }
//
//                    @Override
//                    protected String doInBackground(String... params) {
//                        try{
//                            mediaPlayer.setDataSource(params[0]);
//                            mediaPlayer.prepare();
//                        }
//                        catch (Exception ignored)
//                        {
//
//                        }
//                        return "";
//                    }
//
//                    @Override
//                    protected void onPostExecute(String s) {
//                        mediaFileLength = mediaPlayer.getDuration();
//                        realtimeLength = mediaFileLength;
//                        if(!mediaPlayer.isPlaying())
//                        {
//                            mediaPlayer.start();
//                            btn_play_pause.setImageResource(R.drawable.ic_pause);
//                        }
//                        else
//                        {
//                            mediaPlayer.pause();
//                            btn_play_pause.setImageResource(R.drawable.ic_play);
//                        }
//
//                        updateSeekBar();
//                        mDialog.dismiss();
//                    }
//                };
//                SingerModel singerModel = (SingerModel) getIntent().getSerializableExtra(DETAILED_SINGER_TAG);
//                assert singerModel != null;
//                mp3Play.execute(singerModel.getMp3()); // direct link mp3 file
//            }
//        });
//
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        mediaPlayer.setOnCompletionListener(this);
//
//
//    }

    private void initViews() {
        TextView nameOfSingerTv = findViewById(R.id.tvFillNameOfSinger);
        TextView nameOfSongTv = findViewById(R.id.tvFillNameOfSong);
        TextView numberOfAlbumTv = findViewById(R.id.tvFillAlbum);
        TextView lyricsTv = findViewById(R.id.tvFillLyrics);
        //ImageButton MP3Tv = findViewById(R.id.btn_play_pause);

        ImageView singerIv = findViewById(R.id.detailed_cover_iv);

        Button backButton = findViewById(R.id.detailed_back_btn);

        SingerModel singerModel = (SingerModel) getIntent().getSerializableExtra(DETAILED_SINGER_TAG);

        assert singerModel != null;
        nameOfSingerTv.setText(singerModel.getNameOfSinger());
        nameOfSongTv.setText(singerModel.getNameOfSong());
        numberOfAlbumTv.setText(singerModel.getAlbum());
        lyricsTv.setText(singerModel.getLyrics());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    private void updateSeekBar() {
//        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaFileLength)*100));
//        if(mediaPlayer.isPlaying())
//        {
//            Runnable updater = new Runnable() {
//                @SuppressLint("DefaultLocale")
//                @Override
//                public void run() {
//                    updateSeekBar();
//                    realtimeLength-=1000; // declare 1 second
//                    textView.setText(String.format("%d:%d",TimeUnit.MILLISECONDS.toMinutes(realtimeLength),
//                            TimeUnit.MILLISECONDS.toSeconds(realtimeLength) -
//                                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realtimeLength))));
//
//                }
//
//            };
//            handler.postDelayed(updater,1000); // 1 second
//        }
//    }
//
//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        seekBar.setSecondaryProgress(percent);
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        btn_play_pause.setImageResource(R.drawable.ic_play);
//    }
}
