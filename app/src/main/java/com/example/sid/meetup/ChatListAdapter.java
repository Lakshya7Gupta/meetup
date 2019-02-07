package com.example.sid.meetup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    //the code here acts as a bridge b/w chat message data from firebase and the listview

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapshotList; //DataSnapshot is a type used by firebase for passing data back to our app
    //every time we read data from database we recieve data in form of DataSnapshot

    private ChildEventListener mListener = new ChildEventListener() {
        //this listener will get notified if changes are done to our  database
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            mSnapshotList.add(dataSnapshot);//appending chats in form of data snapshot object
            notifyDataSetChanged();//notifying listview to refresh itself

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
        mActivity = activity;
        mDisplayName = name;
        mDatabaseReference = ref.child("messages");
        mDatabaseReference.addChildEventListener(mListener);

        mSnapshotList = new ArrayList<>();

    }
    static class ViewHolder{
        //holds all the values of all the views in a single row of chat
        TextView body;
        TextView authorName;
       LinearLayout.LayoutParams params; //for styling our message row

    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {

        DataSnapshot snapshot = mSnapshotList.get(position);

        return snapshot.getValue(InstantMessage.class);//converting json from the snapshot into instant message object
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      //convertView here represents the list item
        if(convertView == null){
          //have to create new row from layout file
            LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //layoutInflator will create a new view for us with inflate method
            convertView = inflater.inflate(R.layout.chat_msg_row,parent,false);//inflate method will supply our chat message to xml file and stick that into convert view
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView)convertView.findViewById(R.id.author);//linking up fields of viewholder to views in the chat message
            holder.body = (TextView)convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams)holder.authorName.getLayoutParams();//to get the layout parameters we call getlayoutparam method on text view we stored under aiuthor name

            //to give adapter a way of storing our view holder for short period of time so that we can reuse it later
            //reusing the viewholder will allow us to avoid calling findviewbyid method again
            convertView.setTag(holder);//this temporarily store our view holder in the convertview

      }
        final  InstantMessage message = getItem(position);//get the instant message at current position in the list,This is the Adapter providing the contents of a row to the ListView.
        final ViewHolder holder = (ViewHolder)convertView.getTag();//reusing the viewholder,using getTag method to retrieve the viewholder temporarily saved in convertview

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearance(isMe,holder);

        String author = message.getAuthor();
        holder.authorName.setText(author);


        String msg = message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }
    //styling of chat messages
    private void setChatRowAppearance(boolean isItMe,ViewHolder holder)
    {
        if(isItMe)
        {
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.parseColor("#0cba00"));
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }else
        {
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);

        }
        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }

    //stop checking for new events on the dstabase so that we can free up resources when not needed
    public void cleanup(){
        mDatabaseReference.removeEventListener(mListener);

    }


}
