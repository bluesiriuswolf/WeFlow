package com.etoc.weflow.activity;

import android.graphics.Canvas;
import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etoc.weflow.R;
import com.etoc.weflow.event.FragmentEvent;
import com.etoc.weflow.fragment.DiscoveryFragment;
import com.etoc.weflow.fragment.FlowBankFragment;
import com.etoc.weflow.fragment.HomePageFragment;
import com.etoc.weflow.fragment.MenuFragment;
import com.etoc.weflow.fragment.MyselfFragment;
import com.etoc.weflow.fragment.XFragment;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.version.CheckUpdate;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import de.greenrobot.event.EventBus;

public class MainActivity extends SlidingFragmentActivity implements Callback, OnClickListener {
	
	private final String TAG = "MainActivity";

	private Handler handler;
	private DisplayMetrics dm = new DisplayMetrics();
	
	private XFragment<?> currContentFragment;
	private MenuFragment menuFragment;
	
	private XFragment<?> homePageFragment;
	private XFragment<?> flowBankFragment;
	private XFragment<?> discoveryFragment;
	private XFragment<?> myselfFragment;
	
	private RelativeLayout rlHomePage;
	private RelativeLayout rlBank;
	private RelativeLayout rlDiscover;
	private RelativeLayout rlMe;
	
	private TextView tvTitle;
	
	private static long back_pressed;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		handler = new Handler(this);
		dm = getResources().getDisplayMetrics();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		EventBus.getDefault().register(this);
		
