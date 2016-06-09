//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class SharedLocalElasticsearchResource extends SharedElasticsearchResource {
	private Node node;
	private Path tmpDir;

	@Override
	protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams) throws ResourceInitializationException {
		try{
			tmpDir = Files.createTempDirectory("elasticsearch");
		}catch(IOException ioe){
			throw new ResourceInitializationException(ioe);
		}
		
		Settings settings = Settings.builder()
				.put("path.home", tmpDir.toString())
				.build();
		node = NodeBuilder.nodeBuilder()
				.settings(settings)
				.data(true)
				.local(true)
				.clusterName("test_cluster")
				.node();
		
		return true;
	}

	@Override
	public Client getClient() {
		return node.client();
	}

	@Override
	protected void doDestroy() {
		node.close();
		
		try{
			FileUtils.deleteDirectory(tmpDir.toFile());
		}catch(IOException ioe){
			// Delete what we can, ignore the rest
		}
	}
}
