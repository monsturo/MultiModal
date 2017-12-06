package com.example.lisa.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final static int maxN = 7;
    private Context context;
    private ImageView[][] cell = new ImageView[maxN][maxN];
    private Drawable[] drawCell = new Drawable[6];
    MQTT mqtt = new MQTT();
    CallbackConnection connection;
    private ArrayList<Ship> ships = new ArrayList<>();

    private Button btnPlay;
    private TextView tvTurn;
    private int[][] valueCell = new int[maxN][maxN];
    private int winner;
    private boolean firstmove;
    private int xmove, ymove;
    private int turnsplayed;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        context = this;
        setListes();
        loadResources();
        designBoardGame();

        try {
            mqtt.setHost("test.mosquitto.org", 1883);
            Log.d("hej", mqtt.getHost().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.d("hej", "lige inden connection.listener");
        connection = mqtt.callbackConnection();
        connection.listener(new Listener() {


            @Override
            public void onConnected() {
                Log.d("hej", "inde i succes connection.listener");
            }

            @Override
            public void onDisconnected() {
                Log.d("hej", "inde i failure connection.listener");
            }

            @Override
            public void onPublish(UTF8Buffer topic, final org.fusesource.hawtbuf.Buffer body, Runnable ack) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = body.ascii().toString();
                        Log.d("hej", msg);
                    }
                });
                ack.run();
            }

            @Override
            public void onFailure(Throwable value) {
                Log.d("hej", "inde i failure BLARGl.listener");
                Log.e("hej", "Throwable", value);
            }
        });
        Log.d("hej", "Lige EFTER Listener");
        connection.connect(new Callback<Void>() {
            public void onFailure(Throwable value) {
                Log.d("hej", " fejl connection");
                connection.failure(); // If we could not connect to the server.
            }

            // Once we connect..
            public void onSuccess(Void v) {

                // Subscribe to a topic
                Topic[] topics = {new Topic("demo/battleships/android/device1", QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    public void onSuccess(byte[] qoses) {
                        Log.v("Hej", "Subscribe Succes");
                    }

                    public void onFailure(Throwable value) {
                        Log.d("hej", " fejl i subscribe");
                        connection.failure(); // subscribe failed.
                    }
                });

            }
        });
        Log.d("Hej", "Til sidst i onCreate");

        connection.publish("demo/battleships/android/device1", "Det virker".getBytes(), QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
            public void onSuccess(Void v) {
                Log.d("hej", " succes i send msg");
            }

            public void onFailure(Throwable value) {
                Log.d("hej", " fejl i send msg");
            }
        });
        addShips();
    }


    private void setListes() {
        btnPlay = (Button) findViewById(R.id.gametoplay);
        btnPlay.setText("Play Game");
        tvTurn = (TextView) findViewById(R.id.tvTurn3);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_game();
                play_game();
            }
        });
    }

    private void play_game() {
        Random r = new Random();
        turnsplayed = r.nextInt(2) + 1;
        if (turnsplayed == 1) {
            hit();
        } else {
            miss();
        }


    }

    private void hit() {
        tvTurn.setText("Player 1");
        isClicked = false;
    }

    private void miss() {
        tvTurn.setText("Player 2");
        if (firstmove) {
            xmove = 7;
            ymove = 7;
            makeAMove();
        } else {
            //finde bedst place
        }
    }

    private void makeAMove() {
        cell[xmove][ymove].setImageDrawable(drawCell[turnsplayed]);
        //play sound for player in turn (bomb for hit og splash for miss)
        // start vibration stærk for hit svagere for miss

        if (turnsplayed == 1) {
            turnsplayed = (1 + 2) - turnsplayed;
            miss();
        } else {
            turnsplayed = 3 - turnsplayed;
        }

    }

    private void hitEnemy(){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bomb);
        mediaPlayer.setVolume((float)0.5,(float)0.5);
        mediaPlayer.start();
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(200);
    }
    private void hitByEnemy() {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bomb);
        mediaPlayer.setVolume(1,1);
        mediaPlayer.start();
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(300);
    }

    private void missEnemy() {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.splash);
        mediaPlayer.setVolume((float)0.5, (float)0.5);
        mediaPlayer.start();
        Vibrator vibrator = getSystemService(Vibrator.class);
        vibrator.vibrate(100);
    }

    private void missedByEnemy(){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.splash);
        mediaPlayer.setVolume(1,1);
        mediaPlayer.start();
    }

    private void init_game() {
        firstmove = true;
        winner = 0;
        int i;
        int j;

        for (i = 0; i < maxN; i++) {
            for (j = 0; j < maxN; j++) {
                cell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j] = 0;

            }
        }
    }

    private void loadResources() {
        drawCell[0] = null;
        drawCell[1] = context.getResources().getDrawable(R.drawable.kryds);
        drawCell[2] = context.getResources().getDrawable(R.drawable.circle);
        drawCell[3] = context.getResources().getDrawable(R.drawable.cellbg);
        drawCell[4] = context.getResources().getDrawable(R.drawable.fire);
        drawCell[4] = context.getResources().getDrawable(R.drawable.ship);

    }

    private boolean isClicked;

    private void designBoardGame() {
        int sizeofCell = Math.round(ScreenWidth() / maxN);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeofCell * maxN, sizeofCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);

        LinearLayout linBoardGame = (LinearLayout) findViewById(R.id.linBoardGame);

        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            for (int j = 0; j < maxN; j++) {
                cell[i][j] = new ImageView(context);
                cell[i][j].setBackground(drawCell[3]);
                final int x = i;
                final int y = j;
                cell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (turnsplayed == 1 || !isClicked) {
                            isClicked = true;
                            xmove = x;
                            ymove = y;
                            makeAMove();
                            if (y % 2 == 0){
                                missEnemy();
                            } else if (x % 2 == 0){
                                hitEnemy();
                            } else {
                                hitByEnemy();
                            }
                        }
                    }
                });
                linRow.addView(cell[i][j], lpCell);

            }
            linBoardGame.addView(linRow, lpRow);
        }
    }

    private void sendHit(int x, int y){
        boolean hit = false;
        for (Ship ship : ships){
            if (ship.x == x && ship.y == y){
                hit = true;
                hitEnemy();
                break;
            }
        }
        if (!hit){
            missEnemy();
        }
    }

    private void recieveHit(int x, int y){
        boolean hit = false;
        for (Ship ship : ships){
            if (ship.x == x && ship.y == y){
                ship.hit = true;
                hit = true;
                hitByEnemy();
                break;
            }
        }
        if (!hit){
            missedByEnemy();
        }
    }
    private void addShips(){
        ships.add(addShip(0,1));
        ships.add(addShip(0,2));
        ships.add(addShip(0,3));
        ships.add(addShip(0,4));
        ships.add(addShip(4,1));
        ships.add(addShip(5,1));
        ships.add(addShip(6,1));
        ships.add(addShip(4,5));
        ships.add(addShip(4,6));
        ships.add(addShip(6,5));
        ships.add(addShip(6,6));
    }

    private Ship addShip(int x, int y){
        return new Ship(x,y);
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    private class Ship{
        public int x,y;
        public boolean hit;

        public Ship(int x, int y){
            this.x = x;
            this.y = y;
            hit = false;
        }

        public void hit(int x, int y){
            if (this.x == x && this.y == y){
               hit = true;
            }
        }
    }
}

