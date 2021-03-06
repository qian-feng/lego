package transformers;

import java.util.*;
import soot.jimple.*;
import soot.*;
import soot.Body;
import soot.BodyTransformer;
import soot.SootClass;
import soot.SootMethod;

//just a manual one, for now
public class LeakContactsTransformer extends BodyTransformer {

	private List<String> m_Classes;
		
	private LinkedHashMap<String, String> mClassToMethod;

	public LeakContactsTransformer() {
		m_Classes = new ArrayList<String>();
		
		mClassToMethod  = new LinkedHashMap<String, String>();
	}

	@Override
	protected void internalTransform(Body body, String string, Map map) {
		
		SootMethod method = body.getMethod();
		
		Iterator iter = body.getUnits().iterator();
		while(iter.hasNext()){
			Stmt s = (Stmt)iter.next();
			if(s instanceof DefinitionStmt){
				Value rhs = ((DefinitionStmt) s).getRightOp();
				if(rhs instanceof InvokeExpr){
					//calleridfaker
					if(((InvokeExpr) rhs).getMethod().getSignature().equals("<android.content.Intent: android.net.Uri getData()>")){
					/*
					//monkeyjump
					if(((InvokeExpr) rhs).getMethod().getSignature().equals(
							"<android.content.ContentResolver: android.database.Cursor query(android.net.Uri,java.lang.String[],java.lang.String,java.lang.String[],java.lang.String)>")){
					*/
						SootClass soot_class = method.getDeclaringClass();
						
						//System.out.println(method + " contains <android.content.Intent: android.net.Uri getData()>");
						//System.out.println("method's signature: " + method.getSubSignature());
						//System.out.println("class name: " + soot_class.getName());
						
						//calleridfaker
						if(method.getSubSignature().equals("void onActivityResult(int,int,android.content.Intent)") 
								&& soot_class.getName().equals("net.bsdtelecom.calleridfaker.Majicall")){
						
						
						
						/*
						//monkeyjump
						if((method.getSubSignature().equals("boolean b(java.lang.String)") || method.getSubSignature().equals("java.util.Vector a()"))
								&& soot_class.getName().equals("com.dseffects.MonkeyJump2.jump2.e.c")){
						*/
							System.out.println("adding source class: " + soot_class.getName());
							m_Classes.add(soot_class.getName());
							mClassToMethod.put(soot_class.getName()+"|"+method.getSignature(),
									method.getSubSignature());
						}
					}
				}
				//if source is location, also need to consider this case. To be fixed later.
				/*
				 * 	public void onLocationChanged(android.location.Location)
    				{
        				com.flurry.android.FlurryAgent r0;
        				android.location.Location r1;
        				java.lang.Throwable $r2, $r4;

        				r0 := @this: com.flurry.android.FlurryAgent;
        				r1 := @parameter0: android.location.Location;
        				entermonitor r0;

     					label0:
        					r0.<com.flurry.android.FlurryAgent: android.location.Location D> = r1;
        				label1:
        				exitmonitor r0;
        				return;
        			}
				 */
			}
		}
		
		/*
		if (method.getName().equals(MyConstants.mainEntry) || method.getName().equals(MyConstants.onCreateEntry)
			|| method.getName().equals(MyConstants.threadEntry)) {
			System.out.println(method.toString());
			SootClass soot_class = method.getDeclaringClass();
			System.out.println("adding class: " + soot_class.getName());
			m_Classes.add(soot_class.getName());
			mClassToMethod.put(soot_class.getName(), method.getName());
		}
		*/
	}

	public List<String> getClasses() {
		return m_Classes;
	}
		
	public LinkedHashMap<String, String> getClassToMethod(){
		return this.mClassToMethod;
	}
}