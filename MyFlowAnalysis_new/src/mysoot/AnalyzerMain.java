package mysoot;
import soot.Type;
import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;
import soot.G;
import soot.Local;
import soot.PackManager;
import soot.PointsToAnalysis;
import soot.PrimType;
import soot.RefLikeType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;
import transformers.APIGraph;
import transformers.APIGraphNode;
import transformers.Predecessor;
import transformers.AndroidSourceSinkSummary;
import transformers.FindEntryPointsTransformer;
import transformers.FindSourcesTransformer;
import transformers.FindUncalledMethodsTransformer;
import transformers.FindUncalledOverridingMethodsTransformer;
import transformers.FlowSinkTransformer;
import transformers.FlowSourceTransformer;
import transformers.GlobalAPISubGraphTransformer;
import transformers.GlobalBackwardDataflowAnalysis;
import transformers.GlobalForwardDataflowAnalysis;
import transformers.Log;
import transformers.MyConstants;
import transformers.OnCreateTransformer;
import transformers.PackageNameTransformer;
import transformers.PointsToAnalysisTransformer;
import transformers.RecursionDetectionTransformer;
import transformers.SensitiveAPICategory;
import transformers.SplitTag;
import transformers.TaintTag;
import soot.jimple.LookupSwitchStmt;

//this version can run interprocedual dataflow analysis, after configuring sources and sinks correctly. Need to test it in more complex lejos program.
//To see if this works properly in complex function call situation...

public class AnalyzerMain {
	public static JimpleBody mBody;
	public static Chain<SootClass> classes;
	public static String LOG_DOT;
	public static String CLASSPATH;
	public static String DEX_FULLPATH;
	public static String OUTPUT;
	public static String LIB_DIR;
	public static String API_PERMISSION_MAP;
	public static String API_PERMISSION_MAP_PSCOUT;
	public static String LOG;
	public static String API_LOCAL_LOG;
	public static String API_LOCAL_DOT;
	public static String API_GLOBAL_LOG;
	public static String API_GLOBAL_DOT;
	public static String DDG_LOCAL_DOT;
	public static String DDG_GLOBAL_DOT;
	public static String DDG_GLOBAL_SUCCINCT_DOT;
	public static String DDG_GLOBAL_CXL;
	public static String DDG_GLOBAL_GXL_DIR;
	public static String VPT_CXL_1;
	public static String VPT_CXL_2;
	public static String MAL_FEATURE_LOG;
	public static String BENIGN_FEATURE_LOG;
	public static String ACTIVITY_LOG;
	public static String GSPAN_LOG;
	public static String SENSITIVEAPIDUMP_DOT;
	public static String INDEXTOSIG;
	public static String CONF;
	public static String MANIFEST_FILE;
	public static String GRAPH_MATCHING_TOOLKIT_DIR;
	public static String OUTPUT_DIR;
	public static String VULNERABILITY_TEST_RESULT;
	public static String COMMON_LOGGING;
	public static String COMMON_CODEC;
	public static String HTTP_CLIENT;
	public static String HTTP_CORE;
	public static String JAVA_AWT;
	public static String JODA_TIME;
	public static String JODA_CONVERT;
	public static String XML_PARSER_APIS;
	public static String XML_APIS;
	public static String SIGNPOST_CORE;
	public static String SIGNPOST_COMMONSHTTP;
	public static String JSR305;
	public static String SLF4J_API;
	public static String LOG4J;
	public static String AMAZON_MAPS;
	public static String AMAZON_DEVICE_MESSAGING;
	public static String LIB_PHONE_NUMBER;
	public static String JAVAX_SERVLET;
	public static String ROME;
	public static String JDOM;
	public static String AD_SDK;
	public static String ANDROID_SUPPORT_V4;
	public static String GOOGLE_PLAY_SERVICES;
	public static String SNAPPY_JAVA;
	public static String ANDROID_21;
	public static String ANDROID_20;
	public static String ANDROID_19;
	public static String ANDROID_18;
	public static String ANDROID_17;
	public static String ANDROID_16;
	public static String ANDROID_15;
	public static String ANDROID_10;
	public static String ANDROID_8;
	public static String ANDROID_7;
	public static String ANDROID_4;
	public static String ANDROID_3;
	public static String GOOGLE_MAPS_21;
	public static String GOOGLE_MAPS_17;
	public static String GOOGLE_MAPS_16;
	public static String GOOGLE_MAPS_15;
	public static String GOOGLE_MAPS_10;
	public static String GOOGLE_MAPS_8;
	public static String GOOGLE_MAPS_7;
	public static String GOOGLE_MAPS_4;
	public static String GOOGLE_MAPS_3;
	public static String GOOGLE_ANALYTICS;
	public static String GOOGLE_ANALYTICS_V2;
	public static String GOOGLE_ADMOB_ADS;
	public static String INFO_SOURCE;
	public static String INFO_SINK;
	public static String APPNAME;
	public static int EFFECTIVE_GRAPH_SIZE_LIMIT = 3;
	public static List<APIGraphNode> apiGraphNode = new ArrayList<APIGraphNode>();
	public static FindUncalledMethodsTransformer findUncalledMethodsTransformer;
	public static FindUncalledOverridingMethodsTransformer findUncalledOverridingMethodsTransformer;
	public static FindEntryPointsTransformer findEntryPointsTransformer;
	//public static APISubGraphTransformer apiSubGraphTransformer;
	public static GlobalAPISubGraphTransformer globalAPISubGraphTransformer;
	public static GlobalBackwardDataflowAnalysis globalBackwardDataflowAnalysis;
	public static GlobalForwardDataflowAnalysis globalForwardDataflowAnalysis;
	private static FindSourcesTransformer findSourcesTransformer;
	private static FlowSourceTransformer flowSourceTransformer;
	private FlowSinkTransformer flowSinkTransformer;
	public static RecursionDetectionTransformer recursionDetectionTransformer;
	public static PointsToAnalysisTransformer pointsToAnalysisTransformer;
	private PackageNameTransformer pkgTransformer;
	public static LinkedHashMap<String, String> entryPoints;
	public static List<String> appUncalledMethods;
	public static List<String> appMethods;
	public static List<String> appCalledMethods;
	public static List<String> uncalledOverridingFrameworkMethods;
	private OnCreateTransformer m_OnCreateTransformer;
	public static LinkedHashMap<String, LinkedHashMap<String, String>> sourcesLocationMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
	public static LinkedHashMap<String, Integer> sources;
	public static LinkedHashMap<String, Integer> sinks;
	public static SootMethod main;
	public static boolean DEBUG_CONDITION = false;
	public static boolean dataFlowForCondition = true;
	//this is added for NLP, to locate conditional statement
	public static SootMethod SINK_METHOD;
	public static LinkedHashMap<APIGraphNode, String> conditionalStmtToSigMap = new LinkedHashMap<APIGraphNode, String>();
	//conditionForAPI: fist string is node signature, second LinkedList<String> is the conditions extracted from analysis
	public static LinkedHashMap<String, LinkedList<String>> conditionForAPI = new LinkedHashMap<String, LinkedList<String>>();
	public static LinkedHashMap<String, List<APIGraphNode>> conditionToDDGMap = new LinkedHashMap<String, List<APIGraphNode>>();
	// these two data structures are used to contain the merged graph info for
	// both forward and backward analysis
	// nodeStruct: first string is the node signature, second LinkedList<String>
	// is the attributes.
	// edgeStruct: first string is the source node signature, second string is
	// the destination node signature.
	// reversedEdgeStruct: first string is the destination node signature,
	// second string is the source node signature.
	public static LinkedHashMap<String, LinkedList<String>> nodeStruct = new LinkedHashMap<String, LinkedList<String>>();
	public static LinkedHashMap<String, LinkedList<String>> edgeStruct = new LinkedHashMap<String, LinkedList<String>>();
	public static LinkedHashMap<String, LinkedList<String>> reversedEdgeStruct = new LinkedHashMap<String, LinkedList<String>>();
	public static int API_LEVEL = 0;
	public static List<String> unloadedClasses = new ArrayList<String>();
	public static String currAnalysisNodeSig = "";
	
