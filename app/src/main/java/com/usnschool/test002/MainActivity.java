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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SnowEvent snow;
    private TextView scoretextview;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            scoretextview.setText(String.valueOf(msg.what));
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoretextview = (TextView)findViewById(R.id.scoretextview);
        snow = (SnowEvent)findViewById(R.id.view);
        snow.setHandler(handler);

        Thread thread = new Thread(snow);
        thread.start();

    }
}

class SnowEvent extends View implements Runnable {
    private int r = 30;
    private ArrayList<threadball> threball = new ArrayList<>();
    private Player player;
    private Paint playerpaint = new Paint();
    private Resources src = this.getResources();
    private Bitmap cirimg = BitmapFactory.decodeResource(src, R.drawable.snow);
    private int playerradiusscale = 3;
    private Bitmap playerimg = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(src, R.drawable.snowmantrans), r * playerradiusscale*2, r * playerradiusscale*2, true);
    private boolean flag = true;
    private Handler handler;
    private int score = 0;
    private int eachtimescore = 100;
    public SnowEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        playerpaint.setColor(Color.rgb((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        player = new Player((int)(this.getWidth()/2), (int)(this.getHeight()-r*playerradiusscale), r*playerradiusscale);

        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    public boolean  onTouchEvent(MotionEvent event) {
        player.setX((int)event.getX());
        if(!flag){
            threball.clear();
            Message msg = handler.obtainMessage();
            msg.what = 0;
            handler.sendMessage(msg);
            eachtimescore = 100;
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
        canvas.drawBitmap(playerimg, player.getX()-r*playerradiusscale, player.getY()-r*playerradiusscale, null);
        for(int i=0; i < threball.size(); i++){
            dst = new Rect(threball.get(i).getX()-r, threball.get(i).getY()-r, threball.get(i).getX()+r, threball.get(i).getY()+r );
            canvas.drawBitmap(cirimg, null, dst, null);
            if(threball.get(i).y >(getHeight()-1)) {
                threball.remove(i);
                score += eachtimescore;
                Message msg = handler.obtainMessage();
                msg.what = score;
                handler.sendMessage(msg);
            }
            distance = Math.sqrt(Math.pow(player.getX()-threball.get(i).getX(), 2)+Math.pow(player.getY()-threball.get(i).getY(), 2));
            if(distance < (player.getR()+threball.get(i).getR())){
                flag = false;
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public synchronized void run() {
        int eachtimescorenum = 1;
        while(true){
            try {
                Thread.sleep(500);
                eachtimescorenum++;
                if(eachtimescorenum%120 == 0){
                    eachtimescore += 100;
                }
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
            int yfallingheight = 3;
            int eachtimenum = 1;
            while(true){
                try {
                    Thread.sleep(5);
                    y=y+yfallingheight;
                    if(eachtimenum%12000 ==0){
                        yfallingheight++;
                    }
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

