package com.inc.droidcast.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.inc.droidcast.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	@BindView(R.id.btn_playlist) Button btnPlaylist;
	@BindView(R.id.btn_podcastSearch) Button btnPodcastSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);
		btnPlaylist.setOnClickListener(this);
		btnPodcastSearch.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == btnPodcastSearch) {
			Intent intent = new Intent(MainActivity.this, PodcastSearchActivity.class);
			startActivity(intent);
		}

		if(v == btnPlaylist) {
			Intent intent = new Intent(MainActivity.this, PlaylistMenuActivity.class);
			startActivity(intent);
		}

	}
}
