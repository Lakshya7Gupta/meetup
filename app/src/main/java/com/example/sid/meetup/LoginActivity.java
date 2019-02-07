package com.example.sid.meetup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    public static final String CHAT_PREFS1 = "ChatPrefs1";
    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";

    public SharedPreferences prefs;


    // TODO: Add member variables here:
    private FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);

        prefs=getSharedPreferences(CHAT_PREFS1,0);
        if(prefs.getString(EMAIL_KEY,"") != "" && prefs.getString(PASSWORD_KEY,"") !=""){
            startActivity(new Intent(LoginActivity.this,MainChatActivity.class));
            finish();
        }
       else {

            //if enter key is pressed in soft keyboard
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            // TODO: Grab an instance of FirebaseAuth
            mAuth = FirebaseAuth.getInstance();
        }

    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // TODO: Call attemptLogin() here
         attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.example.sid.meetup.RegisterActivity.class);
        //finish();
        startActivity(intent);
    }

    // TODO: Complete the attemptLogin() method
    private void attemptLogin() {

          String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.equals("") || password.equals("")) return;
        Toast.makeText(this,"Login in progress...",Toast.LENGTH_SHORT).show();

        // TODO: Use FirebaseAuth to sign in with email & password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Meetup","signInWithEmail() onComplete: " + task.isSuccessful());

                if(!task.isSuccessful())
                {
                    Log.d("Meetup","Problem in signing in: " + task.getException());

                    showErrorDialog("There was a problem in signing in!!");
                    mEmailView.setText("");
                    mPasswordView.setText("");

                    mEmailView.requestFocus();

                }else{
                    String Email = mEmailView.getText().toString();
                    String Password = mEmailView.getText().toString();


                    prefs.edit().putString(EMAIL_KEY,Email).apply();
                    prefs.edit().putString(PASSWORD_KEY,Password).apply();



                    Intent intent = new Intent(LoginActivity.this,MainChatActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });




    }

    // TODO: Show error on screen with an alert dialog
    private void showErrorDialog(String message){

        new AlertDialog.Builder(this).setTitle("Oops").setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }



}