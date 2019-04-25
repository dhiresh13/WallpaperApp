package com.example.mitnoteblock.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavouritesFragment extends Fragment {

    List<wallpaper> favWalls;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    WallpaperAdapter adapter;

    DatabaseReference dbFavs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites , container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favWalls = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressbar);
        adapter = new WallpaperAdapter(getActivity(), favWalls);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_area, new SettingsFragment())
                    .commit();
            return;
        }

        dbFavs = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");

        progressBar.setVisibility(View.VISIBLE);
        dbFavs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);

                for (DataSnapshot category : dataSnapshot.getChildren()) {

                    for (DataSnapshot wallpaperSnapshot : category.getChildren()) {


                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);

                        wallpaper w = new wallpaper(id, title, desc, url, category.getKey());
                        w.isFavourite = true;

                        favWalls.add(w);

                    }

                }

                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
