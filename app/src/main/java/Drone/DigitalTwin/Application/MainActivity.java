package Drone.DigitalTwin.Application;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View.OnTouchListener;

import com.example.ros_nodes.R;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosImageView;
import org.ros.android.view.RosTextView;
import org.ros.android.view.visualization.layer.LaserScanLayer;
import org.ros.android.view.visualization.layer.RobotLayer;
import org.ros.namespace.NameResolver;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
//The following class publish twist messages into the /cmd_vel topic

public class MainActivity extends RosActivity {
    private static final String TAG = "ROS_NODE_MAIN";
    private static final int MAKE_MAP_DIALOG_ID = 0;

    private NodeMainExecutor nodeMainExecutor;
    private NodeConfiguration nodeConfiguration;

    private RosImageView<sensor_msgs.CompressedImage> cameraView;
    private static final String cameraTopic = "/camera/color/image_raw/compressed";

    RelativeLayout layout_joystick;
    ImageView image_joystick, image_border;
    TextView textView1, textView2, textView3, textView4, textView5;

    JoyStickClass js;

    private RosTextView<std_msgs.String> rosTextView_x,rosTextView_y,rosTextView_z,rosTextView_ox,rosTextView_oy,rosTextView_oz,rosTextView_ow;
    private RosTextView<std_msgs.String> rosTextView_b;

    //private OccupancyGridLayer mapLayer = null;
    private LaserScanLayer laserScanLayer = null;
    private RobotLayer robotLayer = null;
    private Talker talker;
    private ZTalker ztalker;
    private Listener listener;
    private ProgressBar progress;
    public MainActivity(){

        super("Ros-Node-Notfication-ticker","ROS-Nodes-Notification-Title");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        cameraView = (RosImageView<sensor_msgs.CompressedImage>) findViewById(R.id.image);
        cameraView.setTopicName(cameraTopic);
        cameraView.setMessageType(sensor_msgs.CompressedImage._TYPE);
        cameraView.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        Switch arm_switch = (Switch)findViewById(R.id.arm_switch);
        arm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Talker.cmd = "ARM";
                }
                else {
                    Talker.cmd = "ARM_OFF";
                }
            }
        });

        Switch offboard_switch = (Switch)findViewById(R.id.offboard_switch);
        offboard_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Talker.cmd = "TO";
                }
                else {
                    Talker.cmd = "TO_OFF";
                }
            }
        });



        SeekBar sb = (SeekBar)findViewById(R.id.height);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ZTalker.cmd = Integer.toString(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        rosTextView_x = (RosTextView<std_msgs.String>) findViewById(R.id.text_x);
        rosTextView_x.setTopicName("/drone/position/x");
        rosTextView_x.setMessageType(std_msgs.String._TYPE);
        rosTextView_x.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });

        rosTextView_y = (RosTextView<std_msgs.String>) findViewById(R.id.text_y);
        rosTextView_y.setTopicName("/drone/position/y");
        rosTextView_y.setMessageType(std_msgs.String._TYPE);
        rosTextView_y.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });

        rosTextView_z = (RosTextView<std_msgs.String>) findViewById(R.id.text_z);
        rosTextView_z.setTopicName("/drone/position/z");
        rosTextView_z.setMessageType(std_msgs.String._TYPE);
        rosTextView_z.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });
        rosTextView_ox = (RosTextView<std_msgs.String>) findViewById(R.id.text_ox);
        rosTextView_ox.setTopicName("/drone/position/ox");
        rosTextView_ox.setMessageType(std_msgs.String._TYPE);
        rosTextView_ox.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });
        rosTextView_oy = (RosTextView<std_msgs.String>) findViewById(R.id.text_oy);
        rosTextView_oy.setTopicName("/drone/position/oy");
        rosTextView_oy.setMessageType(std_msgs.String._TYPE);
        rosTextView_oy.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });
        rosTextView_oz = (RosTextView<std_msgs.String>) findViewById(R.id.text_oz);
        rosTextView_oz.setTopicName("/drone/position/oz");
        rosTextView_oz.setMessageType(std_msgs.String._TYPE);
        rosTextView_oz.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {

                return message.getData();
            }
        });
        rosTextView_ow = (RosTextView<std_msgs.String>) findViewById(R.id.text_ow);
        rosTextView_ow.setTopicName("/drone/position/ow");
        rosTextView_ow.setMessageType(std_msgs.String._TYPE);
        rosTextView_ow.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                return message.getData();
            }
        });
        progress = (ProgressBar) findViewById(R.id.progress);
        rosTextView_b = (RosTextView<std_msgs.String>) findViewById(R.id.text);
        rosTextView_b.setTopicName("/drone/battery");
        rosTextView_b.setMessageType(std_msgs.String._TYPE);
        rosTextView_b.setMessageToStringCallable(new MessageCallable<String, std_msgs.String>() {
            @Override
            public String call(std_msgs.String message) {
                int value = Integer.parseInt(message.getData());
                progress.setProgress(value);
                return message.getData();
            }
        });



        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.rec);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);



        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    int direction = js.get8Direction();
                    if(direction == JoyStickClass.STICK_UP) {
                        Talker.cmd = "GO";
                    } else if(direction == JoyStickClass.STICK_UPRIGHT) {
                        Talker.cmd = "GRIGHT";
                    } else if(direction == JoyStickClass.STICK_RIGHT) {
                        Talker.cmd = "RIGHT";
                    } else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
                        Talker.cmd = "BRIGHT";
                    } else if(direction == JoyStickClass.STICK_DOWN) {
                        Talker.cmd = "BACK";
                    } else if(direction == JoyStickClass.STICK_DOWNLEFT) {
                        Talker.cmd = "BLEFT";
                    } else if(direction == JoyStickClass.STICK_LEFT) {
                        Talker.cmd = "LEFT";
                    } else if(direction == JoyStickClass.STICK_UPLEFT) {
                        Talker.cmd = "GLEFT";
                    } else if(direction == JoyStickClass.STICK_NONE) {
                        Talker.cmd = "STOP";
                    }
                } else{
                    Talker.cmd = "STOP";
                }
                return true;
            }
        });


    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
