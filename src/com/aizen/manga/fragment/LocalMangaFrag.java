package com.aizen.manga.fragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.aizen.manga.LocalMangaActivity;
import com.aizen.manga.LocalMangaInfoActivity;
import com.aizen.manga.R;
import com.aizen.manga.adapter.LocalListAdapter;
import com.aizen.manga.module.Manga;
import com.aizen.manga.sql.MangaDBManager;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LocalMangaFrag extends Fragment {

	LocalListAdapter localListAdapter;
	SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
	ListView localListView;
	MangaDBManager mangadbmgr;
	ArrayList<Manga> localMangas = new ArrayList<>();
	Handler handle = new Handler();
	ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private RelativeLayout statusLayout;
	private ImageView statusImageView;
	private TextView statusTextView;
	
	public static String loadLocalMangaDir = "";

	private static LocalMangaFrag uniqueLocalMangaFrag = null;

	public static LocalMangaFrag newInstance() {
		if (uniqueLocalMangaFrag == null) {
			uniqueLocalMangaFrag = new LocalMangaFrag();
		}
		return uniqueLocalMangaFrag;
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
		localMangas = new ArrayList<>();
		View rootView = inflater.inflate(R.layout.fragment_local_list,
				container, false);
		localListView = (ListView) rootView.findViewById(R.id.localList);
		localListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getActivity(),LocalMangaInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(LocalMangaInfoActivity.LOCAL_MANGA_KEY, loadLocalMangaDir+"/"+localMangas.get(position).getId());
				it.putExtras(bundle);
				startActivity(it);
			}
		});
		int layoutID = R.layout.listview_locallist;
		localListAdapter = new LocalListAdapter(getActivity(), layoutID, localMangas, loadLocalMangaDir);
		swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(localListAdapter);
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(localListView);
		localListView.setAdapter(swingBottomInAnimationAdapter);
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getLocalMangas();
			}
		});
		return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		statusLayout = (RelativeLayout) getActivity().findViewById(R.id.ReadDataStatusLayout);
		statusImageView = (ImageView) getActivity().findViewById(R.id.StatusImage);
		statusTextView = (TextView) getActivity().findViewById(R.id.StatusText);
	}
	
	public void getLocalMangas(){
		final ArrayList<Manga> readList = mangadbmgr.queryLocalMangas();
		System.out.println(readList.size());
		handle.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				statusLayout.setVisibility(View.GONE);
				localMangas.addAll(readList);
				localListAdapter.notifyDataSetChanged();
				swingBottomInAnimationAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mangadbmgr.closeDB();
	}
	
	public void somethingWrong() {
		statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.wrong));
		statusTextView.setText(getResources().getString(R.string.status_text_wrong));
		statusLayout.setVisibility(View.VISIBLE);
	}
}
