package com.yy;
import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;

import android.graphics.Canvas;

import android.graphics.Color;

import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.RectF;

import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Vector;



/**

 * Created by yeqi on 2018/5/10.

 */

class my{//新建一个类 里面的东西都是静态的 当全局变量用

    public static int js=0;//击杀数

    public static int w,h;//屏幕的宽高

    public static float bili;//比例，用于适应不同屏幕

    public static Vector<hj> list=new Vector<hj>();//所有飞行物的集合,添加进这个集合才能被画出来

    public static Vector<hj> drlist=new Vector<hj>();//敌人飞机的集合，添加进这个集合才能被子弹打中



    public static Bitmap myhj,drhj,bj,myzd;//图片：我的灰机 敌人灰机 背景 我的子弹

    public static myhj my;//我的灰机

    public static bj b;//背景

}



class hua extends View{//画

    private Paint p=new Paint();//画笔

    private float x,y;//按下屏幕时的坐标

    private float myx,myy;//按下屏幕时玩家飞机的坐标



    public hua(Context context) {

        super(context);

        //添加事件控制玩家飞机

        setOnTouchListener(new OnTouchListener() {

            @Override

            public boolean onTouch(View view, MotionEvent e) {

                if(e.getAction()==MotionEvent.ACTION_DOWN){

                    x=e.getX();

                    y=e.getY();

                    myx=my.my.r.left;

                    myy=my.my.r.top;

                }

                float xx=myx+e.getX()-x;

                float yy=myy+e.getY()-y;

                //我的飞机不能飞出屏幕

                xx=xx<my.w-my.my.w/2?xx:my.w-my.my.w/2;

                xx=xx>-my.my.w/2?xx:-my.my.w/2;

                yy=yy<my.h-my.my.h/2?yy:my.h-my.my.h/2;

                yy=yy>-my.my.h/2?yy:-my.my.h/2;

                my.my.setX(xx);

                my.my.setY(yy);

                return true;

            }

        });



        setBackgroundColor(Color.BLACK);//设背景颜色为黑色



        my.myhj= BitmapFactory.decodeResource(getResources(),R.mipmap.hj);//加载图片

        my.drhj=BitmapFactory.decodeResource(getResources(),R.mipmap.dr);

        my.myzd=BitmapFactory.decodeResource(getResources(),R.mipmap.zd);

        my.bj=BitmapFactory.decodeResource(getResources(), R.mipmap.bj);



        new Thread(new re()).start();//新建一个线程 让画布自动重绘

        new Thread(new loaddr()).start();//新建一个 加载敌人的线程

    }

    @Override

    protected void onDraw(Canvas g) {//这个相当于swing的paint方法吧 用于绘制屏幕上的所有物体

        super.onDraw(g);

        g.drawBitmap(my.b.img,null,my.b.r,p);//画背景 我没有把背景添加到list里



        for(int i=0;i<my.list.size();i++){//我们把所有的飞行物都添加到了my.list这个集合里

            hj h=my.list.get(i);           //然后在这里用一个for循环画出来

            g.drawBitmap(h.img,null,h.r,p);

        }

        g.drawText("击杀："+my.js,0,my.h-50,p);



    }

    @Override

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//这个方法用来获取屏幕宽高的

        super.onSizeChanged(w, h, oldw, oldh);

        my.w=w;//获取宽

        my.h=h;//高



        //获取手机（应该不是手机的吧 是这控件的吧）分辨率和1920*1080的比例

        //然后飞机的宽高乘上这个分辨率就能在不同大小的屏幕正常显示了

        //为什么用1920*1080呢 因为我手机就是这个分辨率。。。

        my.bili= (float) (Math.sqrt(my.w * my.h)/ Math.sqrt(1920 * 1080));

        p.setTextSize(50*my.bili);//设置字体大小，“击杀”的大小

        p.setColor(Color.WHITE);//设为白色

        //好了 到这里游戏开始了

        my.b=new bj();//初始化背景