//        super.init(nodeMainExecutor);
        talker = new Talker();
        nodeConfiguration = NodeConfiguration.newPublic(getRosHostname(),getMasterUri());
        nodeConfiguration.setNodeName("talker");
        nodeMainExecutor.execute(talker, nodeConfiguration);

        ztalker = new ZTalker();
        nodeConfiguration = NodeConfiguration.newPublic(getRosHostname(),getMasterUri());
        nodeConfiguration.setNodeName("ztalker");
        nodeMainExecutor.execute(ztalker, nodeConfiguration);

        listener = new Listener();
        nodeConfiguration.setNodeName("listener");
        nodeMainExecutor.execute(listener, nodeConfiguration);

//        this.nodeMainExecutor = nodeMainExecutor;

        NameResolver appNameResolver = nodeConfiguration.getParentResolver();
        nodeMainExecutor.execute(rosTextView_x,nodeConfiguration.setNodeName("android/textView_x"));
        nodeMainExecutor.execute(rosTextView_y,nodeConfiguration.setNodeName("android/textView_y"));
        nodeMainExecutor.execute(rosTextView_z,nodeConfiguration.setNodeName("android/textView_z"));
        nodeMainExecutor.execute(rosTextView_ox,nodeConfiguration.setNodeName("android/textView_ox"));
        nodeMainExecutor.execute(rosTextView_oy,nodeConfiguration.setNodeName("android/textView_oy"));
        nodeMainExecutor.execute(rosTextView_oz,nodeConfiguration.setNodeName("android/textView_oz"));
        nodeMainExecutor.execute(rosTextView_ow,nodeConfiguration.setNodeName("android/textView_ow"));
        nodeMainExecutor.execute(rosTextView_b,nodeConfiguration.setNodeName("android/textView_b"));


        nodeMainExecutor.execute(cameraView, nodeConfiguration.setNodeName("android/camera_view"));


        //cameraView.setTopicName(getAppNameSpace().resolve(cameraTopic).toString());
        //nodeMainExecutor.execute(cameraView, nodeConfiguration.setNodeName("android/camera_view"));
    }
}