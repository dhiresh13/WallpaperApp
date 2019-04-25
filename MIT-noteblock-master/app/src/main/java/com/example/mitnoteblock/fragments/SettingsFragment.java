package com.example.mitnoteblock.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mitnoteblock.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsFragment extends Fragment {

    private static final int Google_sign_in_code=212;
    private GoogleSignInClient mGoogleSignInClient;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return inflater.inflate(R.layout.fragment_settings_default, container, false);

        }
        return inflater.inflate(R.layout.fragment_settings_loggedin, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            ImageView imageView = view.findViewById(R.id.image_view);
            TextView textViewName = view.findViewById(R.id.text_view_name);
            TextView textViewEmail = view.findViewById(R.id.text_view_email);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(getActivity())
                    .load(user.getPhotoUrl().toString())
                    .into(imageView);


            textViewName.setText(user.getDisplayName());
            textViewEmail.setText(user.getEmail());


            view.findViewById(R.id.text_view_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area, new SettingsFragment())
                                    .commit();
                        }
                    });


                }
            });









        } else {
            view.findViewById(R.id.google_signin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent, Google_sign_in_code);
                }
            });
        }
    }
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Google_sign_in_code) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
           AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(),
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_area, new SettingsFragment())
                                        .commit();

                                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Login Failure", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }