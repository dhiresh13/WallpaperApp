package com.example.mitnoteblock.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;


import com.example.mitnoteblock.R;
import com.example.mitnoteblock.adapters.WallpaperAdapter;
import com.example.mitnoteblock.models.wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WallpapersActivity extends AppCompatActivity {
    List<wallpaper> wallpaperList;
    List<wallpaper> FavList;

    RecyclerView recyclerView;

    WallpaperAdapter adapter;

    DatabaseReference dbwallpapers, dbFavs;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);

        FavList = new ArrayList<>();
        wallpaperList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WallpaperAdapter(this, wallpaperList);
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressbar);

        dbwallpapers = FirebaseDatabase.getInstance().getReference("images")
                .child(category);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            dbFavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);
            fetchFavWallpapers(category);
        } else {
            fetchWallpapers(category);
        }
    }


    private void fetchFavWallpapers(final String category) {

        progressBar.setVisibility(View.VISIBLE);
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        wallpaper w = new wallpaper(id, title, desc, url, category);
                        FavList.add(w);
                    }
                }
                fetchWallpapers(category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchWallpapers(final String category) {

        progressBar.setVisibility(View.VISIBLE);
        dbwallpapers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpapersnapshot : dataSnapshot.getChildren()) {


                        String id = wallpapersnapshot.getKey();
                        String title = wallpapersnapshot.child("title").getValue(String.class);
                        String desc = wallpapersnapshot.child("desc").getValue(String.class);
                        String url = wallpapersnapshot.child("url").getValue(String.class);


                        wallpaper w = new wallpaper(id, title, desc, url, category);
                        if (isFavourite(w)) {
                            w.isFavourite = true;
                        }

                        wallpaperList.add(w);

                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
            private boolean isFavourite(wallpaper w) {
                for (wallpaper f : FavList) {
                    if (f.id.equals(w.id)) {
                        return true;
                    }
                }
                return false;
            }
}

