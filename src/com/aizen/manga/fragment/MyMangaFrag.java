package com.aizen.manga.fragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.aizen.manga.DetailActivity;
import com.aizen.manga.R;
import com.aizen.manga.adapter.MyFavorListAdapter;
import com.aizen.manga.module.Manga;
import com.aizen.manga.sql.MangaDBManager;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.contextualundo.ContextualUndoAdapter.DeleteItemCallback;
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
import android.widget.ListView;

public class MyMangaFrag extends Fragment implements OnDismissCallback,
		DeleteItemCallback {

	MyFavorListAdapter myFavorListAdapter;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	ListView myFavorListView;
	MangaDBManager mangadbmgr;
	ArrayList<Manga> myFavorMangas = new ArrayList<Manga>();
	private Handler handler = new Handler();
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	private static MyMangaFrag uniqueMyMangaFrag = null;

	public static MyMangaFrag newInstance() {
		if (uniqueMyMangaFrag == null) {
			uniqueMyMangaFrag = new MyMangaFrag();
		}
		return uniqueMyMangaFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mangadbmgr = new MangaDBManager(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		myFavorMangas = new ArrayList<Manga>();
		View rootView = inflater.inflate(R.layout.fragment_myfavor_list,
				container, false);
		myFavorListView = (ListView) rootView.findViewById(R.id.mangaList);
		int layoutID = R.layout.listview_myfavorlist;
		myFavorListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(), DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(MangaInfoFrag.MANGA_LINK_STRING, myFavorMangas
						.get(position).getLink());
				it.putExtras(bundle); 
				startActivity(it);
				Toast.makeText(getActivity(),
						myFavorMangas.get(position).getLink(),
						Toast.LENGTH_SHORT).show();
			}
		});
		myFavorListAdapter = new MyFavorListAdapter(getActivity(), layoutID,
				myFavorMangas, getActivity().getCacheDir().getAbsolutePath());
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(myFavorListAdapter, this));
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(myFavorListView);
		myFavorListView.setAdapter(swingBottomInAnimationAdapter);
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					// myFavorMangas = null;
					getLikedMangaList();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		return rootView;
	}

	public void getLikedMangaList() {
		final ArrayList<Manga> queryResult = mangadbmgr.queryLikedMangas();
		System.out.println(queryResult.size());
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				myFavorMangas.addAll(queryResult);
				myFavorListAdapter.notifyDataSetChanged();
				swingBottomInAnimationAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mangadbmgr.closeDB();
	}

	@Override
	public void onDismiss(final AbsListView listView,
			final int[] reverseSortedPositions) {
		// TODO Auto-generated method stub
		for (final int position : reverseSortedPositions) {
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					String idString = myFavorMangas.get(position).getId();
					mangadbmgr.setUnlike(idString);
					mangadbmgr.delete(idString);
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							myFavorMangas.remove(position);
							myFavorListAdapter.notifyDataSetChanged();
							swingBottomInAnimationAdapter
									.notifyDataSetChanged();
						}
					});
				}
			});

		}
	}

	@Override
	public void deleteItem(int arg0) {
		// TODO Auto-generated method stub

	}

}