        my.my=new myhj();//初始化 我的灰机

    }

    private class re implements Runnable {

        @Override

        public void run() {

            //每10ms刷新一次界面

            while(true){

                try { Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

                postInvalidate();//刷新画布


            }

        }

    }

    private class loaddr implements Runnable{

        @Override

        public void run() {

            while(true){

                //每1000ms刷一个敌人

                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

                try {

                    new drhj();

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        }

    }

}

class hj{//游戏内所有物体的父类

    public RectF r=new RectF();//这个是用来确定位置的

    public int hp;//生命

    public float w,h;//宽高

    public Bitmap img;//图片





    //这里的画图方法和swing的不太一样

    //设两个方法来设置x,y的坐标

    public void setX(float x){

        r.left=x;

        r.right=x+w;

    }

    public void setY(float y){

        r.top=y;

        r.bottom=y+h;

    }



    public boolean pengzhuang(hj obj,float px) {//判断碰撞 判断时忽略px个像素

        px*=my.bili;//凡是涉及到像素的 都乘一下分辨率比例my.bili

        if (r.left+px - obj.r.left <= obj.w && obj.r.left - this.r.left+px <= this.w-px-px)

            if (r.top+px - obj.r.top <= obj.h && obj.r.top - r.top+px <= h-px-px) {

                return true;

            }

        return false;



    }

}

class bj extends hj implements  Runnable{//背景

    public bj(){

        w=my.w;

        h=my.h*2;//背景的高是 屏幕高的两倍

        img=my.bj;

        setX(0);

        setY(-my.h);

        new Thread(this).start();

    }

    @Override

    public void run() {

        //这里控制背景一直向下移

        while(true){

            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

            if(r.top+2<=0){

                setY(r.top+2);

            }else{

                setY(-my.h);

            }

        }

    }

}



class drhj extends hj implements Runnable{//敌人灰机

    private long sd0=(long) (Math.random()*10)+10;//生成一个[10,20)的随机数 用来控制敌人速度 敌人速度是不一样的



    public drhj(){

//        w=my.w/5.4f;

//        h=my.h/9.6f;

        w=h=200*my.bili;

        //敌人刷出来的位置

        setX((float)( Math.random()*(my.w-w)));//x是随机的

        setY(-h);//在屏幕外 刚好看不到的位置

        img=my.drhj;

        hp=12;//生命=12

        my.list.add(this);//添加到集合里 这样才能被画出来

        my.drlist.add(this);//添加到敌人的集合 添加进这个集合子弹才打得到

        new Thread(this).start();

    }



    @Override

    public void run() {

        while(hp>0){//如果生命>0 没有死 就继续向前飞，死了还飞什么？

            try {Thread.sleep(sd0);} catch (InterruptedException e) {e.printStackTrace();}

            setY(r.top+2*my.bili);

            if(r.top>=my.h)break;//敌人飞出屏幕 跳出循环

        }

        //从集合删除

        my.list.remove(this);

        my.drlist.remove(this);

    }

}



class myhj extends hj implements Runnable{//我的灰机



    public myhj(){

        w=h=200*my.bili;//凡是涉及到像素的 都乘一下分辨率比例my.bili

        //设置初始位置

        setX(my.w/2-w/2);

        setY(my.h*0.7f-h/2);

        img=my.myhj;//初始化图片

        my.list.add(this);//添加到集合里 这样才能被画出来

        new Thread(this).start();//发射子弹的线程

    }



    @Override

    public void run() {

        while(true){

            //90毫秒发射一发子弹

            try {Thread.sleep(90);} catch (InterruptedException e) {e.printStackTrace();}

            new myzd(this);

        }

    }

}

class myzd extends hj implements Runnable{//我的子弹

    private int dps;

    private float sd0;



    public myzd(hj hj){

        w=h=90*my.bili;//凡是涉及到像素的 都乘一下分辨率比例my.bili

        img=my.myzd;//图片

        sd0=6*my.bili;//速度=6

        dps=6;//伤害=6

        //设在玩家中心的偏上一点

        setX(hj.r.left+hj.w/2-w/2);

        setY(hj.r.top-h/2);

        my.list.add(this);//添加到集合里 这样才能被画出来

        new Thread(this).start();//新建一个子弹向上移动的线程

    }



    @Override

    public void run() {

        boolean flag=false;//一个标记 用来跳出嵌套循环

        while(true){

            try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}

            setY(r.top-sd0);//向上移sd0个像素，sd0=6



            try {//try一下 怕出错

                //这里判断有没有和集合里的敌人发生碰撞

                for(int i=0;i<my.drlist.size();i++){

                    hj h=my.drlist.get(i);

                    if(pengzhuang(h,30)){//判断碰撞

                        h.hp-=dps;//敌人生命-子弹伤害

                        flag=true;//一个标记 用来跳出嵌套循环

                        my.js++;//击杀+1

                        break;

                    }

                }

            } catch (Exception e) {

                e.printStackTrace();

                break;

            }

            if(flag || r.top+h<=0)break;//如果子弹击中过敌人 或者超出屏幕范围 跳出循环

        }

        my.list.remove(this);//从集合删除

    }

}




//添加游戏音效

class GameSoundpool {

    private SoundPool soundPool;

    private int s1;

    private int s2;

    public GameSoundpool(Context context) {

        this.soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);

        s1 = soundPool.load(context, R.raw.shoot,1);

        s2 = soundPool.load(context, R.raw.explosion2,1);

    }

    public void playSound(int s){

        switch (s){

            case 1:

                soundPool.play(s1,1,1,0,1,1.0f);

                break;

            case 2:

                soundPool.play(s2,1,1,0,1,1.0f);

                break;

        }





    }

}






