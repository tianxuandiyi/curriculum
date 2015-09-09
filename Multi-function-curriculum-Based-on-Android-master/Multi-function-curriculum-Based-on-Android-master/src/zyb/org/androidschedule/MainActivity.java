package zyb.org.androidschedule;

import temp.DataBase;
import temp.MyApplication;
import temp.MyDialog;
import temp.ShareMethod;
import zyb.org.editschedule.SetActivity;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends Activity {
	 
	public ListView list[] = new ListView[7];
	private TabHost tabs   = null;
	private TextView exitButton = null; 
	private TextView setButton = null;
	public static DataBase db;
	public Cursor[] cursor=new Cursor[7];
	public SimpleCursorAdapter adapter;
	private SharedPreferences pre;

    //�������Ƽ����ʵ��
	private GestureDetector detector = null;
    //�������ƶ�������֮�����С����
	private final int FLIP_DISTANCE = 200;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		//����activity���뵽MyApplication����ʵ��������
		MyApplication.getInstance().addActivity(this);
		
		db=new DataBase(MainActivity.this);
		pre=getSharedPreferences("firstStart",Context.MODE_PRIVATE);
		/*
		 * �жϳ����Ƿ��һ�����У�����Ǵ������ݿ��
		 */
		if(pre.getBoolean("firstStart", true)){
			SingleInstance.createTable();
			(pre.edit()).putBoolean("firstStart",false).commit();
//			finish();			
		}

		
		exitButton = (TextView)findViewById(R.id.exitButton);
		setButton = (TextView)findViewById(R.id.setButton);
		list[0] = (ListView)findViewById(R.id.list0);
		list[1] = (ListView)findViewById(R.id.list1);	
		list[2] = (ListView)findViewById(R.id.list2);
		list[3] = (ListView)findViewById(R.id.list3);	
		list[4] = (ListView)findViewById(R.id.list4);	
		list[5] = (ListView)findViewById(R.id.list5);
		list[6] = (ListView)findViewById(R.id.list6);			
		tabs  = (TabHost)findViewById(R.id.tabhost);
	    //�������Ƽ����
	    detector = new GestureDetector(this, new DetectorGestureListener());
   
		//�������κε�TabSpec֮ǰ��������TabHost�ϵ��ø÷���
		tabs.setup();
		
		//Ϊ������ע���߸�ѡ�
		TabHost.TabSpec  spec = null;
		addCard(spec,"tag1",R.id.list0,"��");
		addCard(spec,"tag2",R.id.list1,"һ");
		addCard(spec,"tag3",R.id.list2,"��");
		addCard(spec,"tag4",R.id.list3,"��");
		addCard(spec,"tag5",R.id.list4,"��");
		addCard(spec,"tag6",R.id.list5,"��");
		addCard(spec,"tag7",R.id.list6,"��");
		
		//�޸�tabHostѡ��е��������ɫ
		TabWidget tabWidget = tabs.getTabWidget();
		for(int i=0;i<tabWidget.getChildCount();i++){
			TextView tv = (TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(0xff004499);				
		}
		
		//���ô�ʱĬ�ϵ�ѡ��ǵ����ѡ�
		tabs.setCurrentTab(ShareMethod.getWeekDay());
		
		//��������Ϊ��ѡ������Ҫ��ʾ������
		for(int i=0;i<7;i++){
			cursor[i]=MainActivity.db.select(i);		
			list[i].setAdapter(adapter(i));
		}
		
		//����һ����ȡϵͳ��Ƶ�������Ķ���
		final AudioManager audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
		//��ȡ�ֻ�֮ǰ���úõ�����ģʽ,�����ݽ��������ݸ�activity_set
		 final int orgRingerMode = audioManager.getRingerMode(); 
		 
		//Ϊ�˳���ť�󶨼�����
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//����AlertDialog.Builder���󣬸ö�����AlterDialog�Ĵ�������AlterDialog�������������Ի���
			    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				exit(builder);
			}
		}); 
		
		//Ϊ���ð�ť�󶨼�����
		setButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SetActivity.class);
				//��orgRingerMode���ݴ���activity_set
				intent.putExtra("mode_ringer", orgRingerMode);
				startActivity(intent);
			}
		});
		
		for( int day=0;day<7;day++){
			//Ϊ�߸�ListView�󶨴����������������ϵĴ����¼�����GestureDetector����
			//�˼������Ǳ���ģ���Ȼ��������ֻ��ListView�µĿհ�������Ч������ListView����Ч
			list[day].setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event)   {
					return detector.onTouchEvent(event);
				}
			});
			//Ϊÿ��ListView��ÿ��item�󶨼�����������򵯳���AlertDialog�������б�Ի������ѡ��
			list[day].setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						final int id, long arg3) {
					final int currentDay=tabs.getCurrentTab();
					final int n=id;
				    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				    builder.setIcon(R.drawable.ic_launcher);
					builder.setTitle("ѡ��");	
					TextView tv=(TextView)arg1.findViewById(R.id.ltext0);
					Log.i("Test",(tv.getText().toString().equals(""))+"");
					//����γ���ĿΪ�վ�������ӶԻ���
					if((tv.getText()).toString().equals("")){
						//ͨ��������ԴΪ�Ի����е��б����ѡ�����ݣ�����ֻ��һ��ѡ��
						builder.setItems(R.array.edit_options1, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {					
								//��������˸��б������ת���༭�γ���Ϣ�Ľ���
								if(which == 0){					
									new MyDialog(MainActivity.this).add(currentDay,n);
								}
							}
						});
						builder.create().show();
					  }
					//���������޸ĶԻ��򣬻�ֱ��ɾ������
					else{
						builder.setItems(R.array.edit_options2, new DialogInterface.OnClickListener() {
							
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//��������˸��б������ת���༭�γ���Ϣ�Ľ���
								if(which == 0){					
									new MyDialog(MainActivity.this).modify(currentDay,n);
								}
								if(which == 1){
									cursor[currentDay].moveToPosition(n);
									int n1=Integer.parseInt(cursor[currentDay].getString(7));//�γ̵��ܽ���
									int n2=Integer.parseInt(cursor[currentDay].getString(8));//ѡ�е�Ϊ�ÿγ̵ĵڼ���
									switch(n2){
										case 0:
											for(int m=0;m<n1;m++){
												MainActivity.db.deleteData(currentDay,n+m+1);
												}
											break;
	
										case 1:
											MainActivity.db.deleteData(currentDay,n);
											for(int m=1;m<n1;m++){
												MainActivity.db.deleteData(currentDay,n+m);
												}
											break;		
										case 2:
											MainActivity.db.deleteData(currentDay,n-1);
											MainActivity.db.deleteData(currentDay,n);
											for(int m=2;m<n1;m++){
												MainActivity.db.deleteData(currentDay,n+m-1);
												}
												break;
										case 3:
											for(int m=n2;m>=0;m--){
												MainActivity.db.deleteData(currentDay,n-m+1);
												}
												break;
										default:
											break;
									}
									cursor[currentDay].requery();
									list[currentDay].invalidate();
								}
							}
						});
						builder.create().show();
					}
				}
			});
		}
		
	}
	//�ڲ��࣬ʵ��GestureDetector.OnGestureListener�ӿ�
	class DetectorGestureListener implements GestureDetector.OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		//���û��ڴ����ϡ�������ʱ�����˷���
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int i = tabs.getCurrentTab();
			//��һ�������¼���X����ֵ��ȥ�ڶ��������¼���X����ֵ����FLIP_DISTANCE��Ҳ�������ƴ������󻬶�
				if(e1.getX() - e2.getX() > FLIP_DISTANCE){
					if(i<6)
						tabs.setCurrentTab(i+1);
				//	float currentX = e2.getX();
				//	list[i].setRight((int) (inialX - currentX));
					return true;
				}

				//�ڶ��������¼���X����ֵ��ȥ��һ�������¼���X����ֵ����FLIP_DISTANCE��Ҳ�������ƴ������һ���
				else if(e2.getX() - e1.getX() > FLIP_DISTANCE){
					if(i>0)
						tabs.setCurrentTab(i-1);	
					return true;
				}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	
	}
	
	
	//��дActivity�е�onTouchEvent����������Activity�ϵĴ����¼�����GestureDetector����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return detector.onTouchEvent(event);
	}
	
	//���ò˵���ť
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	//������˵��еġ��˳�����ʱ��������ʾ�Ƿ��˳��ĶԻ���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//����AlertDialog.Builder���󣬸ö�����AlterDialog�Ĵ�������AlterDialog�������������Ի���
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(item.getItemId() == R.id.menu_exit){
			exit(builder);
			return true;
		}
		if(item.getItemId() == R.id.menu_settings){
			Intent intent = new Intent(MainActivity.this, SetActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	} 
	

	//�� ����:Ϊ���������ѡ�
	public void addCard(TabHost.TabSpec spec,String tag,int id,String name){
		spec = tabs.newTabSpec(tag);
		spec.setContent(id);
		spec.setIndicator(name);
		tabs.addTab(spec);
	}
	//�ӷ��������������Ƿ��˳�����ĶԻ��򣬲�ִ��ִ���Ƿ��˳�����
	public void exit(AlertDialog.Builder builder){
		//Ϊ�����ĶԻ������ñ��������
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("�˳�����");
		builder.setMessage("ȷ��Ҫ�˳�����γ̱���");
		//������ߵİ�ťΪ��ȷ��������������󶨼�������������˳�
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//�˳�Ӧ�ó��򣬼����ٵ����е�activity
				MyApplication.getInstance().exitApp();
			}
		});
		//�����ұߵİ�ťΪ��ȡ��������������󶨼��������������Ȼͣ���ڵ�ǰ����
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
								
			}
		});
		//��������ʾ�����ĶԻ���
		builder.create().show();
	}
	/*
	 * Ϊÿһ��list�ṩ����������
	 */
	@SuppressWarnings("deprecation")
	public SimpleCursorAdapter adapter(int i){
		return new SimpleCursorAdapter(this, R.layout.list_v2,cursor[i],new String[]{"_id","classes","location",
		"teacher","zhoushu"},new int[]{R.id.number,R.id.ltext0,R.id.ltext1,R.id.ltext6,R.id.ltext7} );
	}
	
	/*
	 * ��һ������ʱ�������ݿ��
	 */
	static class SingleInstance{
		static SingleInstance si;
		private SingleInstance(){
			for(int i=0;i<7;i++){
				db.createTable(i);
			}
		}
		static SingleInstance createTable(){
			if(si==null)
				return si=new SingleInstance();
			return null;
		}
	}
}
