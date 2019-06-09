package com.example.ronen.smartvocallist;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import DataObjects.Checklist;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class CheckListsAdapter extends RecyclerView.Adapter<CheckListsAdapter.ChecklistViewHolder> {
    ArrayList<Checklist> mData;
    OnItemClickedListener mListener;

    public CheckListsAdapter(ArrayList<Checklist> data){
        mData = data;
    }

    interface OnItemClickedListener{
        void onClick(int index);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checklist_row, viewGroup, false);
        ChecklistViewHolder checklistViewHolder = new ChecklistViewHolder(view, mListener);
        return checklistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistViewHolder studentViewHolder, int i) {
        Checklist checkList = mData.get(i);
        studentViewHolder.bind(checkList);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ChecklistViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView mImage;
        TextView mTitle;
        TextView mSubTitle;
        ProgressBar mImageProgressBar;

        public ChecklistViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image_iv);
            mTitle = itemView.findViewById(R.id.title_tv);
            mSubTitle = itemView.findViewById(R.id.subTitle_tv);
            mImageProgressBar = itemView.findViewById(R.id.image_progressBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (listener != null) {
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index);
                        }
                    }
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        // Binding data to the view
        public void bind(Checklist checkList) {
            mTitle.setText(checkList.getName());
            mSubTitle.setText(checkList.getDescription());
            setCheckListImage(checkList);
        }

        private void setCheckListImage(Checklist checkList) {
            //default image
            mImage.setTag(checkList.getId());
            mImage.setImageResource(R.drawable.default_icon);

            if(checkList.getUrl() == null || checkList.getUrl().equals("")) {
                mImageProgressBar.setVisibility(View.INVISIBLE);
            }else {
                Picasso.get().setIndicatorsEnabled(true);
                Target target = new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (mImage.getTag() == checkList.getId()) {
                            mImage.setImageBitmap(bitmap);
                            mImageProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        mImageProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        mImageProgressBar.setVisibility(View.VISIBLE);
                    }
                };

                Picasso.get().load(checkList.getUrl())
                        .placeholder(R.drawable.default_icon)
                        .into(target);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.deleteOption, 0, "Delete Checklist");
            menu.add(this.getAdapterPosition(), R.id.editOption, 1, "Edit Checklist");
        }
    }
}
