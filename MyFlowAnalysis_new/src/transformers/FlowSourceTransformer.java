package transformers;

import java.util.*;

import mysoot.AnalyzerMain;
import soot.jimple.*;
import soot.*;

/*
 * This transformer locates the declaring classes of methods where every source statement gets called
 */
public class FlowSourceTransformer extends BodyTransformer {

	private List<String> m_Classes;
		
	private LinkedHashMap<String, String> mClassToMethod;
	
	private List<String> packageFilter = new ArrayList<String>();

	public FlowSourceTransformer() {
		
		for(String adsPackage : MyConstants.AdLibs){
			packageFilter.add(adsPackage);
		}
		
		m_Classes = new ArrayList<String>();
		
		mClassToMethod  = new LinkedHashMap<String, String>();
	}
	
	private boolean isInFilter(SootClass clazz){
		for (String filter: packageFilter){
			if (clazz.toString().contains(filter)){
				return true;
			}
		}
		return false;
	}

	@Override
	protected void internalTransform(Body body, String string, Map map) {
		
		SootMethod method = body.getMethod();
		
		if(method.getSubSignature().equals(AnalyzerMain.INFO_SOURCE)){
			SootClass soot_class = method.getDeclaringClass();
			if(!soot_class.isPhantom()){
				if(!isInFilter(soot_class)){
					if(MyConstants.DEBUG_INFO)
						System.out.println("adding source class: " + soot_class.getName());
					m_Classes.add(soot_class.getName());
					mClassToMethod.put(soot_class.getName()+"|"+method.getSignature(),
							method.getSubSignature());
				}
			}
		}
				
		Iterator iter = body.getUnits().iterator();
		
		// For every unit in the body
		while(iter.hasNext()){
			Stmt s = (Stmt)iter.next();			
			if(s instanceof DefinitionStmt){
				Value rhs = ((DefinitionStmt) s).getRightOp();
				if(rhs instanceof InvokeExpr){
					// INFO_SOURCE is the current source statement
					if(((InvokeExpr) rhs).getMethod().getSignature().equals(AnalyzerMain.INFO_SOURCE)){
						SootClass soot_class = method.getDeclaringClass();
						if(!soot_class.isPhantom()){
							if(!isInFilter(soot_class)){
								if(MyConstants.DEBUG_INFO)
									System.out.println("adding source class: " + soot_class.getName());
								m_Classes.add(soot_class.getName());
								mClassToMethod.put(soot_class.getName()+"|"+method.getSignature(),
										method.getSubSignature());
							}
						}
					}
				}else if(rhs instanceof InstanceFieldRef){
					if(((InstanceFieldRef) rhs).getField().getSignature().equals(AnalyzerMain.INFO_SOURCE)){
						if(MyConstants.DEBUG_INFO)
							System.out.println("[SOURCE] " + ((InstanceFieldRef) rhs).getField().getSignature());
						SootClass soot_class = method.getDeclaringClass();
						if(!soot_class.isPhantom()){
							if(!isInFilter(soot_class)){
								if(MyConstants.DEBUG_INFO)
									System.out.println("adding source class: " + soot_class.getName());
								m_Classes.add(soot_class.getName());
								mClassToMethod.put(soot_class.getName()+"|"+method.getSignature(),
										method.getSubSignature());
							}
						}
					}
				}
			}else if(s instanceof InvokeStmt){
				// INFO_SOURCE is the current source statement
				if((s.getInvokeExpr()).getMethod().getSignature().equals(AnalyzerMain.INFO_SOURCE)){
					SootClass soot_class = method.getDeclaringClass();
					if(!soot_class.isPhantom()){
						if(!isInFilter(soot_class)){
							System.out.println("adding source class: " + soot_class.getName());
							m_Classes.add(soot_class.getName());
							mClassToMethod.put(soot_class.getName()+"|"+method.getSignature(),
									method.getSubSignature());
						}
					}
				}
			}
		}
	}
	
	private boolean equalsMethod(String sig1, String sig2){
		if(sig1.equals(sig2)){
			return true;
		}
		return false;
	}

	public List<String> getClasses() {
		return m_Classes;
	}
		
	public LinkedHashMap<String, String> getClassToMethod(){
		return this.mClassToMethod;
	}
}