	public static List<List<Stmt>> paths = new ArrayList<List<Stmt>>();
	//public static String source = "<lejos.robotics.SampleProvider: void fetchSample(float[],int)>";
	//public static String sink = "<lejos.robotics.EncoderMotor: void setPower(int)>";
	public static String sink = "<test: boolean inBounds(int,int)>";
	public static String source = "<test: int get()>";
	
	public static void main(String[] args) {
		File directory = new File(".");
		String OUTPUT_DIR = "/Users/qian/workspace/lego/MyFlowAnalysis_new/sootoutput/";
		LOG = OUTPUT_DIR + "LOG.log";
		LOG_DOT = OUTPUT_DIR + "LOG_DOT.log";
		API_LOCAL_LOG = OUTPUT_DIR + "API_LOCAL_LOG.log";
		API_LOCAL_DOT = OUTPUT_DIR + "API_LOCAL_DOT.log";
		API_GLOBAL_LOG = OUTPUT_DIR + "API_GLOBAL_LOG.log";
		API_GLOBAL_DOT = OUTPUT_DIR + "API_GLOBAL_DOT.log";
		DDG_LOCAL_DOT = OUTPUT_DIR + "DDG_LOCAL_DOT.log";
		DDG_GLOBAL_DOT = OUTPUT_DIR + "DDG_GLOBAL_DOT.log";
		DDG_GLOBAL_SUCCINCT_DOT = OUTPUT_DIR + "DDG_GLOBAL_SUCCINCT_DOT.log";
		DDG_GLOBAL_CXL = OUTPUT_DIR + "DDG_GLOBAL_CXL.log";
		VPT_CXL_1 = OUTPUT_DIR + "test.vpt.1.cxl";
		VPT_CXL_2 = OUTPUT_DIR + "test.vpt.2.cxl";
		DDG_GLOBAL_GXL_DIR = OUTPUT_DIR + "gxl_file/";
		INDEXTOSIG = OUTPUT_DIR + "INDEXTOSIG.log";
		Log.init(LOG);
		Log.init(API_LOCAL_LOG);
		Log.init(API_LOCAL_DOT);
		Log.init(API_GLOBAL_LOG);
		Log.init(API_GLOBAL_DOT);
		Log.init(DDG_LOCAL_DOT);
		Log.init(DDG_GLOBAL_DOT);
		Log.init(DDG_GLOBAL_SUCCINCT_DOT);
		Log.init(DDG_GLOBAL_CXL);
		Log.init(VPT_CXL_1);
		Log.init(VPT_CXL_2);
		SensitiveAPICategory.init();
		AnalyzerMain analyzer = new AnalyzerMain();
		analyzer.run();
		//analyzer.globalAPIsub();
		//analyzer.globalAPIsub();
	}

