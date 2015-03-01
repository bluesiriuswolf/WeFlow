package com.etoc.weflowdemo.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.activity.AdvertActivity.AdAdapter;
import com.etoc.weflowdemo.adapter.BannerAdapter;
import com.etoc.weflowdemo.net.GsonResponseObject.AdvInfo;
import com.etoc.weflowdemo.util.DisplayUtil;
import com.etoc.weflowdemo.util.ViewUtils;
import com.etoc.weflowdemo.view.autoscrollviewpager.AutoScrollViewPager;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.viewpagerindicator.PageIndicator;

public class AdvertActivity extends TitleRootActivity {

	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	private GridView gvRecommentAd = null;
	BannerAdapter bannerAdapter = null;
	private AdAdapter adapter = null;
	private PullToRefreshScrollView ptrScrollView = null;
	ArrayList<Integer> imgIds = new ArrayList<Integer>();
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	
	private ImageView ivNewest1 = null;
	private ImageView ivNewest2 = null;
	private ImageView ivNewest3 = null;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		setTitleText("看视频");
		hideRightButton();
//		setRightButtonText("记录");
		
		viewPager = (AutoScrollViewPager) findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) findViewById(R.id.indicator_service);
        /*int [] adRes = {R.drawable.banner_ad_1,R.drawable.banner_ad_2,R.drawable.banner_ad_3};
        for (int i = 0;i < 3; i++) {
        	imgIds.add(adRes[i]);
        }*/
        ArrayList<AdvInfo> adList = new ArrayList<AdvInfo>();
        String[] imgUrls = {"http://www.adzop.com//uploadpic/xcp/1412/P190.rmvb_20141222_110554.306.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P186.rmvb_20141222_110108.278.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P184.rmvb_20141222_110040.713.jpg",
        		};
        String[] contents = {"2014地球一小时 Earth Hour 高清宣传片",
        		"Switzerland 瑞士旅游高清宣传片 冰天雪地雪山",
        		"国外知名制药企业高清宣传片 细胞素材 病毒扩散"
        		};
        String[] titles = {"珍惜地球",
        		"瑞士旅游",
        		"病毒扩散"
        };
        String[] videoUrls = {
        		"http://v.adzop.com/xcp/1412/P190.mp4",
        		"http://v.adzop.com/xcp/1412/P186.mp4",
        		"http://v.adzop.com/xcp/1412/P184.mp4",
        		
        };
        
        for (int i = 0;i < 3;i++) {
        	AdvInfo info = new AdvInfo();
        	info.content = contents[i];
        	info.title = titles[i];
        	info.coverurl = imgUrls[i];
        	info.videourl = videoUrls[i];
        	
        	adList.add(info);
        }
        
        bannerAdapter = new BannerAdapter(getSupportFragmentManager(), R.drawable.small_pic_default, adList);
        viewPager.setAdapter(bannerAdapter);
        
        mIndicator.setViewPager(viewPager);
		mIndicator.notifyDataSetChanged();
        
        gvRecommentAd = (GridView) findViewById(R.id.gv_recomment_ad);
        ArrayList<AdInfo> infoList = new ArrayList<AdvertActivity.AdInfo>();
        int [] resArr = {R.drawable.rec_ad_1,R.drawable.rec_ad_2,R.drawable.rec_ad_3,R.drawable.rec_ad_4};
        for(int i = 0;i < 4;i++) {
        	AdInfo info = new AdInfo();
        	info.imgId = resArr[i];
        	info.content = "广告文字...";
        	info.scroe = (20 + i * 10) + "";
        	infoList.add(info);
        }
        adapter = new AdAdapter(this, infoList);
        gvRecommentAd.setAdapter(adapter);
        int gridHeight = getGridViewHeight(gvRecommentAd);
		ViewUtils.setHeightPixel(gvRecommentAd, gridHeight);
		
		gvRecommentAd.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AdvertActivity.this, AdDetailActivity.class);
				startActivity(i);
				
			}
		});
		
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.small_pic_default)
				.showImageOnFail(R.drawable.small_pic_default)
				.showImageOnLoading(R.drawable.small_pic_default)
				.displayer(new RoundedBitmapDisplayer(5))
