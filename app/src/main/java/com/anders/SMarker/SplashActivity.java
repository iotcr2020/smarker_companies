package com.anders.SMarker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anders.SMarker.fcm.FirebaseInstanceIDService;
import com.anders.SMarker.http.NetworkTask;
import com.anders.SMarker.service.UnCatchTaskService;
import com.anders.SMarker.utils.AppVariables;
import com.anders.SMarker.utils.ServiceUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
    private Handler mWaitHandler = new Handler();
    private final  int PERMISSIONS_REQUEST_READ_PHONE_STATE  = 0;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    ArrayList<String> permissions = new ArrayList<String>();
    private boolean permission = false;



    private String[] permisionlist = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
            //Manifest.permission.WRITE_EXTERNAL_STORAGE, // 기기, 사진, 미디어, 파일 엑세스 권한
    };
    private final int PERMISSION_REQ_CODE = 225;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        setContentView(R.layout.activity_splash);
        if(AppVariables.soundPoolEmer == null || AppVariables.soundPoolNormal == null) {
            AppVariables.notifySound(getApplicationContext());//알림 소리 load
        }


        mWaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences auto = getSharedPreferences("setting", Activity.MODE_PRIVATE);
                    AppVariables.getSettingValue(auto);
                    tedPermission();

                    if (ServiceUtils.isServiceRunningCheck(getApplicationContext(), getPackageName() + ".service.BleService")) {
                        //서비스가 실행중이라면 바로 메인으로
                        finish();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 3000);

        startService(new Intent(this, UnCatchTaskService.class));
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("권한 허용").setMessage("앱을 사용하려면 \"다른 앱 위에 그리기 권한\"을 허용해야합니다.");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else {
                chkAgreeNextPage();
            }
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation){
        if(Build.VERSION.SDK_INT != Build.VERSION_CODES.O){
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    private boolean tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                permission = true;
                TelephonyManager tMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                if (tMgr != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(permisionlist, PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    }
                    String mPhoneNumber = tMgr.getLine1Number();

                    if (TextUtils.isEmpty(mPhoneNumber)) {
                    }

                    if (mPhoneNumber.startsWith("+82")) {
                        mPhoneNumber = mPhoneNumber.replace("+82", "0");
                    }
                    AppVariables.User_Phone_Number = mPhoneNumber;

                    if(!AppVariables.User_Hp_Token.equals("")) {
                        FirebaseInstanceIDService.sendRegistrationToServer(AppVariables.User_Hp_Token);
                    }
                }
                //getMyPhoneNumber();
                checkPermission();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한 요청 실패
                permission = false;
                Toast.makeText(SplashActivity.this, "권한요청이 정상적으로 이루어 지지않아 앱을 종료 합니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(permisionlist)
                .check();

        return permission;

    }

    private void getMyPhoneNumber(){
        try
        {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (tMgr != null)
            {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(permisionlist, PERMISSIONS_REQUEST_READ_PHONE_STATE );
                    }
                }
                String mPhoneNumber = tMgr.getLine1Number();
                if(mPhoneNumber.startsWith("+82")){
                    mPhoneNumber = mPhoneNumber.replace("+82", "0");
                }
                AppVariables.User_Phone_Number = mPhoneNumber;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) //권한 요청에 대한 응답
        {
            try {

                if(permission == true)
                {

                    TelephonyManager tMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    if (tMgr != null) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
                            }
                        }
                        String mPhoneNumber = tMgr.getLine1Number();
                        if (mPhoneNumber.startsWith("+82")) {
                            mPhoneNumber = mPhoneNumber.replace("+82", "0");
                        }
                        AppVariables.User_Phone_Number = mPhoneNumber;

                        FirebaseInstanceIDService.sendRegistrationToServer(AppVariables.User_Hp_Token);
                    }

                }
                else {
                    Toast.makeText(this, "권한요청이 정상적으로 이루어 지지않아 앱을 종료 합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            catch (Exception e) {
            }
        }else{
            Toast.makeText(this, "권한요청이 정상적으로 이루어 지지않아 앱을 종료 합니다.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void chkAgreeNextPage(){
        //if(permission && AppVariables.getConnectivityStatus(getApplicationContext()) != AppVariables.TYPE_NOT_CONNECTED) {
            String[] resultBuilder = null;
            ContentValues addData = new ContentValues();
            addData.put("phoneNB", AppVariables.User_Phone_Number);
            String[] serverAddress = {"https://www.ktsmarker.co.kr/ktssouthapi",
                    "https://www.smarker.co.kr/ktsnorthapi",
                    "https://www.smarker.co.kr/ktmsouthapi",
                    "https://www.smarker.co.kr/ktmnorthapi",
                    "https://www.smarker.co.kr/ktsspapi",
                    "https://www.smarker.co.kr/ktsnpapi",
                    "https://www.smarker.co.kr/ktslapi",
                    "https://www.smarker.co.kr/ktpapi"};
            String connFl = "n";

            Log.i("-----------휴대폰번호----------->", AppVariables.User_Phone_Number);

            try {
                for (int i = 0; i < serverAddress.length; i++) {
                    NetworkTask.API_SERVER_ADRESS = serverAddress[i];
                    NetworkTask.API_CHECK_AGREE = NetworkTask.API_SERVER_ADRESS + "/agreecheck";
                    NetworkTask.API_UPDATE_AGREE = NetworkTask.API_SERVER_ADRESS + "/agree";
                    NetworkTask.API_CHECK_PHONE_NUMBER = NetworkTask.API_SERVER_ADRESS + "/phone";
                    NetworkTask.API_FCM_HPSET = NetworkTask.API_SERVER_ADRESS + "/fcm";
                    NetworkTask.API_LOG_OFF = NetworkTask.API_SERVER_ADRESS + "/logout";
                    NetworkTask.API_MAIN_ALERT_RECEIVE = NetworkTask.API_SERVER_ADRESS + "/mainmessage";
                    NetworkTask.API_UPDATE_USER_STRIP_MAC = NetworkTask.API_SERVER_ADRESS + "/stripmac";
                    NetworkTask.API_UPDATE_USER_Helmet_MAC = NetworkTask.API_SERVER_ADRESS + "/helmetmac";
                    NetworkTask.API_INSERT_STRIP_STATE = NetworkTask.API_SERVER_ADRESS + "/stripstate";
                    NetworkTask.API_UPDATE_START_WORK = NetworkTask.API_SERVER_ADRESS + "/startwork";
                    NetworkTask.API_UPDATE_STOP_WORK = NetworkTask.API_SERVER_ADRESS + "/stopwork";
                    NetworkTask.API_MESSAGE_RECEIVE_LIST = NetworkTask.API_SERVER_ADRESS + "/messagereceivelist";
                    NetworkTask.API_MESSAGE_SEND_LIST = NetworkTask.API_SERVER_ADRESS + "/messagesendlist";
                    NetworkTask.API_MESSAGE_READ_CHK = NetworkTask.API_SERVER_ADRESS + "/messagereadchk";
                    NetworkTask.API_ADMIN_ACTION_SEND = NetworkTask.API_SERVER_ADRESS + "/adminaction";
                    NetworkTask.API_UPDATE_DUST_VALUE = NetworkTask.API_SERVER_ADRESS + "/dust";
                    NetworkTask.API_TEAM_LIST = NetworkTask.API_SERVER_ADRESS + "/userlist";
                    NetworkTask.API_TEAM_LIST_PAGE = NetworkTask.API_SERVER_ADRESS + "/userlistpage";
                    NetworkTask.API_ALARM_SEND = NetworkTask.API_SERVER_ADRESS +"/useralarm";
                    NetworkTask.API_ADMIN_ALARM_SEND = NetworkTask.API_SERVER_ADRESS +"/adminalarm";
                    NetworkTask.API_IMAGE_UPLOAD_SERVER = NetworkTask.API_SERVER_ADRESS + "/photoupload";
                    NetworkTask.API_GET_WORK_FL = NetworkTask.API_SERVER_ADRESS + "/getworkfl";
                    NetworkTask.API_WORK_FL = NetworkTask.API_SERVER_ADRESS + "/workfl";
                    NetworkTask.API_BATTERY_INFO = NetworkTask.API_SERVER_ADRESS + "/battery";
                    NetworkTask.API_TEAM_TREE = NetworkTask.API_SERVER_ADRESS + "/teamlist";

                    NetworkTask networkTask = new NetworkTask(NetworkTask.API_CHECK_AGREE, addData);
                    String result = networkTask.execute().get();
                    if (result != null && !result.isEmpty()) {
                        resultBuilder = result.split("\\|", -1);
                        if (resultBuilder[0].equals("Y")) {
                            startLoginActivity();
                            connFl = "y";
                        } else if (resultBuilder[0].equals("N")) {
                            startAgreeActivity();
                            connFl = "y";
                        } else if (resultBuilder[0].equals("F")) {
                            connFl = "n";
                            continue;
                        }

                        if ("y".equals(connFl)) {
                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            FirebaseInstanceIDService.sendRegistrationToServer(refreshedToken);

                            AppVariables.User_Name = resultBuilder[1].trim();
                            AppVariables.User_Email = resultBuilder[2].trim();
                            AppVariables.User_Team = resultBuilder[3].trim();
                            AppVariables.Strip_Mac_Adress = resultBuilder[4].trim();
                            AppVariables.Helmet_Mac_Adress = resultBuilder[5].trim();
                            AppVariables.User_Idx = resultBuilder[6].trim();
                            AppVariables.User_Permission = resultBuilder[7].trim();
                            AppVariables.User_Hp_Token = refreshedToken;
                            AppVariables.User_co_idx = resultBuilder[9].trim();
                        }

                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ("n".equals(connFl)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Smarker");
                builder.setMessage("등록되지 않은 사용자입니다. 관리자에게 문의하세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }

        //}else{
           // showRetryDialog();
            //chkAgreeNextPage();
        //}
    }

    private void startAgreeActivity(){
        Intent intent = new Intent(getApplicationContext(), AgreeActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showRetryDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.btn_network_dlg_close)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                chkAgreeNextPage();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                checkPermission();
            } else {
                chkAgreeNextPage();
            }
        }
    }
}
