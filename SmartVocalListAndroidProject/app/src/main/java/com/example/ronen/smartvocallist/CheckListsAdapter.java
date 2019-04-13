package com.example.ronen.smartvocallist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import DataObjects.Checklist;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckListsAdapter extends RecyclerView.Adapter<CheckListsAdapter.StudentViewHolder> {
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
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checklist_row, viewGroup, false);
        StudentViewHolder studentViewHolder = new StudentViewHolder(view, mListener);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder studentViewHolder, int i) {
        Checklist checkList = mData.get(i);
        studentViewHolder.bind(checkList);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mName;
        TextView mDescription;

        public StudentViewHolder(@NonNull View itemView, final OnItemClickedListener listener) {
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
        }

        // Binding data to the view
        public void bind(Checklist checkList){
            mName.setText(checkList.getName());
            mDescription.setText(checkList.getDescription());
            // For now it's always the default image
            mImage.setImageResource(R.drawable.default_icon);
        }
    }
}
