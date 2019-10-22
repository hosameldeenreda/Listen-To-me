package com.example.listentomiii;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class Deaf extends AppCompatActivity {

    Button b;
    private LatLng CurLction;
    FirebaseAuth mAuth;
    private ArrayList<User> users;
    private double min= Double.MAX_VALUE;
    double longitude=0.0;
    double latitude=0.0;
    int targetUser=0;
    String deafId;

    public Deaf() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            String value = extras.getString("message");
            String value1 = extras.getString("message1");
            deafId = extras.getString("deafId");
            latitude= Double.parseDouble(value);
            longitude= Double.parseDouble(value1);
        }

        setContentView(R.layout.activity_deaf);
        b = (Button) findViewById(R.id.go);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference usersdRef = rootRef.child("Users");
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("Type").getValue(String.class).equals("مساعد"))
                            {
                                String id = ds.child("id").getValue(String.class);
                                String longitudeString = ds.child("longitude").getValue(String.class);
                                String latitudeString = ds.child("latitude").getValue(String.class);
                                String username = ds.child("username").getValue(String.class);
                                User u = new User(Double.parseDouble(longitudeString), Double.parseDouble(latitudeString), id,username);
                                users.add(u);
                                //Toast.makeText(Deaf.this, users.get(0).getId(), Toast.LENGTH_SHORT).show();
                            }
                        }
                            //Toast.makeText(Deaf.this, String.valueOf(users.size()), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < users.size(); ++i) {
                            double distance=GetDistance(longitude,users.get(i).getLongitude(),latitude,users.get(i).getLatitude());
                            if(distance<min)
                            {
                                min=distance;
                                targetUser=i;
                            }
                        }
                        //Toast.makeText(Deaf.this, users.get(targetUser).getId(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(Deaf.this, "الآن أنت مع أقرب مساعد ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Deaf.this, MessageActivityDeaf.class);
                        intent.putExtra("message1",users.get(targetUser).getId() );
                        intent.putExtra("message2",users.get(targetUser).getUsername() );
                        intent.putExtra("deafId",deafId);
                        intent.putExtra("longitude",String.valueOf(longitude));
                        intent.putExtra("latitude",String.valueOf(latitude));

                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                usersdRef.addListenerForSingleValueEvent(eventListener);
            }

        });
    }



    public double GetDistance(double x1,double x2,double y1,double y2)
    {
        double dis;
        dis=Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
        return dis;
    }
}
