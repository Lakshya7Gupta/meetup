package com.example.sid.meetup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class MainChatActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101 ;
    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private   TextView navHeadText;
     private  Uri uriProfileImage;
     private ImageView navProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);


        // TODO: Set up the display name and get the Firebase reference
          setupDisplayName();
          mDatabaseReference = FirebaseDatabase.getInstance().getReference();//we obtain a database reference object
          //we need database refernce object becuz it represent perticular location in our cloud database, n therefore is used for reading n writing data to that location in database


        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigationView);

        View headerView = mNavigationView.getHeaderView(0);
        navHeadText = (TextView)headerView.findViewById(R.id.nav_head_text);
        navProfileImage = headerView.findViewById(R.id.nav_head_image);
        navHeadText.setText(mDisplayName);


        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage();
                return true;//depicting that event has been handled
            }
        });


        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        //navigation drawer click events
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

               switch (menuItem.getItemId())
               {
                   case R.id.nav_weather:
                       menuItem.setChecked(true);
                       mDrawerLayout.closeDrawers();
                       Intent implicit1 = new Intent(MainChatActivity.this,WeatherController.class);
                       startActivity(implicit1);
                       return true;


                   case R.id.nav_location:
                       menuItem.setChecked(true);
                       mDrawerLayout.closeDrawers();
                       return true;

                   case R.id.nav_news:
                       menuItem.setChecked(true);
                       mDrawerLayout.closeDrawers();
                       Intent implicit3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.timesofindia.com"));
                       startActivity(implicit3);
                       return true;

                   case R.id.nav_gotomail:
                       menuItem.setChecked(true);
                       mDrawerLayout.closeDrawers();
                       Intent implicit4 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gmail.com"));
                       startActivity(implicit4);
                       return true;



                   case R.id.nav_logout:
                       menuItem.setChecked(true);

                       mDrawerLayout.closeDrawers();

                       android.app.AlertDialog.Builder alert=new android.app.AlertDialog.Builder(MainChatActivity.this);
                       alert.setTitle("EXIT?");
                       alert.setCancelable(false);//whther to cancel dailog box if user clicks anywhere else
                       alert.setMessage("Do you want to logout...");
                       alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               Toast.makeText(MainChatActivity.this,"Logged Out",Toast.LENGTH_SHORT).show();

                               SharedPreferences sharedPreferences=getSharedPreferences(LoginActivity.CHAT_PREFS1, Context.MODE_PRIVATE);
                               sharedPreferences.edit().clear().commit();//used clear commits
                               startActivity(new Intent(MainChatActivity.this,LoginActivity.class));
                               finish();
                           }
                       });
                       alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                           }
                       });
                       alert.show();




                       return true;


               }


                return false;
            }
        });

    }

    // TODO: Retrieve the display name from the Shared Preferences
    private void setupDisplayName(){
       /* SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS2,MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY,null);

        if( mDisplayName==null)
            mDisplayName="Anonymous";*/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDisplayName = user.getDisplayName();





    }


    private void sendMessage() {
        Log.d("Meetup","I sent something");
        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = mInputText.getText().toString();
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input,mDisplayName);
            //saving our chat message to database
            mDatabaseReference.child("messages").push().setValue(chat);
            //database refernce is a perticular location in our database
            //child() method specifies the place where our chat will be store i.e messages
            //push() method helps to get a refernce to this child location
            //setValue to commit our chat message to this location

            mInputText.setText("");

        }



    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.


    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new ChatListAdapter(this,mDatabaseReference,mDisplayName);
        mChatListView.setAdapter(mAdapter);


    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){

           uriProfileImage = data.getData();//as it returns uri object

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                navProfileImage.setImageBitmap(bitmap);
                


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    public void profileheader(View view) {
        //onclick for profile pic in nav head
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);

    }
}
