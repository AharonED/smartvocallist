package com.example.ronen.smartvocallist;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import DataObjects.Checklist;
import DataObjects.ChecklistReported;

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
        TextView mName;
        TextView mDescription;

        public ChecklistViewHolder(@NonNull View itemView, final CheckListsReportedAdapter.OnItemClickedListener listener) {
            super(itemView);
            mImage = itemView.findViewById(R.id.checkListImage);
            mName = itemView.findViewById(R.id.checkListName);
            mDescription = itemView.findViewById(R.id.checkListDescription);

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

            itemView.setOnCreateContextMenuListener(this);
        }

        // Binding data to the view
        public void bind(Checklist checkList){
            mName.setText(checkList.getName());
            mDescription.setText(checkList.getDescription());
            // For now it's always the default image
            mImage.setImageResource(R.drawable.default_icon);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), R.id.reportedDeleteOption, 0, "Delete Checklist");
        }
    }
}