//构建游戏开始界面

 class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
    private  GameMenu gameMenu;
    private Bitmap bmpMainBG;
    private Bitmap bmpLogo;
    private Bitmap bmpButton;
    private Bitmap bmpText;
    public  static  int screenwidth;
    public  static  int  screenheight;


    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint;

    public GameSurface(Context context) {





        super(context);


        surfaceHolder = this.getHolder();

        surfaceHolder.addCallback(this);

        paint=new Paint();

        paint.setAntiAlias(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenwidth=this.getWidth();
        screenheight=this.getHeight();


        initBitmap();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mydraw();

            }
        }).start();

    }

    private void mydraw() {
        canvas=surfaceHolder.lockCanvas();

        gameMenu.draw(canvas,paint);
        if (canvas!=null){
            surfaceHolder.unlockCanvasAndPost(canvas);

        }



    }
    private  void  initBitmap(){
        bmpMainBG= BitmapFactory.decodeResource(this.getResources(),R.drawable.mainmenu);
        bmpLogo= BitmapFactory.decodeResource(this.getResources(),R.drawable.logo);
        bmpButton= BitmapFactory.decodeResource(this.getResources(),R.drawable.menustartpress);
        bmpText= BitmapFactory.decodeResource(this.getResources(),R.drawable.starttext);



        gameMenu=new GameMenu(bmpMainBG,bmpLogo,bmpButton,bmpText);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}





 class GameMenu {
    private Bitmap bmpMainBG;
    private Bitmap bmpLogo;
    private Bitmap bmpButton;
    private Bitmap bmpText;
    private Rect rect;
    private Rect rect2;

    public   GameMenu(Bitmap bmpMainBG,Bitmap bmpLogo,Bitmap bmpButton,Bitmap bmpText ){
        this.bmpButton=bmpButton;
        this.bmpLogo=bmpLogo;
        this.bmpMainBG=bmpMainBG;
        this.bmpText=bmpText;
        rect=new Rect(0,GameSurface.screenheight/6,GameSurface.screenwidth,GameSurface.screenheight/5+GameSurface.screenheight/5);
        rect2=new Rect(0,0,GameSurface.screenwidth,GameSurface.screenheight);

    }

    public  void draw(Canvas canvas, Paint paint) {

        canvas.drawBitmap(bmpMainBG,null,rect2,paint);

        canvas.drawBitmap(bmpLogo,null,rect,paint);
        int x=GameSurface.screenwidth/2-bmpButton.getWidth()/2;
        int y=GameSurface.screenheight/3*2;
        canvas.drawBitmap(bmpButton,x,y,paint);
        int x1=GameSurface.screenwidth/2-bmpText.getWidth()/2;
        int y1=GameSurface.screenheight/3*2+20;
        canvas.drawBitmap(bmpText,x1,y1,paint);

    }

}
