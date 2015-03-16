package com.etoc.weflow.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.etoc.weflow.Config;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.event.RequestEvent;
import com.etoc.weflow.net.GsonRequestObject.*;
import com.etoc.weflow.net.GsonResponseObject.*;
import com.etoc.weflow.utils.VMobileInfo;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Requester {
	private static final int isDebug = 0;
	
	///////////////////////////////////////Response code:
	public static final int RESPONSE_TYPE_TEST = 0xffee2000;
	public static final String RIA_INTERFACE_TEST = "/test/ct";
	
	//2.1.1 车上虚拟注册
	public static final int RESPONSE_TYPE_SENDSMS = 0xffee2100;
	public static final String RIA_INTERFACE_SENDSMS = "/vs/api/getAuthCode";
	
	public static final int RESPONSE_TYPE_LOGIN = 0xffee2101;
	public static final String RIA_INTERFACE_LOGIN = "/vs/api/user/login";
	
	public static final int RESPONSE_TYPE_ACC_INFO = 0xffee2102;
	public static final String RIA_INTERFACE_ACC_INFO = "/rw/service/getaccountinfo.html";
	
	public static final int RESPONSE_TYPE_ADV_INFO = 0xffee2103;
	public static final String RIA_INTERFACE_ADV_INFO = "/rw/service/getadvinfo.html";
	
	public static final int RESPONSE_TYPE_ORDER_LARGESS = 0xffee2104;
	public static final String RIA_INTERFACE_ORDER_LARGESS = "/interface/service/orderLargess";
	
	public static final int RESPONSE_TYPE_REGISTER = 0xffee2105;
	public static final String RIA_INTERFACE_REGISTER = "/vs/api/user/register";

	public static final int RESPONSE_TYPE_VERIFY_CODE = 0xffee2106;
	public static final String RIA_INTERFACE_VERIFY_CODE = "/vs/api/user/verifyAuthCode";
	
	public static String IMEI = VMobileInfo.getIMEI();
	public static String MAC  = VMobileInfo.getDeviceMac();
	
	public static void test(Handler handler) {
		testRequest request = new testRequest();
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_TEST, testResponse.class);
		worker.execute(RIA_INTERFACE_TEST, request);
	}
	
	public static void sendSMS(Handler handler, String tel, String type) {
		getAuthCodeRequest request = new getAuthCodeRequest();
		request.tel  = tel;
		request.type = type;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_SENDSMS, sendSMSResponse.class);
		worker.execute(RIA_INTERFACE_SENDSMS, request);
	}
	
	public static void verifyAuthCode(Handler handler, String tel, String authcode, String type) {
		verifyAuthCodeRequest request = new verifyAuthCodeRequest();
		request.tel  = tel;
		request.authcode = authcode;
		request.type = type;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_VERIFY_CODE, verifyAuthCodeResponse.class);
		worker.execute(RIA_INTERFACE_VERIFY_CODE, request);
	}
	
	public static void login(Handler handler, String tel, String pass) {
		loginRequest request = new loginRequest();
		request.tel = tel;
		request.password = "app";
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_LOGIN, loginResponse.class);
		worker.execute(RIA_INTERFACE_LOGIN, request);
	}
	
	public static void register(Handler handler, String tel, String pass) {
		registerRequest request = new registerRequest();
		request.tel = tel;
		request.password = pass;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_REGISTER, registerResponse.class);
		worker.execute(RIA_INTERFACE_REGISTER, request);
	}
	
