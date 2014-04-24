package com.aizen.manga;

import java.util.ArrayList;

import com.aizen.manga.util.SystemUiHider;
import com.aizen.manga.fragment.ImageDetailFragment;
import com.aizen.manga.util.ImageFetcher;
import com.aizen.manga.BuildConfig;
import com.aizen.manga.util.Utils;
import com.aizen.manga.R;
import com.aizen.manga.util.ImageCache;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MangaActivity extends Activity implements OnClickListener {
	private static final String IMAGE_CACHE_DIR = "images";
	public static final String EXTRA_IMAGE = "extra_image";

	private ImagePagerAdapter mAdapter;
	private ImageFetcher mImageFetcher;
	private ViewPager mPager;
	private ArrayList<String> imageurls = new ArrayList<>();
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * ´«ÈëURL
	 */
	public static final String CHAPTER_LINK_KEY = "chapterlink";

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@TargetApi(VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Utils.enableStrictMode();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manga);

		imageurls.add("http://i3.manhuadao.com/bcover/2012/12/261757398_h.jpg");
		imageurls
				.add("http://cartoon.jide123.cc:8080/manhuatuku/1835/2014417171542670.jpg");
		imageurls
				.add("http://t6.mangafiles.com:88/Files/Images/3682/99100/imanhua_001.jpg");
		// imageurls.addAll(savedInstanceState.getStringArrayList(CHAPTER_LINK_KEY));
		
		// Fetch screen height and width, to use as our max size when loading
		// images as this
		// activity runs full screen
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		// For this sample we'll use half of the longest width to resize our
		// images. As the
		// image scaling ensures the image is larger than this, we should be
		// left with a
		// resolution that is appropriate for both portrait and landscape. For
		// best image quality
		// we shouldn't divide by 2, but this will use more memory and require a
		// larger memory
		// cache.
		final int longest = (height > width ? height : width) / 2;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(this, longest);
		mImageFetcher.addImageCache(getFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);

		// Set up ViewPager and backing adapter
		mAdapter = new ImagePagerAdapter(getFragmentManager(), imageurls);
		mPager = (ViewPager) findViewById(R.id.imagepager);
		mPager.setAdapter(mAdapter);
		mPager.setPageMargin((int) getResources().getDimension(
				R.dimen.horizontal_page_margin));
		mPager.setOffscreenPageLimit(2);

		// Set up activity to go full screen
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

		// Enable some additional newer visibility and ActionBar features to
		// create a more
		// immersive photo viewing experience
		if (Utils.hasHoneycomb()) {
			final ActionBar actionBar = getActionBar();

			// Hide title text and set home as up
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);

			// Hide and show the ActionBar as the visibility changes
			mPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int vis) {
					if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
						actionBar.hide();
					} else {
						actionBar.show();
					}
				}
			});

			// Start low profile mode and hide ActionBar
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			actionBar.hide();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.clear_cache:
			mImageFetcher.clearCache();
			Toast.makeText(this, R.string.clear_cache_complete_toast,
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Called by the ViewPager child fragments to load images via the one
	 * ImageFetcher
	 */
	public ImageFetcher getImageFetcher() {
		return mImageFetcher;
	}

	/**
	 * The main adapter that backs the ViewPager. A subclass of
	 * FragmentStatePagerAdapter as there could be a large number of items in
	 * the ViewPager and we don't want to retain them all in memory at once but
	 * create/destroy them on the fly.
	 */
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
		private final int mSize;
		private final ArrayList<String> mUrls;

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> urls) {
			super(fm);
			mSize = urls.size();
			mUrls = urls;
		}

		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailFragment.newInstance(mUrls.get(position));
		}
	}

	/**
	 * Set on the ImageView in the ViewPager children fragments, to
	 * enable/disable low profile mode when the ImageView is touched.
	 */
	@TargetApi(VERSION_CODES.HONEYCOMB)
	@Override
	public void onClick(View v) {
		final int vis = mPager.getSystemUiVisibility();
		if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		} else {
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}
}
