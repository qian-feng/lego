package mysoot;

public class Test {

	public static void main(String[] args) {
		String str = "((@this: LineFollower).<LineFollower: lejos.robotics.EncoderMotor r>.<lejos.robotics.EncoderMotor: void setPower(int)>(((int) (40.0 - (400.0 * (0.2 - ((double) ((newarray (float)[1])[0])))))))";
		int numOfL = 0;
		int numOfR = 0;
		for(int i=0;i<str.length();i++){
			if(str.charAt(i)=='(') numOfL++;
			else if(str.charAt(i)==')')  numOfR++;
		}
		System.out.println(numOfL +"  "+numOfR);
	}

}
