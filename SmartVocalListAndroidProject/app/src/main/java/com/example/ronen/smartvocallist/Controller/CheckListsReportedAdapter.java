package com.example.ronen.smartvocallist.Controller;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ronen.smartvocallist.Model.ModelChecklists;
import com.example.ronen.smartvocallist.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import com.example.ronen.smartvocallist.DataObjects.Checklist;

public class CheckListsReportedAdapter  extends RecyclerView.Adapter<CheckListsReportedAdapter.ChecklistViewHolder> {
    ArrayList<Checklist> mData;
    CheckListsReportedAdapter.OnItemClickedListener mListener;

    public CheckListsReportedAdapter(ArrayList<Checklist> data) {
        mData = data;
    }

    interface OnItemClickedListener{
        void onClick(int index);
    }

    public void setOnItemClickedListener(CheckListsReportedAdapter.OnItemClickedListener listener){
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
    public void onBindViewHolder(@NonNull ChecklistViewHolder checklistViewHolder, int i) {
        Checklist checkList = mData.get(i);
        checklistViewHolder.bind(checkList);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ChecklistViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView mImage;
        TextView mTitle;
        TextView mSubTitle;
        ProgressBar mImageProgressBar;

        public ChecklistViewHolder(@NonNull View itemView, final CheckListsReportedAdapter.OnItemClickedListener listener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image_iv);
            mTitle = itemView.findViewById(R.id.title_tv);
            mSubTitle = itemView.findViewById(R.id.subTitle_tv);
            mImageProgressBar = itemView.findViewById(R.id.image_progressBar);
            mImageProgressBar.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if(listener != null){
                        if(index != RecyclerView.NO_POSITION){
                            listener.onClick(index);
                        }
                    }
                }
            });
        }

        // Binding data to the view
        public void bind(Checklist checkList) {
            mTitle.setText(checkList.getName());
            mSubTitle.setText(checkList.getDescription());
            setCheckListImage(checkList);

            // Only owner can edit and delete checklist
            if(ModelChecklists.getInstance().getOwnerName().equals(checkList.getOwner()))
                itemView.setOnCreateContextMenuListener(this);
        }

        private void setCheckListImage(Checklist checkList) {
            //default image
            mImage.setImageResource(R.drawable.default_icon);

            if(checkList.getUrl() == null || checkList.getUrl().equals("")) {
                mImageProgressBar.setVisibility(View.INVISIBLE);
            }else {
                Picasso.get().setIndicatorsEnabled(true);
                Target target = new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (mImage.getTag() == this) {
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

                // Used to set a strong reference, also validity check
                mImage.setTag(target);
                RequestCreator request = Picasso.get().load(checkList.getUrl()).placeholder(R.drawable.default_icon);
                request.into(target);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.reportedDeleteOption, 0, "Delete Checklist");
        }
    }
}
