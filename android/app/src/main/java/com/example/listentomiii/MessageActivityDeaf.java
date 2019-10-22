package com.example.listentomiii;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MessageActivityDeaf extends AppCompatActivity {
    String helperId;
    String helperUserName;
    String deafId;
    double longitude=0.0;
    double latitude=0.0;
    TextView ProfileNameView;
    EditText TextSend;
    ArrayList<Chat> messages;
    ArrayList<String>realMessages;
    ImageView Refr;
    ListView listView;
    TextView textView;
    Button sendBtn;
    Button Record;
    private static final int VIDEO_CAPTURE = 101;
    private Uri fileUri;
    MediaMetadataRetriever mediaMetadataRetriever = null;

    //ArrayAdapter<String> adapter;
    private ChatArrayAdapter chatArrayAdapter;
    private boolean side = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle extras = getIntent().getExtras();
        helperId = extras.getString("message1");
        helperUserName = extras.getString("message2");
        deafId = extras.getString("deafId");
        longitude = Double.parseDouble(extras.getString("longitude"));
        latitude = Double.parseDouble(extras.getString("latitude"));
        TextSend=(EditText) findViewById(R.id.text_send);
        ProfileNameView=(TextView)findViewById(R.id.ProfileName);
        ProfileNameView.setText(helperUserName);
        listView=(ListView)findViewById(R.id.listview);
        TextSend=(EditText)findViewById(R.id.text_send);
        textView=(TextView)findViewById(R.id.textView);
        Refr=(ImageView)findViewById(R.id.Refresh);
        sendBtn=(Button)findViewById(R.id.btn_send);
        ProfileNameView=(TextView)findViewById(R.id.ProfileName);
        Record=(Button)findViewById(R.id.RecordVideoo);
        ProfileNameView.setText(helperUserName);
        messages=new ArrayList<>();
        realMessages=new ArrayList<>();
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_item_right);
        updateMessages();
        listView.setDivider(null);
        Refr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMessages();
                Toast.makeText(MessageActivityDeaf.this, "refresh", Toast.LENGTH_SHORT).show();
                messages.clear();
                realMessages.clear();
                updateMessages();
                //adapter.notifyDataSetInvalidated();
            }
        });

        Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = Uri.fromFile(mediaFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, VIDEO_CAPTURE);
            }
        });

    }


    private void SaveImage(Bitmap finalBitmap,int i) {
        /*
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File image = new File(sdCardDirectory, "test"+String.valueOf(i)+".png");
        boolean success = false;

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (success) {
            Toast.makeText(getApplicationContext(), "Image saved with success",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error during image saving", Toast.LENGTH_LONG).show();
        }

    }


    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver );
        hashMap.put("message", message);
        hashMap.put("longitude", longitude);
        hashMap.put("latitude", latitude);

        reference.child("Chats").push().setValue(hashMap);
    }
    public void Send(View view) {
        String msg = TextSend.getText().toString();
        if (!msg.equals("")) {
            side = true;
            sendMessage(deafId,helperId, String.valueOf(TextSend.getText()));
            chatArrayAdapter.add(new Chat(side,msg));
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MessageActivityDeaf.this, "You can't send an empty message",
                    Toast.LENGTH_SHORT).show();
        }
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
                        if((receiverId.equals(deafId)&&senderId.equals(helperId)))
                            receivehistory(false);
                        else
                            receivehistory(true);

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
    public void receivehistory(boolean temp) {
        //Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        side=temp;
        String msg = TextSend.getText().toString();
        chatArrayAdapter.add(new Chat(side,msg));
        //side = !side;
        TextSend.setText("");

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            MediaMetadataRetriever tRetriever = new MediaMetadataRetriever();
            try {
                tRetriever.setDataSource(getBaseContext(), uri);
                mediaMetadataRetriever = tRetriever;
                String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec  = Long.parseLong(time );
                Toast.makeText(this, "timeInMillisec", Toast.LENGTH_SHORT).show();

                for(int i=1000000;i<timeInMillisec*1000;i+=1000000){
                    Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(i );
                    SaveImage(bmFrame,i);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                Toast.makeText(MessageActivityDeaf.this,
                        "Something Wrong!",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