//				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				.build();
		
		
		AdvInfo info1 = new AdvInfo();
		AdvInfo info2 = new AdvInfo();
		AdvInfo info3 = new AdvInfo();
		
		info1.coverurl = "http://www.adzop.com//uploadpic/xcp/1412/P176.rmvb_20141222_105653.404.jpg";
		info1.title = "情侣婚纱照";
		info1.content = "某婚纱品牌高清宣传片 情侣婚纱照 ";
		info1.videourl = "http://v.adzop.com/xcp/1412/P176.mp4";
		
		info2.coverurl = "http://www.adzop.com//uploadpic/xcp/X012.rmvb_20140630_104659.890.jpg";
		info2.title = "排气管宣传";
		info2.content = "国内某排气管制造公司高清宣传片雨露数控办公拥抱太阳会议";
		info2.videourl = "http://v.adzop.com/xcp/1412/X012.mp4";
		
		info3.coverurl = "http://www.adzop.com//uploadpic/xcp/X008.rmvb_20140630_104623.859.jpg";
		info3.title = "食品特效";
		info3.content = "食品特效高清素材 水果 咖啡 蔬菜 烤肉 香料 餐具";
		info3.videourl = "http://v.adzop.com/xcp/1412/X008.mp4";
		RelativeLayout rlAd1 = (RelativeLayout) findViewById(R.id.rl_newest_1);
		RelativeLayout rlAd2 = (RelativeLayout) findViewById(R.id.rl_newest_2);
		RelativeLayout rlAd3 = (RelativeLayout) findViewById(R.id.rl_newest_3);
		rlAd1.setTag(info1);
		rlAd2.setTag(info2);
		rlAd3.setTag(info3);
		
		rlAd1.setOnClickListener(this);
		rlAd2.setOnClickListener(this);
		rlAd3.setOnClickListener(this);
		
		ivNewest1 = (ImageView) findViewById(R.id.iv_ad_img_1);
		ivNewest2 = (ImageView) findViewById(R.id.iv_ad_img_2);
		ivNewest3 = (ImageView) findViewById(R.id.iv_ad_img_3);
		
		imageLoader.displayImage(info1.coverurl, ivNewest1, imageLoaderOptions);
		imageLoader.displayImage(info2.coverurl, ivNewest2, imageLoaderOptions);
		imageLoader.displayImage(info3.coverurl, ivNewest3, imageLoaderOptions);
		
		ptrScrollView = (PullToRefreshScrollView) findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("加载更多");
		ptrScrollView.setReleaseLabel("松开加载更多");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_newest_1:
		case R.id.rl_newest_2:
		case R.id.rl_newest_3:
			AdvInfo info = (AdvInfo) v.getTag();
			Intent i = new Intent(AdvertActivity.this, AdDetailActivity.class);
			i.putExtra("adinfo", new Gson().toJson(info));
			startActivity(i);
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	private int getGridViewHeight(GridView gridView) {
		int count = gridView.getAdapter().getCount();
		int rowNum = (int)Math.ceil(count / (double)2);
		int height = DisplayUtil.getSize(this, 202) * rowNum  + cn.trinea.android.common.util.ViewUtils.getGridViewVerticalSpacing(gridView) * (rowNum + 1);
		return height;
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_advert;
	}
	
	class AdInfo {
		int imgId;
		String content;
		String scroe;
	}
	
	class AdHolder {
		ImageView imgView;
		TextView tvContent;
		TextView tvScore;
	}
	
	class AdAdapter extends BaseAdapter {

		ArrayList<AdInfo> infoList = new ArrayList<AdInfo>();
		private LayoutInflater inflater;
		private Context context;
		
		public AdAdapter(Context context,ArrayList<AdInfo> list) {
			this.infoList = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			AdHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_recomment_ad, null);
				
				holder = new AdHolder();
				holder.imgView = (ImageView)convertView.findViewById(R.id.iv_ad_img);
				holder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
				holder.tvScore = (TextView)convertView.findViewById(R.id.tv_score);
				
				ViewUtils.setHeight(convertView, 202);
				convertView.setTag(holder);
			} else {
				holder = (AdHolder)convertView.getTag();
			}
			
			AdInfo info = infoList.get(position);
			holder.imgView.setImageResource(info.imgId);
			holder.tvContent.setText(info.content);
			holder.tvScore.setText(info.scroe + "流量币");
			
			return convertView;
		}
		
	}

}
