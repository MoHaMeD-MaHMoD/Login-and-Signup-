package com.example.trackyourhealth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackyourhealth.LogInAndSignUp.Helper;
import com.example.trackyourhealth.LogInAndSignUp.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference mUserReference;
    @BindView(R.id.Wellcome)
    TextView Wellcome;
    @BindView(R.id.Name)
    TextView Name;
    @BindView(R.id.Email)
    TextView Email;
    @BindView(R.id.ProfilePic)
    ImageView ProfilePic;
    @BindView(R.id.Logout_Button)
    Button LogoutButton;
    @BindView(R.id.activated)
    TextView activated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // get database instant
        auth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ((auth.getCurrentUser() == null)) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
  if (auth.getCurrentUser().isEmailVerified()){
            // Write new User to DataBse
            Helper helper = new Helper();

            helper.updateIsVerified(auth.getUid(), true);


        }


        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);

                // Set Profie Data
                Name.setText(user.name);
                Email.setText(user.email);
                if (user.isVerifyed)
                    activated.setText("Your Email is Verified");
                else
                    activated.setText("Your Email is NOT Verified");
                String photoUrl = user.photoUri;
                Picasso.get().load(photoUrl).into(ProfilePic);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Toast.makeText(MainActivity.this, databaseError.toException().getMessage(),
                        Toast.LENGTH_LONG).show();                // ...
            }
        };

        if ((auth.getCurrentUser() != null)) {

            String usreID = auth.getCurrentUser().getUid();


            mUserReference.child("users").child(usreID).addValueEventListener(userListener);
        }

    }

    @OnClick(R.id.Logout_Button)
    public void onViewClicked() {

        auth.signOut();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }


}


