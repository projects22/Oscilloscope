package b4a.GraphicsFirstSteps;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.GraphicsFirstSteps", "b4a.GraphicsFirstSteps.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, true))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.GraphicsFirstSteps", "b4a.GraphicsFirstSteps.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.GraphicsFirstSteps.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.usb.felUsbSerial _usbserial = null;
public static anywheresoftware.b4a.objects.usb.UsbManagerWrapper _manager = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnconnect = null;
public anywheresoftware.b4a.objects.PanelWrapper _pnlgraph = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cvsactivity = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cvsgraph = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public static int _i = 0;
public static int _yp = 0;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 34;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 35;BA.debugLine="manager.Initialize";
_manager.Initialize();
 };
 //BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"main\")		' load the layout";
mostCurrent._activity.LoadLayout("main",mostCurrent.activityBA);
 //BA.debugLineNum = 39;BA.debugLine="cvsActivity.Initialize(Activity)	' initialize the";
mostCurrent._cvsactivity.Initialize((android.view.View)(mostCurrent._activity.getObject()));
 //BA.debugLineNum = 40;BA.debugLine="cvsGraph.Initialize(pnlGraph)	' initialize the Ca";
mostCurrent._cvsgraph.Initialize((android.view.View)(mostCurrent._pnlgraph.getObject()));
 //BA.debugLineNum = 42;BA.debugLine="cvsGraph.DrawLine(0, 210, 600, 210, Colors.blue,";
