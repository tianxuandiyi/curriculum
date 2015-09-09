package zyb.org.editschedule;

import temp.MyApplication;
import zyb.org.about.AboutUsActivity;
import zyb.org.androidschedule.R;
import zyb.org.service.RemindReceiver;
import zyb.org.version.VersionActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SetActivity extends Activity {
	
	//����һ��SharedPreferences������������switch����Ŀ�����Ϣ
	private SharedPreferences preferences = null;
	//editor����������preferences��д������
	private SharedPreferences.Editor editor = null;	
	
	//����һ��SharedPreferences������������time_choice��ֵ
	private SharedPreferences pre = null;
	//pre_editor����������pre��д������
	private SharedPreferences.Editor pre_editor = null;	
	
	//����һ��AlarmManager��������������ǰ���ѷ���
	private AlarmManager alarmManager = null;
	//����һ��PendingIntent��������ָ��alarmManagerҪ���������
	private PendingIntent pi = null;
	private Intent alarm_receiver = null;
	 
	//���嵥ѡ�б�Ի����id���öԻ���������ʾ��ǰ����ʱ��Ŀ�ѡ��
	final int SINGLE_DIALOG = 0x113;
	//����ѡ�е�ʱ��
	private int time_choice = 0;
	
	private Switch switch_quietButton;
	private Switch switch_remindButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		//����activity���뵽MyApplication����ʵ��������
		MyApplication.getInstance().addActivity(this);
		
		//����һ����ȡϵͳ��Ƶ�������Ķ���
		final AudioManager audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
		//��MainAcivity�л�ȡԭʼ���õ�����ģʽ
		Intent intent = getIntent();
		final int orgRingerMode = intent.getIntExtra("mode_ringer", AudioManager.RINGER_MODE_NORMAL);
		//��ȡϵͳ�����Ӷ�ʱ����
		alarmManager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
		
		//ָ��alarmManagerҪ���������
		alarm_receiver = new Intent(SetActivity.this,RemindReceiver.class);
//		alarm_receiver.putExtra("anvance_remindtime", time_choice);
		pi = PendingIntent.getBroadcast(SetActivity.this, 0, alarm_receiver, 0);
		
		//ȡ�������
		TextView backButton = (TextView)findViewById(R.id.backtoMainButton);
		switch_quietButton = (Switch)findViewById(R.id.switch_quiet);
		switch_remindButton = (Switch)findViewById(R.id.switch_remind);
		
		//����ģʽһ��Ҫ����ΪMODE_MULTI_PROCESS������ʹ��Ӧ��xml�ļ��������и��£�RemindReceiver��Ҳ���ܻ�ȡ���º�����ݣ�����һֱ��ȡ�ϴε����ݣ� ������ջ���
		this.pre = SetActivity.this.getSharedPreferences("time", Context.MODE_MULTI_PROCESS);
		this.pre_editor = pre.edit();	
		
		//ָ����SharedPreferences���ݿ��Կ���Ƶ���
		this.preferences = SetActivity.this.getSharedPreferences("switch", Context.MODE_MULTI_PROCESS);
		this.editor = preferences.edit();		
		//ÿ�δ�����activityʱ����preferences�ж�ȡswitch_quietButton��switch_remindButton�Ŀ�����Ϣ������
		Boolean quiet_status = preferences.getBoolean("switch_quiet", false);
		Boolean remind_status = preferences.getBoolean("switch_remind", false);
		switch_quietButton.setChecked(quiet_status);
		switch_remindButton.setChecked(remind_status);		
		
		//Ϊ���ذ�ť�󶨼�����
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
//				Intent intent = new Intent(Set.this,MainActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//ˢ��
//				startActivity(intent);
			}
		});
		
		//Ϊ�Զ��������ذ�ť�󶨼�����
		switch_quietButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//�����Զ�������service
				Intent intent = new Intent();
				intent.setAction("zyb.org.service.QUIET_SERVICE");
				
				if(isChecked){
					if(startService(intent) != null)
						Toast.makeText(SetActivity.this, "�ɹ��������Ͽ��ڼ�����罫�Զ�תΪ��ģʽ", 3000).show();
					else{
						Toast.makeText(SetActivity.this, "δ�ܳɹ������������³���", 3000).show();
						switch_quietButton.setChecked(false);
					}
				}
				else{
					if(stopService(intent))
						Toast.makeText(SetActivity.this, "�ɹ��رգ��ָ���ԭ��������ģʽ", 3000).show();
					else{
						Toast.makeText(SetActivity.this, "δ�ܳɹ��رգ������³���", 3000).show();
						switch_quietButton.setChecked(true);
					}
					audioManager.setRingerMode(orgRingerMode);
				}
				//��������Ϣ���ݱ����preferences��
				SetActivity.this.editor.putBoolean("switch_quiet", isChecked);
				editor.commit();
			}
		});
		
		//Ϊ��ǰ���ѿ��ذ�ť�󶨼�����
		switch_remindButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					showDialog(SINGLE_DIALOG);
				}
				else{
					alarmManager.cancel(pi);
				}
				//��������Ϣ���ݱ����preferences��
				SetActivity.this.editor.putBoolean("switch_remind", isChecked);
				editor.commit();
			}
		});
		
	}
				
	@Override
	//�÷������ص�Dialog����showDialog()�����ص�
	protected Dialog onCreateDialog(int id, Bundle args) {
		//�ж����ɺ������͵ĶԻ���
		if(id == SINGLE_DIALOG){
			Builder b = new AlertDialog.Builder(this);
			// ���öԻ���ı���
			b.setTitle("ѡ���ǰ����ʱ��");
			// Ϊ�Ի������ö���б�����-1��ʾĬ�ϲ�ѡ���κ�ѡ��
			b.setSingleChoiceItems(R.array.set_remind, -1, new DialogInterface.OnClickListener(){
				
				@Override
				public void onClick(DialogInterface dialog,
					int which){
					switch (which){
						case 0:
							time_choice = 5;
							break;
						case 1:						
							time_choice = 10;
							break;
						case 2:
							time_choice = 20;
							break;
						case 3:
							time_choice = 30;
							break;
						case 4:
							time_choice = 40;
							break;
						case 5:
							time_choice = 50;
							break;
						case 6:
							time_choice = 60;
							break;	
					}
				}
			});
			// ���һ����ȷ������ť�����ڹرոöԻ���
			b.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					System.out.println("SetActivity:" + time_choice);
					if(time_choice == 0){
						Toast.makeText(SetActivity.this, "��ѡ���ǰ���ѵ�ʱ��", 3000).show();
						switch_remindButton.setChecked(false);
					}else{
						SetActivity.this.pre_editor.putInt("time_choice", time_choice);
						pre_editor.commit();
						//�ӵ�ǰʱ�俪ʼ��ÿ��һ��������һ��piָ���������������һ�ι㲥
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi);
						Toast.makeText(SetActivity.this, "���óɹ���ϵͳ���ڿ�ǰ" + time_choice + "����������", Toast.LENGTH_LONG).show();
					}
				}
			});
			//���һ����ȡ������ť
			b.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch_remindButton.setChecked(false);	
				}
			});
			// �����Ի���
			return b.create();
	 	}
		else
			return null;
	}



	//���������������ǡ���TextView������ת
	public void click_us(View v){
		Intent intent = new Intent(SetActivity.this, AboutUsActivity.class);
		startActivity(intent);
	}
	//���������汾֧�֡���TextView������ת
	public void click_version(View v){
		Intent intent = new Intent(SetActivity.this, VersionActivity.class);
		startActivity(intent);
	}
	public void click_revision(View v){
		Log.i("MyDebug", "revision");
	} 
}
