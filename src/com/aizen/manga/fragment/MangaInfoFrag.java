package com.aizen.manga.fragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.aizen.manga.MangaActivity;
import com.aizen.manga.R;
import com.aizen.manga.adapter.ChapterListAdapter;
import com.aizen.manga.module.Chapter;
import com.aizen.manga.module.Manga;
import com.aizen.manga.util.NetAnalyse;
import com.aizen.manga.view.NoScrollGridView;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MangaInfoFrag extends Fragment {

	public static MangaInfoFrag newInstance(String url) {
		MangaInfoFrag fragment = new MangaInfoFrag();
		Bundle args = new Bundle();
		args.putString(MANGA_LINK_STRING, url);
		fragment.setArguments(args);
		return fragment;
	}

	public static final String MANGA_LINK_STRING = "url";
	private Handler handler = new Handler();
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private Manga mangaDetail = new Manga();
	private String url;
	private ImageView coverView;
	private TextView nameView, authorView, descView, statusView;
	NoScrollGridView chapterGridView;
	ArrayList<Chapter> chapters = new ArrayList<>();
	ChapterListAdapter chaptersAdapter;
	private ProgressDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_manga_info,
				container, false);
		url = getActivity().getString(R.string.domain)
				+ getArguments().getString(MANGA_LINK_STRING);
		coverView = (ImageView) rootView.findViewById(R.id.mangainfo_cover);
		nameView = (TextView) rootView.findViewById(R.id.mangainfo_name);
		authorView = (TextView) rootView.findViewById(R.id.mangainfo_author);
		descView = (TextView) rootView.findViewById(R.id.mangainfo_desc);
		statusView = (TextView) rootView.findViewById(R.id.mangainfo_status);
		chapterGridView = (NoScrollGridView) rootView
				.findViewById(R.id.mangainfo_chaptergrid);
		chapterGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), chapters.get(position).getLink(),
						Toast.LENGTH_SHORT).show();
				Intent it = new Intent(getActivity(), MangaActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList(
						MangaActivity.CHAPTER_LINK_KEY,
						NetAnalyse.parseHtmlToPageURLs(R.string.domain
								+ chapters.get(position).getLink()));
			//	bundle.putString(MangaActivity.CHAPTER_LINK_KEY,
			//			chapters.get(position).getLink());
				it.putExtras(bundle);
				startActivity(it);

			}
		});
		int layoutID = R.layout.gridview_mangachapter;
		chaptersAdapter = new ChapterListAdapter(getActivity(), chapters,
				layoutID);
		chapterGridView.setAdapter(chaptersAdapter);
		dialog = ProgressDialog.show(getActivity(), "Loading", "正在加载。。。。，请稍等！");
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(url);
					getMangaInfo(url);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return rootView;
	}

	private void getMangaInfo(String url) throws Exception {
		final Manga mangainfo = NetAnalyse.parseHtmlToInfo(url, getActivity()
				.getCacheDir().getAbsolutePath());
		final ArrayList<Chapter> chs = NetAnalyse.parseHtmlToChapters(url);
		try {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					coverView.setImageBitmap(mangainfo.getCover());
					nameView.setText(mangainfo.getName());
					authorView.setText(mangainfo.getAuthor());
					System.out.println(mangainfo.getAuthor());
					statusView.setText(mangainfo.getStatusIntro());
					descView.setText(mangainfo.getDescription().length() > 60 ? mangainfo
							.getDescription().substring(0, 60) + "..."
							: mangainfo.getDescription());
					chapters.addAll(chs);
					chaptersAdapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
