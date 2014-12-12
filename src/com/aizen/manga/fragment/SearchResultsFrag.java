package com.aizen.manga.fragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import com.aizen.manga.DetailActivity;
import com.aizen.manga.R;
import com.aizen.manga.adapter.SearchResultListAdapter;
import com.aizen.manga.module.Manga;
import com.aizen.manga.util.NetAnalyse;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultsFrag extends Fragment implements IXListViewListener {

	public final static String QUERRY_KEY = "searchQuerryKey";

	public static SearchResultsFrag newInstance(String keyword) {
		SearchResultsFrag fragment = new SearchResultsFrag();
		Bundle args = new Bundle();
		args.putString(QUERRY_KEY, keyword);
		fragment.setArguments(args);
		return fragment;
	}

	private SearchResultListAdapter resultAdapter;
	private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	private ArrayList<Manga> results = new ArrayList<Manga>();
	private XListView resultListView;
	private Handler handler = new Handler();
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private int page = 1;
	private String keyword;

	public SearchResultsFrag() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_search_result,
				container, false);
		keyword = getArguments().getString(QUERRY_KEY);
		int layoutID = R.layout.listview_searchresultlist;
		resultListView = (XListView) rootView
				.findViewById(R.id.SearchResultList);
		resultListView.setPullLoadEnable(true);
		resultListView.setPullRefreshEnable(false);
		resultListView.setXListViewListener(this);
		resultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(MangaInfoFrag.MANGA_LINK_STRING, getActivity()
						.getResources().getString(R.string.domain)
						+ results.get(position - 1).getLink());
				it.putExtras(bundle); 
				startActivity(it);
				Toast.makeText(getActivity(),
						results.get(position - 1).getLink(), Toast.LENGTH_SHORT)
						.show();
			}
		});
		resultAdapter = new SearchResultListAdapter(getActivity(), layoutID,
				results);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				resultAdapter);
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(resultListView);
		resultListView.setAdapter(swingBottomInAnimationAdapter);

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					getResultByKeyword(page);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return rootView;
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
				// TODO Auto-generated method stub
				try {
					getResultByKeyword(page);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void getResultByKeyword(int page) throws Exception {
		String searchUrl = getResources().getString(R.string.search_url)
				+ java.net.URLEncoder.encode(keyword, "utf-8") + "_p" + page;
		System.out.println(searchUrl);
		final ArrayList<Manga> resultDataList = NetAnalyse.parseHtmlToResults(
				searchUrl, getActivity().getCacheDir().getAbsolutePath());
		// final ArrayList<Manga> resultDataList = new ArrayList<>();
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				results.addAll(resultDataList);
				resultAdapter.notifyDataSetChanged();
				swingBottomInAnimationAdapter.notifyDataSetChanged();
			}
		});
	}
}
