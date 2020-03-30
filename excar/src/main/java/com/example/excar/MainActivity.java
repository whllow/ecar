package com.example.excar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button up,down,left,right;
    private Button open,close,change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();

    }
    private void bindView(){
        up = findViewById(R.id.upgo);
        down = findViewById(R.id.downgo);
        left  = findViewById(R.id.leftgo);
        right = findViewById(R.id.rightgo);

        open  = findViewById(R.id.open);
        close = findViewById(R.id.close);
        change = findViewById(R.id.change);

        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        change.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
       int id =  view.getId();
       switch(id){
           case R.id.open:
               Intent intent = new Intent(MainActivity.this,BlueLink.class);
               startActivity(intent);
               break;

           case R.id.close:

               break;

           case R.id.change:

               break;

           case R.id.upgo:

               break;

           case R.id.downgo:

               break;

           case R.id.leftgo:

               break;

           case R.id.rightgo:

               break;


       }




    }
}
