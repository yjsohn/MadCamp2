package com.example.camp_proj1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.Profile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddMoneyInfo extends AppCompatActivity {


    public String writer, date,money_total,account_address,participant_list;

    public ArrayList<String> participants;

    EditText ewriter,eparticipants, edate,emoney_total,eaccount_address;

    String id="abcdef";

    public String [] items = {};

    public ArrayList<UserInfo> list = Fragment1.data_deliver;

    public ArrayList<String> participant_arraylist ;

    public ArrayList<String> participant_number_list;


    //public String UserID;
    int[] images = {R.drawable.basic,R.drawable.basic2,R.drawable.basic3};
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final List<String> selectedItems = new ArrayList<String>();


        setContentView(R.layout.addmoneyinfo);
        Button savebutton = (Button) findViewById(R.id.save);
        Button cancelbutton = (Button) findViewById(R.id.cancel);
        Button participant_btn = (Button) findViewById(R.id.participant_select);

        //참가자들 선택하는 버튼 누르면 멀티셀렉 드롭다운 리스트 나오게 함

        participant_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               //여기에는 친구 목록 불러와야됨
               //datadeliver에 친구들 이름,이메일,폰번호 담겨있음. -> [{},{},{}]의 형태.
               //public ArrayList<UserInfo> data = new ArrayList<>(); datadeliver는 fragment1dml data와 같은 애임! static 퍼블릭변수로 가져옴

               //불러옴

              for (int i=0; i<list.size();i++ ){
                  items[i] = list.get(i).name;
              }

              //data에 있는 userinfo를 포문을 사용하여 전체 친구이름을 추출하고, 사용가능한형태로 다시 저장. 밑에 코드들에서 items 사용.

              int s_length = items.length;
              boolean[] array = new boolean[s_length];


               //final String[] items = new String[]{"IT/Computer", "Game", "Fashion", "VR", "Kidult", "Sports", "Music", "Movie"};

               //

               AlertDialog.Builder dialog = new AlertDialog.Builder(AddMoneyInfo.this);
               dialog.setTitle("Select the Participants")
                       .setMultiChoiceItems(items,
                               array,
                               new DialogInterface.OnMultiChoiceClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                       if (isChecked) {
                                           Toast.makeText(AddMoneyInfo.this, items[which], Toast.LENGTH_SHORT).show();
                                           selectedItems.add(items[which]);
                                       } else {
                                           selectedItems.remove(items[which]);
                                       }
                                   }
                               })

                       .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               if (selectedItems.size() == 0) {
                                   Toast.makeText(AddMoneyInfo.this, "You selected no one", Toast.LENGTH_SHORT).show();
                               } else {
                                   String items = "";
                                   for (String selectedItem : selectedItems) {
                                       items += (selectedItem + ", ");
                                   }

                                   //participant_list에 지금 추가한 친구들의 이름이 쭉 담겨 있음.

                                   participant_list = items;

                                   //초기화
                                   selectedItems.clear();
                                   items = items.substring(0, items.length() - 2);
                                   Toast.makeText(AddMoneyInfo.this, items, Toast.LENGTH_SHORT).show();

                               }

                           }

                       }).create().show();
           }
       });

        participant_arraylist = new  ArrayList<String>(Arrays.asList(participant_list.split(",")));

        for (int i=0; i<=participant_list.length();i++){
            for (int j=0; j<=list.size();j++){
                if (list.get(j).name == participant_arraylist.get(i) ){
                    participant_number_list.add(list.get(j).phoneNumber);
                }
            }
        }

        //participant_number_list 는 친구들의 전화번호 정보 담은 arraylist



        ewriter = findViewById(R.id.writer);

        //eparticipants = findViewById(R.id.participant_select);

        edate = findViewById(R.id.date);
        emoney_total = findViewById(R.id.money_total);
        eaccount_address = findViewById(R.id.account_address);



        savebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ArrayList<String> url = new ArrayList<>();


                    writer = ewriter.getText().toString();

                    participants = participant_arraylist;
                    date = edate.getText().toString();
                    money_total = emoney_total.getText().toString();
                    account_address = eaccount_address.getText().toString();
                   // url.add("http://192.249.18.248:8080/addMoneyList?id=");
                    //new AddMoneyTask().execute(url, participants, participant_number_list);
                    //new AddMoneyTask().execute("http://192.249.18.251:8080/addMoneyList?id=");//AsyncTask 시작시킴
                }
            });

        cancelbutton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public String UserID = Profile.getCurrentProfile().getId();
    public class AddMoneyTask extends AsyncTask<ArrayList<String>, String, String>{
        @Override
        protected String doInBackground(ArrayList<String>... values) {
            try {
                JSONObject jsonObject = new JSONObject();
                //넘겨줄 정보
                jsonObject.accumulate("writer", UserID);
                jsonObject.accumulate("participants", participants);
                jsonObject.accumulate("date",date);
                jsonObject.accumulate("money", money_total);
                jsonObject.accumulate("account",account_address);
                jsonObject.accumulate("parts_id",participant_number_list);

                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    //연결할 URL
                    URL url = new URL(values[0] + id);
                    //URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌
                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

}