	public void globalAPIsub() {
		GlobalAPISubGraphTransformer globalForwardDataflowAnalysis = new GlobalAPISubGraphTransformer();
		Transform transform1 = new Transform("wjtp.GlobalAPISubGraphTransformer", globalForwardDataflowAnalysis);
		PackManager.v().getPack("wjtp").add(transform1);
		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../test/bin");
		//sootArgs.add("../PetriNet/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");
		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}
		soot.Main.main(soot_args);
	}

	public void run() {
		AndroidSourceSinkSummary.buildSourceAndSinkSummary();
		findUncalledMethods();
		appMethods = findUncalledMethodsTransformer.getAppMethods();
		appCalledMethods = findUncalledMethodsTransformer.getCalledMethods();
		appUncalledMethods = new ArrayList<String>();
		for (String appMethod : appMethods) {
			if (!appCalledMethods.contains(appMethod)) {
				if (!appUncalledMethods.contains(appMethod)) {
					appUncalledMethods.add(appMethod);
				}
			}
		}
		G.reset();
		findUncalledOverridingMethods();
		uncalledOverridingFrameworkMethods = findUncalledOverridingMethodsTransformer.getUncalledOverridingFrameworkMethods();
		 G.reset();
		entryPoints = findUncalledOverridingMethodsTransformer.getClassToMethod();
		// TODO
		scanForSourcesAndSinks();
		G.reset();
		sources = findSourcesTransformer.getSources();
		sinks = findSourcesTransformer.getSinks();
		System.out.println("locating all sources and sinks");
		System.out.println("sources are: " + sources);
		System.out.println("sinks are: " + sinks);
		
		buildGlobalAPISubGraph();
		//find all the conditional predecessors of sensitive APIs in CFG and do Data-flow analysis for conditions
		//added for NLP project
		//for(SootMethod syncEntryPoint: globalAPISubGraphTransformer.implicitAsyncGlobalApiGraphs.keySet())
		//{
			SootMethod entry = Scene.v().getEntryPoints().get(0);
			System.out.println("Entry point is :" + entry.getName());
			conditionAnalysis(entry);
		//}
		/*  //comments by Qian Feng
		dataFlowForCondition = false;		
		Set<String> keySet = sinks.keySet();
		Iterator<String> iter = keySet.iterator();
		while (iter.hasNext()) {
			String signature = iter.next();
			INFO_SINK = signature;
			if (MyConstants.DEBUG_INFO) {
				System.out.println("INFO_SINK: " + signature + "  |  " + flowSinkTransformer.getClassToMethod());
			}
			G.reset();
			locateSinks();
			G.reset();
			sourcesLocationMap.put(signature, flowSinkTransformer.getClassToMethod());
		}
		doGlobalBackwardDataflowAnalysis();
		G.reset();
		sourcesLocationMap.clear();
		Set<String> keySetSource = sources.keySet();
		Iterator<String> iterSource = keySetSource.iterator();
		
		// Go through every source to store entry points info into 'sourcesLocationMap'
		while(iterSource.hasNext()){
			String signature = iterSource.next();
			
			INFO_SOURCE = signature;
			if(MyConstants.DEBUG_INFO)
				System.out.println("INFO_SOURCE: " + signature);
			
			// get the declaring class of method where INFO_SOURCE gets called
			locateSources();
			G.reset();
			sourcesLocationMap.put(signature, flowSourceTransformer.getClassToMethod());
		}
		G.reset();
		doGlobalForwardDataflowAnalysis();
		dumpDDGSetToCXL(nodeStruct, edgeStruct, DDG_GLOBAL_CXL);
		
		System.err.println("\n"+"There are " + paths.size() + " paths"+"\n");
		
		for(int i=0;i<paths.size();i++){
			for(int j=0;j<paths.get(i).size();j++) System.err.println(paths.get(i).get(j));
			System.err.println();
			System.err.println();
		}
		
		//for(int i=0;i<paths.size();i++){
			constructConditionStmtRep(paths.get(1));
		//}
		*/ 
	}
	
	public static void constructConditionStmtRep(List<Stmt> path){
		//paths.get(0) is the source, paths.get(length-1) is the sink, conditional stmt representation starts from paths.get(2), or any index larger than 2
		List<Stmt> list = new ArrayList<Stmt>();
		Value value = null;
		String def = null;
		String use = null;
		value = path.get(1).getDefBoxes().get(0).getValue();
		for(int j=0;j<path.size();j++){
			Stmt s = path.get(j);
			if(s instanceof DefinitionStmt){
				list.add(s);
			}
		}
		list.add(path.get(path.size()-1));			
		for(int j=0;j<list.size();j++){
			Stmt s = list.get(j);
			if(!s.toString().contains(sink)){
				if(j==0){
					def = s.getDefBoxes().get(0).getValue().toString().replace("$", "");
					use = s.getUseBoxes().get(0).getValue().toString().replace("$", "");
					//System.err.println("------" + use);
				}else if(s.toString().contains("@parameter")){
					def = s.getDefBoxes().get(0).getValue().toString().replace("$", "");
				} 
			else {
					use = s.getUseBoxes().get(0).getValue().toString().replace("$", "").replace(def, "("+use+")");
					def = s.getDefBoxes().get(0).getValue().toString().replace("$", "");
					value = s.getDefBoxes().get(0).getValue();
				}
			}
		}
		System.err.println("Def is: " + def);
		System.err.println("Use is: " + use);
		System.err.println("Final value is: " + value);

		//Based on the sink stmt, do variable substitution related to other values which are not data dependent on the source. Subsitution only happens within the current function.
		Stmt sinkStmt = list.get(list.size()-1);
		//sinkStmt.get
		System.err.println("SINK is: " + sinkStmt.toString());
		List<Value> useValue = new ArrayList<Value>();
		for(int i=0;i<sinkStmt.getUseBoxes().size();i++){
			Value v = sinkStmt.getUseBoxes().get(i).getValue();
			if(v instanceof Local && v!=value){
				useValue.add(v);
			}
		}
		SootMethod method = null;
		SootClass cLass = null;
		for(int i=0;i<apiGraphNode.size();i++){
			if(apiGraphNode.get(i).getStmt().equals(sinkStmt)){
				method = apiGraphNode.get(i).getHostMethod();
				cLass = method.getDeclaringClass();
			}
		}
		System.err.println(cLass.getName().toString()+"::"+method.getName().toString());
		JimpleBody body = GlobalBackwardDataflowAnalysis.methodTObody.get(cLass.getName().toString()+":"+method.getName().toString());
		if(body!=null){
			System.err.println("Function body is found");
		}
		
		List<Stmt> lIst = new LinkedList<Stmt>();
		List<String> stmtList = new LinkedList<String>();
		Iterator it = body.getUnits().iterator();
		System.err.println();
		while (it.hasNext()){
			Stmt s = (Stmt) it.next();
			if (s.equals(sinkStmt)){
				break;
			}
			if(s instanceof DefinitionStmt){
				//System.err.println(s.toString());
				lIst.add(s);
				stmtList.add(s.toString().replace("$", ""));
			}
		}
		List<String> defList = new LinkedList<String>();
		for(int i=0;i<stmtList.size();i++) {
			System.err.println(stmtList.get(i));
			String[] items = stmtList.get(i).split(" ");
			defList.add(items[0]);
			//System.err.println(items[0]);
		}
		
		List<String> valueList = new LinkedList<String>();
		for(int i=0;i<useValue.size();i++)	valueList.add(useValue.get(i).toString().replace("$", ""));
		
		//In our running example, only one variable needed to be subsituted. Make things simple here
		String tmp = valueList.get(0);
		System.err.println(tmp);
		String[] items;
		if(defList.contains(tmp)){
			int index = defList.indexOf(tmp);
			if(stmtList.get(index).contains(":=")){
				items = stmtList.get(index).split(" := ");
				tmp = items[2];
			}else if(stmtList.get(index).contains(" = ")){
				items = stmtList.get(index).split(" = ");
				tmp = items[1];
			}
		}
		//System.err.println(tmp);
		while(true){
			boolean check = false;
			for(int i=0;i<defList.size();i++){
				if(tmp.contains(defList.get(i))){
					check = true;
					if(stmtList.get(i).contains(":=")){
						items = stmtList.get(i).split(" := ");
						tmp = tmp.replace(defList.get(i), "("+items[1]+")");
					}else if(stmtList.get(i).contains(" = ")){
						items = stmtList.get(i).split(" = ");
						tmp = tmp.replace(defList.get(i), "("+items[1]+")");
					}
				}
			}
			if(check==false) break;
		}
		System.err.println(tmp);
		String def1 = valueList.get(0);
		String use1 = tmp;
		
		//TODO Check which conditional branch is taken  achieve this dataflow
		//1st step: get condition stmts towards sink of current dataflow path
		
		//TODO conditionForAPI stores: <sensitive API, List<Stmt> conditions>. In this easy running example, there is only one conditon for each sensitive API. 
		//We only retrieve conditionForAPI.get(sensitiveAPI).get(0) for testing. Need to be more general in the furture
		System.err.println("=================conditionForAPI==================");
		it = conditionForAPI.entrySet().iterator();
		String key = null;
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			if(pair.getKey().toString().contains(sinkStmt.toString())){
				key = pair.getKey().toString();
				System.err.println(key + "====");
				for(int i=0;i<conditionForAPI.get(key).size();i++){
					System.err.println(conditionForAPI.get(key).get(i));
				}
				break;
			}
		}
		System.err.println(key+" "+conditionForAPI.get(key));
				
		List<APIGraphNode> ddgCondition = conditionToDDGMap.get(conditionForAPI.get(key).get(0));
		String defCon = null;
		String useCon = null;
		Stmt condition = null;
		System.err.println("==============ConditionForDDGMap==============");
		condition = ddgCondition.get(0).getStmt();
		for(int i=ddgCondition.size()-1;i>=0;i--){
			Stmt s = ddgCondition.get(i).getStmt();
			System.err.println("..."+s.toString());
			if(s instanceof DefinitionStmt&&!condition.toString().contains(s.toString())){
				if(defCon==null&&useCon==null){
					defCon = s.getDefBoxes().get(0).getValue().toString().replace("$", "");
					useCon = s.getUseBoxes().get(0).getValue().toString().replace("$", "");
				}else{
					useCon = s.getUseBoxes().get(0).getValue().toString().replace("$", "").replace(defCon, "("+useCon+")");
					defCon = s.getDefBoxes().get(0).getValue().toString().replace("$", "");
					}
				}
			//System.err.println("Current defCon  "+defCon);
			//System.err.println("Current useCon  "+useCon);
		}
		System.err.println();
		System.err.println();
		//At last, need to determine if the condition is taken or not.
		//Currently, only need to check if the branch is taken, if the target will be achieved or not. 		
		Stmt target = ((IfStmt) condition).getTarget();
		//locate this target in soot method		
		System.err.println("If the branch is taken, it will jump to " + target.toString());
		APIGraphNode sinkNode = null;
		APIGraphNode conditionTarget = null;
		for(int i=0;i<apiGraphNode.size();i++){
			if(apiGraphNode.get(i).getStmt().toString().equals(target.toString())){
				//locate the direct branch target stmt in apiGraphNode data structure
				System.err.println(apiGraphNode.get(i).getStmt().toString() + " is found");
				conditionTarget = apiGraphNode.get(i);
			}
			if(apiGraphNode.get(i).getStmt().toString().equals(sinkStmt.toString())){
				//locate the current sink stmt in apiGraphNode data structure
				System.err.println(apiGraphNode.get(i).getStmt().toString() + " is found");
				sinkNode = apiGraphNode.get(i);
			}
		}
		//if check is true: the branch is taken to go to the sink. 
		//else, the branch is not taken
		((IfStmt)condition).setTarget(sinkStmt);
		System.err.println("New condition Stmt is: "+condition.toString());
		String finalStr = null;
		boolean check = conditionTarget.hasSuccRecursive(sinkNode, null);
		System.err.println(conditionTarget.getStmt().toString()+"::"+sinkNode.getStmt().toString());
		if(check) {
			//Branch is taken. No negation, just do variable substitutions
			System.err.println("Taken");
			finalStr = condition.toString().replace("$", "").replace(def, "("+use+")").replace(defCon, "("+useCon+")").replace(def1, "("+use1+"");;
		}else{
			//Branch is not taken. Negate the condition stmt, and so variable substitution
			System.err.println("Not Taken");
			String conStr = "";
			conStr = ((IfStmt)condition).getCondition().toString();
			conStr = conStr.replace("$", "").replace(defCon, "("+useCon+")");
			conStr = "if "+"!" + "(" + conStr + ")" + " goto ";
			finalStr = conStr + sinkStmt.toString().replace("$", "").replace(def, "("+use+")").replace(def1, "("+use1+"");
			//System.err.println("Condition is : " + conStr);
		}
		System.err.println(finalStr);
	}
	
	/**
	 * This function is to analyze the conditions of all the sensitive APIs in the application
	 * 1. It locates all the sensitive APIs
	 * 2. Use getConditionalPredecessors() to extract all the conditional statements of each API
	 * 3. Put all the unique conditions of each sensitive API in sourcesLocationMap data structure and perform backward DFA
	 * 4. call RetrieveConditionFromDDG() to retrieve supported conditions
	 * 5. Write the condition information to 'conditionForAPI'
	 * 
	 * OK. There maybe a little confusing for so many data structures
	 *  
	 * @param syncEntryPoint is the entry point of each program slice in super graph
	 */
	@SuppressWarnings("unchecked")
	private void conditionAnalysis(SootMethod entry)
	{
		APIGraph graph = globalAPISubGraphTransformer.globalApiGraphs.get(entry);
		//for every node in the graph, if it is sensitive, extract its conditional predominators 
		//Finally extract the conditions
		for(APIGraphNode node : graph.getAPIGraph())
		{
			//will analyze each stmt which invoke sensitive APIs
			Stmt statement = node.getStmt();
			String nodeSig = node.getHostMethod() + ": " + statement.toString();
			//System.out.println(nodeSig);
			currAnalysisNodeSig = nodeSig;
			
			if(!statement.containsInvokeExpr()) continue;
		
			List<Predecessor> conditionalPredecessors = new ArrayList<Predecessor>();
			String apiSig = statement.getInvokeExpr().getMethod().getSignature();
			
			if(IsSensitiveAPI(apiSig))
			{
				String methodName = statement.getInvokeExpr().getMethod().getName();
				String hostMethodName = node.getHostMethod().getName();
				
				System.out.println(hostMethodName+":"+methodName);
				conditionalPredecessors = graph.getConditionalPredecessors(node);
				//all the predominators are conditional predominators. i.e. IF statement
				//this one stores all the conditional statements for current sensitive API node
				//it will later be stored in 'sensitiveNodeSigToConditionStmtMap'
				LinkedList<APIGraphNode> conditionalStmtList = new LinkedList<APIGraphNode>();
				
				//for every predominators, extract the condition and its value boxes. 
				//Do backward data flow analysis on each value box (not true, we just do one DFA per predominator)
				for(Predecessor cond : conditionalPredecessors)
				{
					System.out.println("Condition is :"+cond.pred_node.getStmt());
					Stmt condStatement  = cond.pred_node.getStmt();
					Vector<ValueBox> l = new Vector<ValueBox>();
					if(condStatement instanceof IfStmt)
					{
						Stmt tar = ((IfStmt)condStatement).getTarget();
//						for(Object v: ((IfStmt)condStatement).getUnitBoxes())
//						{
//							//ValueBox vb = (ValueBox)v;
//							//Value value = vb.getValue();
//						}
//						
						l.addAll(((IfStmt)condStatement).getCondition().getUseBoxes());
					}
					else if(condStatement instanceof TableSwitchStmt)
					{
						l.add(((TableSwitchStmt)condStatement).getKeyBox());
					}
					else if(condStatement instanceof LookupSwitchStmt)
					{
						l.add(((LookupSwitchStmt)condStatement).getKeyBox());
					}
					for(ValueBox vb : l)
					{
						Value v = vb.getValue();
						Type type = v.getType();
						//for now, we only care about two types of conditions
						if(type instanceof  RefLikeType || type instanceof PrimType)
						{
							System.out.println("Analyzing: "+v);
							dataFlowForCondition = true;
							INFO_SINK = condStatement.toString();
							SINK_METHOD = cond.pred_node.getHostMethod();
							G.reset();
							locateSinks();
							G.reset();
							String conditionSig = condStatement.toString() + "|" + flowSinkTransformer.sinkSig + " ### " + String.valueOf(cond.getCondValue()); 
							conditionalStmtList.add(cond.pred_node);
							conditionalStmtToSigMap.put(cond.pred_node, conditionSig);
							System.out.println(cond.pred_node.getStmt()+"========================"+conditionSig);
							//if(!conditionToDDGMap.containsKey(conditionSig))
							//{
							sourcesLocationMap.put(condStatement.toString(), flowSinkTransformer.getClassToMethod());
							
							//add content to conditionForAPI
							if(conditionForAPI.containsKey(currAnalysisNodeSig)){
								LinkedList<String> conds = conditionForAPI.get(currAnalysisNodeSig);
								if(!conds.contains(conditionSig))
								{
									conds.add(conditionSig);
								}
							}else{
								LinkedList<String> conds = new LinkedList<String>();
								conds.add(conditionSig);
								conditionForAPI.put(currAnalysisNodeSig, conds);
							}
							//}
							break;
						}
						else continue;
					}
					//end for
				}
				//end for(APIGraphNode n : preDominators)
				
				if(!sourcesLocationMap.isEmpty()){
					System.out.println("START TO DO BACKWARDS ANALYSIS");
					doGlobalBackwardDataflowAnalysis();
				}
				
				G.reset();
				sourcesLocationMap.clear();
				/*for(APIGraphNode conditionalStmt: conditionalStmtList)
				{
					String conditionalStmtSig = conditionalStmtToSigMap.get(conditionalStmt);
					long id = RetrieveSwitchStmtID(node, conditionalStmt);
					RetrieveConditionFromDDG(id, conditionalStmtSig);
				}*/
				
				//store condition to sensitive API mapping
				/*for(APIGraphNode conditionalStmt: conditionalStmtList)
				{
					String conditionalStmtSig = conditionalStmtToSigMap.get(conditionalStmt);

					long id = RetrieveSwitchStmtID(node, conditionalStmt);
					System.out.println(conditionalStmt.getStmt());
					System.out.println(conditionalStmtSig);
					System.out.println(id);
					//RetrieveConditionFromDDG(id, conditionalStmtSig);
				}*/
								
				//break;
				}
			}//isSensitiveAPI()
		//at this point: conditionForAPI and conditionToDDGMap should be constructed. Let's check
		System.out.println("=================conditionForAPI==================");
		Iterator it = conditionForAPI.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey());
	        System.out.println(pair.getValue());
	    }
		System.out.println("=================conditionToDDGMap==================");
		it = conditionToDDGMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " ======= ");
	        List<APIGraphNode> list = (List<APIGraphNode>) pair.getValue();
	        for(int i=0;i<list.size();i++) System.out.println(list.get(i).getStmt());
	    }	
		
	}	///end for node: graph
	
	
	//This function returns entry points (nodes without predecessor)
	//called by getAllConnectedGraphs()
	private LinkedList<String> getEntryPoints() 
	{
		LinkedList<String> entryPoints = new LinkedList<String>();
		
		for(String node: nodeStruct.keySet())
		{
			//if reversedEdgeStruct contains node, that means the node has at least one predecessor
			if(reversedEdgeStruct.containsKey(node))
				continue;
			else
			{
				entryPoints.add(node);
				//System.err.println("entry point: " + node);
			}
		}
		
		//System.err.println("Have " + entryPoints.size() + " entry points in total");
		return entryPoints;
	}
		
		private static boolean IsSensitiveAPI(String apiSig)
		{
			for(String api: MyConstants.sensitiveAPIList)
			{
				if(apiSig.equals(api))
					return true;
			}
			
			return false;
		}
		

		/**
		 * put DDG into 'conditionToDDGMap'
		 */
		public static void AddConditionSigToDDGMapping(String conditionSig, List<APIGraphNode> apiGraph)
		{
			List<APIGraphNode> clonedMap = new ArrayList<APIGraphNode>();
			LinkedHashMap<APIGraphNode, APIGraphNode> oldToNew = new LinkedHashMap<APIGraphNode, APIGraphNode>();
			//clone all the nodes
			for(APIGraphNode oldNode: apiGraph)
			{
				APIGraphNode clonedNode = oldNode.clone();
				clonedMap.add(clonedNode);
				oldToNew.put(oldNode, clonedNode);
			}
			
			//clone all the successors and predecessors
			for(APIGraphNode oldNode : apiGraph)
			{
				APIGraphNode newNode = oldToNew.get(oldNode);
				
				for(APIGraphNode oldPred : oldNode.getPredecessors()){
					APIGraphNode newPred = oldToNew.get(oldPred);
					newNode.addPred(newPred);
				}
				
				for(APIGraphNode oldSucc : oldNode.getSuccessors()){
					APIGraphNode newSucc = oldToNew.get(oldSucc);
					newNode.addSucc(newSucc);
				}
			}
			conditionToDDGMap.put(conditionSig, clonedMap);
		}
		
		
		/** 
		 * Added for NLP project, this function is to retrieve conditions for sensitive API from DDG
		 * Currently, we consider the following conditions:
		 * 		1.UI (findViewById)
		 * 		2.Phone state (wifi, network, mute, bluetooth, GPS, vibrate, wakelock, NFC, keyguard, etc)
		 * 		3.Time
		 * 		4.Location
		 * 		5.BOOT_COMPLETE
		 * 
		 * Parameter: id is the switch-case statement id that triggers the sensitive API
		 **/
		private void RetrieveConditionFromDDG(long id, String conditionSig)
		{
			//if id extracted from switch-case statement matches with UI id, just add it to condition list
			/*if(uiResIDTextMapping.resIDToTextMapping.containsKey(id))
			{
				String condition = uiResIDTextMapping.resIDToTextMapping.get(id);
				AddCondition(condition);
			}*/
			
			for(APIGraphNode ddgNode : conditionToDDGMap.get(conditionSig))
			{
				Stmt statement = ddgNode.getStmt();			
				if(statement.containsInvokeExpr())
				{
					InvokeExpr expr = statement.getInvokeExpr();
					//check all kinds of conditions, and put the conditions into AnalyzerMain.conditionForAPI
					//CheckUIConditions(expr);
					//CheckBootConditions(expr);
					//CheckDownloadCompleteConditions(expr);
					//CheckPhoneStateConditions(expr, id);
					//CheckEnvironmentalConditions(expr);
				}
			}
			
		}
		
		
		
		private long RetrieveSwitchStmtID(APIGraphNode node, APIGraphNode cond) 
		{
			Stmt condStmt = cond.getStmt();
			long id = -1;
			
			if(condStmt instanceof TableSwitchStmt)
			{
				Vector<APIGraphNode> succs = cond.getSuccessors();
				for(APIGraphNode succ: succs)
				{
					if(succ.hasSuccRecursive(node, cond))
					{
						int low = ((TableSwitchStmt) condStmt).getLowIndex();
						int high = ((TableSwitchStmt) condStmt).getHighIndex();
						for(int i = 0; i <= high - low; i++)
						{
							Stmt target = (Stmt)((TableSwitchStmt) condStmt).getTarget(i);
							Stmt succStmt = succ.getStmt();

							if(target.equals(succStmt))
							{
								id = i + low;
								break;							
							}
						}//end for(int i = 0; i <= high - low; i++)
						break;
					}	
				}//end for(APIGraphNode succ: succs)
			}
			else if(condStmt instanceof LookupSwitchStmt)
			{
				Vector<APIGraphNode> succs = cond.getSuccessors();
				for(APIGraphNode succ: succs)
				{
					if(succ.hasSuccRecursive(node, cond))
					{
						@SuppressWarnings("unchecked")
						List<IntConstant> values = ((LookupSwitchStmt) condStmt).getLookupValues();
						int numberOfValues = values.size();
						for(int i = 0;i < numberOfValues; i++)
						{
							Stmt target = (Stmt)((LookupSwitchStmt) condStmt).getTarget(i);
							Stmt succStmt = succ.getStmt();
																
							if(target.equals(succStmt))
							{
								id = ((LookupSwitchStmt) condStmt).getLookupValue(i);
								if(DEBUG_CONDITION)
									System.err.println("id: " + id);
								break;
							}
						}//end for(int i = 0;i < numberOfValues; i++)
						break;
					}
				}//end for(APIGraphNode succ: succs)
			}
			return id;
		}
	
	private void buildGlobalAPISubGraph(){
		globalAPISubGraphTransformer = new GlobalAPISubGraphTransformer();
		Transform transform = new Transform("wjtp.GlobalAPISubGraphTransformer", globalAPISubGraphTransformer);
		PackManager.v().getPack("wjtp").add(transform);
		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");
		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}
		soot.Main.main(soot_args);
	}
	
	private void doGlobalForwardDataflowAnalysis() {
		/*LinkedHashMap<String, String> mClassToMethod = findUncalledOverridingMethodsTransformer.getClassToMethod();
		List<SootMethod> entry_points = new ArrayList<SootMethod>();
		Set<String> keySet = mClassToMethod.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		while (keyIterator.hasNext()) {
			String mClass = keyIterator.next();
			String method = mClassToMethod.get(mClass);

			System.out.println("building entry points:" + mClass + "|" + method);

			mClass = mClass.substring(0, mClass.indexOf("|"));

			SootClass main_soot_class = Scene.v().loadClassAndSupport(mClass);
			SootMethod sMethod = main_soot_class.getMethod(method);
			sMethod.setDeclaringClass(main_soot_class);

			System.out.println("entry point:" + method);

			entry_points.add(sMethod);
		}
		Scene.v().setEntryPoints(entry_points);*/
		globalForwardDataflowAnalysis = new GlobalForwardDataflowAnalysis();
		Transform transform1 = new Transform("wjtp.GlobalForwardDataflowAnalysis", globalForwardDataflowAnalysis);
		PackManager.v().getPack("wjtp").add(transform1);
		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");
		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}
		soot.Main.main(soot_args);
	}

	
	private void findUncalledOverridingMethods() {
		findUncalledOverridingMethodsTransformer = new FindUncalledOverridingMethodsTransformer(appUncalledMethods);
		Transform transform = new Transform("jtp.FindUncalledOverridingMethodsTransformer",findUncalledOverridingMethodsTransformer);
		PackManager.v().getPack("jtp").add(transform);
		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");		
		sootArgs.add("-allow-phantom-refs");
		// sootArgs.add("-w");
		sootArgs.add("-O");

		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}

		soot.Main.main(soot_args);
	}

	private void findUncalledMethods() {
		findUncalledMethodsTransformer = new FindUncalledMethodsTransformer();
		Transform transform = new Transform("jtp.FindUncalledMethodsTransformer", findUncalledMethodsTransformer);
		PackManager.v().getPack("jtp").add(transform);

		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");		
		sootArgs.add("-allow-phantom-refs");

		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}
		soot.Main.main(soot_args);
	}

	private void doGlobalBackwardDataflowAnalysis() {

		/*LinkedHashMap<String, String> mClassToMethod = findUncalledOverridingMethodsTransformer.getClassToMethod();
		List<SootMethod> entry_points = new ArrayList<SootMethod>();
		Set<String> keySet = mClassToMethod.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		while (keyIterator.hasNext()) {
			String mClass = keyIterator.next();
			String method = mClassToMethod.get(mClass);
			System.out.println("building entry points:" + mClass + "|" + method);
			mClass = mClass.substring(0, mClass.indexOf("|"));
			SootClass main_soot_class = Scene.v().loadClassAndSupport(mClass);
			SootMethod sMethod = main_soot_class.getMethod(method);   
			sMethod.setDeclaringClass(main_soot_class);
			System.out.println("entry point:" + method);
			entry_points.add(sMethod);
		}*/
		//System.out.println("start doing backward dataflow analysis");
		//Scene.v().setEntryPoints(entry_points);
		//Scene.v().setEntryPoints(Scene.v().getEntryPoints());
		globalBackwardDataflowAnalysis = new GlobalBackwardDataflowAnalysis();
		Transform transform1 = new Transform("wjtp.GlobalBackwardDataflowAnalysis", globalBackwardDataflowAnalysis);

		PackManager.v().getPack("wjtp").add(transform1);
		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");

		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}
		soot.Main.main(soot_args);
	}

	private void locateSources() {

		flowSourceTransformer = new FlowSourceTransformer();
		Transform transform1 = new Transform("jtp.FlowSourceTransformer", flowSourceTransformer);
		PackManager.v().getPack("jtp").add(transform1);

		List<String> sootArgs = new ArrayList<String>();

		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");

		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}

		soot.Main.main(soot_args);
	}

	private void locateSinks() {

		flowSinkTransformer = new FlowSinkTransformer();
		Transform transform1 = new Transform("jtp.FlowSinkTransformer", flowSinkTransformer);
		PackManager.v().getPack("jtp").add(transform1);

		List<String> sootArgs = new ArrayList<String>();

		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");

		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}

		soot.Main.main(soot_args);
	}

	private void scanForSourcesAndSinks() {
		findSourcesTransformer = new FindSourcesTransformer();
		Transform transform1 = new Transform("jtp.FindSourcesTransformer", findSourcesTransformer);
		PackManager.v().getPack("jtp").add(transform1);
		List<String> sootArgs = new ArrayList<String>();
		sootArgs.add("-src-prec");
		sootArgs.add("class");
		sootArgs.add("-soot-class-path");
		sootArgs.add("./tmp/rt.jar");
		sootArgs.add("-output-dir");
		sootArgs.add("./sootoutput");
		sootArgs.add("-output-format");
		sootArgs.add("jimple");
		sootArgs.add("-process-dir");
		sootArgs.add("../LineFollower/bin");
		sootArgs.add("-allow-phantom-refs");
		sootArgs.add("-w");
		String[] soot_args = new String[sootArgs.size()];
		for (int i = 0; i < sootArgs.size(); i++) {
			soot_args[i] = sootArgs.get(i);
		}
		// each time when wen use soot.Main.main(soot_args) to analyze project, we need to 
		// provide correct project location: "../test/bin" in this example
		soot.Main.main(soot_args);
	}

	public static void findPath(APIGraphNode node, List<Stmt> item){
		if(node.getStmt().toString().contains(sink)){
			item.add(node.getStmt());
			paths.add(item);
			return;
		}
		//this is a loop
		if(item.contains(node.getStmt())) return;
		
		List<APIGraphNode> succs = node.getSuccessors();
		if(succs.size()==0) return;
		for(APIGraphNode succ : succs){
			item.add(node.getStmt());
			findPath(succ, new ArrayList<Stmt>(item));
			item.remove(item.size()-1);
		}
	}
	
	// Dump the graph to data structures.
	// This function will be called twice by both backward and forward data flow
	// analysis
	// so that two graphs could be combined
	public static void dumpDDGtoStructure(List<APIGraphNode> graph, boolean forwardAnalysis) {
		if(forwardAnalysis){
			for (APIGraphNode node : graph){
				if(node.getStmt().toString().contains(source)){
					//start from all possible sources, find all available paths ending with sink API recursivly
					findPath(node,new ArrayList<Stmt>());
				}
			}
		}
			
		for (APIGraphNode node : graph) {
			String nodeSig = node.getHostMethod() + ": " + node.getStmt().toString();
			List<APIGraphNode> succNodes = node.getSuccessors();
			List<APIGraphNode> predNodes = node.getPredecessors();
			// fill the edge structure
			for (APIGraphNode succNode : succNodes) {
				String succ = succNode.getHostMethod() + ": " + succNode.getStmt().toString();
				//System.out.println(succ);
				if (edgeStruct.containsKey(nodeSig)) {
					LinkedList<String> succs = edgeStruct.get(nodeSig);
					if (!succs.contains(succ)) {
						succs.add(succ);
					}
				} else {
					LinkedList<String> succs = new LinkedList<String>();
					succs.add(succ);
					edgeStruct.put(nodeSig, succs);
				}
			}

			// fill the reversed edge structure
			for (APIGraphNode predNode : predNodes) {
				String pred = predNode.getHostMethod() + ": " + predNode.getStmt().toString();
				System.out.println(pred);
				if (reversedEdgeStruct.containsKey(nodeSig)) {
					LinkedList<String> preds = reversedEdgeStruct.get(nodeSig);
					if (!preds.contains(pred)) {
						preds.add(pred);
					}
				} else {
					LinkedList<String> preds = new LinkedList<String>();
					preds.add(pred);
					reversedEdgeStruct.put(nodeSig, preds);
				}
			}
		} // end of edge loop

		for (APIGraphNode node : graph) {
			String nodeSig = node.getHostMethod() + ": " + node.getStmt().toString();
			if (nodeStruct.containsKey(nodeSig)) continue;			
			LinkedList<String> attr = new LinkedList<String>();
			SootMethod hostMethod = node.getHostMethod();
			Stmt statement = node.getStmt();
			// add host method & declaring class name of host method
			attr.add(hostMethod.getDeclaringClass().getName());

			if (statement.containsInvokeExpr()) {
				String sig = statement.getInvokeExpr().getMethod().getSignature();
				// add function category
				if (SensitiveAPICategory.sensitiveAPICategories.containsKey(sig)) 
					attr.add(SensitiveAPICategory.sensitiveAPICategories.get(sig) + "#"
							+ statement.getInvokeExpr().getMethod().getName());
				else
					attr.add(statement.getInvokeExpr().getMethod().getName());
				// add function prototype
				attr.add(nodeSig);
			} else {
				// add function category
				attr.add(" ");
				// add function prototype
				attr.add(/*statement.toString()*/nodeSig);
			}

			// add entry point
			List<SootMethod> entryMethods = new ArrayList<SootMethod>();
			GlobalForwardDataflowAnalysis.findEntryMethodsForMethodCall(node.getHostMethod(), entryMethods);
			SootMethod entryPoint;
			if (entryMethods.size() != 0)
				entryPoint = entryMethods.get(0);
			else
				entryPoint = hostMethod;

			attr.add(getEntryPointAttribute(entryPoint));
			String paramsString = " ";
			if(forwardAnalysis)
			{
				if(statement.getTags().contains(GlobalForwardDataflowAnalysis.STRING_CONST_TAG))
				{
					paramsString = "CONST";
				}
			}
			else
			{
				if(statement.getTags().contains(GlobalBackwardDataflowAnalysis.STRING_CONST_TAG))
				{
					paramsString = "CONST";
				}
			}
			attr.add(paramsString);
			//add conditions
			//added for NLP project
			if(conditionForAPI.containsKey(nodeSig))
			{
				attr.addAll(conditionForAPI.get(nodeSig));
			}
			nodeStruct.put(nodeSig, attr);
		}
		// end of node loop
		/*System.out.println("::::::::::PRINT OUT node struct:::::::::");
		Iterator k = nodeStruct.entrySet().iterator();
	    while (k.hasNext()) {
	        Map.Entry pair = (Map.Entry)k.next();
	        System.out.println("---"+pair.getKey()+"---");
	        LinkedList<String> list = (LinkedList<String>) pair.getValue();
	        for(int i=0;i<list.size();i++) System.out.println(list.get(i));
	    }*/
		// additional node loop is needed for CONST parameter propagation
		// TODO
		/*for (APIGraphNode node : graph) {
			String nodeSig = node.getHostMethod() + ": " + node.getStmt().toString();
			Stmt statement = node.getStmt();

			LinkedList<String> attrs = nodeStruct.get(nodeSig);

			// if the parameter field is already CONST, just break
			if (attrs.get(4).equals("CONST"))
				continue;

			if (ContainConstParameterAPIWithParameter(statement) && HasConstPredessor(nodeSig))
				attrs.set(4, "CONST");
		}*/

		// Finally, delete all the java.lang.String::append
		/*for (APIGraphNode node : graph) {
			String nodeSig = node.getHostMethod() + ": " + node.getStmt().toString();
			Stmt statement = node.getStmt();

			if (statement.containsInvokeExpr()
					&& (nodeSig.contains("<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>")
							|| nodeSig.contains(
									"<java.lang.String: java.lang.String replace(java.lang.CharSequence,java.lang.CharSequence)>")
					|| nodeSig.contains("<java.lang.String: boolean startsWith(java.lang.String)>")
					|| nodeSig.contains("<android.content.Intent: java.lang.String getAction()>"))) {
				nodeStruct.remove(nodeSig);
				RemoveFromEdgeStruct(nodeSig);
			}

			if (!statement.containsInvokeExpr()) {
				nodeStruct.remove(nodeSig);
				RemoveFromEdgeStruct(nodeSig);
			}
		}*/
	}
	

	// This helper function is to tell if the entry point is user interactive
	// function based on the entry point list.
	private static String getEntryPointAttribute(SootMethod entryPoint) {

		SootClass clazz = entryPoint.getDeclaringClass();
		while (clazz.hasSuperclass() && clazz.isApplicationClass()) {
			clazz = clazz.getSuperclass();
		}

		if (clazz.isApplicationClass()) {
			return " ";
		}

		if (isUserInteractiveEntryPoint(entryPoint)) {
			return "UserInteractiveEntryPoint";
		}
		return " ";
	}

	private static boolean ContainConstParameterAPIWithParameter(Stmt statement) {
		if (!statement.containsInvokeExpr())
			return false;

		if (!IsconstParameterAPI(statement))
			return false;
		else
			return true;
	}

	private static boolean IsconstParameterAPI(Stmt statement) {
		String apiSig = statement.getInvokeExpr().getMethod().getSignature();

		for (String api : MyConstants.constParameterAPIList) {
			if (apiSig.equals(api)) {
				return true;
			}
		}
		return false;
	}

	private static boolean HasConstPredessor(String nodeSig) {
		LinkedList<String> preds = reversedEdgeStruct.get(nodeSig);
		if (preds == null || preds.isEmpty())
			return false;

		LinkedList<String> allPreds = new LinkedList<String>();
		for (String p : preds)
			allPreds.add(p);

		while (!preds.isEmpty()) {
			String source = preds.remove();

			LinkedList<String> attrs = nodeStruct.get(source);
			if (attrs.get(4).equals("CONST")) {
				return true;
			}

			LinkedList<String> predsList = reversedEdgeStruct.get(source);

			if (predsList == null || predsList.isEmpty())
				continue;

			for (String pred : predsList) {
				if (!allPreds.contains(pred)) {
					allPreds.add(pred);
					preds.add(pred);
				}
			}
		} // end of while
		return false;
	}

	private static void RemoveFromEdgeStruct(String nodeSig) {
		LinkedList<String> succs = edgeStruct.get(nodeSig);
		LinkedList<String> preds = reversedEdgeStruct.get(nodeSig);

		if (preds != null && !preds.isEmpty()) {
			for (String pred : preds) {

				LinkedList<String> predSuccs = edgeStruct.get(pred);
				if (predSuccs.contains(nodeSig))
					predSuccs.remove(nodeSig);

				if (succs == null || succs.isEmpty()) {
					continue;
				}

				for (String succ : succs) {
					if (!predSuccs.contains(succ) && !succ.equals(nodeSig))
						predSuccs.add(succ);
				}
			}
		}

		if (succs != null && !succs.isEmpty()) {
			for (String succ : succs) {
				LinkedList<String> succPreds = reversedEdgeStruct.get(succ);
				succPreds.remove(nodeSig);
				if (succPreds.contains(nodeSig))
					succPreds.remove(nodeSig);

				if (preds == null || preds.isEmpty()) {
					continue;
				}

				for (String pred : preds) {
					if (!succPreds.contains(pred) && !pred.equals(nodeSig))
						succPreds.add(pred);
				}
			}
		}

		edgeStruct.remove(nodeSig);
		reversedEdgeStruct.remove(nodeSig);
	}

	// This function is to dump all the connected graph to subGraphSetNodes and
	// subGraphSetEdges.
	// Called by dumpDDGSetToCXL().
	// This function is to dump all the connected graph to subGraphSetNodes and subGraphSetEdges.
		// Called by dumpDDGSetToCXL().
		private void getAllConnectedGraphs(
				LinkedList<LinkedHashMap<String, LinkedList<String>>> subGraphSetNodes,
				LinkedList<LinkedHashMap<String, LinkedList<String>>> subGraphSetEdges) 
		{
			//get entry points in the graph
			//LinkedList<String> entryPoints = nodeStruct.keySet();//getEntryPoints();
			LinkedList<String> entryPoints = new LinkedList<String>();
			for(String node: nodeStruct.keySet())
				entryPoints.add(node);
			
			//for each entry point, we dump the subgraph into subGraphEdges structure. 
			//If the subgraph contains any node that is already in existing subgraph, we combine these two subgraphs
			for(String entryPoint: entryPoints)
			{
				LinkedHashMap<String, LinkedList<String>> subGraphEdges = new LinkedHashMap<String, LinkedList<String>>();
				
				LinkedHashMap<String, LinkedList<String>> nodeInSubGraph = new LinkedHashMap<String, LinkedList<String>>();
				LinkedList<String> queue = new LinkedList<String>();
				queue.add(entryPoint);
				
				nodeInSubGraph.put(entryPoint, nodeStruct.get(entryPoint));
				
				while(!queue.isEmpty())
				{
					String source = queue.remove();
					LinkedList<String> dests = edgeStruct.get(source);
					
					if(dests == null || dests.isEmpty())
						continue;
					
					for(String dest: dests)
					{
						if(!nodeInSubGraph.containsKey(dest))
						{
							queue.add(dest);
							nodeInSubGraph.put(dest, nodeStruct.get(dest));
						}
					}
					
					subGraphEdges.put(source, dests);
				}//end of while
				
				int subGraphIndex = hasSameNodeInExisting(subGraphSetNodes, nodeInSubGraph);
				
				//if we find new graph has same node with the existing graph then combine them.
				//else add new graph.
				if(subGraphIndex != -1)
				{
					combineSubGraphsEdges(subGraphSetEdges.get(subGraphIndex), subGraphEdges);
					combineSubGraphsNodes(subGraphSetNodes.get(subGraphIndex), nodeInSubGraph);
				}
				else
				{
					subGraphSetEdges.add(subGraphEdges);
					subGraphSetNodes.add(nodeInSubGraph);
				}
					
			}//end of entry point loop
		}

	private void combineSubGraphsNodes(LinkedHashMap<String, LinkedList<String>> existingSubGraphNodes,
			LinkedHashMap<String, LinkedList<String>> subGraphNodes) {
		for (String node : subGraphNodes.keySet()) {
			if (!existingSubGraphNodes.containsKey(node)) {
				LinkedList<String> attr = subGraphNodes.get(node);
				existingSubGraphNodes.put(node, attr);
			}
		}

	}

	private void combineSubGraphsEdges(LinkedHashMap<String, LinkedList<String>> existingSubGraphEdges,
			LinkedHashMap<String, LinkedList<String>> subGraphEdges) {
		for (String node : subGraphEdges.keySet()) {
			if (!existingSubGraphEdges.containsKey(node)) {
				LinkedList<String> dests = subGraphEdges.get(node);
				existingSubGraphEdges.put(node, dests);
			} else {
				LinkedList<String> existingDests = existingSubGraphEdges.get(node);
				LinkedList<String> dests = subGraphEdges.get(node);
				for (String dest : dests) {
					if (!existingDests.contains(dest))
						existingDests.add(dest);
				}
			}
		}

	}

	// This function is to check whether a given subgraph is actually a part of
	// existing subgraph
	private int hasSameNodeInExisting(LinkedList<LinkedHashMap<String, LinkedList<String>>> subGraphSetNodes,
			LinkedHashMap<String, LinkedList<String>> nodeInSubGraph) {
		int size = subGraphSetNodes.size();

		for (String node : nodeInSubGraph.keySet()) {
			for (int i = 0; i < size; i++) {
				if (subGraphSetNodes.get(i).containsKey(node))
					return i;
			}
		}
		return -1;
	}

	private void dumpDDGSetToCXL(LinkedHashMap<String, LinkedList<String>> nodes,
			LinkedHashMap<String, LinkedList<String>> edges,
			String fileName)
	{
		Log.dumpln(fileName, "<?xml version=\"1.0\"?>");
		Log.dumpln(fileName, "<GraphCollection>");
		Log.dumpln(fileName, "<graphs>");
		
		//This structure contains all the nodes in connected graphs in the whole DDG.
		LinkedList<LinkedHashMap<String, LinkedList<String>>> subGraphSetNodes = new LinkedList<LinkedHashMap<String, LinkedList<String>>>();
		//This structure contains all the edges in connected graph in the whole DDG.
		LinkedList<LinkedHashMap<String, LinkedList<String>>> subGraphSetEdge = new LinkedList<LinkedHashMap<String, LinkedList<String>>>();
		getAllConnectedGraphs(subGraphSetNodes, subGraphSetEdge);
		
		//dump each connected graph
		int numOfGraphs = subGraphSetNodes.size();
		//effective graph means a graph contains more than 3 nodes
		int numOfEffectiveGraphs = 0;
		
		for(int i = 0; i < numOfGraphs; i++)
		{
			if(subGraphSetNodes.get(i).size() < EFFECTIVE_GRAPH_SIZE_LIMIT)
				continue;
			
			numOfEffectiveGraphs++;
			Log.dumpln(fileName, "<print file=\"" + "TEST" + numOfEffectiveGraphs + ".gxl\"/>");
			Log.dumpln(fileName,"<!--" + subGraphSetNodes.get(i).size() + "-->");
			String file = DDG_GLOBAL_GXL_DIR + "TEST" + numOfEffectiveGraphs + ".gxl";
			dumpDDGtoGXL(subGraphSetNodes.get(i), subGraphSetEdge.get(i), file);
		}
		
		System.err.println("Has " + numOfEffectiveGraphs + " effective graphs");
		//Log.dumpln(fileName, "</graphs>");
		//Log.dumpln(fileName, "</GraphCollection>");
	}
	
	
	// This function is to dump the combined graph to GXL format
	private void dumpDDGtoGXL(LinkedHashMap<String, LinkedList<String>> nodes, LinkedHashMap<String, LinkedList<String>> edges, String fileName) {
		List<List<String>> paths = new ArrayList<List<String>>();
		/*
		Log.init(fileName);
		Log.dumpln(fileName, "<?xml version=\"1.0\"?>");
		Log.dumpln(fileName, "<gxl>");
		 
		String file = fileName.replace(DDG_GLOBAL_GXL_DIR, "");
		Log.dumpln(fileName, "<graph id=\"" + file + "\">");
		 */
		
		/*LinkedHashMap<String, Integer> nodeToIndex = new LinkedHashMap<String, Integer>();
		LinkedHashMap<Integer,String> indexToNode = new LinkedHashMap<Integer, String>();
		
		String source = "<lejos.robotics.SampleProvider: void fetchSample(float[],int)>";
		String sink = "<lejos.robotics.EncoderMotor: void setPower(int)>";
		
		int index = 1;
		
		// dump nodes
		//initialize nodeToIndex and indexToNode map
		for (String node : nodes.keySet()) {
			nodeToIndex.put(node, index);
			indexToNode.put(index, node);
			//LinkedList<String> attr = nodes.get(node);
			//dumpNode(index, attr, fileName);
			System.out.println(index+" "+node);
			index++;
		}
		
		for(String node :  nodes.keySet()){
			//if current node is one source node
			if(node.contains(source)){
				List<String> path = new ArrayList<String>();
				sourceTOsink(node,sink,path,paths,edges);
			}
		}
		
		System.err.println("Found "+paths.size()+" paths in this graph");
		System.err.println(paths.get(0));
		System.err.println(paths.get(1));*/

		// dump edges
		/*for (String edge : edges.keySet()) {
			LinkedList<String> dests = edges.get(edge);
			for (String dest : dests) {
				int sourceIndex = nodeToIndex.get(edge);
				int destIndex = nodeToIndex.get(dest);
				dumpEdge(sourceIndex, destIndex, fileName);
			}
		}*/
		//Log.dumpln(fileName, "</graph>");
		//Log.dumpln(fileName, "</gxl>");
	}

	
	private void sourceTOsink(String start, String sink, List<String> path, List<List<String>> paths, LinkedHashMap<String, LinkedList<String>> edges){
		//no path in this direction
		//if(start==null) return;
		//found one path
		if(start.contains(sink)){
			path.add(start);
			paths.add(path);
			return;
		}
		//a loop
		if(path.contains(start)) return;
		path.add(start);
		LinkedList<String> dests = edges.get(start);
		if(dests==null) return;
		for(String dest : dests){
			//System.out.println(dest);
			sourceTOsink(dest,sink,path,paths,edges);
			//if(path.size()>0)	path.remove(path.size()-1);
			//else return;
		}
	}
	
	// These two helper functions are to dump nodes and edges
	private void dumpNode(int index, LinkedList<String> attr, String fileName)
	{
		//dump node index
		Log.dumpln(fileName, "<node id=\"" + index + "\">");
		
		//dump attributes
		//gxl format file doesn't support '<' or '>' in the string
		String newattr0 = attr.get(0).replace('<', '(');
		newattr0 = newattr0.replace('>', ')');
		newattr0 = newattr0.replace("&", "");
		
		String newattr1 = attr.get(1).replace('<', '(');
		newattr1 = newattr1.replace('>', ')');
		newattr1 = newattr1.replace("&", "");
		
		String newattr2 = attr.get(2).replace('<', '(');
		newattr2 = newattr2.replace('>', ')');
		newattr2 = newattr2.replace("&", "");
		
		String newattr3 = attr.get(3).replace('<', '(');
		newattr3 = newattr3.replace('>', ')');
		newattr3 = newattr3.replace("&", "");
		
		String newattr4 = attr.get(4).replace('<', '(');
		newattr4 = newattr4.replace('>', ')');
		newattr4 = newattr4.replace("&", "");

		Log.dumpln(fileName, "<attr name=\"packageName\"><string>" + newattr0 + "</string></attr>");
		Log.dumpln(fileName, "<attr name=\"functionCategory\"><string>" + newattr1 + "</string></attr>");
		Log.dumpln(fileName, "<attr name=\"functionPrototype\"><string>" + newattr2 + "</string></attr>");
		Log.dumpln(fileName, "<attr name=\"entryPoints\"><string>" + newattr3 + "</string></attr>");
		Log.dumpln(fileName, "<attr name=\"parameters\"><string>" + newattr4 + "</string></attr>");
		
		int size = attr.size();
		for(int i = 5; i < size; i++)
		{
			String newattr = attr.get(i).replace('<', '(');
			newattr = newattr.replace('>', ')');
			newattr = newattr.replace("&", "");
			
			Log.dumpln(fileName, "<attr name=\"condition\"><string>" + newattr + "</string></attr>");
		}
		
		Log.dumpln(fileName, "</node>");
	}

	private void dumpEdge(int sourceIndex, int destIndex, String fileName) {
		Log.dumpln(fileName, "<edge from=\"" + sourceIndex + "\" to=\"" + destIndex + "\">");
		Log.dumpln(fileName, "</edge>");
	}

	private static boolean isUserInteractiveEntryPoint(SootMethod function) {
		int size = MyConstants.userInteractiveEntryPoints.length;

		for (int i = 0; i < size; i++) {
			if (function.getName().contains(MyConstants.userInteractiveEntryPoints[i])) {
				return true;
			}

		}
		return false;
	}
}
