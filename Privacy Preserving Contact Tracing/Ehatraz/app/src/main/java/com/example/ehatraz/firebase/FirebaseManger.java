package com.example.ehatraz.firebase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ehatraz.R;
import com.example.ehatraz.storage.Key;
import com.example.ehatraz.storage.StorgeManger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class FirebaseManger
{
    private Context mContext;
    private static FirebaseManger instance = null;
    private FirebaseAuth mAuth;
    private ArrayList<Key> keys;
    private static final String TAG = "FirebaseManger";

    private FirebaseManger(Context context)
    {
        this.mContext =context;
    }
    public static FirebaseManger getInstance(Context context)
    {
        if(instance == null)
            instance = new FirebaseManger(context);
        return instance;
    }
    public FirebaseUser signIn(String password,String email)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(  new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            //updateUI(user);
                            /*
                                StorgeManger store = StoreManger.getInstance(mContext);
                                store.writeText(user.getUID, store.USER_ID);

                             */
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                        }
                    }
                });
        return mAuth.getCurrentUser();
    }
    public void read(Activity main) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Infected_Keys");
        Query q = myRef.orderByValue();
        StorgeManger store =  StorgeManger.getInstance(mContext);
        keys = (ArrayList<Key>) store.readBeacons();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    System.out.println(postSnapshot.getValue()+"key");
                    ArrayList map ;
                    map = (ArrayList) postSnapshot.getValue();
                    for(Object a: map)
                    {
                        String temp = a.toString().replace("key=","").replace("{","").replace("}","");
                        for (Key e : keys) {
                            if (e.getKey().equals(temp)) {
                                System.out.println("Infected discoverd");
                                GifImageView view = (GifImageView) main.findViewById(R.id.gifImageView);
                                view.setBackgroundColor(Color.RED);
                                Toast.makeText(mContext, "You have been contact with Infected Person", Toast.LENGTH_LONG).show();
//                                break;
//                            }
                        }
                    }



                            //System.out.println(e.getKey()+"best");


                        }
//                        if(postSnapshot.getChildren().iterator().hasNext()) {
//                            String a = (String) postSnapshot.getChildren().iterator().next().getValue();
//                            System.out.println(a + "Infected");
//                            if (e.getKey().equals(a)) {
//                                System.out.println("Infected discoverd");
//                                GifImageView view = (GifImageView) main.findViewById(R.id.gifImageView);
//                                view.setBackgroundColor(Color.RED);
//                                Toast.makeText(mContext, "You have been contact with Infected Person", Toast.LENGTH_LONG).show();
//                                break;
//                            }
//                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public void read(String child)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(child);
        Query q = myRef.orderByValue();
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    System.out.println(postSnapshot.getChildren().iterator().next().getValue()+"order");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public void addKeys()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Keys");
        myRef.orderByValue();
        StorgeManger store = StorgeManger.getInstance(mContext);
        ArrayList<Key> keys = (ArrayList<Key>) store.getInstance(mContext).readBeacons();
        for(Key k: keys)
            System.out.println("the keys are: " + k.getKey());
        myRef.push().setValue(keys);
    }
}
