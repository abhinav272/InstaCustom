package com.abhinav.instacropperpicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abhinav.instacropperpicker.R;
import com.abhinav.instacropperpicker.bean.MediaBean;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalleryAdapter extends ListAdapter<MediaBean, GalleryAdapter.GalleryViewHolder> {

    private Context mContext;
    private boolean inSelectedMode = false;
    private RecyclerOnItemListener recyclerOnItemListener;

    private ArrayList<MediaBean> selectedMedia;

    public GalleryAdapter(@NonNull DiffUtil.ItemCallback<MediaBean> diffCallback, @NonNull Context mContext) {
        super(diffCallback);
        this.mContext = mContext;
        selectedMedia = new ArrayList<>();
    }

    public void setRecyclerOnItemListener(RecyclerOnItemListener recyclerOnItemListener) {
        this.recyclerOnItemListener = recyclerOnItemListener;
    }

    public boolean isInSelectedMode() {
        return inSelectedMode;
    }

    public void setInSelectedMode(boolean inSelectedMode) {
        if (this.inSelectedMode && !inSelectedMode) {
            for (MediaBean bean :
                    selectedMedia) {
                bean.setSelected(false);
            }
            notifyDataSetChanged();
        }
        this.inSelectedMode = inSelectedMode;
    }

    public ArrayList<MediaBean> getSelectedMedia() {
        return selectedMedia;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);
        return new GalleryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.bind(position);
    }

    public interface RecyclerOnItemListener {
        void onClick(View view, int position, MediaBean object);

        boolean onLongClick(View view, int position, MediaBean object, int requestCode);
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;
        ImageView cbSelectedImage;

        GalleryViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.image);
            cbSelectedImage = view.findViewById(R.id.iv_select);
            view.setOnClickListener(this);
        }

        void bind(int position) {
            MediaBean mediaBean = getItem(position);

            Glide.with(mContext)
                    .load(mediaBean.getUri())
                    .into(thumbnail);

//            if (inSelectedMode) {
//                cbSelectedImage.setVisibility(View.VISIBLE);
//            } else {
//                cbSelectedImage.setVisibility(View.GONE);
//            }

            if (mediaBean.isSelected()) {
                cbSelectedImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
            } else {
                cbSelectedImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
            }


        }

        @Override
        public void onClick(final View v) {
            MediaBean mediaBean = getItem(getAdapterPosition());

            recyclerOnItemListener.onClick(v, getAdapterPosition(), getItem(getAdapterPosition()));

//            if (inSelectedMode) {
//
//                if (selectedMedia.contains(mediaBean)) {
//                    mediaBean.setSelected(false);
//                    selectedMedia.remove(mediaBean);
//                } else {
//                    mediaBean.setSelected(true);
//                    selectedMedia.add(mediaBean);
//                }
//
//                notifyItemChanged(getAdapterPosition());
//
//            }

        }
    }

}