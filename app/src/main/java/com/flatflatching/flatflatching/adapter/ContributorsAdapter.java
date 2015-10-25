package com.flatflatching.flatflatching.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.models.FlatMate;

import java.util.List;

/**
 * Created by rafael on 20.10.2015.
 */
public class ContributorsAdapter extends RecyclerView.Adapter<ContributorsAdapter.ContributorViewHolder>{

    private List<FlatMate> contributorsList;

    public ContributorsAdapter(List<FlatMate> contactList) {
        this.contributorsList = contactList;
    }

    @Override
    public int getItemCount() {
        return contributorsList.size();
    }

    @Override
    public void onBindViewHolder(ContributorViewHolder contactViewHolder, int i) {
        FlatMate flatMate = contributorsList.get(i);
        contactViewHolder.flatMateNameTextView.setText(flatMate.getName());
        contactViewHolder.flatMateEmailTextView.setText(flatMate.getEmail());
        contactViewHolder.rootView.setOnClickListener(contactViewHolder);
    }

    @Override
    public ContributorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.contributor_card, viewGroup, false);

        return new ContributorViewHolder(itemView);
    }

    public static class ContributorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView flatMateNameTextView;
        protected TextView flatMateEmailTextView;
        protected View rootView;
        public ContributorViewHolder(View v) {
            super(v);
            flatMateNameTextView =  (TextView) v.findViewById(R.id.contributorNameTextView);
            flatMateEmailTextView = (TextView) v.findViewById(R.id.flatMateEmailTextView);
            rootView = v;
        }

        @Override
        public void onClick(View v) {
            Switch switcher = (Switch)v.findViewById(R.id.switchContributes);
            switcher.setChecked(!switcher.isChecked());
        }
    }
}
