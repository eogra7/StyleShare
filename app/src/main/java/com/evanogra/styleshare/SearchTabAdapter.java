package com.evanogra.styleshare;

import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evan on 2/22/2016.
 */
public class SearchTabAdapter extends RecyclerView.Adapter<SearchTabAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private final FragmentManager fragmentManager;

    public SearchTabAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
        notifyDataSetChanged();
    }

    public void clearUsers() {
        users.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.search_item_fragment, parent, false);

        TextView nameView = (TextView) v.findViewById(R.id.search_item_title);
        TextView chapterView = (TextView) v.findViewById(R.id.search_item_author);
        ImageView imageView = (ImageView) v.findViewById(R.id.search_item_thumbnail);
        CardView cardView = (CardView) v.findViewById(R.id.search_result_card);

        SearchTabAdapter.ViewHolder vh = new ViewHolder(v, imageView, nameView, chapterView, cardView);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCallback = (OnUserSelectedListener) context;

        final User user = users.get(position);
        String name = user.getName();
        String chapter = user.getChapter();

        holder.nameView.setText(name);
        holder.chapterView.setText(chapter);


        Picasso.with(context)
                .load(user.getProfile_picture())
                .transform(new CircleTransform())
                .fit()
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabActivity tabActivity = (TabActivity) context;
                tabActivity.setSelectedUser(user);
                mCallback.onUserSelected(user);
                //tabActivity.setPage(2);
            }
        });

    }

    OnUserSelectedListener mCallback;
    public interface OnUserSelectedListener {
        public void onUserSelected(User user);
    }
    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView nameView;
        private TextView chapterView;
        private CardView cardView;

        public ViewHolder(View itemView, ImageView imageView, TextView nameView, TextView chapterView, CardView cardView) {
            super(itemView);
            this.imageView = imageView;
            this.nameView = nameView;
            this.chapterView = chapterView;
            this.cardView = cardView;
        }
    }
}
