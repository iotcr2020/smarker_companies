package com.anders.SMarker.http;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.anders.SMarker.http.RequestHttpURLConnection;

public class NetworkTask extends AsyncTask<Void, Void, String> {
    public static String API_SERVER_ADRESS ="https://www.ktsmarker.co.kr/ktssouthapi";
    public static String API_CHECK_AGREE = API_SERVER_ADRESS + "/agreecheck";
    public static String API_UPDATE_AGREE = API_SERVER_ADRESS + "/agree";
    public static String API_CHECK_PHONE_NUMBER = API_SERVER_ADRESS + "/phone";
    public static String API_FCM_HPSET = API_SERVER_ADRESS + "/fcm";
    public static String API_LOG_OFF = API_SERVER_ADRESS + "/logout";
    public static String API_MAIN_ALERT_RECEIVE = API_SERVER_ADRESS + "/mainmessage";
    public static String API_UPDATE_USER_STRIP_MAC =API_SERVER_ADRESS + "/stripmac";
    public static String API_UPDATE_USER_Helmet_MAC =API_SERVER_ADRESS + "/helmetmac";
    public static String API_INSERT_STRIP_STATE = API_SERVER_ADRESS + "/stripstate";
    public static String API_UPDATE_START_WORK = API_SERVER_ADRESS + "/startwork";
    public static String API_UPDATE_STOP_WORK = API_SERVER_ADRESS + "/stopwork";
    public static String API_MESSAGE_RECEIVE_LIST = API_SERVER_ADRESS + "/messagereceivelist";
    public static String API_MESSAGE_SEND_LIST = API_SERVER_ADRESS + "/messagesendlist";
    public static String API_MESSAGE_READ_CHK = API_SERVER_ADRESS + "/messagereadchk";
    public static String API_ADMIN_ACTION_SEND = API_SERVER_ADRESS + "/adminaction";
    public static String API_UPDATE_DUST_VALUE = API_SERVER_ADRESS + "/dust";
    public static String API_TEAM_LIST = API_SERVER_ADRESS + "/userlist";
    public static String API_TEAM_LIST_PAGE = API_SERVER_ADRESS + "/userlistpage";
    public static String API_ALARM_SEND = API_SERVER_ADRESS +"/useralarm";
    public static String API_ADMIN_ALARM_SEND = API_SERVER_ADRESS +"/adminalarm";
    public static String API_IMAGE_UPLOAD_SERVER= API_SERVER_ADRESS + "/photoupload";
    public static String API_GET_WORK_FL= API_SERVER_ADRESS + "/getworkfl";
    public static String API_WORK_FL= API_SERVER_ADRESS + "/workfl";
    public static String API_BATTERY_INFO = API_SERVER_ADRESS + "/battery";
    public static String API_TEAM_TREE = API_SERVER_ADRESS + "/teamlist";

    /*public static final String API_SERVER_ADRESS ="http://211.233.58.32:80";
    public static String API_SERVER_ADRESS ="https://ktsmarker.co.kr/ktssouth";
    public static String API_CHECK_PHONE_NUMBER = API_SERVER_ADRESS + "/api/check_phone_number.php"  ;
    public static String API_CHECK_AGREE = API_SERVER_ADRESS + "/api/select_use_agree.php";
    public static String API_UPDATE_AGREE = API_SERVER_ADRESS + "/api/update_use_agree.php";
    public static String API_TEAM_LIST = API_SERVER_ADRESS + "/api/select_team_list.php";
    public static String API_TEAM_LIST2 = API_SERVER_ADRESS + "/api/select_team_list.php";
    public static String API_ALARM_SEND = API_SERVER_ADRESS +"/api/user_team_alarm.php";
    public static String API_FCM_HPSET = API_SERVER_ADRESS +"/api/user_hp_token.php";
    public static String API_ADMIN_ALARM_SEND = API_SERVER_ADRESS +"/api/admin_team_alarm.php";
    public static String API_MESSAGE_RECEIVE_LIST = API_SERVER_ADRESS+"/api/message_receive.php";
    public static String API_MESSAGE_SEND_LIST = API_SERVER_ADRESS+"/api/message_send.php";
    public static String API_MESSAGE_READ_CHK = API_SERVER_ADRESS+"/api/message_read_chk.php";
    public static String API_MESSAGE_RECEIVE_INSERT = API_SERVER_ADRESS+"/api/db_message_receive.php";
    public static String API_IMAGE_UPLOAD_SERVER= API_SERVER_ADRESS + "/user_images/UploadToServer.php";
    public static String API_IMAGE_DELETE_SERVER = API_SERVER_ADRESS + "/user_images/UploadToServerRemove.php";
    public static String API_UPDATE_USER_STRIP_MAC = API_SERVER_ADRESS + "/api/UpdateUserStripMac.php";
    public static String API_UPDATE_USER_Helmet_MAC = API_SERVER_ADRESS + "/api/UpdateUserHelmetMac.php";
    public static String API_UPDATE_START_WORK = API_SERVER_ADRESS + "/api/update_Start_Work.php";
    public static String API_UPDATE_STOP_WORK = API_SERVER_ADRESS + "/api/update_Stop_Work.php";
    public static String API_UPDATE_DUST_VALUE = API_SERVER_ADRESS + "/api/Update_Dust_Value.php";
    public static String API_MAIN_ALERT_RECEIVE = API_SERVER_ADRESS + "/api/message_receive_main.php";
    public static String API_INSERT_STRIP_STATE = API_SERVER_ADRESS + "/api/InsertStripState.php";
    public static String API_ADMIN_ACTION_SEND = API_SERVER_ADRESS + "/api/Admin_Action_Send.php";
    public static String API_LOG_OFF = API_SERVER_ADRESS + "/api/LogOff.php";*/

    public static final String API_OPENWEATHER_KEY ="db1f92d4b15d1b1fe4f6b55dfe7b8f65";

    private String url;
    private ContentValues values;
    private String _method="POST";

    public NetworkTask(String url, ContentValues values) {
        this.url = url;
        this.values = values;
        this._method ="POST";
    }

    public NetworkTask(String url, ContentValues values, String _me) {
        this.url = url;
        this.values = values;
        this._method =_me;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result; // 요청 결과를 저장할 변수.
        RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
        result = requestHttpURLConnection.request(url, values,_method); // 해당 URL로 부터 결과물을 얻어온다.

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
        Log.d("결과값===",s+"");
    }
}
