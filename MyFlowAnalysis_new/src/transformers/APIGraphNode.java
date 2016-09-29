package transformers;


import soot.*;
import soot.jimple.*;

import java.util.*;

public class APIGraphNode {

	private Stmt stmt;
	private Stmt callsite;
	private String annotation;
	private Vector<APIGraphNode> successors;
	private Vector<APIGraphNode> predecessors;
	private Vector<Predecessor> preds;
	private APIGraphNode immediateDom;
	private SootMethod hostMethod;

	
	public APIGraphNode(Stmt stmt){
		this.stmt = stmt;
		this.callsite = null;
		this.annotation = "";
		this.successors = new Vector<APIGraphNode>();
		this.predecessors = new Vector<APIGraphNode>();
		this.preds = new Vector<Predecessor>();
		this.hostMethod = null;
	}
	
	public APIGraphNode(Stmt stmt, SootMethod host){
		this.stmt = stmt;
		this.callsite = null;
		this.annotation = "";
		this.successors = new Vector<APIGraphNode>();
		this.predecessors = new Vector<APIGraphNode>();
		this.preds = new Vector<Predecessor>();
		this.hostMethod = host;
	}
	

	
	
	public boolean equal(APIGraphNode node)
	{
		if(node == null)
			return false;
		
		if(!this.stmt.toString().equals(node.getStmt().toString()))
			return false;
			
		if(this.callsite == null)
		{
			if(node.callsite == null)
				return true;
			else
				return false;
		}
		
		if(node.callsite == null)
			return false;
		
		return this.callsite.toString().equals(node.callsite.toString());
	}
	
	public APIGraphNode clone(){
		APIGraphNode clone = new APIGraphNode((Stmt)this.getStmt());
		if(this.callsite == null){
			clone.callsite = null;
		}else{
			clone.callsite = (Stmt)this.callsite;
		}
		clone.annotation = this.annotation;
		clone.hostMethod = this.hostMethod;
				
		return clone;
	}
	
		
	public Stmt getStmt(){
		return this.stmt;
	}
	
	public Stmt getCallsite(){
		return this.callsite;
	}
	
	public String getAnnotation(){
		return this.annotation;
	}
	
	public SootMethod getHostMethod(){
		return this.hostMethod;
	}
	
	public Vector<APIGraphNode> getSuccessors(){
		return this.successors;
	}
	
	public Vector<APIGraphNode> getPredecessors(){
		return this.predecessors;
	}
	
	
	public void setCallsite(Stmt callsite){
		this.callsite = callsite;
	}
	
	public void setAnnotation(String annotation){
		this.annotation = annotation;
	}

	public boolean hasSucc(APIGraphNode succ){
		return this.successors.contains(succ);
	}
	
	public boolean hasSuccRecursive(APIGraphNode succ, APIGraphNode parent)
	{
		//this LinkedList stores all the successors
		LinkedList<APIGraphNode> succAll = new LinkedList<APIGraphNode>();
		//this LinkedList stores all the successors in queue
		LinkedList<APIGraphNode> succs = new LinkedList<APIGraphNode>();

		succs.addAll(this.successors);
		succAll.addAll(succs);
		
		while(!succs.isEmpty())
		{
			APIGraphNode currNode = succs.remove();
			
			if(currNode.equal(parent))
				return false;
			
			if(currNode.equal(succ))
				return true;
			
			//This linkedList stores all the predecessors of current node 'currNode'		
			LinkedList<APIGraphNode> currSuccs = new LinkedList<APIGraphNode>();		
			currSuccs.addAll(currNode.getSuccessors());
			
			if(currSuccs == null || currSuccs.isEmpty())
				continue;
			
			for (APIGraphNode n : currSuccs)
			{
				if(!succAll.contains(n))
				{
					succs.add(n);
					succAll.add(n);			
				}
			}	
		}//end of while	
		return false;
	}
	
	
	public void addSucc(APIGraphNode succ){
		if(!hasSucc(succ)){
			this.successors.add(succ);
		}
	}
	
	public void removeSucc(APIGraphNode succ){
		if(hasSucc(succ)){
			this.successors.remove(succ);
		}
	}
	
	public void clearSucc()
	{
		this.successors.clear();
	}
	
	public boolean hasPred(APIGraphNode pred){
		return this.predecessors.contains(pred);
	}
	
	public void addPred(APIGraphNode pred){
		if(!hasPred(pred)){
			this.predecessors.add(pred);
			Predecessor predc = new Predecessor(pred);
			predc.updateCondValue(this.stmt);
			this.preds.add(predc);
		}
	}
	
	public void removePred(APIGraphNode pred){
		if(hasPred(pred)){
			this.predecessors.remove(pred);
			this.preds.remove(getPred(pred));
		}
	}
	
	public void clearPred()
	{
		this.predecessors.clear();
		this.preds.clear();
	}
	
	public void removeDom(){
			this.immediateDom = null;
	}
	
	public void setImmediateDom(APIGraphNode dom)
	{
		this.immediateDom = dom;
	}
	
	public APIGraphNode getImmediateDom()
	{
		return this.immediateDom;
	}
	
	public void resetPreds(Vector<APIGraphNode> preds){
		this.predecessors.clear();
		this.predecessors.addAll(preds);
	}
	
	public void resetSuccs(Vector<APIGraphNode> succs){
		this.successors.clear();
		this.successors.addAll(succs);
	}
	
	public Vector<Predecessor> getPreds(){
		return this.preds;
	}
	
	public boolean hasPred(Predecessor pred){
		return this.preds.contains(pred);
	}
	
	public void addPred(Predecessor pred){
		if(!hasPred(pred)){
			this.preds.add(pred);
		}
	}
	
	public Predecessor getPred(APIGraphNode node)
	{
		for(Predecessor pred: this.preds)
		{
			if(pred.pred_node==node)
				return pred;
		}
		return null;
	}
	
	public void removePred(Predecessor pred){
		if(hasPred(pred)){
			this.preds.remove(pred);
		}
	}
	
	public void clearPreds()
	{
		this.preds.clear();
	}

}


