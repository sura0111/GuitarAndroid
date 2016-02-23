package com.example.sura.guitar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
//import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

public class Main extends Activity implements View.OnTouchListener {
    DisplayView view;
    boolean pinFlag;
    boolean touchFlag=false;
    int inst[] = new int[6];
    float xMax;
    float stringX;
    boolean enableSerial = false;
    float speedX;
    int count;
    int x, y;
    //int strings=0;
    SoundPool soundpool;
    int soundid[] = new int[6];
    Physicaloid mPhysicaloid;
    UartConfig uartConfig = new UartConfig(921600, 8, 0, 0, false, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout display = new LinearLayout(this);

        view = new DisplayView(this);
        display.addView(view);
        setContentView(display);
        view.setOnTouchListener(this);
        mPhysicaloid = new Physicaloid(this);
        xMax=200;
        stringX=0;
        speedX=1.0f;
        count=0;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE, 0, Menu.NONE, "OpenPort");
        menu.add(Menu.NONE, 1, Menu.NONE, "ClosePort");
        menu.add(Menu.NONE, 2, Menu.NONE, "ShowPin");
        menu.add(Menu.NONE, 3, Menu.NONE, "HidePin");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case 0:
                if(mPhysicaloid.open(uartConfig)){
                    enableSerial=true;}
                break;

            case 1:
                if(mPhysicaloid.close()){
                    enableSerial = false;}
                break;
            case 2:
                pinFlag = true;
                break;
            case 3:
                pinFlag = false;
                break;

        }
        return true;
    }
    /*@Override
    protected void onResume(){
        super.onResume();
        view.resume();
    }*/
    @Override
    protected void onPause(){
        super.onPause();
        view.pause();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = (int)event.getX();
                y = (int)event.getY();
                touchFlag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int)event.getX();
                y = (int)event.getY();
                touchFlag = true;
                break;
            case MotionEvent.ACTION_UP:
                x = 0;
                y = 0;
                touchFlag = false;
                break;
            default:
                return true;
        }
        return true;
    }

    public class DisplayView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
        public final static int ELECTRODE_NUM = 61;
        TactileDisplay shapes;
        Thread thread;
        boolean loop;
        //SurfaceHolder holder;
        int strFlag[] = new int[6];
        Resources r = getResources();
        float screenWidth, screenHeight;
        byte electrodeFlag[] = new byte[ELECTRODE_NUM+3];
        byte electrode[] = new byte[(ELECTRODE_NUM+3)/8];
        Bitmap background = BitmapFactory.decodeResource(r, R.drawable.guitar);
        Bitmap sImage = BitmapFactory.decodeResource(r, R.drawable.stringsv2);

        GString gstring = new GString(DisplayView.this, sImage);


        public DisplayView(Context context) {
            super(context);
            getHolder().addCallback(this);
            for(int i=0; i<6; i++){strFlag[i]=0;}
            loop = true;
            soundpool  = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
            /*soundpool = new SoundPool.Builder().setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build())
                    .setMaxStreams(6)
                    .build();*/
            for(int i=0; i<6; i++){
                soundid[i]=0;
                inst[i]=0;
            }
            soundid[0]= soundpool.load(this.getContext(), R.raw.string1, 1);
            soundid[1]= soundpool.load(this.getContext(), R.raw.string2, 1);
            soundid[2]= soundpool.load(this.getContext(), R.raw.string3, 1);
            soundid[3]= soundpool.load(this.getContext(), R.raw.string4, 1);
            soundid[4]= soundpool.load(this.getContext(), R.raw.string5, 1);
            soundid[5]= soundpool.load(this.getContext(), R.raw.string6, 1);

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //SurfaceViewが作成された時の処理（初期画面の描画等）
            thread = new Thread(this);
            loop = true;
            thread.start();
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if(mPhysicaloid.open(uartConfig)){
                enableSerial = true;
            }
            loop = true;
            screenWidth = width;
            screenHeight = height;
            gstring.setWH(screenWidth, screenHeight);
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            pause();
        }
        public void pause() {
            loop = false;
            /*try {thread.join();}
            catch (InterruptedException e){e.printStackTrace();}*/
            thread = null;
        }
        @Override
        public void run(){
            shapes = new TactileDisplay();
            if(mPhysicaloid.open(uartConfig)){
                enableSerial = true;
            }
            while(loop){// && enableSerial){
                if(touchFlag) {
                    electrodeFlag = shapes.getPin();
                    for(int pinNo = 0; pinNo < (ELECTRODE_NUM)/8; pinNo++){
                        int pinNo1 = pinNo*8;
                        electrode[pinNo] = (byte) (
                                (electrodeFlag[pinNo1] << 7) |
                                (electrodeFlag[pinNo1 + 1] << 6) |
                                (electrodeFlag[pinNo1 + 2] << 5) |
                                (electrodeFlag[pinNo1 + 3] << 4) |
                                (electrodeFlag[pinNo1 + 4] << 3) |
                                (electrodeFlag[pinNo1 + 5] << 2) |
                                (electrodeFlag[pinNo1 + 6] << 1) |
                                (electrodeFlag[pinNo1 + 7]));
                    }
                }else{
                    for(int pinNo=0; pinNo<8; pinNo++){
                        electrode[pinNo]=0;
                    }
                }
                if(enableSerial){
                    mPhysicaloid.write(electrode, electrode.length);
                }
                if(touchFlag){
                    shapes.pinPos(x,y);
                }
                doDraw(getHolder());
                //strings++;
                //if(strings>5)strings=0;
                for (int strings = 0; strings < 6; strings++) {
                    if ((x > 1080 / 12 + 1080 * strings / 6 - 80 && x < 1080 / 12 + 1080 * strings / 6 + 80) && touchFlag) {
                        strFlag[strings] = 1;
                        //inst=i;
                        if(inst[strings]<1){
                            soundpool.play(soundid[strings],1,1,0,0,1);
                            inst[strings]=1;
                        }
                    } else{
                        strFlag[strings] = 0;
                        inst[strings]=0;
                    }
                }
            }
        }
        private void doDraw(SurfaceHolder holder){
            Canvas canvas = holder.lockCanvas();
            canvas.drawBitmap(background, new Rect(0, 0, background.getWidth(), background.getHeight()), new Rect(0,0,(int)screenWidth, (int)screenHeight),null);
            shapes.InitShapes(canvas);
            shapes.initPin();
            for (int i = 0; i < 6; i++) {
                shapes.electricLine(1080/12+1080*i/6);
            }
            onDraw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
        @Override
        protected void onDraw(Canvas canvas){
                gstring.onDraw(canvas, strFlag[0], 0);
                gstring.onDraw(canvas, strFlag[1], 1);
                gstring.onDraw(canvas, strFlag[2], 2);
                gstring.onDraw(canvas, strFlag[3], 3);
                gstring.onDraw(canvas, strFlag[4], 4);
                gstring.onDraw(canvas, strFlag[5], 5);
            if(touchFlag && pinFlag){
                shapes.drawPin(electrodeFlag, true);
            }
        }
    }
}

