package restart.com.countdowntime;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * 弱引用 Handler 防止内存泄漏
 * 弱引用：
 *      相较于弱引用 强引用的对象，垃圾回收机制绝不会回收它  当内存不足时，
 *      jvm宁愿抛出OutOfMemoryError的错误使进程停止，也不会随意回收强引用的对象
 *      来解决内存不足的问题
 *     而弱引用，就好像是可有可无的生活用品，一旦gc发现了具有弱引用的对象，不管内存
 *     是否足够，都会回收它的内存（不过，由于垃圾回收器是一个优先级很低的线程，
 *     因此不一定会马上发现那些只具有弱引用的对象）
 *     例子
 *     Student student = new Student();
 *     WeakReference<Student> weakStudent = new WeakReference<Student>(student);
 *     引用使用：
 *     weakStudent.get();
 *     如果该方法返回为空，则说明对象已经被回收
 *     因此 我们常在内部类的Handler中使用Activity弱引用，防止内存泄漏
 */
public class MainActivity extends AppCompatActivity {

    public static final int COUNTDOWN_TIME_CODE = 1001;//handler 倒计时标识
    public static final int DELAY_MILLIS = 1000;//倒计时间隔
    public static final int MAX_COUNT = 10;//倒计时最大值
    private TextView tv;//显示控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);//获得控件
        /*
        * 得到一个静态的Handler
        * */
        CountdownTimeHandler handler = new CountdownTimeHandler(this);
        //新建一个message
        Message message = Message.obtain();
        message.what = COUNTDOWN_TIME_CODE;
        message.arg1 = MAX_COUNT;
//        第一次发送message
        handler.sendMessageDelayed(message, DELAY_MILLIS);
    }

    public static class CountdownTimeHandler extends Handler {
        public static final int MIN_COUNT = 0;
        //弱引用
        final WeakReference<MainActivity> weakReference;

        CountdownTimeHandler(MainActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();

            switch (msg.what) {
                case COUNTDOWN_TIME_CODE:
                    int value = msg.arg1;
                    activity.tv.setText(String.valueOf(value--));
                    if (value > MIN_COUNT) {
                        Message message = Message.obtain();
                        message.what = COUNTDOWN_TIME_CODE;
                        message.arg1 = value;
                        sendMessageDelayed(message, DELAY_MILLIS);
                    }
                    break;
            }
        }
    }
}
