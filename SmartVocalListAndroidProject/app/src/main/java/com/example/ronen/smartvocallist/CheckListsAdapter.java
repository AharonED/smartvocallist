package com.example.ronen.smartvocallist;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import DataObjects.Checklist;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        public ChecklistViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.checkListImage);
            mTitle = itemView.findViewById(R.id.checkListName);
            mSubTitle = itemView.findViewById(R.id.checkListDescription);

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
            // For now it's always the default image
            mImage.setImageResource(R.drawable.default_icon);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.deleteOption, 0, "Delete Checklist");
            menu.add(this.getAdapterPosition(), R.id.editOption, 1, "Edit Checklist");
        }
    }
}