		initController();
		initMain(savedInstanceState);
		//检查更新
		CheckUpdate.getInstance(this).update();
	}
	
	private void initController() {
		rlHomePage = (RelativeLayout) findViewById(R.id.rl_btn_weflow);
		rlBank     = (RelativeLayout) findViewById(R.id.rl_btn_bank);
		rlDiscover = (RelativeLayout) findViewById(R.id.rl_btn_discover);
		rlMe       = (RelativeLayout) findViewById(R.id.rl_btn_me);
		
		rlHomePage.setOnClickListener(this);
		rlBank.setOnClickListener(this);
		rlDiscover.setOnClickListener(this);
		rlMe.setOnClickListener(this);
		
		tvTitle = (TextView) findViewById(R.id.tv_title);
	}
	
	private void initMain(Bundle savedInstanceState) {
		
		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		
		if(savedInstanceState != null) {
			
			homePageFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, HomePageFragment.class.getName());
			
			flowBankFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, FlowBankFragment.class.getName());
			
			discoveryFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, DiscoveryFragment.class.getName());
			
			myselfFragment = (XFragment<?>)getSupportFragmentManager().getFragment(
					savedInstanceState, MyselfFragment.class.getName());
			
			String contentClass = savedInstanceState.getString("content");
			if (contentClass != null) {
				currContentFragment = (XFragment<?>) getSupportFragmentManager().getFragment(
						savedInstanceState, contentClass);
			}
		}
		
		if (homePageFragment == null) {
			homePageFragment = new HomePageFragment();
		}
		
		if (flowBankFragment == null) {
			flowBankFragment = new FlowBankFragment();
		}
		
		if (discoveryFragment == null) {
			discoveryFragment = new DiscoveryFragment();
		}
		
		if (myselfFragment == null) {
			myselfFragment = new MyselfFragment();
		}
		
		if (currContentFragment == null) {
			currContentFragment = homePageFragment;
		}
		
		showTitle(currContentFragment);
		
		if (null != getSupportFragmentManager().findFragmentByTag(
				currContentFragment.getClass().getName())) {
			getSupportFragmentManager().beginTransaction().remove(currContentFragment);
		}
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.content_frame, currContentFragment,currContentFragment.getClass().getName()).commit();
		// set the Behind View Fragment
		if (menuFragment != null
				&& null != getSupportFragmentManager().findFragmentByTag(
						menuFragment.getClass().getName())) {
			getSupportFragmentManager().beginTransaction().remove(menuFragment);
		}
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		if (menuFragment == null) {
			menuFragment = new MenuFragment();
		}
		
		t.replace(R.id.menu_frame, menuFragment);
		t.commit();
		
		// customize the SlidingMenu
		final int leftOffset = 120 * dm.widthPixels / 720;
		final SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindOffset(leftOffset);
		sm.setFadeEnabled(false);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		sm.setShadowDrawable(R.drawable.shadow_left);
		sm.setTopShadowDrawable(R.drawable.shadow_top);
		sm.setBottomShadowDrawable(R.drawable.shadow_bottom);
		sm.setShadowWidth(DisplayUtil.getSize(this, 30));
		
		sm.setBackgroundImage(R.drawable.img_frame_background);
		
		final float defaultPercent = 910.0f/(1280 - 50);
		final float defaultMidPos = (577.0f) * dm.heightPixels / 1280;
		sm.setTopEdge((int) (defaultMidPos * (1 - defaultPercent)));
		Log.d(TAG,"defaultMidPos = " + defaultMidPos);
		sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * (1 - defaultPercent) + defaultPercent);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						defaultMidPos);
			}
		});

		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * (1 - defaultPercent));
				canvas.scale(scale, scale, 0, defaultMidPos);
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState != null && currContentFragment != null) {
			Log.d(TAG, "onSaveInstanceState currContentFragment="
					+ currContentFragment);
			outState.putString("content", currContentFragment.getClass()
					.getName());
			getSupportFragmentManager().putFragment(outState, currContentFragment.getClass()
					.getName(), currContentFragment);

		}
		
		if (outState != null && menuFragment != null) {
			Log.d(TAG, "onSaveInstanceState menuFragment="
					+ menuFragment);
//			getSupportFragmentManager().putFragment(outState, menuFragment.getClass()
//					.getName(), menuFragment);
//			getSupportFragmentManager().beginTransaction().remove(menuFragment).commit();
		}
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
//		init(savedInstanceState);
	}
	
	public void switchContent(XFragment<?> fragment) {
		Log.d(TAG, "fragment=" + fragment);
		if (fragment == null) {
			return;
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		if (currContentFragment != null){
//			ft.hide(currContentFragment);
			if (currContentFragment.getIndex() < fragment.getIndex()) {
				ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			} else {
				ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
			}
		}
		
		if (currContentFragment != null && currContentFragment != fragment) {
			Log.d(TAG,"switchContent hide");
			ft.hide(currContentFragment);
			fragment.onShow();
		}
		
		if (null == getSupportFragmentManager().findFragmentByTag(
				fragment.getClass().getName())) {
			Log.d(TAG, "add fragment=" + fragment + " name = " + fragment.getClass().getName());
			ft.add(R.id.content_frame, fragment, fragment.getClass().getName());
			// ft.addToBackStack(null);
		} else {
			Log.d(TAG, "show fragment=" + fragment);
			ft.show(fragment);
		}
		
		showTitle(fragment);
		currContentFragment = fragment;
		ft.commitAllowingStateLoss();
		showContent();
	}
	
	private void showTitle(XFragment<?> fragment) {
		String title = "";
		if(fragment != null) {
			if(fragment instanceof HomePageFragment) {
				title = "微流量";
			} else if(fragment instanceof FlowBankFragment) {
				title = "流量银行";
			} else if(fragment instanceof DiscoveryFragment) {
				title = "发现";
			} else if(fragment instanceof MyselfFragment) {
				title = "我";
			}
		}
		tvTitle.setText(title);
	}
	
	@Override
	public void showContent() {
		// TODO Auto-generated method stub
//		Log.e(TAG,"showContent in - devID:" + Info.getDevId(this));
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}
	
	@Override
	public void onBackPressed(){
		if(back_pressed + 2000 > System.currentTimeMillis()){
			super.onBackPressed();
		}else{
			Toast.makeText(getBaseContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
		}
		back_pressed = System.currentTimeMillis();
	}
	
	@Override
	public void showMenu() {
		// TODO Auto-generated method stub
		Log.d(TAG,"showMenu in");
		super.showMenu();
	}
	
	public void onEvent(FragmentEvent event) {
		switch(event) {
		
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_btn_weflow:
			switchContent(homePageFragment);
			break;
		case R.id.rl_btn_bank:
			switchContent(flowBankFragment);
			break;
		case R.id.rl_btn_discover:
			switchContent(discoveryFragment);
			break;
		case R.id.rl_btn_me:
			switchContent(myselfFragment);
			break;
		}
	}
}