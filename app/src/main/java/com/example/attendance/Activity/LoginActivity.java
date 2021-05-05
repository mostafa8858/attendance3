package com.example.attendance.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendance.DataBase.DataBaseFire;
import com.example.attendance.Domin.User;
import com.example.attendance.Domin.User_model;
import com.example.attendance.Prevalent;
import com.example.attendance.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL = "email";

    private LoginButton faceBookLogin;
    private ImageView registerImage;
    private TextView registertext,userSingIn,adminSignIn;
    private EditText edEmail, edPassword;
    private Button loginButton;
    private ProgressBar progressBar;
    public FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;
    private static final String TAG = "FacebookAuth";
    private DataBaseFire dataBaseFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        changeStatusBarColor();
        adminSignIn=findViewById(R.id.adminSignIn);
        userSingIn=findViewById(R.id.userSingIn);
        adminSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin ");
                adminSignIn.setVisibility(v.INVISIBLE);
                userSingIn.setVisibility(v.VISIBLE);
                Prevalent.DATA_BASE_NAME_User="Users";
            }
        });
        userSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login ");
                adminSignIn.setVisibility(v.VISIBLE);
                userSingIn.setVisibility(v.INVISIBLE);
                Prevalent.DATA_BASE_NAME_User="Users";
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        dataBaseFire = new DataBaseFire();

        FacebookSdk.sdkInitialize(LoginActivity.this);
        mCallbackManager = CallbackManager.Factory.create();


        faceBookLogin = findViewById(R.id.login_button_facebook);
        registerImage = findViewById(R.id.plus_image_in_login);
        registertext = findViewById(R.id.tv_register_in_login);
        edEmail = findViewById(R.id.editInputEmaillogin);
        edPassword = findViewById(R.id.editInputPasswordlogin);
        loginButton = findViewById(R.id.loginbutton);
        progressBar = findViewById(R.id.progressBar_login);

        firebaseAuth.signOut();

    }

    @Override
    public void onStart() {
        super.onStart();


        faceBookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFaceBook();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  userLogin();
                userLoginRealTime();

            }
        });
        registerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                finish();
            }
        });
        registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
                finish();
            }
        });


        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }

    }

    // FaceBook Login
    private void loginWithFaceBook() {

        faceBookLogin.setPermissions(Arrays.asList(EMAIL));
        faceBookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError" + error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), "login sucsses", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "please sign to continue", Toast.LENGTH_SHORT).show();
        }
    }
    // End of FaceBook Login

    /*  private void userLogin() {
          String email, password;
          email = edEmail.getText().toString();
          password = edPassword.getText().toString();

          //check Name
          if (email.isEmpty()) {
              edEmail.setError("Email is required");
              edEmail.requestFocus();
          }
          //check password
          else if (password.isEmpty()) {
              edPassword.setError("password is required");
              edPassword.requestFocus();
          }


          //Done
          else {
              progressBar.setVisibility(View.VISIBLE);
              firebaseAuth = FirebaseAuth.getInstance();
              firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if (task.isSuccessful()) {
                          if (dataBaseFire.adminCheck(email)) {
                              Toast.makeText(getBaseContext(),"admin",Toast.LENGTH_LONG).show();
                          } else if(dataBaseFire.StudentCheck(email)) {
                              Toast.makeText(getBaseContext(),"student",Toast.LENGTH_LONG).show();

                          }


                          progressBar.setVisibility(View.GONE);
                          Toast.makeText(getBaseContext(), "login sucsses", Toast.LENGTH_LONG).show();
                          startActivity(new Intent(getBaseContext(), AdminActivity.class));
                          finish();
                      } else {
                          progressBar.setVisibility(View.GONE);
                          Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();

                      }

                  }
              });

          }
      }*/
    private void userLoginRealTime() {
        String email, password;
        email = edEmail.getText().toString();
        password = edPassword.getText().toString();

        //check Name
        if (email.isEmpty()) {
            edEmail.setError("Email is required");
            edEmail.requestFocus();
        }
        //check password
        else if (password.isEmpty()) {
            edPassword.setError("password is required");
            edPassword.requestFocus();
        }


        //Done
        else {
            progressBar.setVisibility(View.VISIBLE);
            AllowAccessToAcountUser(email, password);
            AllowAccessToAcountAdmin(email,password);
        }


    }
   private void AllowAccessToAcountUser(String email, String  password){
       final DatabaseReference myRootRef;
       myRootRef = FirebaseDatabase.getInstance().getReference();
       myRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.child(Prevalent.DATA_BASE_NAME_User).child(email).exists()) {
                   User_model users_model = snapshot.child(Prevalent.DATA_BASE_NAME_User).child(email).getValue(User_model.class);
                   if (users_model.getEmailSinUp().equals(email)) {
                       if (users_model.getPassword().equals(password)) {

                           Intent intent = new Intent(getBaseContext(), MainActivity.class);
                           startActivity(intent);

                           Toast.makeText(LoginActivity.this, "Done Login ", Toast.LENGTH_SHORT).show();
                       } else {

                           Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       Toast.makeText(LoginActivity.this, "this account with this email " + email + " not exist", Toast.LENGTH_SHORT).show();

                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   }
    private void AllowAccessToAcountAdmin(String email, String  password){
       final DatabaseReference myRootRef;
       myRootRef = FirebaseDatabase.getInstance().getReference();
       myRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.child(Prevalent.DATA_BASE_NAME_ADMINS).child(email).exists()) {
                   User_model users_model = snapshot.child(Prevalent.DATA_BASE_NAME_ADMINS).child(email).getValue(User_model.class);
                   if (users_model.getEmailSinUp().equals(email)) {
                       if (users_model.getPassword().equals(password)) {

                           Intent intent = new Intent(getBaseContext(), AdminActivity.class);
                           startActivity(intent);

                           Toast.makeText(LoginActivity.this, "Done Login ", Toast.LENGTH_SHORT).show();
                       } else {

                           Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       Toast.makeText(LoginActivity.this, "this account with this email " + email + " not exist", Toast.LENGTH_SHORT).show();

                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });


}
    public void changeStatusBarColor () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.login_bk_color));
        }
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
