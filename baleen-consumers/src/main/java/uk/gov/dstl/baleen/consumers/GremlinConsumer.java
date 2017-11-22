//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenConsumer;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

import java.util.*;

/**
 * Output Baleen documents into Gremlin, using a custom format.
 *
 * @baleen.javadoc
 */
public class GremlinConsumer extends BaleenConsumer {

    /**
     * The types that should be merged with existing nodes
     * where possible (short name only)
     *
     * @baleen.config Buzzword,CommsIdentifier,FinancialAccount,Frequency,Url
     */
    public static final String PARAM_MERGE_TYPES = "mergeTypes";
    @ConfigurationParameter(name = PARAM_MERGE_TYPES, defaultValue = {"Buzzword", "CommsIdentifier", "FinancialAccount",
            "Frequency", "Url"})
    private String[] mergeTypes;

    /**
     * Should a hash of the content be used to generate the ID?
     * If false, then a hash of the Source URI is used instead.
     *
     * @baleen.config true
     */
    public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
    @ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
    private boolean contentHashAsId = true;

    /**
     * Tinkerpop configuration file to use to create and connect to a graph.
     *
     * The format of the implementation will be dependent on the graph being
     * used. For more information, refer to the Tinkerpop documentation or
     * the implementation documentation.
     *
     * @baleen.config
     */
    public static final String PARAM_GRAPH_CONFIG = "graphConfig";
    @ConfigurationParameter(name = PARAM_GRAPH_CONFIG)
    private String graphConfig;

    private Graph g;

    public static final String LABEL_DOCUMENT = "document";
    public static final String LABEL_ENTITY = "entity";
    public static final String LABEL_METADATA = "metadata";
    public static final String LABEL_PUBLISHED_ID = "publishedId";
    public static final String LABEL_REFERENCE = "reference";

    public static final String LABEL_EDGE_CONTAINS = "contains";
    public static final String LABEL_EDGE_COREF = "coreference";
    public static final String LABEL_EDGE_METADATA = "metadata";
    public static final String LABEL_EDGE_PUBLISHED_ID = "publishedId";
    public static final String LABEL_EDGE_REFERENCE = "reference";
    public static final String LABEL_EDGE_RELATION = "relation";

