package transformers;

import soot.options.*;
import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

public class PaddleMain {

	private static SootClass loadClass(String name, boolean main) {
		SootClass sClass = Scene.v().loadClassAndSupport(name);
		sClass.setApplicationClass();
		if (main) {
			Scene.v().setMainClass(sClass);
		}
		return sClass;
	}
		 
	private static void main(String[] args) {
		Options.v().set_keep_line_number(true);
		Options.v().setPhaseOption("cg", "verbose:true");
		Options.v().set_full_resolver(true);
		Options.v().allow_phantom_refs();
		 
		loadClass("Test1",true);
		
		 
		Scene.v().loadNecessaryClasses();
		 
		
		CHATransformer.v().transform();
		SootClass a = Scene.v().getSootClass("Test1");
		SootMethod src = Scene.v().getMainClass().getMethodByName("main");
		CallGraph cg = Scene.v().getCallGraph();
		// setting options HashMap
		
		/*
		PaddleTransformer pt = new PaddleTransformer();
		PaddleOptions paddle_opt = new PaddleOptions(opt);
		pt.setup(paddle_opt);
		pt.solve(paddle_opt);
		*/
		//Verifying Scene.v().getContextSensitiveCallGraph();
		 
	}
}
