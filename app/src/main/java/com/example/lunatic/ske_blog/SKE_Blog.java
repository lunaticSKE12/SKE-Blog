package com.example.lunatic.ske_blog;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * SKE_Blog create for offline capability
 * Created by Lunatic on 5/24/2017.
 */

public class SKE_Blog extends Application {

	public void onCreate() {
		super.onCreate();

		if (!FirebaseApp.getApps(this).isEmpty()) {
			FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		}

		Picasso.Builder builder = new Picasso.Builder(this);
		builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
		Picasso built = builder.build();
		built.setIndicatorsEnabled(false);
		built.setLoggingEnabled(true);
		Picasso.setSingletonInstance(built);

	}

}
