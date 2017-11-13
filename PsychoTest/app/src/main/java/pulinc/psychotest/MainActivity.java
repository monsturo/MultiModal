package pulinc.psychotest;

import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void begin(View view) {
        Intent intent = new Intent(this, ShakerActivity.class);
        Button button = (Button) findViewById(R.id.button);
        startActivity(intent);
    }

}
