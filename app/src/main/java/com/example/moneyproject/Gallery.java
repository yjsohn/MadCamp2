package com.example.moneyproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import static android.app.Activity.RESULT_OK;
//!!!!!파일 경로:  file:///storage/emulated/0/Pictures/building.jpg


public class Gallery extends Fragment {
    View view;
    Context context;

    private static final String TAG = "blackjin";

    private Boolean isPermission = true;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private ArrayList<Picture> mArrayList = new ArrayList<>();;
    static private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FragmentManager fragmentManager;
    private MenuItem.OnMenuItemClickListener listener;

    public GetImageTask gct;

    private File tempFile;
    private String id;

    public Gallery() {
        // Required empty public constructor
    }


    public static Gallery newInstance() {
        Gallery gallery = new Gallery();
        return gallery;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        GetImageTask gct = new GetImageTask();
        //mArrayList = new ArrayList<>();
        gct.execute("http://192.249.18.248:8080/getImages?id=");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery, container, false);
        context = view.getContext();
        tedPermission();
        setHasOptionsMenu(true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.galleryView);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(context, 3);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new PictureAdapter(context, mArrayList, getTargetFragment());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //MyDB.insertPicture("/storage/emulated/0/Pictures/duck.jpg", "", "", "");
        //
        //mArrayList = MyDB.getAllImages();

        id = "abcdef";


        listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.gallery:  //click gallery
                        if(isPermission)
                            goToAlbum();
                        break;
                    case R.id.camera:   //click camera
                        if(isPermission)
                            takePhoto();
                        break;
                }
                return true;
            }
        };

        view.findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {  //추가버튼 클릭
            @Override
            public void onClick(View view) {

                PopupMenu popup= new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.gallery:  //click gallery
                                if(isPermission)
                                    goToAlbum();
                                break;
                            case R.id.camera:   //click camera
                                if(isPermission)
                                    takePhoto();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        view.findViewById(R.id.deleteBtn).setOnClickListener(new View.OnClickListener() {   //삭제버튼 클릭
            @Override
            public void onClick(View view) {
                //Todo: DB 삭제
                refreshFragment();
            }
        });
    }

    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (tempFile != null) {

            /**
             *  안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지로 FileUriExposedException 발생
             *  Uri 를 FileProvider 도 감싸 주어야 합니다.
             *
             *  참고 자료 http://programmar.tistory.com/4 , http://programmar.tistory.com/5
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(context,
                        "com.example.basicapplicationfunction", tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } else {
                Uri photoUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }
    private File createImageFile() throws IOException {
        // 이미지 파일 이름 ( blackJin_{시간}_ )
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "basicapp_" + timeStamp + "_";

        // 이미지가 저장될 파일 주소 ( blackJin )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");   //저장 위치 확인해보기
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.popup_menu, menu);
    }

    void refreshFragment(){
        //refresh fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    //사진첩에서 사진 받아오기
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM) {

            Uri photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri);

            Cursor cursor = null;

            try {
                //Uri 스키마를 content:/// 에서 file:/// 로  변경
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContext().getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            //refresh fragment
            addImage();
            refreshFragment();
        }
        else if(requestCode == PICK_FROM_CAMERA){
            refreshFragment();
            addImage();
        }
    }

    private void addImage() {
        String path = tempFile.getPath();
        //MyDB.insertPicture(path, "", "", "");
        //서버에 upload

        //2번째 parameter는 사진
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        byte[] image = outStream.toByteArray();
        String profileImageBase64 = Base64.encodeToString(image, 0);
        String data = profileImageBase64;

        new UploadImageTask().execute("http://192.249.18.248:8080/uploadImage?id=", data);//AsyncTask 시작시킴
        mArrayList.add(new Picture(data));

    }

    //권한 설정
    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true;
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                isPermission = false;
                Log.e("ERROR", "권한 설정 오류");
            }
        };

        //Todo: 반복 문구 수정
        TedPermission.with(context)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    //사진 업로드
    public class UploadImageTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... values) {
            try {
                JSONObject jsonObject = new JSONObject();
                //넘겨줄 정보
                jsonObject.accumulate("id", "hey1");
                jsonObject.accumulate("image", values[1]); //set image to upload -> value가 뭘까?
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    //연결할 URL
                    URL url = new URL(values[0] + id);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    //서버로 보내기위해서 스트림 만듦
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
                    return buffer.toString(); //서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
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
            //어떤 형태로 이미지를 불러오는가
            //imageView.setImageURI(result);//서버로 부터 받은 값을 출력해주는 부분
        }
    }

    public class GetImageTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                //넘겨줄 정보
                jsonObject.accumulate("id", "hey1");
                HttpURLConnection con = null;
                BufferedReader reader = null;
                try{
                    //연결할 URL
                    URL url = new URL(urls[0] + id);
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
                    return buffer.toString(); //서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임
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
            super.onPostExecute(result);    //서버로 부터 받은 값을 출력해주는 부분
            //반대의 경우
            // base64String 데이터 -> stream데이터 -> image데이터
            // System.out.println(result);
            //return된 json array 중 0번째를 string으로 -> result_picture
            JSONArray jsonArray;
            String result_picture = "";
            try {
                jsonArray = new JSONArray(result);
                //array 안의 모든 데이터 arraylist에 저장 -> setAdapter
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject order = jsonArray.getJSONObject(i);
                    result_picture = order.getString("image");
                    mArrayList.add(new Picture(result_picture));
                }
                mAdapter = new PictureAdapter(context, mArrayList, getTargetFragment());
                mRecyclerView.setAdapter(mAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
            //데이터 base64 형식으로 Decode
            byte[] bytePlainOrg = Base64.decode(result_picture, 0);

            //byte[] 데이터  stream 데이터로 변환 후 bitmapFactory로 이미지 생성
            ByteArrayInputStream inStream = new ByteArrayInputStream(bytePlainOrg);
            Bitmap bm = BitmapFactory.decodeStream(inStream);


            mAdapter = new PictureAdapter(context, mArrayList, getTargetFragment());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(layoutManager);

             */
        }
    }
}