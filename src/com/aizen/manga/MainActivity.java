package com.aizen.manga;

import com.aizen.manga.fragment.HotMangaFrag;
import com.aizen.manga.fragment.LocalMangaFrag;
import com.aizen.manga.fragment.MyMangaFrag;
import com.aizen.manga.fragment.NavigationDrawerFragment;
import com.aizen.manga.fragment.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.aizen.manga.fragment.RecentMangaFrag;
import com.aizen.manga.util.Utils;
import com.astuetz.PagerSlidingTabStrip;
import com.aizen.manga.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v13.app.FragmentPagerAdapter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class MainActivity extends Activity implements NavigationDrawerCallbacks {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private NavigationDrawerFragment mNavigationDrawerFragment;

	private PagerSlidingTabStrip tabs;
	private RelativeLayout statusLayout;
	private ImageView statusImageView;
	private TextView statusTextView;
	private Drawable oldBackground = null;
	private int currentColor;
	private final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initValue();
		initPager();
		if (!isNetworkConnected(this)) {
			statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.timeout));
			statusTextView.setText(getResources().getString(R.string.status_text_timeout));
			mViewPager.setVisibility(View.GONE);
		}
	}

	private void initValue() {
		if (Utils.hasSDCard()) {
			LocalMangaFrag.loadLocalMangaDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
			System.out.println(LocalMangaFrag.loadLocalMangaDir);
			//System.out.println(Environment.DIRECTORY_DOWNLOADS);
			//request.setDestinationInExternalPublicDir(getActivity().getFilesDir().getAbsolutePath()+"/"+mangaDetail.getId()+"/"+chapterName+"/", pagename);
			//System.out.println(getActivity().getFilesDir().getAbsolutePath()+"/"+mangaDetail.getId()+"/"+chapterName+"/");
		} else {
			LocalMangaFrag.loadLocalMangaDir = getFilesDir().getAbsolutePath();
			//request.setDestinationUri(Uri.fromFile(new File(getActivity().getFilesDir().getAbsolutePath()+"/"+mangaDetail.getId()+"/aabbbbbb.gif")));
		}
	}
	
	private void initPager() {
		setContentView(R.layout.activity_main);
		currentColor = getResources().getColor(R.color.ActionBarDeepBlue);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.main_navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.main_navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		// Set up the action bar.
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		statusLayout = (RelativeLayout) findViewById(R.id.ReadDataStatusLayout);
		statusImageView = (ImageView) findViewById(R.id.StatusImage);
		statusTextView = (TextView) findViewById(R.id.StatusText);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		tabs.setViewPager(mViewPager);
		changeColor(currentColor);
	}
	
	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	private void changeColor(int newColor) {

		tabs.setIndicatorColor(newColor);

		// change ActionBar color just if an ActionBar is available
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			Drawable colorDrawable = new ColorDrawable(newColor);
			Drawable bottomDrawable = getResources().getDrawable(
					R.drawable.actionbar_bottom);
			LayerDrawable ld = new LayerDrawable(new Drawable[] {
					colorDrawable, bottomDrawable });

			if (oldBackground == null) {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
					ld.setCallback(drawableCallback);
				} else {
					getActionBar().setBackgroundDrawable(ld);
				}

			} else {

				TransitionDrawable td = new TransitionDrawable(new Drawable[] {
						oldBackground, ld });

				// workaround for broken ActionBarContainer drawable handling on
				// pre-API 17 builds
				// https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
					td.setCallback(drawableCallback);
				} else {
					getActionBar().setBackgroundDrawable(td);
				}

				td.startTransition(200);

			}

			oldBackground = ld;

			// http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
			getActionBar().setDisplayShowTitleEnabled(false);
			getActionBar().setDisplayShowTitleEnabled(true);

		}

		currentColor = newColor;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		ComponentName cn = new ComponentName(this, SearchResultActivity.class);
		SearchView searchView = (SearchView) menu.findItem(R.id.searchManga)
				.getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return HotMangaFrag.newInstance();
			case 1:
				return RecentMangaFrag.newInstance();
			case 2:
				return MyMangaFrag.newInstance();
			case 3:
				return LocalMangaFrag.newInstance();	
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.hot_tab_title);
			case 1:
				return getString(R.string.rec_tab_title);
			case 2:
				return getString(R.string.fav_tab_title);
			case 3:
				return getString(R.string.loc_tab_title);
			}
			return null;
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// TODO Auto-generated method stub

	}

}
