package com.aizen.manga.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.aizen.manga.MangaActivity;
import com.aizen.manga.R;
import com.aizen.manga.adapter.ChapterListAdapter;
import com.aizen.manga.module.Chapter;
import com.aizen.manga.module.Manga;
import com.aizen.manga.sql.MangaDBManager;
import com.aizen.manga.util.NetAnalyse;
import com.aizen.manga.util.Utils;
import com.aizen.manga.view.NoScrollGridView;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MangaInfoFrag extends Fragment implements MultiChoiceModeListener{

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
	private TextView nameView, authorView, descView, statusView, lastReadView;
	private ImageButton shareBtn, favorBtn, downloadBtn;
	// private ImageButton refreshBtn;
	NoScrollGridView chapterGridView;
	ArrayList<Chapter> chapters = new ArrayList<>();
	ChapterListAdapter chaptersAdapter;
	private Map<Integer, Boolean> selectMap = new HashMap<Integer, Boolean>();
	private ProgressDialog dialog;
	private MangaDBManager mangadbmgr;
	
	private String serviceString = Context.DOWNLOAD_SERVICE;
	private DownloadManager downloadManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mangadbmgr = new MangaDBManager(getActivity());
		downloadManager = (DownloadManager) getActivity().getSystemService(serviceString);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_manga_info,
				container, false);
		url = getArguments().getString(MANGA_LINK_STRING);
		coverView = (ImageView) rootView.findViewById(R.id.mangainfo_cover);
		nameView = (TextView) rootView.findViewById(R.id.mangainfo_name);
		authorView = (TextView) rootView.findViewById(R.id.mangainfo_author);
		descView = (TextView) rootView.findViewById(R.id.mangainfo_desc);
		statusView = (TextView) rootView.findViewById(R.id.mangainfo_status);
		lastReadView = (TextView) rootView.findViewById(R.id.mangainfo_lastRead);
		chapterGridView = (NoScrollGridView) rootView.findViewById(R.id.mangainfo_chaptergrid);		
		shareBtn = (ImageButton) rootView.findViewById(R.id.mangainfo_share_btn);
		favorBtn = (ImageButton) rootView.findViewById(R.id.mangainfo_favor_btn);
		downloadBtn = (ImageButton) rootView.findViewById(R.id.mangainfo_download_btn);
		// refreshBtn = (ImageButton)
		// rootView.findViewById(R.id.mangainfo_refresh_btn);
		chapterGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final Chapter chapter = chapters.get(position);
				Toast.makeText(getActivity(), chapter.getLink(),
						Toast.LENGTH_SHORT).show();
				executorService.submit(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						setLastRead(mangaDetail.getId(), chapter);
					}
				});
			}
		});
		chapterGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		chapterGridView.setMultiChoiceModeListener(this);
		
		int layoutID = R.layout.gridview_mangachapter;
		chaptersAdapter = new ChapterListAdapter(getActivity(), chapters,
				layoutID);
		chapterGridView.setAdapter(chaptersAdapter);
		dialog = ProgressDialog.show(getActivity(),
				getString(R.string.load_dialog_title),
				getString(R.string.load_dialog_content));
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(url);
					mangaDetail = getMangaInfo(url);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		favorBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mangaDetail.isLike()) {
					mangadbmgr.delete(mangaDetail.getId());
					favorBtn.setImageDrawable(getActivity().getResources()
							.getDrawable(R.drawable.ic_action_favorite));
					mangaDetail.setLike(false);
				} else {
					mangadbmgr.add(mangaDetail);
					mangadbmgr.setLike(mangaDetail.getId());
					favorBtn.setImageDrawable(getActivity().getResources()
							.getDrawable(R.drawable.ic_action_favorite_red));
					mangaDetail.setLike(true);
				}

			}
		});
		downloadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadSelectChapters();
			}
		});
		descView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textView = (TextView) v;
				descView.setText(textView.getText().length() > 60 + "..."
						.length() ? mangaDetail.getDescription().substring(0,
						60)
						+ "..." : mangaDetail.getDescription());
			}
		});
		return rootView;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mangadbmgr.closeDB();
	}

	private Manga getMangaInfo(String url) throws Exception {
		final Manga mangainfo = NetAnalyse.parseHtmlToInfo(url, getActivity()
				.getCacheDir().getAbsolutePath());
		mangainfo.setLike(mangadbmgr.isLike(mangainfo.getId()));
		System.out.println(mangainfo.isLike());
		mangainfo.setLastRead(mangadbmgr.getLastRead(mangainfo.getId()));
		final ArrayList<Chapter> chs = NetAnalyse.parseHtmlToChapters(url);
		try {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mangainfo.isLike()) {
						favorBtn.setImageDrawable(getActivity().getResources()
								.getDrawable(R.drawable.ic_action_favorite_red));
					}
					coverView.setImageBitmap(mangainfo.getCover());
					nameView.setText(mangainfo.getName());
					authorView.setText(mangainfo.getAuthor());
					System.out.println(mangainfo.getAuthor());
					statusView.setText(mangainfo.getStatusIntro());
					lastReadView.setText(mangainfo.getLastRead() == null?"未看":"上次看到："+mangainfo.getLastRead());
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
		return mangainfo;
	}
	
	private void setLastRead(String id, final Chapter chapter) {
		mangadbmgr.setLastRead(id, chapter.getTitle());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String chapterUrl = getString(R.string.domain)
						+ chapter.getLink();
				System.out.println(chapterUrl);
				final Intent it = new Intent(getActivity(), MangaActivity.class);
				final Bundle bundle = new Bundle();
				bundle.putString(MangaActivity.CHAPTER_KEY, chapterUrl);
				it.putExtras(bundle);
				startActivity(it);
			}
		});
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.chapter_choice, menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.chapter_selectall:
			for (int i = 0; i < chapterGridView.getCount(); i++) {
				chapterGridView.setItemChecked(i, true);
				selectMap.put(i, true);
			}
			break;

		case R.id.chapter_unselectall:
			for (int i = 0; i < chapterGridView.getCount(); i++) {
				chapterGridView.setItemChecked(i, false);
			}
			selectMap.clear();
			break;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub
		chaptersAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		// TODO Auto-generated method stub
		RelativeLayout itemLayout = (RelativeLayout) chapterGridView.getChildAt(position);
		CheckBox checkBox = (CheckBox) itemLayout.findViewById(R.id.mangainfo_chapter_select_check);
		checkBox.setChecked(checked);
		selectMap.put(position, checked);
		checkBox.setVisibility(checked?View.VISIBLE:View.GONE);
		mode.invalidate();
	}
	
	private void downloadSelectChapters() {
		for (final Integer position : selectMap.keySet()) {
			if (selectMap.get(position)) {
				System.out.println(position);
				executorService.submit(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						final String chapterURL = getString(R.string.domain)+chapters.get(position).getLink();
						final String chapterName = chapters.get(position).getTitle();
						System.out.println(chapterURL);
						final ArrayList<String> pageUrls = NetAnalyse.parseHtmlToPageURLs(chapterURL);
						final ArrayList<Long> references = new ArrayList<>();
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								for (String pageurl : pageUrls) {
									System.out.println(pageurl);
									String pagename = pageurl.substring(pageurl.lastIndexOf("/")+1);
									DownloadManager.Request request = new Request(Uri.parse(pageurl));
									request.addRequestHeader("Referer", url.equals("")?"http://www.imanhua.com/comic/76/list_59262.html":url);
									request.addRequestHeader("User-Agent", "UserAgent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
									request.addRequestHeader("Proxy-Connection", "Keep-Alive");
									if (Utils.hasSDCard()) {
										request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS+"/"+mangaDetail.getId()+"/"+chapterName+"/", pagename);
									} else {
										request.setDestinationUri(Uri.fromFile(new File(getActivity().getFilesDir().getAbsolutePath()+"/"+mangaDetail.getId()+"/aabbbbbb.gif")));
									}
									references.add(downloadManager.enqueue(request));
								}
							}
						});
					}
				});
			}
		}
		mangadbmgr.addLocal(mangaDetail);
	}
}
