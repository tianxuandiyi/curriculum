package temp;

import java.util.Calendar;
import java.util.Date;

public class ShareMethod {
	
	//��ȡ���������ڼ������������ߡ�һ......�ֱ�Ϊ����0��1......
	public static int getWeekDay(){
		Calendar calendar=Calendar.getInstance();
		Date date=new Date(System.currentTimeMillis());
		calendar.setTime(date);
		int weekDay=calendar.get(Calendar.DAY_OF_WEEK)-1;
		return weekDay;	
	}
	
	//��ȡ��ǰ��ʱ��,�����ַ���"xx:xx"����ʽ����
	public static String getTime(){
		Calendar c=Calendar.getInstance();
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);		
		//��ȡ������ʱ�䣬��ֻ��һλ������ǰ���0
		StringBuffer s_hour = new StringBuffer();
		StringBuffer s_minute = new StringBuffer();
		s_hour.append(hourOfDay);
		s_minute.append(minute);
		if(hourOfDay<10){
			s_hour.insert(0,"0");
		}
		if(minute<10){
			s_minute.insert(0,"0");
		}
		return s_hour.toString() + ":" + s_minute.toString();
	}
	
}
