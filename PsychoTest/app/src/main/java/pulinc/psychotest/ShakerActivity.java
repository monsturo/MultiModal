package pulinc.psychotest;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;

public class ShakerActivity extends AppCompatActivity {
    private int shake = 250;
    private int value;
    private int index = 0;
    private boolean running = true;
    private Vibrator vibrator = (Vibrator) getSystemService(Vibrator.class);
    private Random random = new Random();
    private ArrayList<Tuple> tuples = new ArrayList<Tuple>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaker);
        vibrator.vibrate(shake);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value = shake + (random.nextInt() % 100) - (random.nextInt() % 100);
        vibrator.vibrate(value);
    }

    public void shake(boolean left){
        if (index == 40) {
            index++;
            value = shake + (random.nextInt() % 100) - (random.nextInt() % 100);
            tuples.add(new Tuple(left, value));
        } else {

            Intent intent = new Intent(this, EndingActivity.class);
        }
    }

    public void shakeFirst(View view){
        Button button = (Button) findViewById(R.id.button);
        shake(true);
    }

    public void shakeLast(View view){
        Button button = (Button) findViewById(R.id.button2);
        shake(false);
    }

    public class Tuple{
        boolean standard;
        int value;
        public Tuple(boolean standard, int value) {
            this.standard = standard;
            this.value = value;
        }

        public boolean getStandard(){
            return standard;
        }

        public int getValue(){
            return value;
        }
    }
}
