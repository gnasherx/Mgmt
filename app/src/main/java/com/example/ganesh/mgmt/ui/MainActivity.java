package com.example.ganesh.mgmt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ganesh.mgmt.R;
import com.example.ganesh.mgmt.auth.CreateUserActivity;
import com.example.ganesh.mgmt.model.Project;
import com.example.ganesh.mgmt.ui.activeProjectDetails.ActiveProjectDetailsActivity;
import com.example.ganesh.mgmt.ui.activeProjects.AddNewProjectFragment;
import com.example.ganesh.mgmt.ui.activeProjects.SimpleDividerItemDecoration;
import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.ganesh.mgmt.utils.Constants.FIREBASE_LOCATION_ACTIVE_PROJECTS;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabasrProjectRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RecyclerView mProjectRecyclerView;

    private FloatingActionButton mFloatingActionbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatingActionbutton = (FloatingActionButton) findViewById(R.id.fab);
        mProjectRecyclerView = (RecyclerView) findViewById(R.id.recyler_view_project_list);
        mProjectRecyclerView.setHasFixedSize(true);
        mProjectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProjectRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabasrProjectRef = mDatabase.child(FIREBASE_LOCATION_ACTIVE_PROJECTS);
        Log.e("Main", "mDatabasrProjectRef:" + mDatabasrProjectRef);
        mDatabase.keepSynced(true);
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent createAccountIntent = new Intent(MainActivity.this, CreateUserActivity.class);
                    createAccountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(createAccountIntent);
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Project, ProjectViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Project, ProjectViewHolder>(
                Project.class,
                R.layout.single_active_project_list,
                ProjectViewHolder.class,
                mDatabasrProjectRef
        ) {
            @Override
            protected void populateViewHolder(ProjectViewHolder viewHolder, Project model, int position) {
                final String project_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,ActiveProjectDetailsActivity.class));
                        Toast.makeText(MainActivity.this, "" + project_key, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mProjectRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createNewProject(View view) {
        DialogFragment dialog = new AddNewProjectFragment();
        dialog.show(MainActivity.this.getSupportFragmentManager(), "AddNewProjectFragment");
    }

    private static class ProjectViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ProjectViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView project_title = (TextView) mView.findViewById(R.id.text_view_project_title);
            project_title.setText(title);
        }

    }
}
