package com.aizen.manga.fragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import com.aizen.manga.DetailActivity;
import com.aizen.manga.R;
import com.aizen.manga.adapter.MangaListAdapter;
import com.aizen.manga.module.Manga;
import com.aizen.manga.util.NetAnalyse;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class HotMangaFrag extends Fragment implements OnDismissCallback,
		IXListViewListener {

	MangaListAdapter mangasAdapter;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	ArrayList<Manga> mangas = new ArrayList<Manga>();
	XListView mangaListView;
	private Handler handler = new Handler();
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private String FRAG_STRING_URL;
	private int page = 1;
	private RelativeLayout statusLayout;
	private ImageView statusImageView;
	private TextView statusTextView;
	// private ProgressDialog dialog;

	private static HotMangaFrag uniqueHotMangaFrag = null;

	public static HotMangaFrag newInstance() {
		if (uniqueHotMangaFrag == null) {
			uniqueHotMangaFrag = new HotMangaFrag();
		}
		return uniqueHotMangaFrag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mangas = new ArrayList<>();
		page = 1;
		View rootView = inflater.inflate(R.layout.fragment_manga_list,
				container, false);
		mangaListView = (XListView) rootView.findViewById(R.id.mangaList);
		mangaListView.setPullLoadEnable(true);
		mangaListView.setPullRefreshEnable(false);
		int layoutID = R.layout.listview_mangalist;
		mangaListView.setXListViewListener(this);
		mangaListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// position位置从1开始是因为位置0被headview占用了
				Intent it = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(MangaInfoFrag.MANGA_LINK_STRING, 
						getActivity().getString(R.string.domain)
						+ mangas.get(position - 1).getLink());
				it.putExtras(bundle); 
				startActivity(it);
				Toast.makeText(getActivity(),
						mangas.get(position - 1).getLink(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		mangasAdapter = new MangaListAdapter(getActivity(), layoutID, mangas);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mangasAdapter);
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(mangaListView);
		mangaListView.setAdapter(swingBottomInAnimationAdapter);

		FRAG_STRING_URL = getActivity().getResources().getString(
				R.string.hot_manga_list);
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// mangas = null;
					// mangas.add(new Manga("", "naruto", "anben", "9.9",
					// "none"));
					refreshHotMangaList(page);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					somethingWrong();
					e.printStackTrace();
				}
			}
		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		statusLayout = (RelativeLayout) getActivity().findViewById(R.id.ReadDataStatusLayout);
		statusImageView = (ImageView) getActivity().findViewById(R.id.StatusImage);
		statusTextView = (TextView) getActivity().findViewById(R.id.StatusText);
	}

	public void refreshHotMangaList(int pagenum) throws Exception {
		String hotMangaListURL = FRAG_STRING_URL + "-p" + pagenum;
		final ArrayList<Manga> mangaDataList = NetAnalyse.parseHtmlToList(
				hotMangaListURL, getActivity().getCacheDir().getAbsolutePath());
		try {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// mangas.add(new Manga("", "naruto", "anben", "9.9",
					// "none"));
					statusLayout.setVisibility(View.GONE);
					mangas.addAll(mangaDataList);
					mangasAdapter.notifyDataSetChanged();
					swingBottomInAnimationAdapter.notifyDataSetChanged();
					// dialog.dismiss();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			somethingWrong();
			e.printStackTrace();
		}
	}

	@Override
	public void onDismiss(final AbsListView listView,
			final int[] reverseSortedPositions) {
		// TODO Auto-generated method stub
		for (int position : reverseSortedPositions) {
			mangas.remove(position);
			mangasAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		page++;
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(page);
					refreshHotMangaList(page);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					somethingWrong();
				}
			}
		});
	}
	
	public void somethingWrong() {
		statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.wrong));
		statusTextView.setText(getResources().getString(R.string.status_text_wrong));
		statusLayout.setVisibility(View.VISIBLE);
	}
}
