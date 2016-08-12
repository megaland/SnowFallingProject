package com.usnschool.test002;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SnowEvent snow = new SnowEvent(this);
        setContentView(snow);
        Thread thread = new Thread(snow);
        thread.start();


    }


}

class SnowEvent extends View implements Runnable {
    private int r = 30;
    ArrayList<threadball> threball = new ArrayList<threadball>();
    Player player;
    Paint playerpaint = new Paint();
    Resources src = this.getResources();
    Bitmap cirimg = BitmapFactory.decodeResource(src, R.drawable.snow);

    boolean flag = true;
    public SnowEvent(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        playerpaint.setColor(Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        player = new Player((int)(this.getWidth()/2), (int)(this.getHeight()-r), 50);

        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    public boolean  onTouchEvent(MotionEvent event) {
        player.setX((int)event.getX());
        if(!flag){
            threball.clear();
            wakeupall();
        }
        return super.onTouchEvent(event);


    }

    public synchronized void wakeupall(){
        flag = true;
        notifyAll();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        double distance = 0;
        Rect dst;
        for(int i=0; i < threball.size(); i++){

            //canvas.drawCircle(threball.get(i).getX(), threball.get(i).getY() ,r, threball.get(i).getPaint());
            dst = new Rect(threball.get(i).getX()-r, threball.get(i).getY()-r, threball.get(i).getX()+r, threball.get(i).getY()+r );
            canvas.drawBitmap(cirimg, null, dst, null);

            if(threball.get(i).y >(getHeight()-1)) {

                threball.remove(i);
            }
            distance = Math.sqrt(Math.pow(player.getX()-threball.get(i).getX(), 2)+Math.pow(player.getY()-threball.get(i).getY(), 2));
            if(distance < (player.getR()+threball.get(i).getR())){
                flag = false;
            }
        }
        canvas.drawCircle(player.getX(), player.getY(), player.getR(), playerpaint);//메인공 위치변화
        super.onDraw(canvas);
    }

    @Override
    public synchronized void run() {

        while(true){
            try {
                Thread.sleep(500);
                if(!flag){
                    wait();
                }
                threadball ball = new threadball((int)(Math.random()*(getWidth()-r))+r, r);
                threball.add(ball);
                ball.start();
                postInvalidate();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    class threadball extends Thread{
        private int x=0;
        private int y=0;
        private int r=0;
        private Paint paint;

        public threadball(int x, int r) {
            this.x = x;
            this.r = r;
            paint = new Paint();
            paint.setColor(Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        }

        @Override
        public synchronized void run() {
            while(true){
                try {
                    Thread.sleep(5);
                    y=y+3;
                    postInvalidate();
                    if(!flag){

                        wait();

                    }
                    if(y>getHeight()-1){


                        break;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getR() {
            return r;
        }

        public Paint getPaint() {
            return paint;
        }
    }

    class Player{
        int x;
        int y;
        int r;

        public Player(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }
    }

}

