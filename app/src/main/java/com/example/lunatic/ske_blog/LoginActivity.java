package com.example.lunatic.ske_blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * login activity
 *
 * @author napong dungduangsasitorn
 */
public class LoginActivity extends AppCompatActivity {

	private EditText mLoginEmailField;
	private EditText mLoginPasswordField;
	private Button mLoginBtn;

	private FirebaseAuth mAuth;
	private DatabaseReference mDatabaseUsers;
	private ProgressDialog mProgress;

	/**
	 * create login page
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mAuth = FirebaseAuth.getInstance();
		mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
		mDatabaseUsers.keepSynced(true);
		mProgress = new ProgressDialog(this);

		mLoginEmailField = (EditText) findViewById(R.id.loginEmailField);
		mLoginPasswordField = (EditText) findViewById(R.id.loginPasswordField);
		mLoginBtn = (Button) findViewById(R.id.loginBtn);

		mLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkLogin();

			}
		});

	}

	/**
	 * checkLogin check log in from database
	 */
	private void checkLogin() {

		String email = mLoginEmailField.getText().toString().trim();
		String password = mLoginPasswordField.getText().toString().trim();

		if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
			mProgress.setMessage("Checking Login");
			mProgress.show();

			mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if(task.isSuccessful()){

						mProgress.dismiss();
						checkUserExist();

					}else{
						mProgress.dismiss();
						Toast.makeText(LoginActivity.this, "Error Login", Toast.LENGTH_LONG).show();
					}

				}
			});

		}
	}

	/**
	 * checkUserExist check user exist in database
	 */
	private void checkUserExist() {
		final String user_id = mAuth.getCurrentUser().getUid();
		mDatabaseUsers.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.hasChild(user_id)){
					Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
					mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(mainIntent);

				}else{
					Toast.makeText(LoginActivity.this, "You need to setup your account.", Toast.LENGTH_LONG).show();
					Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
					setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(setupIntent);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}
}
