package com.example.karaokeparty.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.karaokeparty.R;
import com.example.karaokeparty.model.SingerModel;

import java.util.ArrayList;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.ViewHolder> {

    static final int DEFAULT_POSITION = -1;

    private ArrayList<SingerModel> mSingerList;
    private int checkedPosition;

    private RequestManager mGlide;
    private OnSingerClickListener mSingerListListener;

    SingerAdapter(ArrayList<SingerModel> singerList, Context context, int checkedPosition) {
        this.mSingerList = singerList;
        this.mSingerListListener = (OnSingerClickListener) context;
        this.mGlide = Glide.with(context);
        this.checkedPosition = checkedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SingerModel singerModel = mSingerList.get(position);

        // Initialize each item with the following attributes
        String singer = singerModel.getNameOfSinger();
        holder.singerTv.setText(singer);

        String song = singerModel.getNameOfSong();
        holder.songTv.setText(song);

        mGlide.load(singerModel.getCurrentImagePath())
                .placeholder(R.drawable.image_singer_not_loaded)
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);

                        Log.d("SingerAdapter", "Was unable to load image via glide: " + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.image_singer_not_loaded)
                .into(holder.singerIv);

        if (position == checkedPosition) {
            holder.favoriteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.favoriteIcon.setVisibility(View.INVISIBLE);
        }

        // Keep the image URL reference for late usage (as a main activity cover).
        holder.singerUrl = singerModel.getCurrentImagePath();
    }

    @Override
    public int getItemCount() {
        return mSingerList.size();
    }



    SingerModel getItem(int position) {
        if (mSingerList.size() > position) {
            return mSingerList.get(position);
        } else {
            return null;
        }
    }

    private void setCheckedPosition(int position) {
        int prevChecked = checkedPosition;
        checkedPosition = position;

        if (prevChecked != DEFAULT_POSITION) {
            notifyItemChanged(prevChecked);
        }
        notifyItemChanged(checkedPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView singerTv;
        TextView songTv;
        ImageView singerIv;
        String singerUrl;
        ImageView favoriteIcon;
        ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Create references for item's fields
            singerTv = itemView.findViewById(R.id.detailed_name_singer_tv);
            songTv = itemView.findViewById(R.id.detailed_name_song_tv);
            singerIv = itemView.findViewById(R.id.item_singer_picture_iv);
            favoriteIcon = itemView.findViewById(R.id.item_singer_favorite_iv);
            progressBar = itemView.findViewById(R.id.detailed_progressbar);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View itemView) {
            // Notify to the main that an item was chosen.
            mSingerListListener.onSingerClick(getAdapterPosition());

            // Update current checked position
            if (checkedPosition != getAdapterPosition()) {
                setCheckedPosition(getAdapterPosition());
            }
        }
    }

    public interface OnSingerClickListener {

        void onSingerClick(int position);

    }
}
