package transformers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AndroidSourceSinkSummary{

	public static LinkedHashMap<String, List<Integer>> sourceSummary = new LinkedHashMap<String, List<Integer>>();
	public static LinkedHashMap<String, List<Integer>> callbackSourceSummary = new LinkedHashMap<String, List<Integer>>();
	public static LinkedHashMap<String, List<Integer>> callbackSourceSubSummary = new LinkedHashMap<String, List<Integer>>();
	public static LinkedHashMap<String, String> callbackSourceSubSignatureMap = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, List<Integer>> sinkSummary = new LinkedHashMap<String, List<Integer>>();
	
	public static List<String> SOURCES = new ArrayList<String>();
	public static List<String> SINKS = new ArrayList<String>();
		
	private static Integer ret = new Integer(MyConstants.returnValue);
	private static Integer thiz = new Integer(MyConstants.thisObject);
	private static Integer param0 = new Integer(0);
	private static Integer param1 = new Integer(1);
	private static Integer param2 = new Integer(2);
	private static Integer param3 = new Integer(3);
	private static Integer param4 = new Integer(4);
	private static Integer param5 = new Integer(5);
	private static Integer param6 = new Integer(6);
	private static Integer param7 = new Integer(7);
	private static Integer param8 = new Integer(8);
	private static Integer all = new Integer(-1);
	
	public static void buildSourceAndSinkSummary(){
		
		/*if(MyConstants.MU_TEST){
			buildTestSourceSummary();
		}else{*/
			buildSourceSummary();
		//}
		
		/*if(MyConstants.MU_TEST){
			buildTestCallbackSourceSummary();
		}else{*/
		//	buildCallbackSourceSummary();
		//}		
		
		/*if(MyConstants.MU_TEST){
			buildTestSinkSummary();
		}else{*/
			buildSinkSummary();
		//}
		
		buildSourceSinkList();
	}
	
	public static boolean isSource(String signature){
		if(sourceSummary.containsKey(signature)){
			return true;
		}
		
		if(callbackSourceSubSummary.containsKey(signature)){
			return true;
		}
		
		return false;
	}
	
	public static boolean isSink(String signature){
		if(sinkSummary.containsKey(signature)){
			return true;
		}
		return false;
	}
	
	public static boolean isCallbackSource(String signature){
		if(callbackSourceSubSummary.containsKey(signature)){
			return true;
		}
		return false;
	}
	
	public static boolean isNonCallbackSource(String signature){
		if(sourceSummary.containsKey(signature)){
			return true;
		}
		return false;
	}
	
	public static void buildSourceSinkList(){
		SOURCES.addAll(sourceSummary.keySet());
		//SOURCES.addAll(callbackSourceSubSummary.keySet());
		SINKS.addAll(sinkSummary.keySet());
	}
	
	public static void buildSourceSummary(){		
		List<Integer> sourceIndexes = null;
		//lejos fetch sample
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		sourceSummary.put("<lejos.robotics.SampleProvider: void fetchSample(float[],int)>", sourceIndexes);
	}
	
	public static void buildTestCallbackSourceSummary(){	
		/*
		List<Integer> sourceIndexes = null;
		//android.permission.CAMERA
		//<android.hardware.Camera: android.hardware.Camera open()>
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		callbackSourceSummary.put("<android.hardware.Camera$PictureCallback: void onPictureTaken(byte[],android.hardware.Camera)>", sourceIndexes);
		callbackSourceSubSummary.put("void onPictureTaken(byte[],android.hardware.Camera)", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onPictureTaken(byte[],android.hardware.Camera)", 
				"android.hardware.Camera$PictureCallback");
				*/
	}
	
	public static void buildTestSourceSummary(){
		/*
		List<Integer> sourceIndexes = null;
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(ret);
		sourceSummary.put("<android.telephony.TelephonyManager: java.lang.String getDeviceId()>", sourceIndexes);
		*/	
	}	
	
	public static void buildCallbackSourceSummary(){		
		List<Integer> sourceIndexes = null;
		
		//android.permission.ACCESS_COARSE_LOCATION, android.permission.ACCESS_FINE_LOCATION
		/*
		<android.location.LocationManager: void requestLocationUpdates(long,float,android.location.Criteria,android.location.LocationListener,android.os.Looper)>
		<android.location.LocationManager: void requestSingleUpdate(android.location.Criteria,android.app.PendingIntent)>
		<android.location.LocationManager: void requestLocationUpdates(java.lang.String,long,float,android.location.LocationListener)>
		<android.location.LocationManager: void requestLocationUpdates(long,float,android.location.Criteria,android.app.PendingIntent)>
		<android.location.LocationManager: void requestLocationUpdates(java.lang.String,long,float,android.app.PendingIntent)>
		<android.location.LocationManager: void requestSingleUpdate(java.lang.String,android.location.LocationListener,android.os.Looper)>
		<android.location.LocationManager: void requestSingleUpdate(android.location.Criteria,android.location.LocationListener,android.os.Looper)>
		<android.location.LocationManager: void requestSingleUpdate(java.lang.String,android.app.PendingIntent)>
		<android.location.LocationManager: void requestLocationUpdates(java.lang.String,long,float,android.location.LocationListener,android.os.Looper)>
		 */
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		callbackSourceSummary.put("<android.location.LocationListener: void onLocationChanged(android.location.Location)>", sourceIndexes);
		callbackSourceSubSummary.put("void onLocationChanged(android.location.Location)", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onLocationChanged(android.location.Location)", 
				"android.location.LocationListener");
		
		//android.permission.CAMERA
		//<android.hardware.Camera: android.hardware.Camera open()>
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		callbackSourceSummary.put("<android.hardware.Camera$PictureCallback: void onPictureTaken(byte[],android.hardware.Camera)>", sourceIndexes);
		callbackSourceSubSummary.put("void onPictureTaken(byte[],android.hardware.Camera)", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onPictureTaken(byte[],android.hardware.Camera)", 
				"android.hardware.Camera$PictureCallback");
				//"<android.hardware.Camera.PictureCallback: void onPictureTaken(byte[],android.hardware.Camera)>");
		
		//android.permission.READ_PHONE_STATE
		//<android.telephony.TelephonyManager: void listen(android.telephony.PhoneStateListener,int)>
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		sourceIndexes.add(param1);
		callbackSourceSummary.put("<android.telephony.PhoneStateListener: void onCallStateChanged(int,java.lang.String)>", sourceIndexes);
		callbackSourceSubSummary.put("void onCallStateChanged(int,java.lang.String)", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onCallStateChanged(int,java.lang.String)", 
				"android.telephony.PhoneStateListener");
		
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		callbackSourceSummary.put("<android.telephony.PhoneStateListener: void onCellInfoChanged(java.util.List)>", sourceIndexes);
		callbackSourceSubSummary.put("void onCellInfoChanged(java.util.List)", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onCellInfoChanged(java.util.List)", 
				"android.telephony.PhoneStateListener");
		
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		callbackSourceSummary.put("<android.telephony.PhoneStateListener: void onCellLocationChanged(android.telephony.CellLocation)>", sourceIndexes);
		callbackSourceSubSummary.put("void onCellLocationChanged(android.telephony.CellLocation)", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onCellLocationChanged(android.telephony.CellLocation)", 
				"android.telephony.PhoneStateListener");
		
		//android.permission.GET_ACCOUNTS
		//<android.accounts.AccountManager: void addOnAccountsUpdatedListener(android.accounts.OnAccountsUpdateListener,android.os.Handler,boolean)>
		sourceIndexes = new ArrayList<Integer>();
		sourceIndexes.add(param0);
		callbackSourceSummary.put("<android.accounts.OnAccountsUpdateListener: void onAccountsUpdated(android.accounts.Account[])>", sourceIndexes);
		callbackSourceSubSummary.put("void onAccountsUpdated(android.accounts.Account[])", sourceIndexes);
		callbackSourceSubSignatureMap.put("void onAccountsUpdated(android.accounts.Account[])", 
				"android.accounts.OnAccountsUpdateListener");
	}
	
	public static void buildTestSinkSummary(){
		List<Integer> sinkIndexes = null;
		
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkIndexes.add(param1);
		sinkIndexes.add(param2);
		sinkSummary.put("<android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)>", sinkIndexes);
	
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(param0);
		sinkSummary.put("<java.lang.Runtime: java.lang.Process exec(java.lang.String)>", sinkIndexes);
		
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(param0);
		sinkSummary.put("<java.lang.Runtime: java.lang.Process exec(java.lang.String)>", sinkIndexes);
		
		//Network
		//android.permission.INTERNET
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.net.ServerSocket: void bind(java.net.SocketAddress)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkSummary.put("<java.net.URL: java.io.InputStream openStream()>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkSummary.put("<java.net.URL: java.net.URLConnection openConnection()>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.net.Socket: void <init>(int)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.net.MulticastSocket: void <init>(int)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.net.HttpURLConnection: void <init>(int)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.net.ServerSocket: void <init>(int)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<org.apache.http.impl.client.DefaultHttpClient: org.apache.http.HttpResponse execute(org.apache.http.client.methods.HttpUriRequest)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<org.apache.http.impl.client.AbstractHttpClient: org.apache.http.HttpResponse execute(org.apache.http.client.methods.HttpUriRequest)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<org.apache.http.client.HttpClient: org.apache.http.HttpResponse execute(org.apache.http.client.methods.HttpUriRequest)>", sinkIndexes);		

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<org.apache.http.client.HttpClient: org.apache.http.HttpResponse execute(org.apache.http.client.methods.HttpUriRequest,org.apache.http.protocol.HttpContext)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<org.apache.http.impl.client.DefaultHttpClient: void <init>(int)>", sinkIndexes);

		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.net.DatagramSocket: void <init>(int)>", sinkIndexes);

		//<android.webkit.WebView: void <init>(android.content.Context)>
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<android.webkit.WebView: void loadUrl(java.lang.String)>", sinkIndexes);
		
		//I/O		
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.OutputStream: void write(byte[],int,int)>", sinkIndexes);
				
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.OutputStream: void write(byte[])>", sinkIndexes);
				
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.FileOutputStream: void write(byte[],int,int)>", sinkIndexes);
				
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.FileOutputStream: void <init>(java.lang.String)>", sinkIndexes);
				
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.FileOutputStream: void write(byte[])>", sinkIndexes);
				
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.DataOutputStream: void write(byte[],int,int)>", sinkIndexes);
				
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(thiz);
		sinkIndexes.add(param0);
		sinkSummary.put("<java.io.FilterOutputStream: void write(byte[])>", sinkIndexes);
	}
	
	public static void buildSinkSummary(){
		List<Integer> sinkIndexes = null;

		//lejos setpower
		sinkIndexes = new ArrayList<Integer>();
		sinkIndexes.add(param0);
		sinkSummary.put("<lejos.robotics.EncoderMotor: void setPower(int)>", sinkIndexes);
	}
}
