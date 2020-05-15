package com.example.karaokeparty.view;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.karaokeparty.R;
import com.example.karaokeparty.model.SingerModel;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CreateSingerDialog extends DialogFragment {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String SINGER_LISTENER = "singerListenerTAG";
    private static final int WRITE_REQUEST_CODE = 1;

    private EditText nameOfSingerEt, nameOfSongEt, numberOfAlbumEt, lyricsEt, urlEt;
    private ImageView mPicture;
    private File mPictureFile;
    private boolean photoTaken = false;


    private CreateSingerInterface listener;

    static CreateSingerDialog newInstance(CreateSingerInterface listener) {

        Bundle args = new Bundle();
        CreateSingerDialog fragment = new CreateSingerDialog();

        args.putParcelable(SINGER_LISTENER, listener);


        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.dialog_create_singer, container, false);

        if (getArguments() != null) {
            listener = getArguments().getParcelable(SINGER_LISTENER);
        }

        // References
        nameOfSingerEt = view.findViewById(R.id.etNameOfSinger);
        nameOfSongEt = view.findViewById(R.id.etNameOfSong);
        numberOfAlbumEt = view.findViewById(R.id.etAlbum);
        lyricsEt = view.findViewById(R.id.etLyrics);
        lyricsEt.setSingleLine(false);
        lyricsEt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        lyricsEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        lyricsEt.setMaxLines(10);
        urlEt = view.findViewById(R.id.etYoutube);
        mPicture = view.findViewById(R.id.ivPhoto);

        /*// First init
        if (getActivity() != null) {
            mPicturePath = Uri.parse("android.resource://" + getActivity().getPackageName() +
                    "/" + R.drawable.image_singer_not_loaded).toString();
        }*/


        Button closeBtn = view.findViewById(R.id.btnBackSong);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSingerDialog.this.dismiss();
            }
        });

        Button finishBtn = view.findViewById(R.id.btnAddSong);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String singer = nameOfSingerEt.getText().toString();
                String song = nameOfSongEt.getText().toString();
                String album = numberOfAlbumEt.getText().toString();
                String lyrics = lyricsEt.getText().toString();
                String url = urlEt.getText().toString();


                if (singer.isEmpty() || song.isEmpty() || album.isEmpty() || lyrics.isEmpty()  || url.isEmpty() || !photoTaken) {
                    Toast.makeText(getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                } else {
                    if(photoTaken) {
                        SingerModel singerModel = new SingerModel(singer, song, album, lyrics, mPictureFile.getAbsolutePath(),url);
                        listener.insertNewSinger(singerModel);
                        CreateSingerDialog.this.dismiss();
                    }

                }

            }
        });

        Button uploadImageBtn = view.findViewById(R.id.btnCamera);
        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, getUpdatedFileUri());
                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
            }
        });

        lyricsEt.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                if (lyricsEt.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            }
        });


        getPermission();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && getActivity() != null && dialog.getWindow() != null) {
            // Retrieve display dimensions
            Rect displayRectangle = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

            dialog.getWindow().setLayout((int)(displayRectangle.width() * 0.9f), (int)(displayRectangle.height() * 0.95f));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = mPictureFile.getPath();
            Bitmap image = getBitmap(path);
            mPicture.setImageBitmap(image);
            photoTaken = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_REQUEST_CODE && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), R.string.denied_permission, Toast.LENGTH_SHORT).show();
        }
    }



    private Uri getUpdatedFileUri() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String fileName = "IMG_" + formatter.format(calendar.getTime()) + ".jpg";

        mPictureFile =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);

        return Uri.fromFile(mPictureFile);
    }


    private void getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity() != null) {
                int writePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writePermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
                }
            }
        }
    }

    public Bitmap getBitmap(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(path,options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    interface CreateSingerInterface extends Parcelable {
        void insertNewSinger(SingerModel singerModel);

        @Override
        int describeContents();

        @Override
        void writeToParcel(Parcel dest, int flags);
    }
}
