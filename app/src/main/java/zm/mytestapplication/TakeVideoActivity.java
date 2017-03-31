package zm.mytestapplication;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class TakeVideoActivity extends AppCompatActivity {
    static final int REQUEST_VIDEO_CAPTURE = 103;
    private VideoView mVideoView;
    private TextView mTextView;
    private MediaController mediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_video);
        mVideoView = (VideoView) findViewById(R.id.videoView_preview);
        mTextView = (TextView) findViewById(R.id.textView_log_video);
        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(TakeVideoActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            mTextView.setText(videoUri.toString());
            try {
                //set the media controller in the VideoView
                mVideoView.setMediaController(mediaControls);
                //set the uri of the video to be played
                mVideoView.setVideoURI(videoUri);
                mVideoView.start();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void dispatchTakeVideoIntent(View view) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
}
