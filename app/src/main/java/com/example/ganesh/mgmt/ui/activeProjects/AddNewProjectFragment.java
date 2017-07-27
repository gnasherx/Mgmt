package com.example.ganesh.mgmt.ui.activeProjects;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ganesh.mgmt.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.ganesh.mgmt.utils.Constants.FIREBASE_LOCATION_ACTIVE_PROJECTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewProjectFragment extends DialogFragment {

    private static final String LOG_TAG = AddNewProjectFragment.class.getName();
    private EditText mEditTextProjectName;
    private DatabaseReference mDatabaseProjectRef;
    private FirebaseAuth mAuth;
    private String currentUser;
    private ProgressDialog mAuthProgessDialog;

    public AddNewProjectFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        mDatabaseProjectRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_LOCATION_ACTIVE_PROJECTS);
        mDatabaseProjectRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        mAuthProgessDialog = new ProgressDialog(getContext());
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_add_new_project, null);
        mEditTextProjectName = (EditText) rootView.findViewById(R.id.edit_text_project_name);
        mEditTextProjectName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == event.ACTION_DOWN) {
                    addProjectList();
                }
                return true;
            }
        });

        builder.setTitle(R.string.add_project_title).setView(rootView)
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addProjectList();
                    }
                })
                .setNegativeButton(R.string.dailog_cancel, null);

        setCancelable(true);
        return builder.create();

    }

    private void addProjectList() {
        mAuthProgessDialog.show();
        final String userEnterdProjectName = mEditTextProjectName.getText().toString();

        if (!userEnterdProjectName.equals("")) {

            final DatabaseReference project_ref = mDatabaseProjectRef.push();
            project_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    project_ref.child("title").setValue(userEnterdProjectName);

                    mAuthProgessDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(LOG_TAG, "Error while retriving username for stroy!");
                    mAuthProgessDialog.dismiss();
                }
            });

            AddNewProjectFragment.this.getDialog().cancel();

        }

    }
}