    public static final String PROPERTY_BEGIN = "begin";
    public static final String PROPERTY_DOCUMENT = "document";
    public static final String PROPERTY_END = "end";
    public static final String PROPERTY_EXTERNALID = "externalId";
    public static final String PROPERTY_KEY = "key";
    public static final String PROPERTY_SUBTYPE = "subType";
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_VALUE = "value";

    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinConsumer.class);
    private List<String> listMergeTypes = new ArrayList<>();

    @Override
    public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
        try {
            g = GraphFactory.open(graphConfig);
        }catch(RuntimeException re){
            throw new ResourceInitializationException(re);
        }

        Collections.addAll(listMergeTypes, mergeTypes);
    }

    @Override
    protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
        //Document
        String docId = getUniqueId(jCas);

        Vertex vDoc = g.addVertex(T.label, LABEL_DOCUMENT);

        addProperty(vDoc, "externalId", docId);
        addProperty(vDoc, "content", jCas.getDocumentText());

        DocumentAnnotation da = getDocumentAnnotation(jCas);

        addProperty(vDoc, "sourceUri", da.getSourceUri());
        addProperty(vDoc, "docType", da.getDocType());
        addProperty(vDoc, "language", da.getLanguage());
        addProperty(vDoc, "timestamp", da.getTimestamp());
        addProperty(vDoc, "classification", da.getDocumentClassification());
        addListProperty(vDoc, "caveats", UimaTypesUtils.toList(da.getDocumentCaveats()));
        addListProperty(vDoc, "releasablity", UimaTypesUtils.toList(da.getDocumentReleasability()));

        //Published Ids
        for(PublishedId pid : JCasUtil.select(jCas, PublishedId.class)) {
            //Find existing published ID
            GraphTraversal<Vertex, Vertex> t = buildTraversal(LABEL_PUBLISHED_ID,
                    PROPERTY_VALUE, pid.getValue(),
                    PROPERTY_TYPE, pid.getPublishedIdType()
            );

            Vertex vPid;
            if(t.hasNext()){
                vPid = t.next();
            }else{
                vPid = g.addVertex(LABEL_PUBLISHED_ID);

                addProperty(vPid, PROPERTY_VALUE, pid.getValue());
                addProperty(vPid, PROPERTY_TYPE, pid.getPublishedIdType());
            }

            vDoc.addEdge(LABEL_EDGE_PUBLISHED_ID, vPid);
        }

        //Metadata
        for(Metadata md : JCasUtil.select(jCas, Metadata.class)){
            GraphTraversal<Vertex, Vertex> t = buildTraversal(LABEL_METADATA,
                    PROPERTY_KEY, md.getKey(),
                    PROPERTY_VALUE, md.getValue()
            );

            Vertex vMeta;
            if(t.hasNext()) {
                vMeta = t.next();
            }else{
                vMeta = g.addVertex(LABEL_METADATA);
                addProperty(vMeta, PROPERTY_KEY, md.getKey());
                addProperty(vMeta, PROPERTY_VALUE, md.getValue());
            }

            vDoc.addEdge(LABEL_EDGE_METADATA, vMeta);
        }

        //Entities
        Multimap<ReferenceTarget, Entity> targetted = MultimapBuilder.hashKeys().linkedListValues().build();
        for(Entity entity : JCasUtil.select(jCas, Entity.class)) {

            if(entity.getReferent() != null) {
                targetted.put(entity.getReferent(), entity);
            } else {
                // Create a fake reference target
                targetted.put(new ReferenceTarget(jCas), entity);
            }
        }

        Map<String, Vertex> refToEntity = new HashMap<>();

        for(Collection<Entity> entities : targetted.asMap().values()){
            Map<String, Vertex> m = new HashMap<>();

            for(Entity e : entities){
                String key = e.getType().getShortName() + "##" + e.getSubType() + "##" + e.getValue();

                //See if we have already found the entity to link to with this value and sub-type, in this reference group
                Vertex vEntity = m.get(key);

                if(vEntity == null){
                    //Entity hasn't already been found, should we search the graph?
                    if(shouldMerge(e)){
                        GraphTraversal<Vertex, Vertex> t = buildTraversal(LABEL_ENTITY,
                                PROPERTY_VALUE, e.getValue(),
                                PROPERTY_TYPE, e.getType().getShortName(),
                                PROPERTY_SUBTYPE, e.getSubType()
                        );

                        if(t.hasNext())
                            vEntity = t.next();
                    }

                    //Still no entity found, so create a new one
                    if(vEntity == null){
                        vEntity = g.addVertex(LABEL_ENTITY);

                        addProperty(vEntity, PROPERTY_VALUE, e.getValue());
                        addProperty(vEntity, PROPERTY_TYPE, e.getType().getShortName());
                        addProperty(vEntity, PROPERTY_SUBTYPE, e.getSubType());
                    }

                    m.put(key, vEntity);
                }

                Vertex vReference = g.addVertex(LABEL_REFERENCE);

                addProperty(vReference, PROPERTY_EXTERNALID, e.getExternalId());
                addProperty(vReference, PROPERTY_BEGIN, e.getBegin());
                addProperty(vReference, PROPERTY_END, e.getEnd());

                vDoc.addEdge(LABEL_EDGE_CONTAINS, vReference);
                vReference.addEdge(LABEL_EDGE_REFERENCE, vEntity);

                refToEntity.put(e.getExternalId(), vEntity);
            }

            //Coreference all entities in this set
            for(Vertex v1 : m.values()){
                for(Vertex v2 : m.values()){
                    if(v1.equals(v2))
                        continue;

                    Edge e = v1.addEdge(LABEL_EDGE_COREF, v2);
                    e.property(PROPERTY_DOCUMENT, docId);
                }
            }
        }

        //Relations
        for(Relation r : JCasUtil.select(jCas, Relation.class)){
            Vertex vSource = refToEntity.get(r.getSource().getExternalId());
            Vertex vTarget = refToEntity.get(r.getTarget().getExternalId());

            if(vSource == null || vTarget == null) {
                LOGGER.error("Unable to find vertex for relation end point - relation will be skipped");
                continue;
            }

            Edge eRel = vSource.addEdge(LABEL_EDGE_RELATION, vTarget);

            addProperty(eRel, PROPERTY_DOCUMENT, docId);
            addProperty(eRel, PROPERTY_BEGIN, r.getBegin());
            addProperty(eRel, PROPERTY_END, r.getEnd());
            addProperty(eRel, PROPERTY_TYPE, r.getRelationshipType());
            addProperty(eRel, PROPERTY_SUBTYPE, r.getRelationSubType());
            addProperty(eRel, PROPERTY_VALUE, r.getValue());
        }
    }

    @Override
    protected void doDestroy() {
        try {
            g.close();
        }catch(Exception e){
            LOGGER.warn("An error occurred whilst closing the database", e);
        }
    }

    private String getUniqueId(JCas jCas) {
        return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
    }

    private void addProperty(Vertex v, String key, Object value){
        if(value != null){
            v.property(key, value);
        }
    }

    private void addProperty(Edge e, String key, Object value){
        if(value != null){
            e.property(key, value);
        }
    }

    private void addListProperty(Vertex v, String key, List<?> value){
        if(value != null && !value.isEmpty()){
            for(Object o : value)
                v.property(VertexProperty.Cardinality.list, key, o);
        }
    }

    private boolean shouldMerge(Entity e){
        return listMergeTypes.contains(e.getType().getShortName());
    }

    private GraphTraversal<Vertex, Vertex> buildTraversal(String label, Object... values){
        GraphTraversal<Vertex, Vertex> t = g.traversal().V().hasLabel(label);

        for(int i = 0; i < values.length; i+=2){
            String key = values[i].toString();
            Object value = values[i + 1];

            if(value == null){
                t = t.hasNot(key);
            }else{
                t = t.has(key, value);
            }
        }

        return t;
    }
}
