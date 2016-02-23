package com.example.sura.guitar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class TactileDisplay {
    public static final int ELECTRODE_NUM = 61;
    int x, y;
    int pinr, scale = 18;
    Paint black1 = new Paint();
    Paint black2 = new Paint();
    Paint green = new Paint();
    int pinx1[] = new int [ELECTRODE_NUM];
    int piny1[] = new int [ELECTRODE_NUM];
    int pinx2[] = new int [ELECTRODE_NUM];
    int piny2[] = new int [ELECTRODE_NUM];
    Canvas canvas;
    byte electrodeFlag[] = new byte[ELECTRODE_NUM+3];

    int pinSelector[]={0,0};
    int pinCounter[] = {0,30,61};

    public TactileDisplay(){
        black1.setColor(Color.BLACK);
        black1.setStrokeWidth(3);
        black1.setAntiAlias(true);
        black1.setStyle(Paint.Style.STROKE);
        black2.setColor(Color.BLACK);
        black2.setStrokeWidth(3);
        black2.setAntiAlias(true);
        black2.setStyle(Paint.Style.FILL_AND_STROKE);
        green.setColor(Color.GREEN);
        green.setStrokeWidth(3);
        green.setAntiAlias(true);
        green.setStyle(Paint.Style.FILL_AND_STROKE);
        for(int pinNo = ELECTRODE_NUM; pinNo<ELECTRODE_NUM+3; pinNo++){
            electrodeFlag[pinNo]=3;
        }
    }
    public void InitShapes(Canvas c){ canvas = c;}
    public void pinPos(int xaxis, int yaxis){
        int i = 0;
        for (pinr = -8; pinr <= 8; pinr +=2){
            for (x = xaxis - (16 - abs(pinr)) * scale / 2; x <= xaxis + (16 - abs(pinr)) * scale / 2; x += 2 * scale) {
                y = yaxis + (int) (scale * pinr * sqrt(3) / 2);
                pinx1[i] = x;
                piny1[i] = y;
                pinx2[60 - i] = x;
                piny2[60 - i] = y;
                i++;
            }
        }
    }
    public void drawPin(byte[] flag, boolean rear){
        pinSelector[0]++;
        if(pinSelector[0]>1) pinSelector[0]=0;
        for(int j=pinCounter[pinSelector[0]]; j<pinCounter[pinSelector[0]+1]; j++){
            if(rear){
                if(flag[j]==1){
                    canvas.drawCircle(pinx2[j], piny2[j], 10, green);
                }else if(flag[j]==0) {
                    canvas.drawCircle(pinx2[j], piny2[j], 10, black2);
                }
            }
            else{
                if(flag[j]==1){
                    canvas.drawCircle(pinx1[j], piny1[j], 10, green);
                }else if(flag[j]==0){
                    canvas.drawCircle(pinx1[j], piny1[j], 10, black2);
                }
            }
        }
    }
    public byte[] getPin(){
        return electrodeFlag;
    }
    public void initPin(){
        for (int num=0; num<ELECTRODE_NUM+3; num++){
            electrodeFlag[num]=0;
        }
    }
    public void electricLine(int xaxis){
        int x1;
        x1 = xaxis;
        for (int pinNo = 0; pinNo < ELECTRODE_NUM; pinNo++) {
            int xpin = pinx2[pinNo];
            int ypin = piny2[pinNo];
            if (xpin > x1 - 8 && xpin < x1 + 8) {
                if(ypin>0 && ypin<1920) {
                    electrodeFlag[pinNo]=1;
                }
            }
        }
        //canvas.drawLine(x1+xaxis, y1+yaxis, x2+xaxis, y2+yaxis, black1);
    }
}
