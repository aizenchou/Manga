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
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RecentMangaFrag extends Fragment implements OnDismissCallback,
		IXListViewListener {
	MangaListAdapter mangasAdapter;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	ArrayList<Manga> mangas = new ArrayList<Manga>();
	XListView mangaListView;
	private Handler handler = new Handler();
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private String FRAG_STRING_URL;
	private int page = 1;

	// private ProgressDialog dialog;

	private static RecentMangaFrag uniqueRecentMangaFrag = null;

	public static RecentMangaFrag newInstance() {
		if (uniqueRecentMangaFrag == null) {
			uniqueRecentMangaFrag = new RecentMangaFrag();
		}
		return uniqueRecentMangaFrag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mangas = new ArrayList<>();
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
				Intent it = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(MangaInfoFrag.MANGA_LINK_STRING, 
						getActivity().getString(R.string.domain)
						+ mangas.get(position - 1).getLink());
				it.putExtras(bundle); // it.putExtra(“test”, "shuju”);
				startActivity(it);
				Toast.makeText(getActivity(),
						mangas.get(position - 1).getLink(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		mangasAdapter = new MangaListAdapter(getActivity(), layoutID, mangas);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mangasAdapter, this));
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(mangaListView);
		mangaListView.setAdapter(swingBottomInAnimationAdapter);
		// dialog = ProgressDialog.show(getActivity(), "Loading",
		// "正在加载。。。。，请稍等！");
		FRAG_STRING_URL = getActivity().getResources().getString(
				R.string.recent_manga_list);
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					// mangas = null;
					refreshRecMangaList(page);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return rootView;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void refreshRecMangaList(int pagenum) throws Exception {
		String recMangaListURL = FRAG_STRING_URL + "-p" + pagenum;
		final ArrayList<Manga> mangaDataList = NetAnalyse.parseHtmlToList(
				recMangaListURL, getActivity().getCacheDir().getAbsolutePath());
		try {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mangas.addAll(mangaDataList);
					mangasAdapter.notifyDataSetChanged();
					swingBottomInAnimationAdapter.notifyDataSetChanged();
					// dialog.dismiss();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
					refreshRecMangaList(page);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
