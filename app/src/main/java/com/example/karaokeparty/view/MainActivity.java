package com.example.karaokeparty.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.karaokeparty.model.SingerModel;
import com.example.karaokeparty.viewmodel.SingerViewModel;
import com.example.karaokeparty.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


@SuppressLint("ParcelCreator")
public class MainActivity extends AppCompatActivity implements SingerAdapter.OnSingerClickListener, View.OnClickListener, CreateSingerDialog.CreateSingerInterface {

    private static final String PREF_TAG = "Shared_PREF_File";
    private static final String FAV_SINGER_TAG = "My_Favorite_Singer";
    private static final String IS_FIRST_TIME = "Is_List_Loaded";
    public static final String DETAILED_SINGER_TAG = "Singer_Tag";

    private SingerViewModel mSingerViewModel;
    private RecyclerView mRecyclerView;
    private SingerAdapter mSingerAdapter;

    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DialogFragment createSingerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.main_toolbar);
//        setSupportActionBar(mActionBarToolbar);
//        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.default_title);

        // Get reference to Progress Bar
        progressBar = findViewById(R.id.main_content_progressBar);

        // Initialize Collapsing Toolbar attributes
        collapsingToolbarLayout = findViewById(R.id.main_collapsing_toolbar_layout);
        //collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedToolbarLayoutTitleColor);
        //collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedToolbarLayoutTitleColor);

        initRecyclerView();

        // Initialize View Model
        mSingerViewModel = ViewModelProviders.of(this).get(SingerViewModel.class);
        setSingerListObserver();

        initCreateSingerButton();

        if (savedInstanceState == null) {
            // Happens once when activity is created
            showProgress();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Load recorded data from Shared Preference file
        SharedPreferences sp = getSharedPreferences(PREF_TAG, MODE_PRIVATE);
        boolean isFirstTime = sp.getBoolean(IS_FIRST_TIME, true);

        if (isFirstTime) {
            mSingerViewModel.loadData(new SingerViewModel.OnLoadedDataListener() {
                @Override
                public void OnFinished() {
                    hideProgress();
                    SharedPreferences.Editor editor = getSharedPreferences(PREF_TAG, MODE_PRIVATE).edit();
                    editor.putBoolean(IS_FIRST_TIME, false);
                    editor.apply();
                }

                @Override
                public void onFailed(Exception e) {
                    hideProgress();
                    displayError(e);
                }
            });
        } else {
            hideProgress();
            // Load the list from the cached storage
            ArrayList<SingerModel> cachedSingerList = mSingerViewModel.getCachedData();
            mSingerViewModel.setSingerList(cachedSingerList);

            // Cached file couldn't be loaded
            if (cachedSingerList == null) {
                displayError(new Exception(getString(R.string.connection_error)));
            }
        }
    }

    /*
     * @param position - The item position that was clicked
     */
    @Override
    public void onSingerClick(int position) {

        // Scroll to the top of the RecyclerView
        if (mRecyclerView.getLayoutManager() != null) {
            while (!mRecyclerView.getLayoutManager().isSmoothScrolling()) {
                mRecyclerView.smoothScrollToPosition(0);
            }
            AppBarLayout appBarLayout = findViewById(R.id.main_appbarlayout);
            appBarLayout.setExpanded(true, true);

            // Show detailed activity about user's choice
            Intent intent = new Intent(this, DetailedActivity.class);
            ArrayList<SingerModel> singerList = mSingerViewModel.getObservableSingerList().getValue();
            if (singerList != null) {
                intent.putExtra(DETAILED_SINGER_TAG, singerList.get(position));
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        String imageUrl = mSingerAdapter.getItem(position).getCurrentImagePath();
        String singerName = mSingerAdapter.getItem(position).getNameOfSinger();
        changeCover(imageUrl,singerName);

        // Save last checked position in the Shared Preference file
        SharedPreferences.Editor editor = getSharedPreferences(PREF_TAG, MODE_PRIVATE).edit();
        editor.putInt(FAV_SINGER_TAG, position).apply();


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.detailed_cover_iv) {
            // Downcast View into ImageView, then cast it into Bitmap
            Bitmap bitmap = ((BitmapDrawable) ((ImageView) v).getDrawable()).getBitmap();

            // Covert bitmap into Bytecode
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

//            byte[] byteArray = stream.toByteArray();
//
//            Intent imageIntent = new Intent(MainActivity.this, PhotoActivity.class);
//            imageIntent.putExtra(PICTURE_TAG, byteArray);
//            startActivity(imageIntent);
        }
    }

    private void setSingerListObserver() {
        mSingerViewModel.getObservableSingerList().observe(this, new Observer<ArrayList<SingerModel>>() {
            @Override
            public void onChanged(ArrayList<SingerModel> singerList) {

                if (singerList != null) {

                    // Load recorded data from Shared Preference file
                    SharedPreferences sp = getSharedPreferences(PREF_TAG, MODE_PRIVATE);
                    int checkedPosition = sp.getInt(FAV_SINGER_TAG, SingerAdapter.DEFAULT_POSITION);

                    // On singer list change, update Recycler View data
                    mSingerAdapter = new SingerAdapter(singerList, MainActivity.this, checkedPosition);
                    mRecyclerView.setAdapter(mSingerAdapter);

                    // Load checked position model
                    if (checkedPosition != SingerAdapter.DEFAULT_POSITION) {
                        SingerModel singerModel = mSingerAdapter.getItem(checkedPosition);
                        if (singerModel != null) {
                            changeCover(singerModel.getCurrentImagePath(),singerModel.getNameOfSinger());
                        }
                        else {
                            // No singers on the list
                            changeCover(null, "Your singer list is empty!");
                        }
                    } else {
                        // Before picking up a singer for the first time
//                        Toolbar toolbar = findViewById(R.id.main_toolbar);
//                        toolbar.setTitle(R.string.default_title);
//                        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
//                        //toolbar.setTitle(getResources().getString(R.string.default_title));
//                        //.setTitleTextColor(getResources().getColor(R.color.black));
//                        setSupportActionBar(toolbar);

                        ImageView mainCoverImage = findViewById(R.id.detailed_cover_iv);
                        mainCoverImage.setImageDrawable(getResources().getDrawable(R.drawable.karaoke));
                        mainCoverImage.setOnClickListener(MainActivity.this);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        // Initialize Recycler View
        mRecyclerView = findViewById(R.id.main_content_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

            int dragTo = -1, dragFrom = -1;

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (dragFrom == -1) {
                    dragFrom = fromPosition;
                }
                dragTo = toPosition;

                mSingerAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);
                builder.setTitle(R.string.warning)
                        .setMessage(R.string.dialog_delete_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSingerViewModel.removeSinger(position);
                                mSingerAdapter.notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mSingerAdapter.notifyItemChanged(position);
                            }
                        })
                        .show();
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    mSingerViewModel.moveSinger(dragFrom, dragTo);
                }

                dragFrom = dragTo = -1;
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);
    }

    private void initCreateSingerButton() {

        FloatingActionButton fab = findViewById(R.id.main_add_singer_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSingerDialog = CreateSingerDialog.newInstance(MainActivity.this);
                createSingerDialog.show(getSupportFragmentManager(), "NewSingerDialog");
            }
        });

    }



    private void changeCover(String imageUrl, String singerName) {
        // Main Cover references
        ImageView mainCoverImage = findViewById(R.id.detailed_cover_iv);

        // Update Main Cover
        if (singerName != null) {
            collapsingToolbarLayout.setTitle(singerName);
        }
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(mainCoverImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.karaoke)
                    .into(mainCoverImage);
        }

        mainCoverImage.setOnClickListener(this);
        Log.i("MainActivity", getResources().getString(R.string.cover_was_changed));
    }

    private void displayError(Exception e) {
        if (e instanceof NetworkErrorException) {
            Toast.makeText(this, getResources().getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();

            ImageView noConnectionIv = findViewById(R.id.main_no_connection_iv);
            noConnectionIv.setVisibility(View.VISIBLE);
            collapsingToolbarLayout.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, getResources().getString(R.string.error_was_found) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void insertNewSinger(SingerModel singer) {
        mSingerViewModel.addSinger(singer);
        mSingerViewModel.loadData();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
