package com.apps.vj.tictactoe;

import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.vj.tictactoe.R;
import com.apps.vj.tictactoe.utils.ComputeComputerMove;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MainActivity extends Activity {

	HashMap<String, HashMap<String, Integer>> layoutCache = new HashMap<String, HashMap<String, Integer>>();
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, Boolean> tictacToe = new HashMap<Integer, Boolean>();

	static String zero = "ImageZero";
	static String X = "ImageX";

	static int currentPlayer = 1;
	static int lastPlayer = 1;

	boolean init = false;
	boolean soundOn = true;

	boolean computerWorking = false;
	@SuppressLint("UseSparseArrays")
	static HashMap<Integer, PointF> coordinatesMap = new HashMap<Integer, PointF>();

	boolean end;

	SoundPool soundPool;
	int player1Sound;
	int player2Sound;
	int tieSound;
	int winnerSound;
	boolean loaded = false;

	DrawView result1;
	DrawView result2;
	DrawView result3;
	DrawView result4;
	DrawView result5;
	DrawView result6;
	DrawView result8;
	DrawView result7;

	AlertDialog dialog1;
	AlertDialog dialog2;
	String player1Name = "Player1";
	String player2Name = "Player2";

	boolean singlePlayer;

	private AdView adView;

	// Convention: Player 1 will always have X
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeSoundClips();
		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a150f19509627e7");

		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());
		// parent.addView(adView);

		dialog1 = createDialog("Player 1", 1);
		singlePlayer = getIntent().getBooleanExtra("SINGLE", false);
		if (!singlePlayer) {
			dialog2 = createDialog("Player 2", 2);
			dialog2.show();
		} else {
			player2Name = "Device";
			TextView view = (TextView) findViewById(R.id.header);
			view.setText(player1Name + " vs " + player2Name);
		}
		dialog1.show();

		parent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				if (!init)
					init();
				if (end)
					return true;
				if (computerWorking)
					return true;
				if (fill(ev)) {
					playSound(currentPlayer == 1 ? player1Sound : player2Sound);
					switchPlayers();
					end = verifyEnd();
					if (end) {
						TextView view = (TextView) findViewById(R.id.info);
						if (currentPlayer != 0) {
							view.setText((currentPlayer == 1 ? player2Name
									: player1Name) + " wins.");
							playSound(winnerSound);
						} else {
							view.setText("Match Tied.");
							playSound(tieSound);
						}
						Button reset = (Button) findViewById(R.id.clear);
						reset.setText("New Game");
						System.out.println("game Ends here");
					}
					// FOR DEVICE
					else if (singlePlayer) {
						playComputerMove();
					}
				}
				return true;
			}
		});
	}

	AlertDialog createDialog(String player, final int playerId) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogview = inflater.inflate(R.layout.player_entry, null);
		final EditText playerName = (EditText) dialogview
				.findViewById(R.id.player_name);
		Button submit = (Button) dialogview.findViewById(R.id.btn_submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerId == 1) {
					String name = playerName.getText().toString();
					if (name != null && !name.trim().equals("")) {
						player1Name = name.substring(0, name.length() > 8 ? 8
								: name.length());
					}
					dialog1.dismiss();
					TextView view = (TextView) findViewById(R.id.header);
					view.setText(player1Name + " vs " + player2Name);
					TextView view1 = (TextView) findViewById(R.id.info);
					view1.setText(player1Name + "'s Turn");
				} else {
					String name = playerName.getText().toString();
					if (name != null && !name.trim().equals("")) {
						player2Name = name.substring(0, name.length() > 8 ? 8
								: name.length());
					}
					dialog2.dismiss();
					TextView view = (TextView) findViewById(R.id.header);
					view.setText(player1Name + " vs " + player2Name);
					TextView view1 = (TextView) findViewById(R.id.info);
					view1.setText(player1Name + "'s Turn");
				}
			}
		});

		Button ignore = (Button) dialogview.findViewById(R.id.btn_ignore);
		ignore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerId == 1) {
					dialog1.dismiss();
					if (singlePlayer) {
						TextView view = (TextView) findViewById(R.id.header);
						view.setText(player1Name + " vs " + player2Name);
					}
				} else {
					dialog2.dismiss();
				}
			}
		});

		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
		dialogbuilder.setTitle("Enter " + player + "'s Name");
		dialogbuilder.setView(dialogview);
		return dialogbuilder.create();
	}

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
		player1Sound = soundPool.load(this, R.raw.player1, 1);
		player2Sound = soundPool.load(this, R.raw.player2, 1);
		winnerSound = soundPool.load(this, R.raw.winner, 1);
		tieSound = soundPool.load(this, R.raw.tie, 1);
	}

	private void initCrossLines() {
		getImageCordinates();
		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
		result1 = new DrawView(this, coordinatesMap.get(1),
				coordinatesMap.get(3));
		result1.setVisibility(View.INVISIBLE);
		result2 = new DrawView(this, coordinatesMap.get(4),
				coordinatesMap.get(6));
		result2.setVisibility(View.INVISIBLE);
		result3 = new DrawView(this, coordinatesMap.get(7),
				coordinatesMap.get(9));
		result3.setVisibility(View.INVISIBLE);
		result4 = new DrawView(this, coordinatesMap.get(1),
				coordinatesMap.get(7));
		result4.setVisibility(View.INVISIBLE);
		result5 = new DrawView(this, coordinatesMap.get(2),
				coordinatesMap.get(8));
		result5.setVisibility(View.INVISIBLE);
		result6 = new DrawView(this, coordinatesMap.get(3),
				coordinatesMap.get(9));
		result6.setVisibility(View.INVISIBLE);
		result7 = new DrawView(this, coordinatesMap.get(3),
				coordinatesMap.get(7));
		result7.setVisibility(View.INVISIBLE);
		result8 = new DrawView(this, coordinatesMap.get(1),
				coordinatesMap.get(9));
		result8.setVisibility(View.INVISIBLE);
		parent.addView(result1);
		parent.addView(result2);
		parent.addView(result3);
		parent.addView(result4);
		parent.addView(result5);
		parent.addView(result6);
		parent.addView(result7);
		parent.addView(result8);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.exit:
			moveTaskToBack(true);
			return true;
		case R.id.sound:
			AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (soundOn) {
				volumeControl.setStreamMute(AudioManager.STREAM_MUSIC, true);
				soundOn = false;
				item.setTitle("Sound On");
			} else {
				volumeControl.setStreamMute(AudioManager.STREAM_MUSIC, false);
				soundOn = true;
				item.setTitle("Sound Off");
			}
			return true;
		case R.id.ratings:
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=com.apps.tictactoe#rate")));
			return true;
		case R.id.back:
			startActivity(new Intent(this, HomeScreenActivity.class));
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void init() {
		Log.d("TIC TAC TOE", "Init TTT");
		getLayoutCache();
		initCrossLines();
		init = true;
	}

	void getLayoutCache() {
		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
		View view1 = (View) parent.findViewById(R.id.view1);
		View view2 = (View) parent.findViewById(R.id.view2);
		View view3 = (View) parent.findViewById(R.id.view3);
		View view4 = (View) parent.findViewById(R.id.view4);
		View view5 = (View) parent.findViewById(R.id.view5);
		View view12 = (View) parent.findViewById(R.id.view12);

		String firstColumn = view2.getLeft() + "," + view2.getRight();
		layoutCache.put(firstColumn, new HashMap<String, Integer>());
		String secondColumn = view1.getLeft() + "," + view1.getRight();
		layoutCache.put(secondColumn, new HashMap<String, Integer>());
		String thirdColumn = view4.getLeft() + "," + view4.getRight();
		layoutCache.put(thirdColumn, new HashMap<String, Integer>());
		String firstRow = view3.getTop() + "," + view3.getBottom();
		layoutCache.get(firstColumn).put(firstRow, 1);
		layoutCache.get(secondColumn).put(firstRow, 2);
		layoutCache.get(thirdColumn).put(firstRow, 3);
		String secondRow = view5.getTop() + "," + view5.getBottom();
		layoutCache.get(firstColumn).put(secondRow, 4);
		layoutCache.get(secondColumn).put(secondRow, 5);
		layoutCache.get(thirdColumn).put(secondRow, 6);
		String thirdRow = view12.getTop() + "," + view12.getBottom();
		layoutCache.get(firstColumn).put(thirdRow, 7);
		layoutCache.get(secondColumn).put(thirdRow, 8);
		layoutCache.get(thirdColumn).put(thirdRow, 9);

	}

	void getImageCordinates() {
		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
		for (int i = 1; i < 10; i++) {
			try {
				String action1 = X + i;
				final ImageView view1 = (ImageView) parent
						.findViewById((R.id.class.getField(action1)
								.getInt(null)));
				coordinatesMap.put(i,
						new PointF(view1.getLeft(), view1.getTop()));
				System.out.println("Coordinate at posn=" + i + " is "
						+ (view1.getLeft() - view1.getWidth() / 2) + ","
						+ (view1.getTop() - view1.getHeight() / 2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean fill(MotionEvent ev) {
		float x = ev.getX();
		float y = ev.getY();
		System.out.println("Touch at " + ev.getX() + ", " + ev.getY());
		for (Entry<String, HashMap<String, Integer>> entry : layoutCache
				.entrySet()) {
			String key = entry.getKey();
			String[] splits = key.split(",");
			if (x > Integer.parseInt(splits[0])
					&& x < Integer.parseInt(splits[1])) {
				HashMap<String, Integer> anotherMap = entry.getValue();
				for (Entry<String, Integer> anotherEntry : anotherMap
						.entrySet()) {
					key = anotherEntry.getKey();
					splits = key.split(",");
					if (y > Integer.parseInt(splits[0])
							&& y < Integer.parseInt(splits[1])) {
						if (!tictacToe.containsKey(anotherEntry.getValue()))
							fillAt(anotherEntry.getValue());
						else
							return false;
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	protected void fillAt(Integer posn) {
		System.out.println("Fill X|0 at posn =" + posn);
		try {
			String actionImage;
			if (currentPlayer == 1)
				actionImage = X;
			else
				actionImage = zero;
			String action = actionImage + posn;
			final ImageView view = (ImageView) findViewById((R.id.class
					.getField(action).getInt(null)));
			view.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tictacToe.put(posn, currentPlayer == 1 ? true : false);
	}

	protected void switchPlayers() {
		TextView view = (TextView) findViewById(R.id.info);
		if (currentPlayer == 1) {
			currentPlayer = 2;
			view.setText(player2Name + "'s Turn");

		} else {
			currentPlayer = 1;
			view.setText(player1Name + "'s Turn");
		}
	}

	void playComputerMove() {
		computerWorking = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ComputeComputerMove strategy = new ComputeComputerMove(
						tictacToe);
				fillAt(strategy.computeMove());
				switchPlayers();
				end = verifyEnd();
				if (end) {
					TextView view = (TextView) findViewById(R.id.info);
					if (currentPlayer != 0) {
						view.setText((currentPlayer == 1 ? player2Name
								: player1Name) + " wins.");
						playSound(winnerSound);
					} else {
						view.setText("Match Tied.");
						playSound(tieSound);
					}
					Button reset = (Button) findViewById(R.id.clear);
					reset.setText("New Game");
				}
				computerWorking = false;
			}
		}, 1000);

	}

	boolean verifyEnd() {
		boolean done = false;
		try {
			int offset = 1;
			final RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
			for (int i = 0; i < 3; i++) {
				try {
					if (tictacToe.get(1 + i * 3).equals(
							tictacToe.get(2 + i * 3))
							&& tictacToe.get(1 + i * 3).equals(
									tictacToe.get(3 + i * 3))) {
						String result = "result" + (offset + i);
						if (i == 0)
							result1.setVisibility(View.VISIBLE);
						else if (i == 1)
							result2.setVisibility(View.VISIBLE);
						else if (i == 2)
							result3.setVisibility(View.VISIBLE);
						done = true;
						break;
					}
				} catch (NullPointerException e) {
					done = false;
				}
			}

			offset = 4;
			if (!done) {
				for (int i = 0; i < 3; i++) {
					try {
						if (tictacToe.get(1 + i).equals(tictacToe.get(4 + i))
								&& tictacToe.get(1 + i).equals(
										tictacToe.get(7 + i))) {
							String result = "result" + (offset + i);
							if (i == 0)
								result4.setVisibility(View.VISIBLE);
							else if (i == 1)
								result5.setVisibility(View.VISIBLE);
							else if (i == 2)
								result6.setVisibility(View.VISIBLE);
							done = true;
							break;
						}
					} catch (NullPointerException e) {
						done = false;
					}
				}
			}
			offset = 8;
			try {
				if (!done && tictacToe.get(1).equals(tictacToe.get(5))
						&& tictacToe.get(1).equals(tictacToe.get(9))) {
					done = true;
					result8.setVisibility(View.VISIBLE);
				}
			} catch (NullPointerException e) {
				done = false;
			}
			offset = 7;
			try {
				if (!done && tictacToe.get(3).equals(tictacToe.get(5))
						&& tictacToe.get(3).equals(tictacToe.get(7))) {
					done = true;
					result7.setVisibility(View.VISIBLE);
				}
			} catch (NullPointerException e) {
				done = false;
			}

			if (!done && tictacToe.size() == 9) {
				currentPlayer = 0;
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return done;
	}

	void clearViews() {
		clearLineViews();
		clearImageViews();
	}

	void clearLineViews() {
		/*
		 * final RelativeLayout parent = (RelativeLayout)
		 * findViewById(R.id.parent); try { for (int i = 1; i < 6; i++) {
		 * 
		 * String result = "result" + i; final View view2 = (View)
		 * parent.findViewById((R.id.class .getField(result).getInt(null)));
		 * view2.setVisibility(View.INVISIBLE);
		 * 
		 * } } catch (Exception e) { e.printStackTrace(); }
		 */

		if (result1 != null)
			result1.setVisibility(View.INVISIBLE);
		if (result2 != null)
			result2.setVisibility(View.INVISIBLE);
		if (result3 != null)
			result3.setVisibility(View.INVISIBLE);
		if (result4 != null)
			result4.setVisibility(View.INVISIBLE);
		if (result5 != null)
			result5.setVisibility(View.INVISIBLE);
		if (result6 != null)
			result6.setVisibility(View.INVISIBLE);
		if (result7 != null)
			result7.setVisibility(View.INVISIBLE);
		if (result8 != null)
			result8.setVisibility(View.INVISIBLE);

	}

	void clearImageViews() {
		final RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
		try {
			for (int i = 1; i < 10; i++) {

				String action1 = zero + i;
				String action2 = X + i;
				final ImageView view1 = (ImageView) parent
						.findViewById((R.id.class.getField(action1)
								.getInt(null)));
				view1.setVisibility(View.INVISIBLE);
				final ImageView view2 = (ImageView) parent
						.findViewById((R.id.class.getField(action2)
								.getInt(null)));
				view2.setVisibility(View.INVISIBLE);

			}
			/*
			 * final View winner = (View)
			 * parent.findViewById(R.id.class.getField( "winner").getInt(null));
			 * winner.setVisibility(View.INVISIBLE); final View tie = (View)
			 * parent.findViewById(R.id.class.getField( "tie").getInt(null));
			 * tie.setVisibility(View.INVISIBLE);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reset(View view) {
		if (singlePlayer)
			if (computerWorking)
				return;
		System.out.println("Resetting Game");
		clearViews();
		tictacToe.clear();
		end = false;
		currentPlayer = (lastPlayer == 1 ? 2 : 1);
		lastPlayer = currentPlayer;
		TextView tview = (TextView) findViewById(R.id.info);
		tview.setText((currentPlayer == 1 ? player1Name : player2Name)
				+ "'s Turn");
		Button reset = (Button) findViewById(R.id.clear);
		reset.setText("Restart");
		if (singlePlayer) {
			if (currentPlayer == 2) {
				playComputerMove();
			}
		}
	}

	private void playSound(int sound) {
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundPool.play(sound, actualVolume, actualVolume, 1, 0, 1f);
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
}
