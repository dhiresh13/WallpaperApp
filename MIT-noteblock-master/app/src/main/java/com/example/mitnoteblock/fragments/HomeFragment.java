package com.example.mitnoteblock.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.mitnoteblock.R;
import com.example.mitnoteblock.adapters.CategoriesAdapter;
import com.example.mitnoteblock.models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private DatabaseReference dbCategories;
    private List<Category> categoryList;
private CategoriesAdapter adapter;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_home , container , false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar=view.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        categoryList= new ArrayList<>();

        adapter = new CategoriesAdapter(getActivity(),categoryList);
        recyclerView.setAdapter(adapter);

        dbCategories= FirebaseDatabase.getInstance().getReference("categories");

        dbCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    if (dataSnapshot.exists()){


                    progressBar.setVisibility(View.INVISIBLE);
                  String name=ds.getKey();
                  String desc=ds.child("desc").getValue(String.class);
                  String thumb=ds.child("thumbnail").getValue(String.class);

                    Category c=new Category(name,desc,thumb);
                    categoryList.add(c);
                }
adapter.notifyDataSetChanged();
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
