package com.example.lunatic.ske_blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

	private static final String TAG = "LoginActivity";
	private EditText mLoginEmailField;
	private EditText mLoginPasswordField;
	private Button mLoginBtn;
	private Button mNewAccountBtn;

	private FirebaseAuth mAuth;
	private DatabaseReference mDatabaseUsers;
	private ProgressDialog mProgress;
	private SignInButton mGoogleBtn;
	private static final int RC_SIGN_IN = 1;
	private GoogleApiClient mGoogleApiClient;

	/**
	 * create login page
	 *
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
		mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);

		mLoginEmailField = (EditText) findViewById(R.id.loginEmailField);
		mLoginPasswordField = (EditText) findViewById(R.id.loginPasswordField);
		mLoginBtn = (Button) findViewById(R.id.loginBtn);
		mNewAccountBtn = (Button) findViewById(R.id.newAccountBtn);

		mLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkLogin();

			}
		});

		mNewAccountBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent regieIntent = new Intent(LoginActivity.this, RegisterActivity.class);
				regieIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(regieIntent);
			}
		});





		// Google sign in
		// Configure Google Sign In
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

					@Override
					public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


					}
				})
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		mGoogleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				signIn();
			}
		});

	}

	/**
	 * sign in with google account.
	 */
	private void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}

	/**
	 * activity for sign in with google account.
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

			mProgress.setMessage("Strating sign in");
			mProgress.show();

			if (result.isSuccess()) {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = result.getSignInAccount();
				firebaseAuthWithGoogle(account);
			} else {
				// Google Sign In failed, update UI appropriately
				// ...
				mProgress.dismiss();
			}
		}
	}

	/**
	 * firebaseAuthWithGoogle check google account with firebase
	 *
	 * @param acct is account google
	 */
	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success" + task.isSuccessful());
							FirebaseUser user = mAuth.getCurrentUser();

						} else {

							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							Toast.makeText(LoginActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();

						}
						mProgress.dismiss();
						checkUserExist();
						// ...
					}
				});
	}



	/**
	 * checkLogin check log in from database
	 */
	private void checkLogin() {

		String email = mLoginEmailField.getText().toString().trim();
		String password = mLoginPasswordField.getText().toString().trim();

		if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
			mProgress.setMessage("Checking Login");
			mProgress.show();

			mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if (task.isSuccessful()) {

						mProgress.dismiss();
						checkUserExist();

					} else {
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

		if (mAuth.getCurrentUser() != null) {

			final String user_id = mAuth.getCurrentUser().getUid();
			mDatabaseUsers.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if (dataSnapshot.hasChild(user_id)) {
						Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mainIntent);

					} else {
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
}
