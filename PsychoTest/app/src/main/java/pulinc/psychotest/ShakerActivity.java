package pulinc.psychotest;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
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
    private boolean first;
    private Random random = new Random();
    private ArrayList<Tuple> tuples = new ArrayList<Tuple>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaker);
        shakeIt();
    }
    public void shakeIt(){
        Vibrator vibrator = getSystemService(Vibrator.class);
        if (random.nextInt() % 2 == 0) {
            first = true;
            vibrator.vibrate(shake);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            value = shake + (random.nextInt() % 100) - (random.nextInt() % 100);
            vibrator.vibrate(value);
        } else {
            first = false;
            value = shake + (random.nextInt() % 100) - (random.nextInt() % 100);
            vibrator.vibrate(value);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            vibrator.vibrate(shake);
        }
        running = false;
    }

    public void shake(boolean left){

        if (index <= 20) {
            index++;
            value = shake + (random.nextInt() % 100) - (random.nextInt() % 100);
            if (first == left) {
                tuples.add(new Tuple(left, value, index));
            } else {
                tuples.add(new Tuple(left, value, index));
            }
        } else {
            Intent intent = new Intent(this, EndingActivity.class);
            intent.putParcelableArrayListExtra("tuples", tuples);
            startActivity(intent);
        }
        running = true;
        shakeIt();

    }

    public void shakeFirst(View view){
        if (!running) {
            shake(true);
        }
    }

    public void shakeLast(View view){
        if (!running) {
            shake(false);
        }
    }


}
