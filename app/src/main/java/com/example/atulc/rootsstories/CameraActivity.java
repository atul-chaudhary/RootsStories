package com.example.atulc.rootsstories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isaacudy.kfilter.BaseKfilter;
import com.isaacudy.kfilter.KfilterView;
import com.isaacudy.kfilter.filters.GrayscaleFilter;
import com.isaacudy.kfilter.filters.PosterizeFilter;
import com.isaacudy.kfilter.filters.SepiaFilter;
import com.isaacudy.kfilter.filters.WarmFilter;
import com.isaacudy.kfilter.filters.WobbleFilter;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class CameraActivity extends AppCompatActivity {

    CameraView camera;
    String path;
    public KfilterView characterImage;
    Bitmap bitmapCamera;
    int camera_i = 0;
    int flash_j = 0;

    //String timeStamp;
    BaseKfilter baseKfilter = new BaseKfilter();
    GrayscaleFilter grayscaleFilter = new GrayscaleFilter();
    SepiaFilter sepiaFilter = new SepiaFilter();
    PosterizeFilter posterizeFilter = new PosterizeFilter();
    WarmFilter warmFilter = new WarmFilter();
    WobbleFilter wobbleFilter = new WobbleFilter();
    ArrayList arrayList = new ArrayList();

    PropertiesBSFragment mPropertiesBSFragment;
    PhotoEditor mPhotoEditor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        camera = (CameraView) findViewById(R.id.camera);
        characterImage = (KfilterView) findViewById(R.id.kfilterView);
        final ImageView clickButton = (ImageView) findViewById(R.id.click);
        final ImageView nextArrow = (ImageView) findViewById(R.id.next_arrow_white);
        final ImageView backArrow = (ImageView) findViewById(R.id.back_arrow_white);
        final TextView moments_text = (TextView) findViewById(R.id.moments_text);
        //functionality not decided
        ImageView cancel = (ImageView) findViewById(R.id.cancel);

        final ImageView info = (ImageView) findViewById(R.id.info_func);
        final ImageView save = (ImageView) findViewById(R.id.save_alt);
        final ImageView flash_func = (ImageView) findViewById(R.id.flash_funct);
        final ImageView camera_change = (ImageView) findViewById(R.id.change_camera);

        //edit layout button and images
        final RelativeLayout main_layout_for_edit = (RelativeLayout) findViewById(R.id.main_layout_for_edit);
        ImageView undo = (ImageView) findViewById(R.id.undo);
        ImageView draw_edit = (ImageView) findViewById(R.id.paint_brush);
        TextView text_edit = (TextView) findViewById(R.id.text_for_photo);
        ImageView emogi_edit = (ImageView) findViewById(R.id.label);


        //editor functionality
        final PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPropertiesBSFragment = new PropertiesBSFragment();
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();




        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        //camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!
        camera.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE); // Long tap to shoot!
        camera.setLifecycleOwner(this);

        //adding the filters to the array list
        arrayList.add(baseKfilter);
        arrayList.add(sepiaFilter);
        arrayList.add(grayscaleFilter);
        arrayList.add(posterizeFilter);
        arrayList.add(warmFilter);
        arrayList.add(wobbleFilter);

        //total camera functionality
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.capturePicture();
            }
        });

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
            }

            @Override
            public void onPictureTaken(byte[] data) {
                super.onPictureTaken(data);

                int size = data.length;
                bitmapCamera = BitmapFactory.decodeByteArray(data, 0, data.length);
                characterImage.post(new Runnable() {
                    @Override
                    public void run() {

                        storeImage(getResizedBitmap(bitmapCamera, 1000));
                        characterImage.setContentPath(path);
                        characterImage.setVisibility(View.VISIBLE);
                        characterImage.setFilters(arrayList);

                    }
                });
                //view to be gone
                camera.setVisibility(View.GONE);
                clickButton.setVisibility(View.GONE);
                camera_change.setVisibility(View.GONE);
                flash_func.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                //view to be shown
                backArrow.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                //edit view layout
                main_layout_for_edit.setVisibility(View.VISIBLE);


            }

        });

        //back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //relase the resourses of the kfilter lib
                characterImage.releaseRenderingResources();
                characterImage.setVisibility(View.GONE);
                camera.setVisibility(View.VISIBLE);
                clickButton.setVisibility(View.VISIBLE);
                flash_func.setVisibility(View.VISIBLE);
                camera_change.setVisibility(View.VISIBLE);
                moments_text.setVisibility(View.VISIBLE);
                info.setVisibility(View.VISIBLE);
                clickButton.setEnabled(true);
                backArrow.setVisibility(View.GONE);
                nextArrow.setVisibility(View.GONE);
                save.setVisibility(View.GONE);

            }
        });

        //cancel button functionality
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //changing of camera functionality
        camera_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                camera_i = camera_i + 1;
                Log.d("atulLog", ">>>" + camera_i);
                if (camera_i == 1) {
                    camera.setFacing(Facing.FRONT);
                    camera_change.setImageResource(R.drawable.camera_back);
                }
                if (camera_i == 2) {
                    camera.setFacing(Facing.BACK);
                    camera_change.setImageResource(R.drawable.camera_front);
                    camera_i = 0;
                }
            }
        });

        flash_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flash_j = flash_j + 1;
                if (flash_j == 1) {
                    flash_func.setImageResource(R.drawable.flash_on);
                    camera.setFlash(Flash.ON);
                }
                if (flash_j == 2) {
                    flash_func.setImageResource(R.drawable.flash_auto);
                    camera.setFlash(Flash.AUTO);
                }
                if (flash_j == 3) {
                    flash_func.setImageResource(R.drawable.flash_off);
                    camera.setFlash(Flash.OFF);
                    flash_j = 0;
                }

            }
        });

        draw_edit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                characterImage.setGestureEnable(false);
                mPhotoEditor.setBrushDrawingMode(true);
                //mPhotoEditor.setBrushSize(30);
                //mPhotoEditor.setBrushColor(getColor(R.color.blue_color_picker));
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
            }
        });

        text_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(CameraActivity.this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);

                    }
                });


            }
        });

    }

    //method for storing images
    public void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
    }

    //method for retreving the file stored as image
    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getApplicationContext().getPackageName()
                + "/Files");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String mImageName = "img-" + timeStamp + ".jpg";
        path = mediaStorageDir.getPath() + File.separator + mImageName;
        File mediaFile = new File(path);


        return mediaFile;
    }

    //compression of images
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


}