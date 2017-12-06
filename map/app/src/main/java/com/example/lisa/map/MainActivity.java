package com.example.lisa.map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    final static int maxN = 10;
    private Context context;
    private ImageView[][] cell = new ImageView[maxN][maxN];
    private Drawable[] drawCell = new Drawable[6];


    private Button btnPlay;
    private TextView tvTurn;
    private int[][] valueCell= new int[maxN][maxN];
    private int winner;
    private boolean firstmove;
    private int xmove,ymove;
    private int turnsplayed;
    public MainActivity(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        context=this;
        setListes();
        loadResources();
        designBoardGame();
    }
    private void setListes(){
        btnPlay=(Button) findViewById(R.id.gametoplay);
        btnPlay.setText("Play Game");
        tvTurn=(TextView) findViewById(R.id.tvTurn3);
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
        turnsplayed=r.nextInt(2)+1;
        if(turnsplayed==1){
            hit();
        }else {
            miss();
        }


    }
    private void hit() {
      tvTurn.setText("Player 1");
        isClicked=false;
    }

    private void miss() {
        tvTurn.setText("Player 2");
        if (firstmove){
            xmove=7;ymove=7;
            makeAMove();
        }else {
            //finde bedst place
        }
    }
    private void makeAMove(){
        cell[xmove][ymove].setImageDrawable(drawCell[turnsplayed]);
        //play sound for player in turn (bomb for hit og splash for miss)
        // start vibration stærk for hit svagere for miss

        if(turnsplayed==1){
            turnsplayed=(1+2)-turnsplayed;
            miss();
        }else{
            turnsplayed=3-turnsplayed;
        }

    }
    private void playSound(){

    }

    private void init_game() {
        firstmove=true;
        winner=0;
        int i;
        int j;

        for(i=0; i<maxN; i++){
            for(j=0;j<maxN;j++){
                cell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j]=0;

            }
        }
    }

    private void loadResources(){
        drawCell[0]=null;
        drawCell[1]=context.getResources().getDrawable(R.drawable.kryds);
        drawCell[2]=context.getResources().getDrawable(R.drawable.circle);
        drawCell[3]=context.getResources().getDrawable(R.drawable.cellbg);
        drawCell[4]=context.getResources().getDrawable(R.drawable.fire);
        drawCell[4]=context.getResources().getDrawable(R.drawable.ship);

    }
    private boolean isClicked;
    private void designBoardGame(){
        int sizeofCell = Math.round(ScreenWidth()/maxN);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeofCell*maxN, sizeofCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeofCell,sizeofCell);

        LinearLayout linBoardGame = (LinearLayout) findViewById(R.id.linBoardGame);

        for(int i=0; i<maxN; i++){
            LinearLayout linRow = new LinearLayout(context);
            for(int j=0; j<maxN; j++){
                cell[i][j] = new ImageView(context);
                cell[i][j].setBackground(drawCell[3]);
                final int x=i;
                final int y=j;
                cell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     if(turnsplayed==1 || !isClicked){
                         isClicked=true;
                         xmove=x;ymove=y;
                         makeAMove();

                     }
                    }
                });
                linRow.addView(cell[i][j], lpCell);

            }
            linBoardGame.addView(linRow, lpRow);
        }
    }
    private float ScreenWidth (){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }
}