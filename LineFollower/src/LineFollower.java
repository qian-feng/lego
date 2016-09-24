import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.EncoderMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

//In order to make two EV3s cooperate while following the line, I planned to use one color sensor for detecting the black Line. 
//Instead of comparing values of two color sensors to see which one is closer to black line, one color sensor is used, and originally
//set above the edge of black line, the robot is following the line edge instead.
//Note: make sure color sensor is set above the right edge of line, no matter the robot is moving clockwise, or counter-clockwise.
public class LineFollower{
	private EV3 brick;
	private EV3ColorSensor cSensor;
	private EncoderMotor l;
	private EncoderMotor r; 
	public static void main(String[] args) {
		LineFollower follower = new LineFollower(LocalEV3.get(), "A", "D", "S2");
		follower.go();
	}
	public LineFollower(EV3 pBrick, String lPort, String rPort, String cPort) {
		super();
		brick = pBrick;
		brick.getKey("Escape").addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(Key k) {}
			@Override
			public void keyReleased(Key k) {
				System.exit(0);				
			}
		});
		l = new UnregulatedMotor(MotorPort.A);
		r = new UnregulatedMotor(MotorPort.D);
        cSensor = new EV3ColorSensor(brick.getPort(cPort));
	}
	
	private void go(){
		//length unit is inch, angle unite is degree, counterclockwise is positive, clockwise is negative;
		float[] sample1 = new float[1];//color sensor 
		float[] sample2 = new float[1];//gyro sensor
		SampleProvider getRed = cSensor.getRedMode();
		//SampleProvider getAngle = gyroSensor.getAngleMode();

		Sound.beepSequenceUp();
		double time = System.currentTimeMillis();
		//double diff = 0;
			//if(System.currentTimeMillis()-time>100000) break;
		//System.currentTimeMillis()-time<10000
			while(System.currentTimeMillis()-time<10000){
				getRed.fetchSample(sample1, 0);
				//control EV3 by setting different power for each motor
				if(sample1[0]>0.20){
					turnL(sample1[0]);
				}
				else{
					turnR(sample1[0]);
				}		
			}
		}
	
	public void turnL(double sampleValue){
		sampleValue = sampleValue - 0.2;
		 //turn leftz
		 r.setPower(40);
		 l.setPower((int) (40-400*sampleValue));
		 Delay.msDelay(10);
	}
	public void turnR(double sampleValue){
		sampleValue = 0.20 - sampleValue;
		 //turn right
		 r.setPower((int) (40-400*sampleValue));
		 l.setPower(40);
		 Delay.msDelay(10);
	}
}