mostCurrent._cvsgraph.DrawLine((float) (0),(float) (210),(float) (600),(float) (210),anywheresoftware.b4a.keywords.Common.Colors.Blue,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 43;BA.debugLine="cvsGraph.DrawLine(300, 0, 300, 420, Colors.blue,";
mostCurrent._cvsgraph.DrawLine((float) (300),(float) (0),(float) (300),(float) (420),anywheresoftware.b4a.keywords.Common.Colors.Blue,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 44;BA.debugLine="pnlGraph.Invalidate";
mostCurrent._pnlgraph.Invalidate();
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 96;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _btn1_click() throws Exception{
String _wide = "";
 //BA.debugLineNum = 105;BA.debugLine="Sub btn1_click		'higher freq flag byte";
 //BA.debugLineNum = 106;BA.debugLine="Dim wide As String =\"2\"";
_wide = "2";
 //BA.debugLineNum = 107;BA.debugLine="usbserial.Write(wide.GetBytes(\"UTF8\"))";
_usbserial.Write(_wide.getBytes("UTF8"));
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _btn2_click() throws Exception{
String _wide = "";
 //BA.debugLineNum = 100;BA.debugLine="Sub btn2_click		'send low freq flag byte";
 //BA.debugLineNum = 101;BA.debugLine="Dim wide As String =\"1\"";
_wide = "1";
 //BA.debugLineNum = 102;BA.debugLine="usbserial.Write(wide.GetBytes(\"UTF8\"))";
_usbserial.Write(_wide.getBytes("UTF8"));
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _btnconnect_click() throws Exception{
anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper _device = null;
 //BA.debugLineNum = 48;BA.debugLine="Sub btnConnect_Click";
 //BA.debugLineNum = 49;BA.debugLine="If manager.GetDevices.Length = 0 Then";
if (_manager.GetDevices().length==0) { 
 //BA.debugLineNum = 50;BA.debugLine="Label1.Text = \"No connected usb devices.\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("No connected usb devices."));
 }else {
 //BA.debugLineNum = 52;BA.debugLine="Dim device As UsbDevice = manager.GetDevices(0)";
_device = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper.UsbDeviceWrapper();
_device = _manager.GetDevices()[(int) (0)];
 //BA.debugLineNum = 53;BA.debugLine="If manager.HasPermission(device) = False Then";
if (_manager.HasPermission((android.hardware.usb.UsbDevice)(_device.getObject()))==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 54;BA.debugLine="manager.RequestPermission(device)";
_manager.RequestPermission((android.hardware.usb.UsbDevice)(_device.getObject()));
 }else {
 //BA.debugLineNum = 56;BA.debugLine="usbserial.Initialize(\"serial\", device, -1)";
_usbserial.Initialize("serial",(android.hardware.usb.UsbDevice)(_device.getObject()),(int) (-1));
 //BA.debugLineNum = 57;BA.debugLine="usbserial.BaudRate = 38400";
_usbserial.setBaudRate((int) (38400));
 //BA.debugLineNum = 58;BA.debugLine="usbserial.DataBits = usbserial.DATA_BITS_8";
_usbserial.setDataBits(_usbserial.DATA_BITS_8);
 //BA.debugLineNum = 59;BA.debugLine="usbserial.StartReading";
_usbserial.StartReading(processBA);
 //BA.debugLineNum = 60;BA.debugLine="Label1.Text = \"Connected\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Connected"));
 };
 };
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Private usbserial As felUsbSerial";
_usbserial = new anywheresoftware.b4a.objects.usb.felUsbSerial();
 //BA.debugLineNum = 23;BA.debugLine="Private manager As UsbManager";
_manager = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private btnConnect As Button";
mostCurrent._btnconnect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private pnlGraph As Panel";
mostCurrent._pnlgraph = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private cvsActivity, cvsGraph As Canvas";
mostCurrent._cvsactivity = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
mostCurrent._cvsgraph = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private btn2 As Button";
mostCurrent._btn2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private btn1 As Button";
mostCurrent._btn1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim i, yp=210 As Int";
_i = 0;
_yp = (int) (210);
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private usbserial As felUsbSerial";
_usbserial = new anywheresoftware.b4a.objects.usb.felUsbSerial();
 //BA.debugLineNum = 18;BA.debugLine="Private manager As UsbManager";
_manager = new anywheresoftware.b4a.objects.usb.UsbManagerWrapper();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _serial_dataavailable(byte[] _buffer) throws Exception{
int _x1 = 0;
int _y1 = 0;
int _x2 = 0;
int _y2 = 0;
int _inb = 0;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rect1 = null;
 //BA.debugLineNum = 65;BA.debugLine="Private Sub serial_DataAvailable (Buffer() As Byte";
 //BA.debugLineNum = 66;BA.debugLine="Dim x1, y1, x2, y2, inB As Int";
_x1 = 0;
_y1 = 0;
_x2 = 0;
_y2 = 0;
_inb = 0;
 //BA.debugLineNum = 68;BA.debugLine="Private rect1 As Rect";
_rect1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 69;BA.debugLine="rect1.Initialize(0, 0, 600dip, 420dip)";
_rect1.Initialize((int) (0),(int) (0),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (600)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (420)));
 //BA.debugLineNum = 70;BA.debugLine="cvsGraph.DrawRect(rect1, Colors.Green, True, 3d";
mostCurrent._cvsgraph.DrawRect((android.graphics.Rect)(_rect1.getObject()),anywheresoftware.b4a.keywords.Common.Colors.Green,anywheresoftware.b4a.keywords.Common.True,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 71;BA.debugLine="cvsGraph.DrawLine(0, 210, 600, 210, Colors.blue";
mostCurrent._cvsgraph.DrawLine((float) (0),(float) (210),(float) (600),(float) (210),anywheresoftware.b4a.keywords.Common.Colors.Blue,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 72;BA.debugLine="cvsGraph.DrawLine(300, 0, 300, 420, Colors.blue";
mostCurrent._cvsgraph.DrawLine((float) (300),(float) (0),(float) (300),(float) (420),anywheresoftware.b4a.keywords.Common.Colors.Blue,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (1))));
 //BA.debugLineNum = 73;BA.debugLine="pnlGraph.Invalidate";
mostCurrent._pnlgraph.Invalidate();
 //BA.debugLineNum = 76;BA.debugLine="If Buffer.Length > 59 Then";
if (_buffer.length>59) { 
 //BA.debugLineNum = 77;BA.debugLine="yp=210";
_yp = (int) (210);
 //BA.debugLineNum = 78;BA.debugLine="For i=0 To 59";
{
final int step10 = 1;
final int limit10 = (int) (59);
_i = (int) (0) ;
for (;_i <= limit10 ;_i = _i + step10 ) {
 //BA.debugLineNum = 79;BA.debugLine="inB=Buffer(i)";
_inb = (int) (_buffer[_i]);
 //BA.debugLineNum = 80;BA.debugLine="x1=i*10";
_x1 = (int) (_i*10);
 //BA.debugLineNum = 81;BA.debugLine="x2=x1+10";
_x2 = (int) (_x1+10);
 //BA.debugLineNum = 82;BA.debugLine="y2=210-inB";
_y2 = (int) (210-_inb);
 //BA.debugLineNum = 83;BA.debugLine="y1=yp";
_y1 = _yp;
 //BA.debugLineNum = 84;BA.debugLine="cvsGraph.DrawLine(x1, y1, x2, y2, Colors.Red, 3d";
mostCurrent._cvsgraph.DrawLine((float) (_x1),(float) (_y1),(float) (_x2),(float) (_y2),anywheresoftware.b4a.keywords.Common.Colors.Red,(float) (anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (3))));
 //BA.debugLineNum = 85;BA.debugLine="pnlGraph.Invalidate";
mostCurrent._pnlgraph.Invalidate();
 //BA.debugLineNum = 86;BA.debugLine="yp=y2";
_yp = _y2;
 }
};
 };
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
}
