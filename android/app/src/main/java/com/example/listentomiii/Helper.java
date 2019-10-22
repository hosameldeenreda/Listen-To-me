package com.example.listentomiii;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Helper extends AppCompatActivity {
    private String HelperId;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<String> IDS;
    private ArrayList<String> mUsers;
    ListView listView;
    TextView textView;
    ImageView Refr;
    ArrayAdapter<String> adapter;
    DatabaseReference usersdRef;
    private ProgressDialog mLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        listView = (ListView) findViewById(R.id.listview);
        textView = (TextView) findViewById(R.id.textView);
        mLoginProgress = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            HelperId = extras.getString("HelperId");
        }
        Refr = (ImageView) findViewById(R.id.Refresh);

        mUsers = new ArrayList<>();
        IDS = new ArrayList<>();
        readUsers();
        //Toast.makeText(Helper.this, IDS.size(), Toast.LENGTH_SHORT).show();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mUsers);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String value = adapter.getItem(position);
                Intent intent = new Intent(Helper.this, MessageActivityHelper.class);
                intent.putExtra("sender", HelperId);
                intent.putExtra("receiver", IDS.get(position));
                intent.putExtra("DeafName", mUsers.get(position));

                startActivity(intent);
            }
        });
        Refr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsers.clear();
                IDS.clear();
                readUsers();
                // adapter.notifyDataSetInvalidated();
                Toast.makeText(Helper.this, "تم تحديث الصفخة....", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void readIds() {

        usersdRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        usersdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String receiverId = ds.child("receiver").getValue(String.class);
                    String id = ds.child("sender").getValue(String.class);
                    if (receiverId.equals(HelperId) && (!IDS.contains(id))) {
                        IDS.add(id);
                       // Toast.makeText(Helper.this, String.valueOf(id), Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void readUsers() {
        readIds();
        //Toast.makeText(Helper.this, IDS.get(0), Toast.LENGTH_SHORT).show();
        usersdRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < IDS.size(); i++) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String receiverId = ds.child("id").getValue(String.class);
                            if (IDS.get(i).equals(receiverId)) {
                                String userNameTemp = ds.child("username").getValue(String.class);
                                 mUsers.add(userNameTemp);
                                //Toast.makeText(Helper.this, String.valueOf(userNameTemp), Toast.LENGTH_SHORT).show();
                                 break;
                                //Toast.makeText(Helper.this, userNameTemp, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(Helper.this, IDS.size(), Toast.LENGTH_SHORT).show();
                                }
                     }
                }
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
