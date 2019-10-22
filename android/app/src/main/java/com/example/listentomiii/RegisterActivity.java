package com.example.listentomiii;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    MaterialEditText username, email, password, password2;
    Button btnRegister;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    private ProgressDialog mLoginProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);


        final Spinner spinner;
        username = findViewById(R.id.edt_username);
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);
        password2 = findViewById(R.id.edt_password2);
        btnRegister = findViewById(R.id.btn_register);
        mLoginProgress = new ProgressDialog(this);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        final List<String> categories = new ArrayList<String>();
        categories.add("اصم");
        categories.add("مساعد");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_password2 = password2.getText().toString();

                if (!Network()) {
                    Toast.makeText(RegisterActivity.this, "Check Network!", Toast.LENGTH_SHORT).show();
                    mLoginProgress.hide();
                } else {
                    if (txt_username.equals("")) {
                        username.setError("من فضلك ادخل اسمك!");
                    }
                    if (txt_email.equals("")) {
                        email.setError("من فضلك ادخل البريد الالكترونى");
                    }
                    if (txt_password.equals("")) {
                        password.setError("من فضلك ادخل الرقم السرى");
                    }
                    if (txt_password2.equals("")) {
                        password2.setError("من فضلك ادخل اذ كنت اصم ام مساعد!");
                    }
                    if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_password2)) {
                        Toast.makeText(RegisterActivity.this, "من فضلك ادخل جميع البيانات..", Toast.LENGTH_SHORT).show();
                    } else if (txt_password.length() < 8) {
                        Toast.makeText(RegisterActivity.this, "الرقم السرى لابد ان لا يقل عن 8 حروف او ارقام..", Toast.LENGTH_SHORT).show();

                    } else {
                        mLoginProgress.setTitle("يتم انشاء حساب جديد");
                        mLoginProgress.setMessage("برجاء الانتظار.....");
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();
                        registerUser(txt_username, txt_email, txt_password, txt_password2, 0, 0);
                        Toast.makeText(RegisterActivity.this, "تم انشاء حساب جديد بنجاح..!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }
            }
        });
    }

    private void registerUser(final String username, String email, String password, final String Type, final double longitude, final double latitude) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            final HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", String.valueOf(R.drawable.logoo));
                            hashMap.put("Type", Type);
                            if (Type.equals("مساعد")) {
                                hashMap.put("longitude", String.valueOf(longitude));
                                hashMap.put("latitude", String.valueOf(latitude));
                            }
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mLoginProgress.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this,
                                                "Check Network Again...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            //  Toast.makeText(RegisterActivity.this,"That email is already taken.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                password2.setText("اصم");
                break;
            case 1:
                password2.setText("مساعد");
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public boolean Network() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }
}
