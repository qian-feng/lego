package transformers;

import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.util.*;

public class FindUncalledOverridingMethodsTransformer extends BodyTransformer{
	
	private List<String> uncalledOverridingFrameworkMethods;
	private LinkedHashMap<String, String> mClassToMethod;
	private List<String> appUncalledMethods;
	
	public FindUncalledOverridingMethodsTransformer(List<String> appUncalledMethods){
		uncalledOverridingFrameworkMethods = new ArrayList<String>();
		mClassToMethod = new LinkedHashMap<String, String>();
		this.appUncalledMethods = appUncalledMethods;
	}

	protected void internalTransform(Body body, String string, Map map) {
		SootMethod method = body.getMethod();
		//if(!(method.getDeclaringClass().getMethods().contains(o))) return;
		
		if(appUncalledMethods.contains(method.getSignature())){
			//System.out.println("IN\n");
			SootClass clazz = method.getDeclaringClass();
			//System.out.println(method+" "+clazz+" "+clazz.getInterfaces());
			List<SootClass> interfazes = new ArrayList<SootClass>();
			if(clazz.getInterfaceCount()>0){
				interfazes.addAll(clazz.getInterfaces());
			}
					
			if(clazz.hasSuperclass()){
				while(clazz.hasSuperclass() && clazz.isApplicationClass()){					
					clazz = clazz.getSuperclass();
					if(clazz.getInterfaceCount()>0){
						for(SootClass interfaze : clazz.getInterfaces()){
							if(!interfazes.contains(interfaze)){
								interfazes.add(interfaze);
							}
						}
					}
				}
				//System.out.println("uncalled method's super clazz: " + clazz);
				if(!clazz.isApplicationClass()){
					//System.out.println("isn't application "+method.getSubSignature());

					if(clazz.declaresMethod(method.getSubSignature())){
						if(!uncalledOverridingFrameworkMethods.contains(method.getSignature())){
							//System.out.println("ADDING");
							uncalledOverridingFrameworkMethods.add(method.getSignature());
							mClassToMethod.put(method.getDeclaringClass().getName()+ "|" +method.getSignature(), method.getSubSignature());
						}
					}
				}
			}
			if(interfazes.size()>0){
				for(SootClass interfaze : interfazes){
					if(!interfaze.isApplicationClass()){
						//System.out.println("isn't application "+method.getSubSignature());

						if(interfaze.declaresMethod(method.getSubSignature())){
							if(!uncalledOverridingFrameworkMethods.contains(method.getSignature())){
								//System.out.println("ADDING");
								uncalledOverridingFrameworkMethods.add(method.getSignature());
								mClassToMethod.put(method.getDeclaringClass().getName()+ "|" +method.getSignature(), method.getSubSignature());
							}
						}
					}
				}
			}
		}
	}
	
	public List<String> getUncalledOverridingFrameworkMethods(){
		return this.uncalledOverridingFrameworkMethods;
	}
	
	public LinkedHashMap<String, String> getClassToMethod(){
		return this.mClassToMethod;
	}
	
	private boolean isThread(SootClass clazz){
		
		while(clazz.hasSuperclass() && clazz.isApplicationClass()){
			
			for(SootClass interfaze : clazz.getInterfaces()){
				if(interfaze.getName().equals("java.lang.Runnable")){
					return true;
				}
			}
			
			clazz = clazz.getSuperclass();
						
		}
		
		for(SootClass interfaze : clazz.getInterfaces()){
			if(interfaze.getName().equals("java.lang.Runnable")){
				return true;
			}
		}
				
		return false;
	}
}