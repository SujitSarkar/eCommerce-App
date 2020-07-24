package sarkar.kinboami;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Animation left, right, top;
    ImageView app_logo;
    TextView app_slogan;
    View yellowLine,blueLine,redLine,yellowLine2,greenLine,blueLine2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide Action bar...
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        app_logo = findViewById(R.id.app_logo);
        app_slogan = findViewById(R.id.app_slogan);
        yellowLine = findViewById(R.id.yellowLine);
        blueLine = findViewById(R.id.blueLine);
        redLine = findViewById(R.id.redLine);
        yellowLine2 = findViewById(R.id.yellowLine2);
        greenLine = findViewById(R.id.greenLine);
        blueLine2 = findViewById(R.id.blueLine2);

        //Animations...
        left = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_animation);
        right = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_animation);
        top = AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_animation);

        //Set Animations to fields...
        app_logo.setAnimation(left);
        app_slogan.setAnimation(right);
        yellowLine.setAnimation(top);
        blueLine.setAnimation(top);
        redLine.setAnimation(top);
        yellowLine2.setAnimation(top);
        greenLine.setAnimation(top);
        blueLine2.setAnimation(top);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
}