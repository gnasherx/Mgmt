package com.example.ganesh.mgmt.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ganesh.mgmt.ui.MainActivity;
import com.example.ganesh.mgmt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateUserActivity extends AppCompatActivity {
    private ProgressDialog mAuthProgessDialog;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate,mEditTextNameCreate;
    private Button mButtonSignUp;
    private String mUserName, mUserEmail, mUserPassword,mName;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        initializeScreen();
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_create_username);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_create_email);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_create_password);
        mButtonSignUp = (Button) findViewById(R.id.button_create_signup);
        mEditTextNameCreate=(EditText)findViewById(R.id.edit_text_create_name);

        mAuthProgessDialog = new ProgressDialog(this);
        mAuthProgessDialog.setTitle("Loading...");
        mAuthProgessDialog.setMessage("Attempting to create account...");
        mAuthProgessDialog.setCancelable(false);

    }

    public void createAccount() {

        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString();
        mUserPassword = mEditTextPasswordCreate.getText().toString();
        mName=mEditTextNameCreate.getText().toString();

        boolean validName=isNameValid(mName);
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validUserPassword = isPasswordValid(mUserPassword);

        if (!validName  || !validEmail || !validUserName || !validUserPassword) {
            return;
        }

        mAuthProgessDialog.show();
        mAuth.createUserWithEmailAndPassword(mUserEmail, mUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuthProgessDialog.dismiss();
                    String user_id = mAuth.getCurrentUser().getUid();

                    createUserInFirebaseHelper(user_id);

                    Toast.makeText(CreateUserActivity.this, "Register SuccessFully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }

    private void createUserInFirebaseHelper(final String user_id) {
        final DatabaseReference userLocationReference = mDatabase.child(user_id);
        userLocationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userLocationReference.child("username").setValue(mUserName);
                userLocationReference.child("email").setValue(mUserEmail);
                userLocationReference.child("name").setValue(mName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CreateUserActivity.this, "Error While Registration", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNameValid(String mName) {
        if(mName.equals("")){
            mEditTextNameCreate.setError("This cannot be empty.");
            return false;
        }
        return true;
    }
    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid),
                    email));
            return false;
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            mEditTextPasswordCreate.setError(getResources().getString(R.string.error_invalid_password_not_valid));
            return false;
        }
        return true;
    }


}
