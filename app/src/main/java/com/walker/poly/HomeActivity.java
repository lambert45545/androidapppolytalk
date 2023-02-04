package com.walker.poly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walker.poly.Adapters.PostAdapter;
import com.walker.poly.Model.Post;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity  implements  NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer_layout ;
    private Toolbar toolbar ;
    FloatingActionButton fab ;
    private RecyclerView recyclerview ;
    private ProgressBar progerss_cercular ;
    private PostAdapter postAdapter ;
    private List<Post> postList;
    private CircleImageView  navheaderImage;
    private TextView nav_headerEmail , nav_headerName;
    private  DatabaseReference userRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fab = findViewById(R.id.fab);
        drawer_layout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.home_toolbar);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Poly Talk App ");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerview = findViewById(R.id.recyclerview);
        progerss_cercular = findViewById(R.id.progerss_cercular);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(linearLayoutManager);





        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer_layout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,AskActivity.class);
                startActivity(intent);
            }
        });
        nav_headerEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        nav_headerName = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        navheaderImage = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nav_headerName.setText(  snapshot.child("username").getValue().toString());
                     nav_headerEmail.setText(  snapshot.child("email").getValue().toString());

                     String imgurl = snapshot.child("profileimageurl").getValue().toString();
                     Glide.with(HomeActivity.this).load(imgurl).into(navheaderImage);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this,  error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });




        postList = new ArrayList<>();
        postAdapter = new PostAdapter(HomeActivity.this, postList);
        recyclerview.setAdapter(postAdapter);
        readQuestionPosts();



    }

    private void readQuestionPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);


                }
                postAdapter.notifyDataSetChanged();
                progerss_cercular.setVisibility(View.GONE);
                

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_Finance:
                Intent intent  = new Intent(HomeActivity.this,CategoryActivity.class);
                intent.putExtra("title","Finance");
                startActivity(intent);
                break;
            case R.id.nav_sport:
                Intent intentS  = new Intent(HomeActivity.this,CategoryActivity.class);
                intentS.putExtra("title","Sports");
                startActivity(intentS);
                break;
            case R.id.nav_FOOD:
                Intent intentF  = new Intent(HomeActivity.this,CategoryActivity.class);
                intentF.putExtra("title","food");
                startActivity(intentF);
                break;




        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START);
        }else {
        super.onBackPressed();

        }

    }


}