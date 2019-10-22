package com.example.listentomiii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivityHelper extends AppCompatActivity {
    String helperId;
    String deafId;
    String DeafName;
    TextView ProfileNameView;
    EditText TextSend;
    ArrayList<Chat>messages;
    ArrayList<String>realMessages;
    ImageView Refr;
    ListView listView;
    TextView textView;
    Button sendBtn;
    ImageView GoToMapp;
    double helperLongitude=0.0;
    double helperLatitude=0.0;
    double deafLongitude=0.0;
    double deafLatitude=0.0;

    //ArrayAdapter<String> adapter;
    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_helper);
        Bundle extras = getIntent().getExtras();
        listView=(ListView)findViewById(R.id.listview);
        helperId = extras.getString("sender");
        deafId = extras.getString("receiver");
        DeafName = extras.getString("DeafName");
        TextSend=(EditText)findViewById(R.id.text_send);
        textView=(TextView)findViewById(R.id.textView);
        Refr=(ImageView)findViewById(R.id.Refresh);
        GoToMapp=(ImageView)findViewById(R.id.GoToMap);
        sendBtn=(Button)findViewById(R.id.btn_send);
        ProfileNameView=(TextView)findViewById(R.id.ProfileName);
        ProfileNameView.setText(DeafName);
        messages=new ArrayList<>();
        realMessages=new ArrayList<>();
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_item_right);
        updateMessages();
        getHelperLocation();
        listView.setDivider(null);

        GoToMapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageActivityHelper.this, String.valueOf(helperLatitude), Toast.LENGTH_SHORT).show();
                Toast.makeText(MessageActivityHelper.this, String.valueOf(helperLongitude), Toast.LENGTH_SHORT).show();
                Toast.makeText(MessageActivityHelper.this, String.valueOf(deafLatitude), Toast.LENGTH_SHORT).show();
                Toast.makeText(MessageActivityHelper.this, String.valueOf(deafLongitude), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MessageActivityHelper.this, Directions.class);
                intent.putExtra("message",String.valueOf(deafLatitude) );
                intent.putExtra("message1",String.valueOf(deafLongitude));
                startActivity(intent);
            }
        });

        Refr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMessages();
                Toast.makeText(MessageActivityHelper.this, "refresh", Toast.LENGTH_SHORT).show();
                messages.clear();
                realMessages.clear();
                updateMessages();
                //adapter.notifyDataSetInvalidated();
            }
        });

    }
    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver );
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }

    public void Send(View view) {
        //Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        String msg = TextSend.getText().toString();

        if (!msg.equals("")) {
            side = true;
            sendMessage(helperId,deafId, String.valueOf(TextSend.getText()));
            chatArrayAdapter.add(new Chat(side,msg));
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MessageActivityHelper.this, "You can't send an empty message",
                    Toast.LENGTH_SHORT).show();
        }
        TextSend.setText("");
    }

    public void receivehistory(boolean temp) {
        //Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        side=temp;
        String msg = TextSend.getText().toString();
        chatArrayAdapter.add(new Chat(side,msg));
        //side = !side;
        TextSend.setText("");

    }

    public void updateMessages(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference usersdRef = rootRef.child("Chats");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String receiverId=ds.child("receiver").getValue(String.class);
                    String senderId = ds.child("sender").getValue(String.class);
                    if ((receiverId.equals(helperId)&&senderId.equals(deafId))||(receiverId.equals(deafId)&&senderId.equals(helperId))) {
                        String message = ds.child("message").getValue(String.class);
                        Chat chat=new Chat(senderId,receiverId,message);
                        TextSend.setText(message);
                        if((receiverId.equals(helperId)&&senderId.equals(deafId))) {
                            receivehistory(false);
                            Log.d("aaa",message);
                            deafLongitude=ds.child("longitude").getValue(Double.class);
                            deafLatitude= ds.child("latitude").getValue(Double.class);
                        }
                        else {
                            receivehistory(true);

                        }
                        messages.add(chat);

                        //Toast.makeText(MessageActivityHelper.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                listView.setAdapter(chatArrayAdapter);
                chatArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);

    }
    public void removeMessages(){
        chatArrayAdapter.remove();
        chatArrayAdapter.notifyDataSetChanged();
    }
    public void getHelperLocation(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference usersdRef = rootRef.child("Users");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id=ds.child("id").getValue(String.class);
                    if (id.equals(helperId)) {
                        helperLongitude=Double.parseDouble(ds.child("longitude").getValue(String.class));
                        helperLatitude=Double.parseDouble(ds.child("latitude").getValue(String.class));

                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        usersdRef.addListenerForSingleValueEvent(eventListener);
    }
}
