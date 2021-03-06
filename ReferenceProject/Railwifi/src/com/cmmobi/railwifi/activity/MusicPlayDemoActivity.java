package com.cmmobi.railwifi.activity;
 
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
 
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class MusicPlayDemoActivity extends Activity{
     
    private TextView musicName ;//歌曲名称
    private TextView startTime;//播放时间
    private TextView endTime;//结束时间
    private SeekBar seekbar;//进度条
     
    private Button start;//开始按钮
    private Button pause;//暂停按钮
    private Button restart;//重新开始
    private Button stop;//停止按钮
     
    private ListView mylist;//列表
    private SimpleAdapter adapter = null;   // 适配器
     
    private Timer timer;
     
    private MediaPlayer media = null;
    private List<Map<String, String>> musicListData;
     
    private String lastPlayName = null;//记录最后一次播放的歌曲
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
 
    //初始化
    private void init(){
         
        musicListData = 
                new ArrayList<Map<String, String>>();   // 保存所有的List数据
         
        this.musicName = (TextView) this.findViewById(R.id.musicName);
        this.startTime = (TextView)this.findViewById(R.id.startTime);
        this.endTime = (TextView)this.findViewById(R.id.endTime);
        this.seekbar = (SeekBar)this.findViewById(R.id.seekbar);
        this.start = (Button)this.findViewById(R.id.start);
        this.pause = (Button)this.findViewById(R.id.pause);
        this.restart = (Button)this.findViewById(R.id.restart);
        this.stop = (Button)this.findViewById(R.id.stop);
        this.mylist = (ListView)this.findViewById(R.id.mylist);
        addList();
        //点击事件
        this.start.setOnClickListener(new MyClickListener());
        this.pause.setOnClickListener(new MyClickListener());
        this.restart.setOnClickListener(new MyClickListener());
        this.stop.setOnClickListener(new MyClickListener());
         
        this.seekbar.setOnSeekBarChangeListener(new SeekBarOnClickListenerImpl());
        //开启搜索sd卡中MP3文件的线程
        new Thread(){
        public void run(){
            getMusicDate();//搜索sd卡下面的mp3后缀的文件
        }
    }.start();
    }
     
    //待搜索线程完成后，执行这个添加数据到界面上
    public void addList(){
        this.adapter = new SimpleAdapter(this,this.musicListData,R.layout.list,
                new String[]{"name","size"},new int[]{R.id.name,R.id.size});
        this.mylist.setAdapter(adapter);
        this.mylist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapt, View v, int postition,
                    long id) {
                Map<String,String> map = MusicPlayDemoActivity.this.musicListData.get(postition);
                if(MusicPlayDemoActivity.this.media!=null){
                    stop();
                }
                play(map.get("path"));
            }
        });
    }
     
    //获取播放列表
    public void getMusicDate(){
         
        if(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))){
            //如果不sd卡存在
            return;
        }
        File rootPath = Environment.getExternalStorageDirectory();
        File[] listFile = rootPath.listFiles();
        if(listFile!=null){
            for(File f : listFile){
                if(f.isDirectory()){//如果是目录
                    System.out.println("根文件夹："+f.getName());
                    getFile(f);
                }else{//如果是文件
                    getAFile(f);
                }
            }
        }
         
        Message message = new Message();
        message.what = 3;
        MusicPlayDemoActivity.this.handler.sendMessage(message);
        return;
    } 
     
    //文件
    private void getAFile(File f){
        String fName = f.getName();
        if(!fName.endsWith("mp3")){
            return;
        }
        Map<String,String> map = new HashMap<String,String>();
        map.put("name",fName);
        map.put("size", String.valueOf(f.length()/1024/1024)+"MB");
        map.put("path",f.getAbsolutePath());
        musicListData.add(map);
        System.out.println("音乐文件路径："+f.getAbsolutePath());
    }
    //路径
    private void getFile(File f){
        if(!f.isDirectory()){
            getAFile(f);
            return;
        }
        File[] listFile = f.listFiles();
        if(listFile!=null){
            for(File ff : listFile){
                if(f.isDirectory()){//如果是目录
                    System.out.println("文件夹："+ff.getName());
                    getFile(ff);
                }else{//如果是文件
                    System.out.println("文件："+ff.getName());
                    getAFile(ff);
                }
            }
        }
    }
     
    //进度条监听回调
    private class SeekBarOnClickListenerImpl implements SeekBar.OnSeekBarChangeListener {
 
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            if(!fromUser)return;
            if(MusicPlayDemoActivity.this.media!=null){
                 
                Message message = new Message();
                Bundle b = new Bundle();
                b.putInt("p", progress);
                message.setData(b);
                message.what = 2;
                MusicPlayDemoActivity.this.handler.sendMessage(message);
                MusicPlayDemoActivity.this.media.seekTo(progress);
            }       
        }
 
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
             
        }
 
    }
     
    //播放控制的按钮事件
    class MyClickListener implements OnClickListener{
 
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.start:
                    play(Environment.getExternalStorageDirectory()+File.separator+"my.mp3");
                    break;
                case R.id.pause:
                    pause();break;
                case R.id.restart:
                    restart();break;
                case R.id.stop:
                    stop();break;
            }
        }
         
    }
     
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    //停止播放
    private void stop(){
        if(MusicPlayDemoActivity.this.media!=null){
            MusicPlayDemoActivity.this.start.setClickable(true);
            MusicPlayDemoActivity.this.media.release();
            MusicPlayDemoActivity.this.media = null;
        }
    }
     
    //从头开始播放
    private void restart(){
        if(MusicPlayDemoActivity.this.media!=null){
            MusicPlayDemoActivity.this.media.seekTo(0);
        }
    }
     
    //暂停获取继续播放
    private void pause(){
        if(MusicPlayDemoActivity.this.media==null){
            MusicPlayDemoActivity.this.pause.setText("暂停");
            return;
        }
        if("继续".equals(MusicPlayDemoActivity.this.pause.getText().toString())){
            MusicPlayDemoActivity.this.media.start();
            MusicPlayDemoActivity.this.pause.setText("暂停");
        }else if(MusicPlayDemoActivity.this.media.isPlaying()){
            MusicPlayDemoActivity.this.media.pause();
            MusicPlayDemoActivity.this.pause.setText("继续");
        }
    }
 
    //开始播放
    private void play(String path){
        if(path==null||"".equals(path)){
            Toast.makeText(this, "请选择播放文件", Toast.LENGTH_SHORT);
            return;
        }
        this.musicName.setText(path.substring(path.lastIndexOf("/")+ 1));
        File file = new File(path);
        if(file.exists()&&file.length()>0){
            try {
                this.media = new MediaPlayer();
                this.media.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放类型
                this.media.setDataSource(path);
                this.media.setOnCompletionListener(new MediaCompletionListener());
                this.media.setOnErrorListener(new MediaErrorListener());
                this.media.prepare();
                this.media.start();
                this.start.setClickable(false);
                //启动事件更新及进度条更新任务，每0.5s更新一次
                MusicPlayDemoActivity.this.timer = new Timer();
                MusicPlayDemoActivity.this.timer.schedule(new MyTask(), 0, 1000);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "播放文件错误", Toast.LENGTH_SHORT).show();
            }
             
        }else{
            Toast.makeText(this, "播放文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
     
    @Override
    protected void onDestroy() {
        super.onDestroy();
             
        if(this.media != null) {  //如果播放器对象不为空
            this.media.release();    //释放播放器资源
            this.media = null;        //设置对象为空
        }
    }
     
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case 1:
                if(MusicPlayDemoActivity.this.media != null) {
                    int progress = MusicPlayDemoActivity.this.media.getCurrentPosition();  //当前播放毫秒
                    int allTime = MusicPlayDemoActivity.this.media.getDuration();      //总毫秒             
                    MusicPlayDemoActivity.this.seekbar.setMax(allTime);//设置进度条
                    MusicPlayDemoActivity.this.startTime.setText(getTimeFormat(progress));
                    MusicPlayDemoActivity.this.endTime.setText(getTimeFormat(allTime));
                    MusicPlayDemoActivity.this.seekbar.setProgress(progress);
                }
                break;
            case 2:
                break;
            case 3:
                addList();
                break;
            }
             
            super.handleMessage(msg);
        }
    };
     
    private String getTimeFormat(int time) {
        String timeStr = "00:00:00";
        int s = time/1000;   //秒
        int h = s / 3600;    //求整数部分 ，小时
        int r = s % 3600;    //求余数
        int m = 0;
        if(r > 0) {
            m = r / 60;    //分
            r = r % 60;    //求分后的余数，即为秒
        }
         
        if(h < 10) {
            timeStr = "0" + h;
        } else {
            timeStr = "" + h;
        }
         
        if(m < 10) {
            timeStr = timeStr + ":" + "0" + m;
        } else {
            timeStr = timeStr + ":" + m;
        }
         
        if(r < 10) {
            timeStr = timeStr + ":" + "0" + r;
        } else {
            timeStr = timeStr + ":" + r;
        }
         
        return timeStr;
    }
 
     
    private class MyTask extends TimerTask {
 
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            MusicPlayDemoActivity.this.handler.sendMessage(message);
        }       
    }
 
    //播放时候错误回调
    private class MediaErrorListener implements OnErrorListener{
        @Override
        public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
            MusicPlayDemoActivity.this.media.stop();
            MusicPlayDemoActivity.this.media.release();
            MusicPlayDemoActivity.this.media = null;
            Toast.makeText(MusicPlayDemoActivity.this, "播放时候遇到错误，播放停止", Toast.LENGTH_LONG).show();
            return false;
        }
    }
    //播放完成事件
    private class MediaCompletionListener implements OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer arg0) {
            MusicPlayDemoActivity.this.media.stop();
            MusicPlayDemoActivity.this.media.release();
            MusicPlayDemoActivity.this.media = null;
            MusicPlayDemoActivity.this.musicName.setText("当前没有播放");
            MusicPlayDemoActivity.this.startTime.setText("00:00");
            MusicPlayDemoActivity.this.endTime.setText("00:00");
            MusicPlayDemoActivity.this.start.setClickable(true);
        }
         
    }
}