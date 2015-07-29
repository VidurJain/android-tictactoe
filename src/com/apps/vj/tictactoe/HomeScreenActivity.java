package com.apps.vj.tictactoe;

import com.apps.vj.tictactoe.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HomeScreenActivity extends Activity {

	SoundPool soundPool;
	int clickSound;
	boolean loaded = false;

	private void initializeSoundClips() {
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load the sound
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool
				.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool,
							int sampleId, int status) {
						loaded = true;
					}
				});
		clickSound = soundPool.load(this, R.raw.click, 1);
	}

	private void playSound(int sound) {
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundPool.play(sound, actualVolume, actualVolume, 1, 0, 1f);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initializeSoundClips();
		final RelativeLayout home = (RelativeLayout) findViewById(R.id.homescreen);
		final Context context = this;
		Button singlePlayer = (Button) home.findViewById(R.id.singlePlayer);
		singlePlayer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("SINGLE", true);
				playSound(clickSound);
				startActivity(intent);
			}
		});

		Button multiPlayer = (Button) home.findViewById(R.id.multiPlayer);
		multiPlayer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("SINGLE", false);
				playSound(clickSound);
				startActivity(intent);
			}
		});

		Button exit = (Button) home.findViewById(R.id.exit_btn);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playSound(clickSound);
				moveTaskToBack(true);
			}
		});

	}

}
