package com.example.natalia.wifiguard;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;
import android.os.Handler;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();



        try {
            if (CheckIfBrowserIsAlreadyOn()) {
                CheckIfBrowserIsSwitching(true) ;
                //Wait and turn off WiFi
            } else {
                CheckIfBrowserIsSwitching(false);
                //turn on WiFi
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    boolean CheckIfBrowserIsAlreadyOn(){
        boolean isOn = false;
        ActivityManager activityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++){
            if((procInfos.get(i).processName.equals("com.android.browser") ||
                    procInfos.get(i).processName.equals("com.android.chrome")) && procInfos.get(i).lru == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
                Toast.makeText(getApplicationContext(), "Browser is running", Toast.LENGTH_LONG).show();
                isOn = true;
            }
        }
        return isOn;
    }

    void CheckIfBrowserIsSwitching(final boolean inIsSwitchedOn){
        final Context mContext = this;
        final Handler handler = new Handler();
        Thread t = new Thread ( new Runnable() {
            ActivityManager activityManager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);

            @Override
            public void run() {
                boolean isSwitchedOn = inIsSwitchedOn;
                while (true) {
                    boolean switched = false;
                    List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
                    for (int i = 0; i < procInfos.size(); i++) {
                        if (isSwitchedOn && !switched && CheckIfBrowserIsSwitchingOff(procInfos.get(i))) {
                            isSwitchedOn = false;
                            switched = true;
                    }
                        if(!isSwitchedOn && !switched && CheckIfBrowserIsSwitchingOn(procInfos.get(i))){
                            isSwitchedOn = true;
                            switched = true;
                        }
                    }
                }
                //handler.post(this);
            };


        });
        t.start();
    }

    boolean CheckIfBrowserIsSwitchingOff(ActivityManager.RunningAppProcessInfo procInfo){
        if(procInfo.processName.equals("com.android.browser") ||
                procInfo.processName.equals("com.android.chrome") &&
                        procInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND)
            return true;
        return false;
    }
    boolean CheckIfBrowserIsSwitchingOn(ActivityManager.RunningAppProcessInfo procInfo){
        if(procInfo.processName.equals("com.android.browser") ||
                procInfo.processName.equals("com.android.chrome") &&
                procInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true;
        return false;

    }
}
