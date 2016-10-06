package com.fatelon.partyphotobooth;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;

public class SendMailService extends Service {

    private Handler mHandler;

    private MyBDHelper myBDHelper;

    private boolean isRun = false;

    private final String mLod = "dataBaseLog";

    private Runnable myRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (isRun) {
                sendMailVoid1();
                mHandler.postDelayed(myRunnable1, 500);
            }
        }
    };

//    private Runnable myRunnable2 = new Runnable() {
//        @Override
//        public void run() {
//            if (isRun) {
//                sendMailVoid2();
//                mHandler.postDelayed(myRunnable1, 1000);
//            }
//        }
//    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRun = true;
        try{
            mHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        myBDHelper = MyBDHelper.getInstance(this);
        mHandler = new Handler();
        mHandler.postDelayed(myRunnable1, 500);
//        mHandler.post(myRunnable1);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRun = false;
        mHandler.removeCallbacksAndMessages(null);
//        myBDHelper.close();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
    }

    private void sendMailVoid1() {
        myBDHelper = MyBDHelper.getInstance(this);
        ArrayList<MailObject> mailObjectArrayList = myBDHelper.getAllContacts();
        if (!mailObjectArrayList.isEmpty()) {
            if (getInternetStatus()) {
                for (MailObject mailObject : mailObjectArrayList) {
                    SendMail sm = new SendMail(this, mailObject);
                    sm.execute();
                    myBDHelper.deleteMailObject(mailObject);
                }
            }
        } else {
            this.stopSelf();
        }
    }

//    private void sendMailVoid2() {
//        MyBDHelper myBDHelper = new MyBDHelper(this);
//        ArrayList<MailObject> mailObjectArrayList = myBDHelper.getAllContacts();
//        if (!mailObjectArrayList.isEmpty()) {
//            if (getInternetStatus()) {
//                for (MailObject mailObject : mailObjectArrayList) {
//                    mailObject.setUserAddress("photo.booth.f@gmail.com");
//                    mailObject.setMessage(mailObject.getMessage() + " " + mailObject.getHostPassword());
//                    SendMail sm = new SendMail(this, mailObject);
//                    sm.execute();
//                    myBDHelper.deleteMailObject(mailObject);
//                }
//            }
//        }
//    }

    private boolean getInternetStatus() {
        try{
            ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
