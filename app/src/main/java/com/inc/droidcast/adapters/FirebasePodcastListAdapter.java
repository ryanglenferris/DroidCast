package com.inc.droidcast.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.inc.droidcast.Constants;
import com.inc.droidcast.R;
import com.inc.droidcast.models.Podcast;
import com.inc.droidcast.ui.PodcastDetailActivity;
import com.inc.droidcast.ui.PodcastDetailFragment;
import com.inc.droidcast.util.ItemTouchHelperAdapter;
import com.inc.droidcast.util.OnStartDragListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

public class FirebasePodcastListAdapter extends FirebaseRecyclerAdapter<Podcast, FirebasePodcastViewHolder> implements ItemTouchHelperAdapter {
    private DatabaseReference mRef;
    private OnStartDragListener mOnStartDragListener;
    private Context mContext;
    private ChildEventListener mChildEventListener;
    private ArrayList<Podcast> mPodcasts = new ArrayList<>();
    private int mOrientation;

    public FirebasePodcastListAdapter(Class<Podcast> modelClass, int modelLayout, Class<FirebasePodcastViewHolder> viewHolderClass, Query ref, OnStartDragListener onStartDragListener, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mRef = ref.getRef();
        mOnStartDragListener = onStartDragListener;
        mContext = context;

        mChildEventListener = mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mPodcasts.add(dataSnapshot.getValue(Podcast.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void populateViewHolder(final FirebasePodcastViewHolder viewHolder, Podcast model, int position) {
        viewHolder.bindPodcast(model);

//        mOrientation = viewHolder.itemView.getResources().getConfiguration().orientation;
//        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//            createDetailFragment(0);
//        }

        viewHolder.mPodcastImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = viewHolder.getAdapterPosition();

                Intent intent = new Intent(mContext, PodcastDetailActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_POSITION, itemPosition);
                intent.putExtra(Constants.EXTRA_KEY_PODCASTS, Parcels.wrap(mPodcasts));
//                intent.putExtra(Constants.KEY_SOURCE, Constants.SOURCE_SAVED);
                mContext.startActivity(intent);

                //                if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    createDetailFragment(itemPosition);
//                } else {
//                    Intent intent = new Intent(mContext PodcastDetailActivity.class);
//                    intent.putExtra(Constants.EXTRA_KEY_POSITION, itemPosition);
//                    intent.putExtra(Constants.EXTRA_KEY_PODCASTS, Parcels.wrap(mPodcasts));
//                    intent.putExtra(Constants.KEY_SOURCE, Constants.SOURCE_SAVED);
//                    mContext.startActivity(intent);
//                }
            }
        });

    }

//    private void createDetailFragment(int position) {
//        PodcastDetailFragment detailFragment = PodcastDetailFragment.newInstance(mPodcasts, position, Constants.SOURCE_SAVED);
//        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.podcastDetailContainer, detailFragment);
//        ft.commit();
//    }

    @Override
    public boolean onItemMove(int fromPosition, int toPostion) {
        Collections.swap(mPodcasts, fromPosition, toPostion);
        notifyItemMoved(fromPosition, toPostion);
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mPodcasts.remove(position);
        getRef(position).removeValue();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        setIndexInFirebase();
        mRef.removeEventListener(mChildEventListener);
    }

    private void setIndexInFirebase() {
        for (Podcast podcast : mPodcasts) {
            int index = mPodcasts.indexOf(podcast);
            DatabaseReference ref = getRef(index);
            podcast.setIndex(Integer.toString(index));
            ref.setValue(podcast);
        }
    }
}