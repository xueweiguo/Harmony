package xwg.harmony.stopwatch;

import ohos.aafwk.content.Intent;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.text.Font;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.audio.AudioManager;
import ohos.media.audio.SoundPlayer;
import ohos.rpc.RemoteException;

//指针式秒表组件类
public class AnalogStopWatch extends Component implements Component.DrawTask {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00206, "AnalogStopWatch");
    StopWatchAgentProxy stopWatchService = null;
    long millisecond = 0;
    boolean runnable = false;


    public enum PlayStatus{
        STOP,
        PLAYING,
        PSUSE
    }

    //音频播放状态
    SoundPlayer soundPlayer = null;
    int taskId = 0;
    int soundId = 0;
    PlayStatus playStatus = PlayStatus.STOP;
    float padding = 0;

    //构造函数
    public AnalogStopWatch(Context context) {
        super(context);
        Initialize(null);
    }
    //构造函数
    public AnalogStopWatch(Context context, AttrSet attrSet) {
        super(context, attrSet);
        Initialize(attrSet);
    }
    //构造函数
    public AnalogStopWatch(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        Initialize(attrSet);
    }
    //构造函数
    public AnalogStopWatch(Context context, AttrSet attrSet, int resId) {
        super(context, attrSet, resId);
        Initialize(attrSet);
    }

    @Override
    protected void finalize(){
        stopSound();
    }

    public void onForeground(Intent intent){
        runnable = true;
        millisecond = getMilliseconds();
        if(isRunning()){
            onRunTimer();
        }
        resumeSound();
    }

    public void onBackground(){
        pauseSound();
        runnable = false;
    }

    public void setStopWatchService(StopWatchAgentProxy proxy){
        stopWatchService = proxy;
        millisecond = getMilliseconds();
        if(isRunning()){
            onRunTimer();
        }
        resumeSound();
    }

    //获取运行状态
    public boolean isRunning(){
        if(stopWatchService == null) return false;
        try {
            return stopWatchService.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

    }

    //获取当前计时时长
    public long getMilliseconds(){
        if(stopWatchService == null) return 0;
        try {
            return stopWatchService.getTime();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getStartTime(){
        if(stopWatchService == null) return 0;
        try {
            return stopWatchService.getStartTime();
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }


    //根据目前的运行状态，开始或停止计时
    public void start_stop() {
        if(stopWatchService == null) return;
        try {
            if (!stopWatchService.isRunning()) {
                if (stopWatchService.getTime() == 0) {
                    start();
                }
            } else {
                stop();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        if(stopWatchService == null) return;
        try {
            boolean running = stopWatchService.start();
            if(running){
                onRunTimer();
                startSound(1.8f, null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stop(){
        stopSound();
        if(stopWatchService == null) return;
        try {
            stopWatchService.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void recordTime()
    {
        if(stopWatchService == null) return;
        try {
            stopWatchService.recordTime();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String[] getLapTimes()
    {
        if(stopWatchService == null) return null;
        try {
            return stopWatchService.getLapTimes();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }


    //计时Timer
    void onRunTimer(){
        //HiLog.info(LABEL, "AnalogStopWatch.onRunTimer!");
        final long delayTime = 100L;  //延时时间，毫秒单位
        millisecond = getMilliseconds();
        invalidate();  //更新画面表示
        if(runnable && isRunning()) {
            //如果处于运行状态，触发下一次延时执行
            TaskDispatcher uiTaskDispatcher = mContext.getUITaskDispatcher();
            Revocable revocable = uiTaskDispatcher.delayDispatch(new Runnable() {
                @Override
                public void run() {
                    onRunTimer();
                }
            }, delayTime);
        }
    }

    //归零
    public void reset(){
        if(!isRunning() && getMilliseconds() > 0) {
            startSound(2.0f, ()->onResetTimer());
        }
    }

    //归零处理
    void onResetTimer(){
        final long delayTime = 50L;
        long second_value = millisecond / 1000 % 60;
        long minute_value = millisecond / 1000 / 60;
        if(second_value > 0){
            millisecond -= 1000;   //秒针反转
        }
        if(minute_value > 0){
            millisecond -= 1000 * 60;  //分针反转
        }
        if(second_value > 0 || minute_value >0) {
            //如果反转未到位，触发下次延时处理
            TaskDispatcher uiTaskDispatcher = mContext.getUITaskDispatcher();
            Revocable revocable = uiTaskDispatcher.delayDispatch(new Runnable() {
                @Override
                public void run() {
                    onResetTimer();
                }
            }, delayTime);
        }
        else{
            millisecond = 0;
            stopSound();
            try {
                stopWatchService.resetTime();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        invalidate();
    }

    @Override
    public void onDraw(Component component, Canvas canvas)
    {
        Paint paint = new Paint();
        RectFloat bound = getBoundRect();
        Point main_center = bound.getCenter();
        drawScale(canvas, paint, main_center, getRadius(), 1);
        drawScaleValue(canvas, paint, main_center, getRadius() * 0.8f,
                12, 5, (int)(getRadius() * 0.2f));

        Point center = new Point(main_center.getPointX(),
                                bound.top + getRadius() * 0.6f);
        float radius = getRadius() / 3;
        drawScale(canvas, paint, center, radius, 5);
        drawScaleValue(canvas, paint, center, radius * 0.7f,
                12, 5, (int)(radius * 0.25f));

        drawDigitalTime(canvas, paint, main_center.getPointX(),main_center.getPointY() + getRadius() * 0.4f);

        drawMinute(canvas, center, radius);
        drawSecond(canvas, bound);

    }

    //描画刻度线
    private void drawScale(Canvas canvas, Paint paint, Point center, float radius, int scale_interval){
        paint.setColor(Color.WHITE);
        float len5sec = radius / 8;
        float len1sec = radius / 12;
        float len02sec = radius / 20;
        paint.setColor(Color.BLACK);
        for(int i = 0; i < 300; i+= scale_interval){
            float insideRaduis = radius;
            if ((i % 25)==0){
                insideRaduis -= len5sec;
                paint.setStrokeWidth(radius / 60);
            }
            else if((i % 5)==0){
                insideRaduis -= len1sec;
                paint.setStrokeWidth(radius / 80);
            }
            else{
                insideRaduis -= len02sec;
                paint.setStrokeWidth(radius / 120);
            }
            drawRadius(canvas, paint, center, insideRaduis, radius, i * Math.PI / 150);
        }
    }

    //描画全部刻度值
    private void drawScaleValue(Canvas canvas, Paint paint, Point center, float radius, int count, int offset, int font_size){
        for(int i = 1; i <= count; i++){
            drawScaleText(canvas, paint, center, radius, Math.PI * 2 / count * i, offset * i, font_size);
        }
    }

    //描画秒针
    private void drawMinute(Canvas canvas, Point center, float radius){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(radius / 20);
        paint.setStrokeCap(Paint.StrokeCap.ROUND_CAP);
        drawRadius(canvas, paint, center, 0, radius * 0.9f, millisecond / 60 * Math.PI * 2 / 60000);
        float oval_radius = radius / 10;
        canvas.drawOval(new RectFloat(center.getPointX() - oval_radius, center.getPointY() - oval_radius,
                        center.getPointX() + oval_radius, center.getPointY() + oval_radius),
                paint);
    }

    //描画分针
    private void drawSecond(Canvas canvas, RectFloat bound){
        float radius = getRadius();
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(radius / 40);
        paint.setStrokeCap(Paint.StrokeCap.ROUND_CAP);
        drawRadius(canvas, paint, bound.getCenter(), 0, radius * 0.9f, millisecond * Math.PI * 2 / 60000);
        float oval_radius = radius / 20;
        Point center = bound.getCenter();
        canvas.drawOval(new RectFloat(center.getPointX() - oval_radius, center.getPointY() - oval_radius,
                        center.getPointX() + oval_radius, center.getPointY() + oval_radius),
                paint);
    }

    //描画径向直线
    private void drawRadius(Canvas canvas, Paint paint, Point center, float from, float to, double angle){
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        canvas.drawLine(new Point(center.getPointX() + (float)(from * sin),
                        center.getPointY() - (float)(from * cos)),
                        new Point(center.getPointX() + (float)(to * sin),
                        center.getPointY() - (float)(to * cos)),
                        paint);
    }

    //描画单一刻度值
    private void drawScaleText(Canvas canvas, Paint paint, Point center, float radius, double angle, int value, int font_size){
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        Font.Builder builder = new Font.Builder("Arial");
        builder.setWeight(Font.REGULAR);
        paint.setFont(builder.build());
        paint.setTextSize(font_size);
        String text = String.format("%d", value);
        float width = paint.measureText(text);      //计算字符串显示宽度
        canvas.drawText(paint, text,
                center.getPointX() + (float)(radius * sin) - width / 2,
                center.getPointY() - (float)(radius * cos) + font_size / 3);
    }

    //描画数字时间
    private void drawDigitalTime(Canvas canvas, Paint paint, float x, float y){
        Font.Builder builder = new Font.Builder("Arial");
        builder.setWeight(Font.REGULAR);
        paint.setFont(builder.build());
        int font_size = (int)(getRadius() / 4);
        paint.setColor(Color.BLUE);
        paint.setTextSize(font_size);
        String now = String.format("%02d:%02d:%02d",
                        millisecond/1000/3600,   //hour
                        millisecond/1000/60%60,  //minute
                        millisecond/1000%60);     //second
        float width = paint.measureText(now);           //计算字符串显示宽度
        canvas.drawText(paint, now, x - width / 2 , y);
    }

    //获取秒表显示区域
    private RectFloat getBoundRect(){
        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height);
        float x_padding = (width - size) / 2;
        float y_padding = (height - size) / 2;
        return new RectFloat(x_padding + padding, y_padding + padding,
                width - x_padding - padding, height - y_padding - padding);
    }

    interface OnPlayListener{
        void onPlay();
    }

    private void startSound(float speed, OnPlayListener listener) {
        if(playStatus == PlayStatus.STOP) {
            //实例化音频播放器对象
            soundPlayer = new SoundPlayer(AudioManager.AudioVolumeType.STREAM_MUSIC.getValue());
            // 指定音频资源加载并创建短音
            soundId = soundPlayer.createSound(getContext(), ResourceTable.Media_1tick);
            soundPlayer.setOnCreateCompleteListener((soundPlayer1, i, i1) -> {
                // 短音播放，设置音量、循环次数和播放速度
                taskId = soundPlayer.play(soundId);
                soundPlayer.setVolume(taskId, 1.0f);
                soundPlayer.setLoop(taskId, -1); // “-1”表示一直循环播放
                soundPlayer.setPlaySpeedRate(taskId, speed);
                if (listener != null) {
                    listener.onPlay();
                }
                playStatus = PlayStatus.PLAYING;
            });
        }
    }

    private void stopSound(){
        if (playStatus != PlayStatus.STOP) {
            //停止播放
            soundPlayer.stop(taskId);
            taskId = 0;
            // 释放短音资源
            soundPlayer.deleteSound(soundId);
            soundId = 0;
            // 释放播放器
            soundPlayer = null;
            playStatus = PlayStatus.STOP;
        }
    }

    public void pauseSound()
    {
        if(playStatus == PlayStatus.PLAYING){
            soundPlayer.pause(taskId);
            playStatus = PlayStatus.PSUSE;
        }
    }

    public void resumeSound(){
        if(playStatus == PlayStatus.PSUSE){
            soundPlayer.resume(taskId);
            playStatus = PlayStatus.PLAYING;
        }
        else if(playStatus == PlayStatus.STOP){
            if(isRunning()) {
                startSound(1.8f, null);
            }
        }
    }

    public boolean isPlaying(){
        return (taskId != 0);
    }

    //获取显示半径
    private float getRadius(){
        return getBoundRect().getWidth() / 2;
    }

    //获取圆心位置
    private Point getCenter(){
        return getBoundRect().getCenter();
    }

    //初始化
    private void Initialize(AttrSet attrSet){
        padding = 0;
        if(attrSet.getAttr("padding").isPresent()) {
            padding = attrSet.getAttr("padding").get().getDimensionValue();
        }
        addDrawTask(this);
        runnable = true;
    }
}
