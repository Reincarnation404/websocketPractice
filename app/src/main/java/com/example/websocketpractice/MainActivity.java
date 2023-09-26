package com.example.websocketpractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.websocketpractice.adapter.Adapter_ChatMessage;
import com.example.websocketpractice.databinding.ActivityMainBinding;
import com.example.websocketpractice.model.ChatMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    private WebSocketClient client;
//    private JWebSocketClientService jWebSClientService;
    private ActivityMainBinding binding;

    private Adapter_ChatMessage adapter_chatMessage;
//    private JWebSocketClientService.JWebSocketClientBinder binder;

    private Context mContext;

    private EditText et_content;
    private RecyclerView recyclerView;
    private Button btn_send;
    private List<ChatMessage> chatMessageList = new ArrayList<>();//訊息列表
    private ChatMessageReceiver chatMessageReceiver;

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            Log.e("MainActivity", "與service成功綁定");
//            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
//            jWebSClientService = binder.getService();
//            client = jWebSClientService.client;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            Log.e("MainActivity", "與service斷連");
//        }
//    };

    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            ChatMessage chatMessage=new ChatMessage();
            chatMessage.setContent(message);
            chatMessage.setIsMeSend(0);
            chatMessage.setIsRead(1);
            chatMessage.setTime(System.currentTimeMillis()+"");
            chatMessageList.add(chatMessage);
            initChatMsgListView();
            System.out.println("ChatMessageReceiver的onReceive被呼叫"+chatMessage.toString());
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViewById();
//        //啟動service
//        startJWebSClientService();
//        //绑定service
//        bindService();
        //廣播註冊
        doRegisterReceiver();


        createWebSocketClient();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = et_content.getText().toString();
                if (content.length() <= 0) {
                    Toast.makeText(MainActivity.this, "不能為空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (client != null) {
                    client.send(content);

                    //把chatMessage物件存到chatMessageList
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.setContent(content);
                    chatMessage.setIsMeSend(1);
                    chatMessage.setIsRead(1);
                    chatMessage.setTime(System.currentTimeMillis()+"");
                    chatMessageList.add(chatMessage);
                    //更新adapter
                    initChatMsgListView();
                    //輸入欄清空
                    et_content.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "連線斷開or尚未連線", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

//    /**
//     * 绑定service
//     */
//    private void bindService() {
//        Intent bindIntent = new Intent(MainActivity.this, JWebSocketClientService.class);
//        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
//    }
//    /**
//     * 启动服务（websocket客户端服务）
//     */
//    private void startJWebSClientService() {
//        Intent intent = new Intent(MainActivity.this, JWebSocketClientService.class);
//        startService(intent);
//    }
    /**
     * 動態註冊廣播 收到廣播後 才會發出收到對方訊息的intent 才會更新view
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
    }


    private void initChatMsgListView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter_chatMessage = new Adapter_ChatMessage(this, chatMessageList);
        recyclerView.setAdapter(adapter_chatMessage);

    }

    private void findViewById() {
        btn_send = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.chatmsg_listView);
        et_content = findViewById(R.id.et_content);
    }


    /**
     * 初始化websocket connection
     */
    private void createWebSocketClient() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://10.0.2.2:8080/webSocket");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        client = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
              //  client.send("Hello World!");
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");

                Intent intent = new Intent();
                intent.setAction("com.xch.servicecallback.content");
                intent.putExtra("message", s);
                sendBroadcast(intent);

            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };

        //如果連線時間超過10s 視為連線失敗 會sout: failed to connect to /10.0.2.2 (port 8080) from /192.168.232.2 (port 35204) after 10000ms
        client.setConnectTimeout(10000);
        //60s內如果都沒有傳資料 connection會斷開
        client.setReadTimeout(60000);
        //設定5s後會自動重連
        client.enableAutomaticReconnection(5000);

        //連線
        client.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //關閉websocket連線
        client.close();
        //取消註冊廣播
        unregisterReceiver(chatMessageReceiver);
    }
}