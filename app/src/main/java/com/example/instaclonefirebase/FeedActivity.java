package com.example.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<String> userEmailFromFB;
    ArrayList<String> userCommentFromFB;
    ArrayList<String> userImageFromFB;

    FeedRecyclerAdapter feedRecyclerAdapter;

    // To connect the menu with this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Linking the menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.insta_options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    // To select an item from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_post){
            Intent intentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);
        }
        else if (item.getItemId() == R.id.signout){
            firebaseAuth.signOut();

            Intent intentToSignUp = new Intent(FeedActivity.this,SignUpActivity.class);
            startActivity(intentToSignUp);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userCommentFromFB=new ArrayList<>();
        userEmailFromFB = new ArrayList<>();
        userImageFromFB = new ArrayList<>();

        getDataFromFirestore();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // recycleView'ı vertical yapıyor
        feedRecyclerAdapter = new FeedRecyclerAdapter(userEmailFromFB,userCommentFromFB,userImageFromFB);
        recyclerView.setAdapter(feedRecyclerAdapter);
    }

    public void getDataFromFirestore(){
        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        collectionReference.orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null){
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){

                        Map<String,Object> data = snapshot.getData();

                        // Casting
                        String comment = (String) data.get("comment");
                        String userEmail = (String) data.get("email");
                        String downloadUrl = (String) data.get("downloadurl");

                        userCommentFromFB.add(comment);
                        userEmailFromFB.add(userEmail);
                        userImageFromFB.add(downloadUrl);

                        feedRecyclerAdapter.notifyDataSetChanged(); // warn that the data has changed
                    }
                }
            }
        });
    }
}
