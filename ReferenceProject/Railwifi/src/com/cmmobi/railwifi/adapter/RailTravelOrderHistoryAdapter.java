package com.cmmobi.railwifi.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.common.tools.Info;
import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.RailTravelDetailAcitivity;
import com.cmmobi.railwifi.activity.RailTravelOrderHistoryAcitivity;
import com.cmmobi.railwifi.activity.RailTravelOrderPayResultActivity;
import com.cmmobi.railwifi.alipay.PayInfo;
import com.cmmobi.railwifi.alipay.Result;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.TravelOrderInfo;
import com.cmmobi.railwifi.dao.TravelOrderInfoDao;
import com.cmmobi.railwifi.dao.TravelOrderInfoDao.Properties;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DateUtils;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
public class RailTravelOrderHistoryAdapter extends BaseAdapter {

	private final String TAG = "RailTravelOrderHistoryAdapter";
	private Activity context;
	private LayoutInflater inflater;
	private List<TravelOrderInfo> list ;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	private Handler handler;
	private Long mCurrentOrderId;
	
	public RailTravelOrderHistoryAdapter(final RailTravelOrderHistoryAcitivity context, List<TravelOrderInfo> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case Requester.RESPONSE_TYPE_TRAVEL_PAY:
					GsonResponseObject.travePayResp payResp = (GsonResponseObject.travePayResp)msg.obj;
					if(null!=payResp && "0".equals(payResp.status)){
						DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "railwifidb", null);
						SQLiteDatabase db = helper.getWritableDatabase();
						DaoMaster daoMaster = new DaoMaster(db);
						DaoSession daoSession = daoMaster.newSession();
						TravelOrderInfoDao travelOrderInfoDao = daoSession.getTravelOrderInfoDao();
						List<TravelOrderInfo> currentList = travelOrderInfoDao.queryBuilder().where(Properties.Id.eq(mCurrentOrderId)).list();
						//调用支付宝接口返回结果
						
						if(currentList.size()>0){
							final TravelOrderInfo orderInfo = currentList.get(0);
							if (orderInfo != null) {
								if (Config.IS_USE_REAL_PRICE) {
									PayInfo.pay(context, handler, orderInfo.getFullname(), orderInfo.getIntroduction(), orderInfo.getTotal_price(),"" + System.currentTimeMillis());
								} else {
									PayInfo.pay(context, handler, orderInfo.getFullname(), orderInfo.getIntroduction(), "0.01","" + System.currentTimeMillis());
								}
							}
						}
						db.close();
					}else{
						PromptDialog.Dialog(context, "支付失败", "当前网络状态不佳", "稍后再试");
					}
					break;
				case PayInfo.RESPONSE_ALIPAY_RESULT:
					if (msg.obj != null) {
						Result result = (Result) msg.obj;
						if (result != null) {
							if (result.isTransactionSecc()) {
								DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "railwifidb", null);
								SQLiteDatabase db = helper.getWritableDatabase();
								DaoMaster daoMaster = new DaoMaster(db);
								DaoSession daoSession = daoMaster.newSession();
								TravelOrderInfoDao travelOrderInfoDao = daoSession.getTravelOrderInfoDao();
								List<TravelOrderInfo> currentList = travelOrderInfoDao.queryBuilder().where(Properties.Id.eq(mCurrentOrderId)).list();
								currentList.get(0).setIspaid("1");
								currentList.get(0).setOrder_num(result.getTrandeNo());
					        	currentList.get(0).setOrder_time("" + System.currentTimeMillis());
					        	travelOrderInfoDao.update(currentList.get(0));
					    		Intent intent = new Intent(context, RailTravelOrderPayResultActivity.class);
								intent.putExtra("travelOrderInfo", new Gson().toJson(currentList.get(0), TravelOrderInfo.class));
								context.startActivity(intent); 
								context.onCheckChanged(R.id.btn_check_two);
								db.close();
							} else {
								if (!result.isUserCancle()) {
									PromptDialog.Dialog(context, "支付失败", "当前网络状态不佳", "稍后再试");
								}
							}
						}
					}
					break;

				default:
					break;
				}
				return false;
			}
		});
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc()
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnFail(R.drawable.tourism_pic_bg_default)
				.showImageForEmptyUri(R.drawable.tourism_pic_bg_default)
				.showImageOnLoading(R.drawable.tourism_pic_bg_default)
				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
				.build();
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public TravelOrderInfo getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_railtravel_order_item, null);
			holder = new ViewHolder();			
			holder.iv_travelImg = (ImageView) convertView.findViewById(R.id.iv_travel_img);
			RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams) holder.iv_travelImg.getLayoutParams();
			pm.height = DisplayUtil.getSize(context, 260);
			pm.leftMargin = DisplayUtil.getSize(context, 12);
			pm.rightMargin = DisplayUtil.getSize(context, 12);
			pm.topMargin = DisplayUtil.getSize(context, 12);
			holder.iv_travelImg.setLayoutParams(pm);
			holder.tv_tag = (TextView) convertView.findViewById(R.id.tv_tag);
			holder.tv_tag.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
			pm = (RelativeLayout.LayoutParams) holder.tv_tag.getLayoutParams();
			pm.topMargin= DisplayUtil.getSize(context, 12);
			pm.bottomMargin = DisplayUtil.getSize(context, 75);
			pm.width = DisplayUtil.getSize(context, 434);
			pm.height = DisplayUtil.getSize(context, 51);
			holder.tv_tag.setLayoutParams(pm);
			holder.tv_tag.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
			holder.iv_title = (ImageView) convertView.findViewById(R.id.iv_title);
			pm = (RelativeLayout.LayoutParams) holder.iv_title.getLayoutParams();
			pm.leftMargin= DisplayUtil.getSize(context, 21);
			pm.height = DisplayUtil.getSize(context, 90);
			holder.iv_title.setLayoutParams(pm);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_name.setTextSize(DisplayUtil.textGetSizeSp(context, 51));
			holder.tv_descrp = (TextView) convertView.findViewById(R.id.tv_descrp);
			holder.tv_descrp.setTextSize(DisplayUtil.textGetSizeSp(context, 24));
			holder.tv_descrp.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
			holder.tv_descrp.setPadding(0, 0, DisplayUtil.getSize(context, 24), 0);
			holder.rl_travel = (RelativeLayout) convertView.findViewById(R.id.rl_travel);
			
			holder.tvFullName = (TextView) convertView.findViewById(R.id.tv_fullname);
			holder.tvFullName.setTextSize(DisplayUtil.textGetSizeSp(context, 33));
			holder.tvFullName.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvFullName.getLayoutParams();
			lp.height = DisplayUtil.getSize(context, 60);
			lp.leftMargin = DisplayUtil.getSize(context, 24);
			holder.tvFullName.setLayoutParams(lp);
			
			holder.ivLine = (ImageView) convertView.findViewById(R.id.iv_line);
			lp = (LinearLayout.LayoutParams) holder.ivLine.getLayoutParams();
			lp.rightMargin = DisplayUtil.getSize(context, 12);
			lp.leftMargin = lp.rightMargin;
			holder.ivLine.setLayoutParams(lp);
			
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tvTime.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
			holder.tvTime.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
			pm = (RelativeLayout.LayoutParams) holder.tvTime.getLayoutParams();
			pm.leftMargin = DisplayUtil.getSize(context, 12);
			pm.topMargin = DisplayUtil.getSize(context, 12);
			pm.bottomMargin = DisplayUtil.getSize(context, 15);
			holder.tvTime.setLayoutParams(pm);
			
			holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
			holder.tvPrice.setTextSize(DisplayUtil.textGetSizeSp(context, 33));
			pm = (RelativeLayout.LayoutParams) holder.tvPrice.getLayoutParams();
			pm.leftMargin = DisplayUtil.getSize(context, 12);
			holder.tvPrice.setLayoutParams(pm);
			
			holder.tvPriceDetail = (TextView) convertView.findViewById(R.id.tv_price_detail);
			holder.tvPriceDetail.setTextSize(DisplayUtil.textGetSizeSp(context, 36));
			
			holder.tvStartTime = (TextView) convertView.findViewById(R.id.tv_start_time);
			holder.tvStartTime.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
			holder.tvStartTime.setPadding(DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 15), 0, DisplayUtil.getSize(context, 12));
			
			holder.ibtnPay = (ImageButton) convertView.findViewById(R.id.btn_pay);
			holder.ibtnPay.setPadding(0, 0, DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 24));
			
			holder.llLayout = (LinearLayout) convertView.findViewById(R.id.rl_top_info);
			lp = (LinearLayout.LayoutParams) holder.llLayout.getLayoutParams();
			lp.bottomMargin = DisplayUtil.getSize(context, 12);
			holder.llLayout.setLayoutParams(lp);
			
			holder.InsLayout = (View) convertView.findViewById(R.id.inc_travel_item);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final ViewHolder holderimage = holder;
		holderimage.iv_travelImg.setScaleType(ScaleType.FIT_XY);
		imageLoader.displayImage(list.get(position).getImg(), holder.iv_travelImg,imageLoaderOptions, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				holderimage.iv_travelImg.setScaleType(ScaleType.CENTER_CROP);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
		holder.tv_tag.setText(list.get(position).getTag() + " ");
		String color = list.get(position).getColor();
		if("1".equals(color)){
			holder.tv_tag.setBackgroundResource(R.drawable.tourism_pic_title_a);
			holder.iv_title.setImageResource(R.drawable.tourism_title_a);
		}else if("2".equals(color)){
			holder.tv_tag.setBackgroundResource(R.drawable.tourism_pic_title_b);
			holder.iv_title.setImageResource(R.drawable.tourism_title_b);
		}else if("3".equals(color)){
			holder.tv_tag.setBackgroundResource(R.drawable.tourism_pic_title_c);
			holder.iv_title.setImageResource(R.drawable.tourism_title_c);
		}else if("4".equals(color)){
			holder.tv_tag.setBackgroundResource(R.drawable.tourism_pic_title_d);
			holder.iv_title.setImageResource(R.drawable.tourism_title_d);
		}else if("5".equals(color)){
			holder.tv_tag.setBackgroundResource(R.drawable.tourism_pic_title_e);
			holder.iv_title.setImageResource(R.drawable.tourism_title_e);
		}
		
		holder.tv_name.setText(list.get(position).getName());
		holder.tv_descrp.setText(list.get(position).getIntroduction() + " ");
		
		holder.tvFullName.setText(list.get(position).getFullname() + " ");
		
		
		if("0".equals(list.get(position).getIspaid())){
			if(!isOverTime(list.get(position).getOrder_time())){
				holder.tvTime.setText("等待支付时间  " + getOrderLeftTime(list.get(position).getOrder_time()) + " ");
				holder.ibtnPay.setImageResource(R.drawable.btn_paymant_selector);
				holder.ibtnPay.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						CmmobiClickAgentWrapper.onEvent(context, "t_tra_paynow");
						mCurrentOrderId = list.get(position).getId();
						Requester.requestTravelPay(handler, Info.getDevId(context), list.get(position).getLine_id() , list.get(position).getTime(), list.get(position).getTotal_price(), list.get(position).getAdult_ticket(), list.get(position).getChild_ticket(), list.get(position).getUsername(), list.get(position).getCellphone(), list.get(position).getEmailaddr(), list.get(position).getIdcardfnum());
					}
				});
			}else{
				holder.tvTime.setText("订单已过期 ");
				holder.ibtnPay.setImageResource(R.drawable.btn_paymant_restart_selector);
				holder.ibtnPay.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "railwifidb", null);
						SQLiteDatabase db = helper.getWritableDatabase();
						DaoMaster daoMaster = new DaoMaster(db);
						DaoSession daoSession = daoMaster.newSession();
						TravelOrderInfoDao travelOrderInfoDao = daoSession.getTravelOrderInfoDao();
						travelOrderInfoDao.deleteByKey(list.get(position).getId());
						db.close();
						CmmobiClickAgentWrapper.onEvent(context, "t_tra_again");
						Intent intent = new Intent(context, RailTravelDetailAcitivity.class);
						intent.putExtra("lineid", list.get(position).getLine_id());
						context.startActivity(intent);
						context.finish();
					}
				});
			}
		}else{
			holder.tvTime.setText("支付时间  " + DateUtils.getStringFromMilli(list.get(position).getOrder_time(), DateUtils.DATE_FORMAT_NORMAL));
			holder.ibtnPay.setImageResource(R.drawable.btn_payment_success);
			holder.ibtnPay.setOnClickListener(null);
		}
		holder.tvPriceDetail.setText(list.get(position).getTotal_price() + "元");
		holder.tvStartTime.setText("出发时间：" + list.get(position).getTime());
		
		holder.InsLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, RailTravelDetailAcitivity.class);
				intent.putExtra("lineid", list.get(position).getLine_id());
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	

	Boolean isOverTime(String time){
		long iTime = 5*60 - (System.currentTimeMillis() - Long.parseLong(time))/1000/60;
		if(iTime>0){
			return false;
		}else{
			return true;
		}
	}
	
	
	/**
	 * 输入毫秒
	 * 返回格式 00小时00分
	 */
	public String getOrderLeftTime(String time){
		if(time == null) return null;
		String format="";
		try {
			long iTime=5*60 - (System.currentTimeMillis() - Long.parseLong(time))/1000/60;
			for(int i=0;i<2;i++){
				if(0==format.length()){
					format=new DecimalFormat("00").format(iTime%60);
				}else{
					format=new DecimalFormat("00").format(iTime%60)+"小时"+format + "分";
				}
				iTime=iTime/60;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return format;
	}
			
	public class ViewHolder {
		ImageView iv_travelImg;
		TextView tv_tag;
		RelativeLayout rl_travel;
		ImageView iv_title;
		TextView tv_name;
		TextView tv_descrp;
		
		TextView tvFullName;
		ImageView ivLine;
		TextView tvTime;
		TextView tvPrice;
		TextView tvPriceDetail;
		TextView tvStartTime;
		ImageButton ibtnPay;
		
		LinearLayout llLayout;
		
		View InsLayout;
	}

	
	
}