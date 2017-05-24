package com.example.lunatic.ske_blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * MainActivity is first page in android app
 *
 * @author napong dungduangsasitorn
 */
public class MainActivity extends AppCompatActivity {

	private RecyclerView mBlogList;
	private DatabaseReference mDatabase;
	private DatabaseReference mDatabaseUsers;
	private DatabaseReference mDatabaseLike;
	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private boolean mProcessLike = false;

	/**
	 * onCreate is create page view by activity_main
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				if (firebaseAuth.getCurrentUser() == null) {

					Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
					loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(loginIntent);

				}

			}
		};

		mDatabase = FirebaseDatabase.getInstance().getReference().child("blog");
		mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
		mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

		mDatabaseUsers.keepSynced(true);
		mDatabase.keepSynced(true);
		mDatabaseLike.keepSynced(true);

		mBlogList = (RecyclerView) findViewById(R.id.blog_list);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setReverseLayout(true);
		layoutManager.setStackFromEnd(true);

		mBlogList.setHasFixedSize(true);
		mBlogList.setLayoutManager(new LinearLayoutManager(this));

		checkUserExist();


	}

	/**
	 * onStart set start
	 */
	@Override
	protected void onStart() {
		super.onStart();


		mAuth.addAuthStateListener(mAuthListener);

		// create FirebaseRecyclerAdapter and set image, title, description, username's post
		FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter =
				new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
						Blog.class,
						R.layout.blog_row,
						BlogViewHolder.class,
						mDatabase
				) {
					@Override
					protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

						final String post_key = getRef(position).getKey();

						viewHolder.setTitle(model.getTitle());
						viewHolder.setDesc(model.getDesc());
						viewHolder.setImage(getApplicationContext(), model.getImage());
						viewHolder.setUsername("post by " + model.getUsername());
						viewHolder.setLikeBtn(post_key);

						viewHolder.mView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								//Toast.makeText(MainActivity.this, post_key, Toast.LENGTH_LONG).show();

								Intent singleBlogIntent = new Intent(MainActivity.this, BlogSingleActivity.class);
								singleBlogIntent.putExtra("blog_id", post_key);
								startActivity(singleBlogIntent);
							}
						});

						// set like button listener
						viewHolder.mLkieBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mProcessLike = true;


								mDatabaseLike.addValueEventListener(new ValueEventListener() {
									@Override
									public void onDataChange(DataSnapshot dataSnapshot) {

										if (mProcessLike) {
											if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

												mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
												mProcessLike = false;

											} else {
												mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
												mProcessLike = false;

											}
										}

									}

									@Override
									public void onCancelled(DatabaseError databaseError) {

									}
								});
							}

						});

					}
				};

		mBlogList.setAdapter(firebaseRecyclerAdapter);

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
					if (!dataSnapshot.hasChild(user_id)) {
						Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
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

	/**
	 * BlogViewHolder set view in blog page
	 * for use in post card view infomation.
	 */
	public static class BlogViewHolder extends RecyclerView.ViewHolder {

		View mView;
		ImageButton mLkieBtn;
		DatabaseReference mDatabaseLike;
		FirebaseAuth mAuth;

		//TextView post_title;

		/**
		 * BlogViewHolder create BlogViewHolder
		 *
		 * @param itemView view item to create
		 */
		public BlogViewHolder(View itemView) {
			super(itemView);

			mView = itemView;
			mLkieBtn = (ImageButton) mView.findViewById(R.id.likeBtn);
			/*post_title = (TextView) mView.findViewById(R.id.post_title);

			post_title.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					Log.v("MainActivity", "some text");
				}
			});*/

			mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
			mAuth = FirebaseAuth.getInstance();

			mDatabaseLike.keepSynced(true);
		}

		public void setLikeBtn(final String post_key){

			mDatabaseLike.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

						mLkieBtn.setImageResource(R.mipmap.ic_thumb_up_blue_24dp);

					}else{
						mLkieBtn.setImageResource(R.mipmap.ic_thumb_up_gray_24dp);

					}

				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}

		/**
		 * set title of post
		 *
		 * @param title
		 */
		public void setTitle(String title) {

			TextView post_title = (TextView) mView.findViewById(R.id.post_title);
			post_title.setText(title);

		}

		/**
		 * set description of post
		 *
		 * @param desc
		 */
		public void setDesc(String desc) {

			TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
			post_desc.setText(desc);
		}

		/**
		 * set username of post
		 *
		 * @param username
		 */
		public void setUsername(String username) {
			TextView post_usename = (TextView) mView.findViewById(R.id.post_username);
			post_usename.setText(username);

		}

		/**
		 * set image of post
		 *
		 * @param ctx
		 * @param image
		 */
		public void setImage(final Context ctx, final String image) {
			final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
			//Picasso.with(ctx).load(image).into(post_image);
			Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
				@Override
				public void onSuccess() {

				}

				@Override
				public void onError() {
					Picasso.with(ctx).load(image).into(post_image);
				}
			});

		}

	}

	/**
	 * onCreateOptionsMenu set menu icon and action.
	 *
	 * @param menu is menu from main_menu.
	 * @return new menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * onOptionsItemSelected set action on add icon
	 * to start new page(new Activity).
	 *
	 * @param item is MenuItem.
	 * @return item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_add) {
			startActivity(new Intent(MainActivity.this, PostActivity.class));
		}
		if (item.getItemId() == R.id.action_logout) {
			logout();
		}

		return super.onOptionsItemSelected(item);
	}

	private void logout() {
		mAuth.signOut();
	}

}
