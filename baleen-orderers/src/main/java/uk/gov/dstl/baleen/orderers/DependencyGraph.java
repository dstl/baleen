//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.orderers;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import uk.gov.dstl.baleen.core.pipelines.PipelineBuilder;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineActionStore;
import uk.gov.dstl.baleen.core.pipelines.orderers.IPipelineOrderer;

/**
 * Orders analysis engines by constructing a dependency graph and iteratively
 * removing analysis engines that have no dependencies.
 */
public class DependencyGraph implements IPipelineOrderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(DependencyGraph.class);
	
	private Integer edgeId = 0;
	
	@Override
	public List<AnalysisEngine> orderPipeline(List<AnalysisEngine> analysisEngines) {
		Graph<AnalysisEngine, Integer> graph = createDependencyGraph(analysisEngines);
		removeLoops(graph);
		
		List<AnalysisEngine> ordered = new ArrayList<>(analysisEngines.size());
		
		while(true){
			Set<AnalysisEngine> toRemove = removeLayer(graph);
			ordered.addAll(toRemove);
			
			if(toRemove.isEmpty() && graph.getVertexCount() == 0){
				break;
			}else if(toRemove.isEmpty()){
				LOGGER.error("Unsolvable dependency graph. Original order will be used.");
				debugUnresolvedGraph(analysisEngines.get(0).getConfigParameterValue(PipelineBuilder.PIPELINE_NAME).toString(), graph, ordered);

				return analysisEngines;
			}
		}
		
		return ordered;
	}
	
	private Graph<AnalysisEngine, Integer> createDependencyGraph(List<AnalysisEngine> analysisEngines){
		Graph<AnalysisEngine, Integer> graph = new SparseMultigraph<>();
		
		//First, add all annotators onto the graph
		for(AnalysisEngine ae : analysisEngines)
			graph.addVertex(ae);
		
		//Now add dependencies between annotators
		for(AnalysisEngine ae1 : analysisEngines){
			for(AnalysisEngine ae2 : analysisEngines){
				if(ae1 == ae2)
					continue;
				
				addAnnotatorDependencies(graph, ae1, ae2);
			}
		}
		
		return graph;
	}
	
	private void addAnnotatorDependencies(Graph<AnalysisEngine, Integer> graph, AnalysisEngine ae1, AnalysisEngine ae2){
		//If there's already a dependency, then just return as we don't want multiple edges
		if(graph.findEdge(ae1, ae2) != null)
			return;
		
		//If the inputs of ae1 match the outputs of ae2, then ae1 is dependent on ae2
		//We don't need to check both ways as this will be caught by the loop, although
		//we could be more efficient here.
		AnalysisEngineAction a1 = getAction(ae1);
		AnalysisEngineAction a2 = getAction(ae2);
		
		if(overlaps(a1.getInputs(), a2.getOutputs())){
			graph.addEdge(++edgeId, ae2, ae1, EdgeType.DIRECTED);
			return;
		}
	}
	
	private void debugUnresolvedGraph(String pipeline, Graph<AnalysisEngine, Integer> graph, List<AnalysisEngine> ordered){
		if(!LOGGER.isDebugEnabled())
			return;
		
		String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		//Output unresolved analysis engines as CSV
		try(
			FileWriter fileWriter = new FileWriter(timeStamp+"-"+pipeline+"-unsolvedDependencyGraph.csv")
		){
			for(Integer id : graph.getEdges()){
				Pair<AnalysisEngine> pair = graph.getEndpoints(id);
				fileWriter.append(pair.getFirst().getAnalysisEngineMetaData().getName() + "#" + pair.getFirst().getAnalysisEngineMetaData().getUUID());
				fileWriter.append(',');
				fileWriter.append(pair.getSecond().getAnalysisEngineMetaData().getName() + "#" + pair.getSecond().getAnalysisEngineMetaData().getUUID());
				fileWriter.append('\n');
			}
		}catch(IOException ioe){
			LOGGER.warn("Unable to save unsolvable dependency graph to disk", ioe);
		}
		
		//Output original and ordered annotators
		try(
			FileWriter fileWriter = new FileWriter(timeStamp+"-"+pipeline+"-unsolvedDependencyGraph.txt")
		){
			fileWriter.write("Ordered annotators:\n");
			for(AnalysisEngine ae : ordered){
				fileWriter.write("- "+ae.getAnalysisEngineMetaData().getName() + "#" + ae.getAnalysisEngineMetaData().getUUID()+"\n");
			}
			
			fileWriter.write("\nRemaining annotators:\n");
			for(AnalysisEngine ae : graph.getVertices()){
				fileWriter.write("- "+ae.getAnalysisEngineMetaData().getName() + "#" + ae.getAnalysisEngineMetaData().getUUID()+"\n");
			}
		}catch(IOException ioe){
			LOGGER.warn("Unable to save dependency information to disk", ioe);
		}
	}
	
	/**
	 * Determine whether two sets of classes overlap (i.e. contain any of the same classes),
	 * taking into account inheritance and allowing subclasses to count towards any overlap.
	 */
	public static boolean overlaps(Set<Class<? extends Annotation>> s1, Set<Class<? extends Annotation>> s2){
		for(Class<? extends Annotation> c1 : s1){
			for(Class<? extends Annotation> c2 : s2){	
				if(c1.isAssignableFrom(c2))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Find and remove simple loops (e.g. a -> b -> a) from a Jung graph
	 */
	public static <V, E> void removeLoops(Graph<V, E> graph){
		for(V v : graph.getVertices()){
			for(E e : graph.getOutEdges(v)){
				V dest = graph.getDest(e);
				
				E returnEdge = graph.findEdge(dest, v);
				if(returnEdge != null){
					LOGGER.warn("Loop detected between {} and {}. Original order will be preserved.", getName(v), getName(dest));
					graph.removeEdge(returnEdge);
				}
			}
		}
	}
	
	/**
	 * Remove an outer layer of the graph (i.e. any nodes with an inDegree of 0)
	 * 
	 * Returns the set of removed vertices 
	 */
	public static <V, E> Set<V> removeLayer(Graph<V, E> graph){
		Set<V> toRemove = new HashSet<>();
		
		for(V v : graph.getVertices()){
			if(graph.inDegree(v) == 0){
				toRemove.add(v);
			}
		}
		
		for(V v : toRemove)
			graph.removeVertex(v);
		
		return toRemove;
	}
	
	private static String getName(Object o){
		if(o instanceof AnalysisEngine){
			return ((AnalysisEngine)o).getAnalysisEngineMetaData().getName();
		}else{
			return o.toString();
		}
	}
	
	private AnalysisEngineAction getAction(AnalysisEngine ae){
		String uuid = (String) ae.getConfigParameterValue(PipelineBuilder.ANNOTATOR_UUID);
		return AnalysisEngineActionStore.getInstance().get(uuid);
	}

}