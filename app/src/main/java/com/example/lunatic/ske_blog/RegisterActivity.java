package com.example.lunatic.ske_blog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

	private EditText mNameField;
	private EditText mEmailField;
	private EditText mPasswordField;

	private Button mRegisterBtn;
	private FirebaseAuth mAuth;
	private DatabaseReference mDatabase;
	private ProgressDialog mProgress;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		mAuth = FirebaseAuth.getInstance();

		mProgress = new ProgressDialog(this);
		mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ske-blog.firebaseio.com/").child("Users");

		mNameField = (EditText) findViewById(R.id.nameField);
		mEmailField = (EditText) findViewById(R.id.emailField);
		mPasswordField = (EditText) findViewById(R.id.passwordField);
		mRegisterBtn = (Button) findViewById(R.id.registerBtn);

		mRegisterBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				startRegister();
			}
		});


	}

	private void startRegister() {
		final String  name = mNameField.getText().toString().trim();
		String email = mEmailField.getText().toString().trim();
		String password = mPasswordField.getText().toString().trim();

		if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

			mProgress.setMessage("Signing Up");
			mProgress.show();

			mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if(task.isSuccessful()){

						String user_id = mAuth.getCurrentUser().getUid();
						DatabaseReference current_user_db = mDatabase.child(user_id);
						current_user_db.child("name").setValue(name);
						current_user_db.child("image").setValue("dafault");

						mProgress.dismiss();

						Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mainIntent);

					}else{
						new AlertDialog.Builder(RegisterActivity.this)
								.setTitle("Alert")
								.setMessage("This username might be already exist please change your name and email or not an email.")
								.setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mProgress.dismiss();
							}
						}).show();
					}

				}
			});
		}

	}
}
