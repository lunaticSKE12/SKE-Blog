package com.example.lunatic.ske_blog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

/**
 * PostActivity post page to add image, title, and description.
 *
 * @author napong dungduangsasitorn
 */
public class PostActivity extends AppCompatActivity {

	private ImageButton mSelectImage;
	private EditText mPostTitle;
	private EditText mPosrDesc;
	private Button mSubmitBtn;
	private Uri mImageUri = null;
	private StorageReference mStorage;
	private ProgressDialog mProgress;
	private static final int MAX_LENGTH = 50;
	private DatabaseReference mDatabase;

	private static final int GALLERY_REQUEST = 1;


	/**
	 * create views, bind data to lists
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		mStorage = FirebaseStorage.getInstance().getReference();
		mDatabase = FirebaseDatabase.getInstance().getReference().child("blog");
		mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
		mPostTitle = (EditText) findViewById(R.id.titleField);
		mPosrDesc = (EditText) findViewById(R.id.descField);
		mSubmitBtn = (Button) findViewById(R.id.summitBtn);
		mProgress = new ProgressDialog(this);



		mSelectImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
				galleryIntent.setType("image/*");
				startActivityForResult(galleryIntent, GALLERY_REQUEST);
			}
		});

		mSubmitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				startPosting();
			}
		});

	}

	/**
	 * startPosting post to blog
	 */
	private void startPosting() {

		mProgress.setMessage("Posting to Blog");


		final String title_val = mPostTitle.getText().toString().trim();
		final String desc_val = mPosrDesc.getText().toString().trim();

		if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null) {

			mProgress.show();

			// get file path and random image name.
			StorageReference filepath = mStorage.child("Blog_Images").child(random());

			filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

					Uri downloadUrl = taskSnapshot.getDownloadUrl();
					DatabaseReference newPost = mDatabase.push();

					newPost.child("title").setValue(title_val);
					newPost.child("desc").setValue(desc_val);
					newPost.child("image").setValue(downloadUrl.toString());

					mProgress.dismiss();

					startActivity(new Intent(PostActivity.this, MainActivity.class));
				}
			});

		} else {
			//if image or title field or description empty will show alert.
			new AlertDialog.Builder(PostActivity.this)
					.setTitle("Alert")
					.setMessage("Title and Description and Image can't empty")
					.setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mProgress.dismiss();
				}
			}).show();
		}

	}

	/**
	 *
	 * @return random string
	 */
	public static String random() {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int randomLength = generator.nextInt(MAX_LENGTH);
		char tempChar;
		for (int i = 0; i < randomLength; i++) {
			tempChar = (char) (generator.nextInt(96) + 32);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}

	/**
	 * onActivityResult start an activity that lets the user pick a person in a list of contacts
	 *
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * request image from gallery and get image.
		 */
		if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
			mImageUri = data.getData();

			mSelectImage.setImageURI(mImageUri);
		}
	}


}
