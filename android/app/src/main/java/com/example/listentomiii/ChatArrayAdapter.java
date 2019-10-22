package com.example.listentomiii;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<Chat> {

    private TextView chatText;
    private List<Chat> chatMessageList = new ArrayList<Chat>();
    private Context context;

    @Override
    public void add(Chat object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }
    public void remove() {
        chatMessageList.clear();
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public Chat getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.chat_item_right, parent, false);
        }else{
            row = inflater.inflate(R.layout.chat_item_left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(chatMessageObj.getMessage());
        return row;
    }
}