/*	public static void sendSMS(Handler handler, String tel) {
		sendSMSRequest request = new sendSMSRequest();
		request.phone  = tel;
		request.channelid = "app";
		request.transid = "" + System.currentTimeMillis();
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_SENDSMS, sendSMSResponse.class);
		worker.execute(RIA_INTERFACE_SENDSMS, request);
	}
	
	public static void login(Handler handler, String tel, String code) {
		loginRequest request = new loginRequest();
		request.authcode = code;
		request.channelid = "app";
		request.transid = "" + System.currentTimeMillis();
		request.phone = tel;
		request.weixinid = "Wang_JM";
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_LOGIN, loginResponse.class);
		worker.execute(RIA_INTERFACE_LOGIN, request);
	}
	
	public static void getAccInfo(Handler handler, String uuid) {
		getAccInfoRequest request = new getAccInfoRequest();
		request.uuid = uuid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_ACC_INFO, getAccInfoResponse.class);
		worker.execute(RIA_INTERFACE_ACC_INFO, request);
	}
	
	public static void getAdvInfo(Handler handler, String uuid) {
		getAdvInfoRequest request = new getAdvInfoRequest();
		request.uuid = uuid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_ADV_INFO, getAdvInfoResponse.class);
		worker.execute(RIA_INTERFACE_ADV_INFO, request);
	}
	
	public static void orderLargess(Handler handler,String phone,String type,String product) {
		orderLargessRequest request = new orderLargessRequest();
		request.channelid = "app";
		request.transid = "" + System.currentTimeMillis();
		request.productid = product;
		request.phone = phone;
		request.opertype = type;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_ORDER_LARGESS, orderLargessResponse.class);
		worker.execute(RIA_INTERFACE_ORDER_LARGESS, request);
	}*/
	
	public static class PostWorker extends Thread {
		private static final String TAG = "Requester";
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String ria_command_id;
		private Object request;
//		private boolean use_dc;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public PostWorker(/*boolean ria_type, */Handler handler, int responseType, Class<?> cls) {
//			this.use_dc = ria_type;
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(String ria_command_id, Object request) {
			// TODO Auto-generated method stub
			this.ria_command_id = ria_command_id;
			this.request = request;
			this.start();
			
		}
		

		@Override
		public void run() {
			EventBus.getDefault().post(RequestEvent.LOADING_START);
			String url = /*(use_dc?Config.SERVER_DC_URL:Config.SERVER_RIA_URL)*/Config.SERVER_URL + ria_command_id;
			System.out.println("url--->" + url + ", responseType:" + responseType + ", request:" + gson.toJson(request));
		    Log.v(TAG,"request url--->" + url + ", responseType:" + responseType + ", request:" + gson.toJson(request));
			String ret_entity_str = null;
		    Object object = null;
		    
			if(isDebug==1){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				object = FakeData.map.get(ria_command_id);
				
			}
			
			if(object==null){

			    HttpPost httpPostRequest = new HttpPost(url);
//			    httpPostRequest.addHeader("Content-Type", "application/json");
			    DefaultHttpClient httpClient = new DefaultHttpClient();
			    ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

			    String json = gson.toJson(request);
			    
			    String saltstr = MD5.encodeByMd5AndSalt(json);
			    
			    parameters.add(new BasicNameValuePair("json", json));
			    parameters.add(new BasicNameValuePair("sign", saltstr));
			    
//			    String securityJson = "{\"json\": \"" + json + "\", \"sign\":\"" + saltstr + "\"}";
			    
			    try {
					httpPostRequest.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
					//httpPostRequest.setEntity(new StringEntity(securityJson/*json*/, HTTP.UTF_8));

					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					
					HttpResponse localHttpResponse = httpClient.execute(httpPostRequest);
				    
					if(localHttpResponse!=null){
					    ret_entity_str = EntityUtils.toString(localHttpResponse.getEntity());
					    Log.v(TAG, "ret_entity_str:" + ret_entity_str);
					    System.out.println("ret_entity_str:" + ret_entity_str);
					}

				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
		    if(ret_entity_str!=null){
		    	try{
		    		object = gson.fromJson(ret_entity_str, cls);
		    		
		    		/*if (responseType == RESPONSE_TYPE_ORDER_LARGESS) {
		    			orderLargessResponse resp = (orderLargessResponse) object;
		    			if (resp != null && resp.blance != null) {
		    				WeFlowApplication.totalFlow = Integer.parseInt(resp.blance);
		    			}
		    		}*/
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    } else{
		    	Log.v(TAG, "request url--->" + url  + "*******ret_entity_str:" + ret_entity_str);
		    	EventBus.getDefault().post(RequestEvent.RESP_NULL);

		    }
		    
		    EventBus.getDefault().post(RequestEvent.LOADING_END);
		    
			if (handler != null) {
				if(object==null){
					Log.e(TAG, "object is null - ria_command_id:" + ria_command_id);
				}
				
				Message message = handler.obtainMessage(responseType, object);
				try {
					handler.sendMessage(message);
				} catch (Exception e) {
					Log.e(TAG, "Perhaps sending message to a Handler on a dead thread - ria_command_id:" + ria_command_id);
				}

			} else {
				Log.e(TAG, "handler is null, data can not callback - ria_command_id:" + ria_command_id);
			}
			
			
		}

	}

}
