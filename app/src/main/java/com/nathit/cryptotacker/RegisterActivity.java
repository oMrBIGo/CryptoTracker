package com.nathit.cryptotacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, password, confirmPassword;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init_screen();

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        CheckBox mCheckBox = (CheckBox) findViewById(R.id.checkbox);

        progressBar = findViewById(R.id.progressBar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);

        TextView nextToLogin = (TextView) findViewById(R.id.nextToLogin);
        nextToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button RegisterBtn = (Button) findViewById(R.id.RegisterBtn);
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String etEmail = email.getText().toString().trim();
                String etName = name.getText().toString().trim();
                String etPassword = password.getText().toString().trim();
                String etConfirmPassword = confirmPassword.getText().toString().trim();
                String checkPassword = "^(?=\\S+$).{6,20}$";

                if (mCheckBox.isChecked()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()) {
                        email.setError("Please enter your email address completely.");
                        email.requestFocus();
                    } else if (TextUtils.isEmpty(etName)) {
                        name.setError("Please enter your name.");
                        name.requestFocus();
                    } else if (TextUtils.isEmpty(etPassword)) {
                        password.setError("Please enter your password");
                        password.requestFocus();
                    } else if (!etPassword.matches(checkPassword)) {
                        password.setError("Please enter a password of 6 characters or more.");
                        password.requestFocus();
                    } else if (TextUtils.isEmpty(etConfirmPassword)) {
                        confirmPassword.setError("Please confirm your password.");
                        confirmPassword.requestFocus();
                    } else if (!etConfirmPassword.matches(checkPassword)) {
                        confirmPassword.setError("Please enter the same password.");
                        confirmPassword.requestFocus();
                    } else if (!etPassword.matches(etConfirmPassword)) {
                        confirmPassword.setError("Passwords do not match!");
                        confirmPassword.requestFocus();
                    } else {
                        registerCreateUser(etEmail, etName, etPassword);
                    }
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(etEmail).matches()) {
                        email.setError("Please enter your email address completely.");
                        email.requestFocus();
                    } else if (TextUtils.isEmpty(etName)) {
                        name.setError("Please enter your name.");
                        name.requestFocus();
                    } else if (TextUtils.isEmpty(etPassword)) {
                        password.setError("Please enter your password");
                        password.requestFocus();
                    } else if (!etPassword.matches(checkPassword)) {
                        password.setError("Please enter a password of 6 characters or more.");
                        password.requestFocus();
                    } else if (TextUtils.isEmpty(etConfirmPassword)) {
                        confirmPassword.setError("Please confirm your password.");
                        confirmPassword.requestFocus();
                    } else if (!etConfirmPassword.matches(checkPassword)) {
                        confirmPassword.setError("Please enter the same password.");
                        confirmPassword.requestFocus();
                    } else if (!etPassword.matches(etConfirmPassword)) {
                        confirmPassword.setError("Passwords do not match!");
                        confirmPassword.requestFocus();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please read the conditions before registering.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void registerCreateUser(String etEmail, String etName, String etPassword) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(etEmail, etPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            uid = user.getUid();
                            String etEmail = user.getEmail();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("email", etEmail);
                            hashMap.put("name", etName);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("User");

                            reference.child(uid).setValue(hashMap);
                            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                            startActivity(intent);
                            History();
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                password.setError("You password is too weak.");
                                password.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                email.setError("You email is invalid or is already active");
                                email.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                email.setError("User has already registered with this email. Please register with another email address.");
                                email.requestFocus();
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void History() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        String ip = getLocalIpAddress();
        String time = String.valueOf(System.currentTimeMillis());
        String id = databaseReference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("ip", ip);
        hashMap.put("time", time);

        //put this data in database
        databaseReference.child(uid).child("History").child(id).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //added
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed, not added
                    }
                });
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumeration = networkInterface.getInetAddresses(); enumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = enumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

        private void init_screen () {
            final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(flags);

            final View view = getWindow().getDecorView();
            view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        view.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        int backPressed = 0;
        @Override
        public void onBackPressed () {
            backPressed++;
            if (backPressed == 1) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            super.onBackPressed();
        }
    }