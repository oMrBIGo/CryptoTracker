package com.nathit.cryptotacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nathit.cryptotacker.Model.ModelUser;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    TextView tvName, tvEmail;
    ProgressBar progressBar;
    Dialog dialog;
    TextInputEditText etName;
    LinearLayout Show_edit_email, Show_Change_password, Show_Delete_User;

    private TextInputEditText textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPassword;
    private Button buttonUpdateEmail;
    private CardView buttonUpdateEmailCv;
    private TextInputEditText editTextNewEmail, editTextPassword;
    private TextView tvAuthenticated, tvAuthenticatedTt;

    private TextInputEditText editTextPasswordCurrent, editTextPasswordNew, editTextPasswordConfirm;
    private TextView textViewAuthenticatedCP;
    private Button buttonChangePassword, buttonReAuthenticate;
    private CardView buttonChangePasswordCardView;
    private String userPasswordCurrent;

    private TextInputEditText editTextUserPassword;
    private TextView textViewAuthenticateDu;
    private String userPasswordDu;
    private Button buttonReAuthenticateDu, buttonDeleteUser;
    private CardView buttonDeleteCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        init_screen();

        dialog = new Dialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tvName = findViewById(R.id.name);
        tvEmail = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);

        showUserProfile(firebaseUser);

        Show_edit_email = findViewById(R.id.Show_Edit_email);
        Show_Change_password = findViewById(R.id.Show_Change_password);
        Show_Delete_User = findViewById(R.id.Show_Delete_Account);

        //button click show dialog edit profile
        Button editProfile = (Button) findViewById(R.id.button_editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfileDialog();
                Show_edit_email.setVisibility(View.GONE); // hide edit email
                Show_Change_password.setVisibility(View.GONE); // hide change password
                Show_Delete_User.setVisibility(View.GONE); // hide Delete account
            }
        });

        //button click show edit email
        Button ShEditEmail = (Button) findViewById(R.id.button_editEmail);
        ShEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_edit_email.setVisibility(View.VISIBLE); //show edit email
                Show_Change_password.setVisibility(View.GONE); // hide change password
                Show_Delete_User.setVisibility(View.GONE); // hide Delete account
                editEmail();
            }
        });

        //button click show change password
        Button changePasswordBtn = (Button) findViewById(R.id.button_changePassword);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_edit_email.setVisibility(View.GONE); //hide edit email
                Show_Change_password.setVisibility(View.VISIBLE); // show change password
                Show_Delete_User.setVisibility(View.GONE); // hide Delete account
                changePassword();
            }
        });

        //button click show Delete account
        Button DeleteAccountBtn = (Button) findViewById(R.id.button_DeleteUser);
        DeleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_edit_email.setVisibility(View.GONE); //hide edit email
                Show_Change_password.setVisibility(View.GONE); // hide change password
                Show_Delete_User.setVisibility(View.VISIBLE); // show Delete account
                DeleteAccount();
            }
        });

    }

    //show My Profile
    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                if (modelUser != null) {
                    String name = "" + snapshot.child("name").getValue();
                    String email = "" + snapshot.child("email").getValue();

                    tvName.setText("Name : " + name);
                    tvEmail.setText("Email : " + email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEditProfileDialog() {
        dialog.setContentView(R.layout.edit_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        TextInputLayout textInputLayout = dialog.findViewById(R.id.text_input_name);
        textInputLayout.setHintEnabled(false);

        etName = dialog.findViewById(R.id.name);
        String userID = firebaseUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = snapshot.getValue(ModelUser.class);
                if (modelUser != null) {
                    String name = "" + snapshot.child("name").getValue();
                    etName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something is wrong!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonUpdateProfile = dialog.findViewById(R.id.ButtonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

        Button cancelBtn = dialog.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        showUserProfile(firebaseUser);
        dialog.show();
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        String Name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(Name)) {
            etName.setError("Please enter your name.");
            etName.requestFocus();
        } else {
            HashMap<String, Object> result = new HashMap<>();
            result.put("name", Name);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
            String userID = firebaseUser.getUid();
            databaseReference.child(userID).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Name).build();
                            firebaseUser.updateProfile(profileChangeRequest);
                            Toast.makeText(UserProfileActivity.this, "Profile has been edited.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            restartApp();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //show edit Email
    private void editEmail() {
        editTextPassword = findViewById(R.id.editText_update_password_v);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_old);
        buttonUpdateEmail = findViewById(R.id.ButtonUpdateEmail);
        textViewAuthenticated.setOnTouchListener(otl);
        buttonUpdateEmailCv = findViewById(R.id.buttonUpdateEmailCardView);
        tvAuthenticated = findViewById(R.id.textView_update_email_new_header);
        tvAuthenticatedTt = findViewById(R.id.textView_update_email_authenticated_a);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        userOldEmail = firebaseUser.getEmail();
        textViewAuthenticated.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Something is wrong! no user details.", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }
    }

    private View.OnTouchListener otl = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.Button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPassword = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(UserProfileActivity.this, "Password is required to continue.",
                            Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Please enter your password for authentication.");
                    editTextPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //set Text to show that user is authenticated
                                tvAuthenticated.setText("You have been checked");
                                tvAuthenticatedTt.setText("Password us confirmed");
                                Toast.makeText(UserProfileActivity.this, "Password is confirmed"
                                        + "You can change your email address", Toast.LENGTH_SHORT).show();

                                editTextNewEmail.setEnabled(true);
                                editTextPassword.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UserProfileActivity.this,
                                        R.color.purple_200));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editTextNewEmail.getText().toString().trim();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            editTextNewEmail.setError("Please enter the email you wish to change.");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            editTextPassword.setError("Please enter your email.");
                                            editTextPassword.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            editTextNewEmail.setError("Please enter a new email address.");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateValueEmail();
                                        }
                                    }
                                });

                                buttonUpdateEmailCv.setCardBackgroundColor(ContextCompat.getColorStateList(
                                        UserProfileActivity.this, R.color.purple_200));
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UserProfileActivity.this, "Please enter your Password correction.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateValueEmail() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("email", userNewEmail);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateEmail(firebaseUser);
                Toast.makeText(UserProfileActivity.this, "Email information has been updated." +
                                "if you want to go back to your old email Please check your email massage.",
                        Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, "Can't edit email information Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UserProfileActivity.this, "Email updated Please verify your new email." +
                            "Please login again to verify your email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //show change password
    private void changePassword() {
        editTextPasswordNew = findViewById(R.id.editText_change_password_new);
        editTextPasswordCurrent = findViewById(R.id.editText_change_password_current);
        editTextPasswordConfirm = findViewById(R.id.editText_change_password_new_confirm);
        textViewAuthenticatedCP = findViewById(R.id.textView_change_password_authenticated);
        buttonReAuthenticate = findViewById(R.id.Button_change_password_authenticate_user);
        buttonChangePassword = findViewById(R.id.ButtonChangePassword);
        buttonChangePasswordCardView = findViewById(R.id.ButtonChangePasswordCardView);

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Something is wrong! no user details", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticatePassword(firebaseUser);
        }
    }

    private void reAuthenticatePassword(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPasswordCurrent = editTextPasswordCurrent.getText().toString().trim();
                String checkPassword = "^(?=\\S+$).{6,20}$";

                if (TextUtils.isEmpty(userPasswordCurrent)) {
                    Toast.makeText(UserProfileActivity.this, "password required.", Toast.LENGTH_SHORT).show();
                    editTextPasswordCurrent.setError("Please enter your current password to authenticate.");
                    editTextPasswordCurrent.requestFocus();
                } else if (!userPasswordCurrent.matches(checkPassword)) {
                    editTextPasswordCurrent.setError("Please enter a password of 6 characters or more.");
                    editTextPasswordCurrent.requestFocus();
                }
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPasswordCurrent);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            //Disable editText for Current Password / Enable EditText for new Password and Confirm New Password
                            editTextPasswordCurrent.setEnabled(false);
                            editTextPasswordNew.setEnabled(true);
                            editTextPasswordConfirm.setEnabled(true);
                            //Enable Change Password Button. Disable Authenticate Button
                            buttonReAuthenticate.setEnabled(false);
                            buttonChangePassword.setEnabled(true);
                            //set TextView to show User is authenticated / verified
                            textViewAuthenticatedCP.setText("You have been checked/comfirmed."
                                    + "You can change your password instantly!");

                            Toast.makeText(UserProfileActivity.this, "The Password has been confirmed."
                                    + "Change new password", Toast.LENGTH_SHORT).show();

                            //Update color of button Change password
                            buttonChangePasswordCardView.setCardBackgroundColor(ContextCompat.getColorStateList(
                                    UserProfileActivity.this, R.color.purple_200));

                            buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    changePasswordConfirm(firebaseUser);
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(UserProfileActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void changePasswordConfirm(FirebaseUser firebaseUser) {
        String userPasswordNew = editTextPasswordNew.getText().toString().trim();
        String userPasswordConfirmNew = editTextPasswordConfirm.getText().toString();
        String checkPassword = "^(?=\\S+$).{6,20}$";
        if (TextUtils.isEmpty(userPasswordNew)) {
            Toast.makeText(this, "New password is required.", Toast.LENGTH_SHORT).show();
            editTextPasswordNew.setError("Please enter your new password");
            editTextPasswordNew.requestFocus();
        } else if (!userPasswordNew.matches(checkPassword)) {
            editTextPasswordConfirm.setError("Please enter a password of 6 characters or more.");
            editTextPasswordConfirm.requestFocus();
        } else if (TextUtils.isEmpty(userPasswordConfirmNew)) {
            Toast.makeText(this, "Please confirm you new password.", Toast.LENGTH_SHORT).show();
            editTextPasswordConfirm.setError("Please enter a new password again");
            editTextPasswordConfirm.requestFocus();
        } else if (!userPasswordConfirmNew.matches(checkPassword)) {
            editTextPasswordConfirm.setError("Please do not match");
            editTextPasswordConfirm.requestFocus();
        } else if (!userPasswordNew.matches(userPasswordConfirmNew)) {
            Toast.makeText(this, "Please do not match", Toast.LENGTH_SHORT).show();
            editTextPasswordConfirm.setError("Please enter the same password again");
            editTextPasswordConfirm.requestFocus();
        } else if (!userPasswordCurrent.matches(userPasswordCurrent)) {
            Toast.makeText(this, "The new password must not be the same as the old password.",
                    Toast.LENGTH_SHORT).show();
            editTextPasswordNew.setError("Please enter a new password.");
            editTextPasswordNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfileActivity.this, "password has been changed.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    //show Delete account
    private void DeleteAccount() {
        editTextUserPassword = findViewById(R.id.editText_Delete_User);
        textViewAuthenticateDu = findViewById(R.id.textView_Delete_User_Authenticated);
        buttonDeleteUser = findViewById(R.id.ButtonDeleteUser);
        buttonReAuthenticateDu = findViewById(R.id.Button_delete_user_authenticate);
        buttonDeleteCardView = findViewById(R.id.ButtonDeleteCardView);

        //Disable Delete user Button until Suer is authenticated
        buttonDeleteUser.setEnabled(false);

        if (firebaseUser.equals("")) {
            Toast.makeText(this, "Something is wrong! no user details.", Toast.LENGTH_SHORT).show();
        }
        reAuthenticateDelete(firebaseUser);

    }

    private void reAuthenticateDelete(FirebaseUser firebaseUser) {
        buttonReAuthenticateDu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPasswordDu = editTextUserPassword.getText().toString();
                if (TextUtils.isEmpty(userPasswordDu)) {
                    Toast.makeText(UserProfileActivity.this, "password required.", Toast.LENGTH_SHORT).show();
                    editTextUserPassword.setError("Please enter your current password to authenticate.");
                    editTextUserPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    //ReAuthenticate User new
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPasswordDu);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                //Disable editText for Password.
                                editTextUserPassword.setEnabled(false);
                                //Enable Delete User Button. Disable Authenticate Button
                                buttonReAuthenticateDu.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                //set TextView to show User is authenticated / verified
                                textViewAuthenticateDu.setText("You have been checked." +
                                        "You can cancel your account immediately. Please be careful, " +
                                        "You will not be able to recover your account again.");

                                Toast.makeText(UserProfileActivity.this, "Password has been confirmed.",
                                        Toast.LENGTH_SHORT).show();

                                //Update color of Change password Button
                                buttonDeleteCardView.setBackgroundTintList(ContextCompat.getColorStateList(
                                        UserProfileActivity.this, R.color.purple_200));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UserProfileActivity.this, "Please enter your password correctly.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Delete User Account");
        builder.setMessage("Do you want to cancel your profile and related information?" +
                "You will never be able recover your data again!");

        //Open Email apps if User Click/taps Continues button
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserData();
            }
        });

        //Create the AlertDialog
        final AlertDialog alertDialog = builder.create();

        //Return to User Profile Activity User presses Cancel Button
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        //Show the AlertDialog
        alertDialog.show();
    }

    private void deleteUserData() {
        //Delete Data from Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteUser(firebaseUser);
                Toast.makeText(UserProfileActivity.this, "The user account has been canceled successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfileActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.signOut();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UserProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //restartActivity
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
        finish();
    }

    //init_screen
    private void init_screen() {
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

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(UserProfileActivity.this, CryptoActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_user) {
            Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);

        }
        if (id == R.id.nav_history) {
            Intent intent = new Intent(UserProfileActivity.this, HistoryActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    //onBackPressed
    int backPressed = 0;

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}