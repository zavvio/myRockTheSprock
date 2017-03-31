package zm.mytestapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TakePictureActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 102;
    private TextView tv;
    private File defaultDir;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        tv = (TextView) findViewById(R.id.textView_log);
        defaultDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "zm");
        defaultDir.mkdirs();
        mImageView = (ImageView) findViewById(R.id.imageViewThumb);
        checkExternalMedia();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            try {
//                File f = new File(mCurrentPhotoPath);
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(f));
////                bitmap = crupAndScale(bitmap, 300); // if you mind scaling
//                mImageView.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                tv.append("\nFileNotFoundException.");
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                tv.append("\nIOException.");
//                e.printStackTrace();
//            }
            setPic();
            galleryAddPic();
        }
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                tv.append("\n" + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "zm.mytestapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void checkExternalMedia(){
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWritable;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWritable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWritable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWritable = false;
        }
        if (!mExternalStorageAvailable || !mExternalStorageWritable) {
            tv.append("\nExternal Media: readable=" + mExternalStorageAvailable
                    + " writable=" + mExternalStorageWritable);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "testApp_" + timeStamp + ".jpg";
        File image = new File(defaultDir, imageFileName);
        image.createNewFile();
        mCurrentPhotoPath = image.getPath();

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        tv.append("\nImage absolute path: " + mCurrentPhotoPath);
        return image;
    }

    public void deleteAllTempPhotos(View view) {
        File[] paths;
        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String s = name.substring(0, Math.min(name.length(), 7));
                if(s.equals("testApp")) {
                    return true;
                }
                return false;
            }
        };
        // returns pathnames for files and directory
        paths = defaultDir.listFiles(fileNameFilter);
        // for each pathname in pathname array
        for(File path:paths) {
            // prints file and directory paths
            path.delete();
            tv.append("\nRemoved: " + path.getName());
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    // For reference/example purpose only, no actual use
    private void writeToSDFile() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        tv.append("\nExternal file system root: " + storageDir);
        tv.append("\nExternal file system root (absolute path): " + storageDir.getAbsolutePath());
        File file = new File(storageDir, "testApp.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("Hi , How are you");
            pw.println("Hello");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Log.i(TAG, "******* File not found. Did you" +
//                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv.append("\n\nFile written to "+file);
    }
}
