package com.aizen.manga;

import java.util.ArrayList;

import com.aizen.manga.fragment.ImageDetailFragment;
import com.aizen.manga.util.FileLoad;
import com.aizen.manga.util.ImageCache;
import com.aizen.manga.util.ImageFetcher;
import com.aizen.manga.util.Utils;
import com.aizen.manga.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import android.os.Build.VERSION_CODES;

public class LocalMangaActivity extends Activity implements OnClickListener{

	private static final String IMAGE_CACHE_DIR = "images";
	public static final String EXTRA_IMAGE = "extra_image";

	private ImagePagerAdapter mAdapter;
	private ImageFetcher mImageFetcher;
	private ViewPager mPager;
	private ArrayList<String> imageuris = new ArrayList<>();
	public static String chapteruri = "";


	public static final String LOCAL_CHAPTER_KEY = "local_chapter";


	@TargetApi(VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Utils.enableStrictMode();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manga);

		chapteruri = getIntent().getExtras().getString(LOCAL_CHAPTER_KEY);
		System.out.println(chapteruri);
		imageuris = FileLoad.getDirFileURIs(chapteruri);
		

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;


		final int longest = (height > width ? height : width) / 2;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f);


		mImageFetcher = new ImageFetcher(this, longest);
		mImageFetcher.addImageCache(getFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);

		// Set up ViewPager and backing adapter
		mAdapter = new ImagePagerAdapter(getFragmentManager(), imageuris);
		mPager = (ViewPager) findViewById(R.id.imagepager);
		mPager.setAdapter(mAdapter);
		mPager.setPageMargin((int) getResources().getDimension(
				R.dimen.horizontal_page_margin));
		mPager.setOffscreenPageLimit(2);

		// Set up activity to go full screen
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);


		if (Utils.hasHoneycomb()) {
			final ActionBar actionBar = getActionBar();

			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayHomeAsUpEnabled(true);

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

			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			actionBar.hide();
			findViewById(R.id.fullscreen_content_controls).setVisibility(View.INVISIBLE);
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
		//private final int mSize;
		private final ArrayList<String> mUrls;

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> urls) {
			super(fm);
			//mSize = urls.size();
			mUrls = urls;
		}

		@Override
		public int getCount() {
			return mUrls.size();
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
			findViewById(R.id.fullscreen_content_controls).setVisibility(View.VISIBLE);
		} else {
			mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			findViewById(R.id.fullscreen_content_controls).setVisibility(View.INVISIBLE);
		}
	}


}
