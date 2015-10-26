package com.flatflatching.flatflatching.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.models.FlatMate;

import java.util.List;

public class FlatMateAdapter extends RecyclerView.Adapter<FlatMateAdapter.FlatMateViewHolder>{

    private List<FlatMate> flatMateList;
    private View.OnClickListener delete;

    public FlatMateAdapter(List<FlatMate> contactList, View.OnClickListener delete) {
        this.flatMateList = contactList;
        this.delete = delete;
    }

    @Override
    public int getItemCount() {
        return flatMateList.size();
    }

    public void removeItem(int position) {
        flatMateList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(FlatMateViewHolder contactViewHolder, int i) {
        FlatMate flatMate = flatMateList.get(i);
        contactViewHolder.flatMateNameTextView.setText(flatMate.getName());
        contactViewHolder.positionTextView.setText(Integer.toString(i));
        contactViewHolder.flatMateEmailTextView.setText(flatMate.getEmail());
        contactViewHolder.flatMateDeleteBtn.setOnClickListener(this.delete);
    }

    @Override
    public FlatMateViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.flatmate_card, viewGroup, false);

        return new FlatMateViewHolder(itemView);
    }

    public class FlatMateViewHolder extends RecyclerView.ViewHolder{
        protected TextView flatMateNameTextView;
        protected TextView flatMateEmailTextView;
        protected ImageView flatMateDeleteBtn;
        protected TextView positionTextView;
        protected View rootView;
        public FlatMateViewHolder(View v) {
            super(v);
            flatMateNameTextView =  (TextView) v.findViewById(R.id.contributorNameTextView);
            flatMateEmailTextView = (TextView) v.findViewById(R.id.flatMateEmailTextView);
            flatMateDeleteBtn = (ImageView) v.findViewById(R.id.deleteIcon);
            positionTextView = (TextView) v.findViewById(R.id.flatMatePosition);
            rootView = v;
        }
    }
}
