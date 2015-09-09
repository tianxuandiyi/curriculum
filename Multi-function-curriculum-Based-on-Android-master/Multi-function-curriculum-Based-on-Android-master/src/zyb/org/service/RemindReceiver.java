package zyb.org.service;

import java.util.Calendar;
import java.util.Date;
import temp.DataBase;
import temp.ShareMethod;
import zyb.org.editschedule.RemindActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

public class RemindReceiver extends BroadcastReceiver {

	//���屣��ÿ�ű����ݵ�cursor����
	Cursor[] cursor = new Cursor[7];
	//����ʱ�䣬temp[day][row][hm]��ʾ��day+1��tabѡ��еĵ�row+1�������û�����ĵ�һ�������γ̿�ʼ��ʱ����Ϊʱ�ͷ�
	//hmΪ0ʱ��ʾʱ��1��ʾ�֣�2ʱ����ʱ�ͷֵ���ϣ���δ���ǰ���ַ���
	String[][][] temp = new String[7][12][3];
	//��temp�����е��ַ���ת��Ϊ��Ӧ������������ȥ����ʱ�ͷֵ����
	int[][][] start_time = new int[7][12][2];
	private int advance_time;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		//ȡ�����ݿ�
		DataBase db = new DataBase(arg0);
		//ȡ�����ݿ���ÿ�յ����ݣ�������cursor������
		for(int i=0;i<7;i++){
			cursor[i]=db.select(i);					
		}
		//�����ݿ�ȡ���û�������Ͽε�ʱ�ͷ֣��������ÿ�ǰ����
		for(int day=0;day<7;day++){ 
			for(int row=0;row<12;row++){
				cursor[day].moveToPosition(row);
				temp[day][row][2] = cursor[day].getString(5);
	 			if(!temp[day][row][2].equals("")){
	 				temp[day][row][2] = temp[day][row][2].substring(temp[day][row][2].indexOf(":")+2);
	 				temp[day][row][0] = temp[day][row][2].substring(0, temp[day][row][2].indexOf(":"));
	 				temp[day][row][1] = temp[day][row][2].substring(temp[day][row][2].indexOf(":")+1);
	 			}
	 			else{
	 				temp[day][row][0] = temp[day][row][1] = "0";
	 			}
	 			for(int hm=0;hm<2;hm++){
	 				start_time[day][row][hm] = Integer.parseInt(temp[day][row][hm]);
	 			}
			}
		}
		
		//�Ӹ�context���������ѵ�activity������SDK�ĵ���˵������Ҫ����addFlags()һ��
		Intent remind_intent = new Intent(arg0, RemindActivity.class);
		remind_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//��ȡ��ǰ���ѵ�ʱ��ֵ,���û�л�ȡ����ȡĬ��ֵ30����
//		int advance_time = arg1.getIntExtra("anvance_remindtime", 20);
		//����ģʽһ��Ҫ����ΪMODE_MULTI_PROCESS������ʹ��Ӧ��xml�ļ��������и��£�RemindReceiver��Ҳ���ܻ�ȡ���º�����ݣ�����һֱ��ȡ�ϴε����ݣ� ������ջ���
		SharedPreferences pre = arg0.getSharedPreferences("time", Context.MODE_MULTI_PROCESS);
		advance_time = pre.getInt("time_choice", 30);
		int currentday = ShareMethod.getWeekDay();
//		System.out.println(advance_time);
		
		Calendar c = Calendar.getInstance();
		//��ȡ��ǰ��ʱ�ͷ�
		int current_hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int current_minute = c.get(Calendar.MINUTE);
				
		//����һ����־λ�������ų����ظ�������
		boolean flag = true;
		//ѭ���жϵ���Ŀ�ǰ����
		for(int i=0;i<12;i++){
			if(!(start_time[currentday][i][0]==0 && start_time[currentday][i][1]==0)){
				//��calendar��ʱ�ͷ�����Ϊ����ʱ���ʱ�ͷ�
				c.set(Calendar.HOUR_OF_DAY, start_time[currentday][i][0]);
				c.set(Calendar.MINUTE, start_time[currentday][i][1]);
				long remind_time = c.getTimeInMillis()-advance_time*60*1000;		
				Date date=new Date(remind_time);
				c.setTime(date);
				
				//��ȡ���õ����ѵ�ʱ�ͷ�
				int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				
				//��������趨������ʱ�䣬���������ѵ�activity
				if(hourOfDay==current_hourOfDay && minute==current_minute){
					if(flag){
						arg0.startActivity(remind_intent);						
//						System.out.println("time remind" + i);
						flag = false;						
					}
				}else{
					flag = true;
				}
			}
		}
		
	}

}
