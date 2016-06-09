function getExistingPipelines(){
	$("#existingPipelines").empty();
	
	$.ajax({
		url: baleenUrl + "api/1/pipelines"
	})
	.done(function(data){
		if(data.length == 0){
			$("#existingPipelines").append("<tr><td colspan=\"4\"><em>No existing pipelines found</em></td></tr>");
		}else{
			data.forEach(function(el){
				var name = "<td><a href=\"#\" onclick=\"getPipelineYaml('"+el.name+"'); return false;\">" + el.name + "</a></td>";
				var source = "<td>" + (el.source == null ? "<em>REST API</em>" : el.source) + "</td>";
				var running = "<td>", controls = "<td>";
				
				if(el.running){
					running += "<span class=\"glyphicon glyphicon-ok\"></span>";
					
					controls += "<a href=\"#\" onclick=\"restartPipeline('"+el.name+"'); return false;\"><span class=\"glyphicon glyphicon-fast-backward\"></span></a> ";
					controls += "<a href=\"#\" onclick=\"stopPipeline('"+el.name+"'); return false;\"><span class=\"glyphicon glyphicon-stop\"></span></a>";
				}else{
					controls += "<a href=\"#\" onclick=\"startPipeline('"+el.name+"'); return false;\"><span class=\"glyphicon glyphicon-play\"></span></a>";
				}
				running += "</td>";
				controls += " <a href=\"#\" onclick=\"removePipeline('"+el.name+"'); return false;\"><span class=\"glyphicon glyphicon-remove\"></span></a></td>";
				
				$("#existingPipelines").append($("<tr></tr>").append(name, running, source, controls));
			});
		}
	}).error(function(data){
		$("#existingPipelines").append("<tr><td colspan=\"4\"><em>Unable to retrieve pipelines</em></td></tr>");
	});
}

function removePipeline(pipelineName){
	$.ajax({
		url: baleenUrl + "api/1/pipelines?name=" + encodeURIComponent(pipelineName),
		type: "DELETE"
	}).done(function(data){
		getExistingPipelines();
	}).error(function(data){
		$("#alertCouldntDelete").removeClass("hidden");
	});
}

function stopPipeline(pipelineName){
	$.ajax({
		url: baleenUrl + "api/1/pipelines/stop?name=" + encodeURIComponent(pipelineName),
		type: "POST"
	}).done(function(data){
		getExistingPipelines();
	}).error(function(data){
		$("#alertCouldntDelete").removeClass("hidden");
	});
}

function startPipeline(pipelineName){
	$.ajax({
		url: baleenUrl + "api/1/pipelines/start?name=" + encodeURIComponent(pipelineName),
		type: "POST"
	}).done(function(data){
		getExistingPipelines();
	}).error(function(data){
		$("#alertCouldntDelete").removeClass("hidden");
	});
}

function restartPipeline(pipelineName){
	$.ajax({
		url: baleenUrl + "api/1/pipelines/restart?name=" + encodeURIComponent(pipelineName),
		type: "POST"
	}).done(function(data){
		getExistingPipelines();
	}).error(function(data){
		$("#alertCouldntDelete").removeClass("hidden");
	});
}

function getPipelineYaml(pipelineName){
	$.ajax({
		url: baleenUrl + "api/1/config/pipelines?name=" + encodeURIComponent(pipelineName),
		type: "GET"
	}).done(function(data){
		$("#yaml").html("<pre>"+data+"</pre>");
		$("#yamlTitle").text("Configured YAML for Pipeline '"+pipelineName+"'");
		$("#yamlModal").modal("show");
	}).error(function(data){
		$("#alertCouldntLoadYaml").removeClass("hidden");
	});
}