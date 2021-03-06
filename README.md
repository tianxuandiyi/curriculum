<p>
	【基本功能】&nbsp;<br />
	编辑、添加、删除和查看课程表；&nbsp;<br />
	【核心功能】&nbsp;<br />
	1、打开课表时来，默认显示当天的课程信息；&nbsp;<br />
	2、根据用户的设置，在上课前通知提醒；&nbsp;<br />
	3、根据用户的设置，上课时自动将手机调节振动，下课后将手机恢复正常铃声；&nbsp;<br />
	<br />
	![image(]https://github.com/tianxuandiyi/curriculum/blob/master/picture/1.png)<br />
	<br />
	<br />
	完成这个APP之后，大致总结了几点局部功能的实现方法（不是很全），如下：<br />
	1、上课自动静音功能的实现：<br />
	通过开关控制是否启动后台的SetQuierService，从而在程序退出后一直在后台运行，<br />
	在SetQuierService中，用TimeTask，每隔一分钟从数据库中取出一次数据，<br />
	并从系统取得一次当天是星期几，以及当前的时间，<br />
	通过二者对比较，确定是否开启振动模式以及恢复原始铃声模式设置。<br />
	另外：这里写了一个LauncherReceiver用来监听系统开机事件，当用户开启了SetQuierService，但是因为某种原因关机了，<br />
	再次开机后，它会在这种情况下开机自启动SetQuierService。<br />
	<br />
	<br />
	2、课前提醒功能的实现：<br />
	通过开关控制是否每隔一分钟发送一次广播，而该广播会被RemindReceiver接收到，从而每隔一分钟从数据库中取得一次数据，<br />
	并从系统中取得一次当天是星期几，以及当前的时间，<br />
	通过对二者的比较，确定是否启动提醒用户的RemindActivity<br />
	另外，该广播的发送通过AlarmManager来实现，参数里设置的休眠不发送广播，但开机后会继续发送，因此不受关机影响。<br />
	<br />
	<br />
	3、完全退出应用程序采用的方法：<br />
	建立一个继承自Application的类MyApplication，在其中定义一个放置Activity的容器，用单例设计模式取得唯一的MyApplication实例，每建立一个Activity，就将其添加到该MyApplication实例的容器中。<br />
	<br />
	<br />
	4、第一次打开APP时直接创建数据库表，之后直接调用数据库：<br />
	采用单例设计模式创建数据库表，通过SharedPreferences保存一标志位，判断是否是第一次创建数据库表。<br />
	
</p>