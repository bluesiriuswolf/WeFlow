package com.etoc.weflow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.etoc.weflow.R;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.fragment.ExchangeGiftFragment;
import com.etoc.weflow.fragment.GameCoinsFragment;
import com.etoc.weflow.fragment.MobileFlowFragment;
import com.etoc.weflow.fragment.RechargeFragment;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;

public class ExpenseFlowActivity extends TitleRootActivity {

	private PagerSlidingTabStrip titleTab;
	private ViewPager viewPage;
	private MyPagerAdapter adapter;
	
	private boolean isLogin = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
		
		titleTab = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		viewPage = (ViewPager) findViewById(R.id.pager);
		
		titleTab.setTextColorResource(R.color.pagertab_color_green);
		titleTab.setTabPaddingLeftRight(DisplayUtil.getSize(this, 40));
		
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);
		
		titleTab.setViewPager(viewPage);
		
		int index = getIntent().getIntExtra(ConStant.INTENT_EXPENSE_FLOW, 0);
		index = index == 0?0:index - 1;
		viewPage.setCurrentItem(index);
		
		isLogin = getIntent().getBooleanExtra("isLogin", false);
		
	}
	
	private void initViews() {
		setTitleText("花流量币");
		setRightButtonText("记录");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			if(isLogin) {
				Intent ExpenseIntent = new Intent(this, ExpenseBillListActivity.class);
				ExpenseIntent.putExtra(ConStant.INTENT_EXPENSE_FLOW, viewPage.getCurrentItem());
				startActivity(ExpenseIntent);
			} else {
				startActivity(new Intent(this, LoginActivity.class));
				finish();
			}
			break;
		}
		super.onClick(v);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_expense_flow;
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "充话费", "流量包", "游戏币", "礼品券"};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment frag = null;
			switch (position) {
			case 0:
				frag = new RechargeFragment();
				break;
			case 2:
				frag = new GameCoinsFragment();
				break;
			case 1:
				frag = new MobileFlowFragment();
				break;
			case 3:
				frag = new ExchangeGiftFragment();
				break;
			default:
				break;
			}
			return frag;
		}

	}
	
}
