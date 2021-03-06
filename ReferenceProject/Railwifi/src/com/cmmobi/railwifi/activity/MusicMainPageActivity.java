package com.cmmobi.railwifi.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.MusicMainPageListAdapter;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.AlumbElem;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.view.MusicControllerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2015-01-14
 */
public class MusicMainPageActivity extends TitleRootActivity implements OnRefreshListener2<ListView>{

	private String TAG = "MusicMainPageAcitivity";
	private PullToRefreshListView xlvMusicData;
	private ListView lvMusicData;
	private MusicMainPageListAdapter listAdapter;
	private static final int REFRESH_COMPLETE = 0xffabab;
	public static final int BACK_FROM_DETAIL = 0xffaa;
	public static final int BACK_FROM_ALBUM = 0xffab;
	public static final int BACK_FROM_TAG = 0xffac;
	
	private boolean isHasNextPage = true;
	private MusicControllerView musicControllerView;
	
	private int currentPage = 1;
	private ArrayList<MusicElem> musicElems = new ArrayList<GsonResponseObject.MusicElem>(); //专辑歌曲列表
	private ArrayList<MusicElem> list = new ArrayList<GsonResponseObject.MusicElem>(); //单曲列表

	public static final String ALBUM_LIST = "ALBUM_LIST";
	
	private String mTagType = "";
	
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_music_main_page;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("听音乐");
		rightButton.setBackgroundResource(R.drawable.btn_tag);
		int size12 = DisplayUtil.getSize(this, 12);
		findViewById(R.id.ll_music).setPadding(size12, size12, size12, 0);
		xlvMusicData = (PullToRefreshListView) findViewById(R.id.xlv_music_data);
		xlvMusicData.setShowIndicator(false);
		lvMusicData = xlvMusicData.getRefreshableView();
		xlvMusicData.setOnRefreshListener(this);
		xlvMusicData.setPullLabel("加载更多");
		xlvMusicData.setReleaseLabel("松开加载更多");

		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams) xlvMusicData.getLayoutParams();
		pm.bottomMargin = DisplayUtil.getSize(this, 12);
		xlvMusicData.setLayoutParams(pm);
		listAdapter = new MusicMainPageListAdapter(this);

		
	//	xlvMusicData.setAdapter(listAdapter);
		
		Requester.requestMusicList(handler, currentPage, mTagType);

	}
	
		
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(REFRESH_COMPLETE);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub		
		if(isHasNextPage){
			currentPage ++;
			Requester.requestMusicList(handler, currentPage, mTagType);	
		}else{
			handler.sendEmptyMessage(REFRESH_COMPLETE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch(v.getId()){
		case R.id.btn_title_right:
			//标签页
			Intent tagIntent = new Intent(this, TagActivity.class);
			tagIntent.putExtra(TagActivity.KEY_TYPE, GsonResponseObject.MEDIA_TYPE_MUSIC);
			startActivityForResult(tagIntent, BACK_FROM_TAG);
			break;
			default:{
				
			}	
		}
	}	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MusicService.getInstance().stopPlay();
//		unregisterReceiver(playStateReceiver);
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_MEDIA_MUSICLIST:
			final GsonResponseObject.musicListResp musicListResp = (GsonResponseObject.musicListResp) msg.obj;
			if(musicListResp != null && "0".equals(musicListResp.status)){
				findViewById(R.id.rl_empty).setVisibility(View.GONE);
				findViewById(R.id.xlv_music_data).setVisibility(View.VISIBLE);				
				if(currentPage ==1 && musicElems.isEmpty()){
				MyImageLoader imageLoader = null;
				DisplayImageOptions imageLoaderOptions = null;
				imageLoader = MyImageLoader.getInstance();
				imageLoaderOptions = new DisplayImageOptions.Builder()
						.cacheInMemory(true)
						.cacheOnDisc()
						.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				        .bitmapConfig(Bitmap.Config.RGB_565)
						.showImageOnFail(R.drawable.content_pic_default_9)
						.showImageForEmptyUri(R.drawable.content_pic_default_9)
						.showImageOnLoading(R.drawable.content_pic_default_9)
						.displayer(new RoundedBitmapDisplayer(12))
						.build();
				
				View albumView = this.getLayoutInflater().inflate(R.layout.activity_music_mainpage_album, null);
				ImageView ivMusicAlumb = (ImageView)albumView.findViewById(R.id.iv_album);
				imageLoader.displayImage(musicListResp.musicalumb.img_path, ivMusicAlumb, imageLoaderOptions);
				RelativeLayout.LayoutParams pm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, DisplayUtil.getSize(this, 339));
				ivMusicAlumb.setLayoutParams(pm);
				ivMusicAlumb.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MusicMainPageActivity.this, MusicAlbumActivity.class);
						intent.putExtra(ALBUM_LIST, new Gson().toJson(musicListResp.musicalumb, GsonResponseObject.MusicAlumb.class));
						startActivityForResult(intent, BACK_FROM_ALBUM);
					}
				});
				
				musicControllerView = (MusicControllerView)albumView.findViewById(R.id.mcv_music);
				int width = musicControllerView.setDrawableAndGetWidth(false);
				pm = (RelativeLayout.LayoutParams)musicControllerView.getLayoutParams();
				pm.width = width;
				pm.topMargin = DisplayUtil.getSize(this, 219);
				pm.rightMargin = DisplayUtil.getSize(this, 33);
				musicControllerView.setLayoutParams(pm);
				musicControllerView.setIsList(true);
				lvMusicData.addHeaderView(albumView);
				for(GsonResponseObject.MusicElem item : musicListResp.musicalumb.alumblist){
					musicElems.add(item);
				}
				musicControllerView.setPathArray(musicElems);
				MusicService.getInstance().setCurMusicId(musicElems.get(0).media_id);
				}
				if(musicListResp.list!=null && musicListResp.list.length>0){
					for(GsonResponseObject.MusicElem item : musicListResp.list){
						list.add(item);
					}
					listAdapter.setData(list);
					if(lvMusicData.getAdapter() == null){
						lvMusicData.setAdapter(listAdapter);
					}
				}else{
					isHasNextPage = false;
				}
				
				handler.sendEmptyMessage(REFRESH_COMPLETE);
			}else{
				findViewById(R.id.rl_empty).setVisibility(View.VISIBLE);
				findViewById(R.id.xlv_music_data).setVisibility(View.GONE);
			}	
			break;
		case REFRESH_COMPLETE:
			xlvMusicData.onRefreshComplete();
			break;
		default:
			break;
		}
		return false;
	}

	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0 == BACK_FROM_DETAIL){
			musicControllerView.resetButton();
			musicControllerView.setPathArray(musicElems);
			MusicService.getInstance().setCurMusicId(musicElems.get(0).media_id);
		}else if(arg0 == BACK_FROM_ALBUM){
			if(MusicService.getInstance().isPlaying()){
				musicControllerView.setButtonPause();
			}else{
				musicControllerView.setButtonPlay();
			}
		}else if(arg0 == BACK_FROM_TAG && arg1 == RESULT_OK && arg2!=null){
			currentPage = 1;
			list.clear();
			mTagType = arg2.getStringExtra(TagActivity.KEY_TAG_ID);
			String tagName = arg2.getStringExtra(TagActivity.KEY_TAG_NAME);
			setTitleText(tagName);
			Requester.requestMusicList(handler, currentPage, mTagType);
		}
	}
}
