package transformers;

import java.util.*;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
//import soot.util.*;
import soot.options.*;

public class DataPropagationAnalysis extends ForwardFlowAnalysis{
	
	FlowSet emptySet;

    Map<Unit, FlowSet> unitToDefineSet;
    Map<Unit, FlowSet> unitToUseSet;
    //List<Value> trackingSet;
    
    DataPropagationAnalysis(UnitGraph g){
    	
    	super(g);
    	
    	emptySet = new ArraySparseSet();

    	{
	    	unitToDefineSet = new HashMap<Unit, FlowSet>(g.size() * 2 + 1, 0.7f);
	    	Iterator unitIt = g.iterator();
	    	while(unitIt.hasNext()){
	    		Unit s = (Unit)unitIt.next();
	    		FlowSet defineSet = emptySet.clone();
	    		Iterator boxIt = s.getDefBoxes().iterator();
	    		while(boxIt.hasNext()){
	    			ValueBox box = (ValueBox)boxIt.next();
	    			if(box.getValue() instanceof Local){
	    				defineSet.add(box.getValue(), defineSet);
	    			}
	    		}
	    		unitToDefineSet.put(s, defineSet);
	    	}
    	}
    	
    	{
    		unitToUseSet = new HashMap<Unit, FlowSet>(g.size() * 2 + 1, 0.7f);
	    	Iterator unitIt = g.iterator();
	    	while(unitIt.hasNext()){
	    		Unit s = (Unit)unitIt.next();
	    		FlowSet useSet = emptySet.clone();
	    		Iterator boxIt = s.getUseBoxes().iterator();
	    		while(boxIt.hasNext()){
	    			ValueBox box = (ValueBox)boxIt.next();
	    			if(box.getValue() instanceof Local){
	    				useSet.add(box.getValue(), useSet);
	    			}
	    		}
	    		unitToUseSet.put(s, useSet);
	    	}
    	}
    	
        doAnalysis();
    }
    
    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    protected Object entryInitialFlow()
    {
        return emptySet.clone();
    }
    
    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;
        in.union(unitToUseSet.get(unit), out);
        out.difference(unitToDefineSet.get(unit), out);        
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.union(inSet2, outSet);
    }
    
    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }

}
