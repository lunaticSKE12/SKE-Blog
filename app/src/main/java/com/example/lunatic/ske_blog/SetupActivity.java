package com.example.lunatic.ske_blog;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * SetupActivity set set user account
 */
public class SetupActivity extends AppCompatActivity {

	private ImageButton mSetupImageBtn;
	private EditText mNameField;
	private Button mSubmitBtn;

	private static final int GALLERY_REQUEST = 1;

	/**
	 * create setup page
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);

		mSetupImageBtn = (ImageButton) findViewById(R.id.setupImageBtn);
		mNameField = (EditText) findViewById(R.id.setupNameField);
		mSubmitBtn = (Button) findViewById(R.id.setupSubmitBtn);

		mSetupImageBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent galleryIntent = new Intent();
				galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
				galleryIntent.setType("image/*");
				startActivityForResult(galleryIntent, GALLERY_REQUEST);
			}
		});
	}

	/**
	 * onActivityResult request image image from user phone and crop image.
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

			Uri imageUri = data.getData();

			CropImage.activity(imageUri)
					.setGuidelines(CropImageView.Guidelines.ON)
					.setAspectRatio(1, 1)
					.start(this);


		}

		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);

			if (resultCode == RESULT_OK) {

				Uri resultUri = result.getUri();
				mSetupImageBtn.setImageURI(resultUri);

			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception error = result.getError();
			}
		}
	}
}
