package com.my.okhttpdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.okhttpdemo.cookie.CookiesManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_get;
    private Button btn_post;
    private Button btn_post_string;
    private Button btn_post_file;
    private Button btn_post_upload;
    private Button btn_download;
    private TextView tv_result;
    private ImageView iv_result;
    private String mBaseUrl = "http://192.168.1.101:8080/OKHttpDemoServer/";
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_get = (Button) findViewById(R.id.btn_get);
        tv_result = (TextView) findViewById(R.id.tv_result);
        btn_post = (Button) findViewById(R.id.btn_post);
        btn_post_string = (Button) findViewById(R.id.btn_post_string);
        btn_post_file = (Button) findViewById(R.id.btn_post_file);
        btn_post_upload = (Button) findViewById(R.id.btn_post_upload);
        btn_download = (Button) findViewById(R.id.btn_download);
        iv_result = (ImageView) findViewById(R.id.iv_result);
        assert btn_get != null;
        btn_get.setOnClickListener(this);
        btn_post.setOnClickListener(this);
        btn_post_string.setOnClickListener(this);
        btn_post_file.setOnClickListener(this);
        btn_post_upload.setOnClickListener(this);
        btn_download.setOnClickListener(this);

        //创建OkHttpClient对象，并且保持session
        okHttpClient = new OkHttpClient.Builder().cookieJar(new CookiesManager()).build();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                getRequest();   //get请求
                break;

            case R.id.btn_post:
                postRequest();  //post请求
                break;

            case R.id.btn_post_string:
                postRequestSendString();    //post请求发送string
                break;

            case R.id.btn_post_file:
                postRequestUploadSingleFile();  //上传单个文件
                break;

            case R.id.btn_post_upload:
                postRequestUpload();        //post上传文件(表单方式，多个)
                break;

            case R.id.btn_download:
                downloadFile();             //下载文件
                break;
        }
    }

    /**
     * 下载文件
     */
    private void downloadFile() {
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .get()
                .url(mBaseUrl + "res/image/icon.jpg")
                .build();
        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                L.e("onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();
                final long total = response.body().contentLength(); //总长度
                long sum = 0;


                int len = 0;
                File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                byte[] buf = new byte[128];
                FileOutputStream fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);

                    //显示下载进度
                    sum += len;
                    L.e(sum + " / " + total);
                    final long finalSum = sum;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(finalSum + " / " + total);
                        }
                    });
                }


                final Bitmap bitmap = BitmapFactory.decodeStream(is);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_result.setImageBitmap(bitmap);
                    }
                });


                fos.flush();
                fos.close();
                is.close();


                L.e("下载完成");
            }
        });
    }

    /**
     * post上传文件(表单方式，多个)
     */
    private void postRequestUpload() {
        //SD卡的根目录
        File file = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
        if (!file.exists()) {
            L.e(file.getAbsolutePath() + "，文件不存在!");
            return;
        }

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        RequestBody body = multipartBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("username", "github")
                .addFormDataPart("password", "123")
                .addFormDataPart("mPhoto", "icon.jpg", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "uploadInfo").post(body).build();
        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                L.e("onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                L.e("onResponse：" + res);
            }
        });
    }

    /**
     * 上传单个文件
     */
    private void postRequestUploadSingleFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
        if (!file.exists()) {
            L.e(file.getAbsolutePath() + "，文件不存在!");
            return;
        }
        Request.Builder builder = new Request.Builder();
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        CountingRequestBody countingRequestBody = new CountingRequestBody(body,
                new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(long byteWrite, long contentLength) {
                        L.e(byteWrite + " / " + contentLength);
                    }
                });

        Request request = builder.url(mBaseUrl + "postFile").post(countingRequestBody).build();


        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                L.e("onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse：上传完成");
            }
        });
    }

    /**
     * post请求发送string
     */
    private void postRequestSendString() {
        Request.Builder builder = new Request.Builder();
        RequestBody body = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"), "{username:dear浩哥哥;password:123456}");

        Request request = builder.url(mBaseUrl + "postString").post(body).build();
        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                L.e("onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                L.e("onResponse：" + res);
            }
        });
    }

    /**
     * post请求
     */
    private void postRequest() {
        //1.拿到OkHttpClient对象

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("username", "dear浩哥哥").add("password", "123");
        FormBody body = bodyBuilder.build();
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "login").post(body).build();
        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                L.e("onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                L.e("onResponse：" + res);
            }
        });
    }

    /**
     * get请求
     */
    private void getRequest() {
        //1.拿到OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url("https://www.baidu.com/").build();
        //3.将Request封装为Call
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                L.e("onFailure：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                L.e("onResponse：" + res);
            }
        });
    }
}

