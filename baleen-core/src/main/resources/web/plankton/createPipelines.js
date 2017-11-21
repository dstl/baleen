//TODO: Duplicate annotator
//TODO: Duplicate pipeline? Requires YAML parser

var componentListLoadedCount = 0;
function componentListLoaded(){
	componentListLoadedCount++;
	
	if(componentListLoadedCount >= 4){
		$("#createPanelLoading").remove();
		$("#createPanelBody").removeClass("hidden");
	}
}

var baleenDefaultValues;
function getDefaults(){
    $.ajax({
        url: baleenUrl + "api/1/defaults"
    }).done(function(data){
        baleenDefaultValues = data;
    });
}

function loadOrderers(){
	$.ajax({
		url: baleenUrl + "api/1/orderers",
	}).done(function(data){
		var readers = data.match(/[^\r\n]+/g);
		readers.forEach(function(reader){
			var option = "<option>"+reader.substring(2)+"</option>";
			$("#orderer").append(option);
		});
		$("#orderer").change();
		componentListLoaded();
	}).error(function(data){
		$("#alertCouldntLoad").removeClass("hidden");
		$("#createPanel").addClass("hidden");
	});
}

function loadCollectionReaders(){
	$.ajax({
		url: baleenUrl + "api/1/collectionreaders",
	}).done(function(data){
		var readers = data.match(/[^\r\n]+/g);
		readers.forEach(function(reader){
			var option = "<option>"+reader.substring(2)+"</option>";
			$("#collectionReader").append(option);
		});
		$("#collectionReader").change();
		componentListLoaded();
	}).error(function(data){
		$("#alertCouldntLoad").removeClass("hidden");
		$("#createPanel").addClass("hidden");
	});
}

function loadContentExtractors(){
	$.ajax({
		url: baleenUrl + "api/1/contentextractors",
	}).done(function(data){
		var readers = data.match(/[^\r\n]+/g);
		readers.forEach(function(reader){
			var option = "<option>"+reader.substring(2)+"</option>";
			$("#contentExtractor").append(option);
		});
		componentListLoaded();
	}).error(function(data){
		$("#alertCouldntLoad").removeClass("hidden");
		$("#createPanel").addClass("hidden");
	});
}

function loadAnnotators(){
	$.ajax({
		url: baleenUrl + "api/1/annotators",
	}).done(function(data){
		var readers = data.match(/[^\r\n]+/g);
		readers.forEach(function(reader){
			var option = "<option>"+reader.substring(2)+"</option>";
			$("#annotator").append(option);
		});
		componentListLoaded();
	}).error(function(data){
		$("#alertCouldntLoad").removeClass("hidden");
		$("#createPanel").addClass("hidden");
	});
}

function loadConsumers(){
	$.ajax({
		url: baleenUrl + "api/1/consumers",
	}).done(function(data){
		var readers = data.match(/[^\r\n]+/g);
		readers.forEach(function(reader){
			var option = "<option>"+reader.substring(2)+"</option>";
			$("#consumer").append(option);
		});
		componentListLoaded();
	}).error(function(data){
		$("#alertCouldntLoad").removeClass("hidden");
		$("#createPanel").addClass("hidden");
	});
}

function getCollectionReader(name){
	if(name == null || name == "")
		return;
	
	var collectionReaderResources = $("#collectionReaderResources").val().split(",");
	collectionReaderResources.forEach(function(el){
		removeResource({key: el});
	});
	
	$("#collectionReaderParameters").empty();
	$("#contentExtractorPanel").addClass("hidden");
	
	var contentExtractorResources = $("#contentExtractorResources").val().split(",");
	contentExtractorResources.forEach(function(el){
		removeResource({key: el});
	});
	
	$.ajax({
		url: baleenUrl + "api/1/collectionreaders/"+name,
	}).done(function(data){
		name = sanitizeName(name);
	
		var resourcesString = "";
	
		data.forEach(function(el){
			if(el.type == "parameter"){
				if(el.name == "contentExtractor"){
				    var defaultContentExtractor = el.defaultValue.replace(baleenDefaultValues.DEFAULT_CONTENT_EXTRACTOR_PACKAGE + ".", "");

				    $("#contentExtractor").val(defaultContentExtractor);
					$("#contentExtractor-default").val(defaultContentExtractor);
					$("#contentExtractor").change();
					$("#contentExtractorPanel").removeClass("hidden");
				}else{				
					var label = "<label for=\""+name+"-"+el.name+"\">"+el.name+"</label>";
					var toTextArea = " <a href=\"#\" onclick=\"toTextArea('"+name+"-"+el.name+"'); $(this).remove(); return false;\"><span class=\"glyphicon glyphicon-edit\"></span></a>";
					var input = "<input type=\"text\" id=\""+name+"-"+el.name+"\" placeholder=\"Enter value here if you wish to set one\" class=\"form-control\" value=\""+el.defaultValue+"\">";
					var defaultValue = "<input type=\"hidden\" id=\""+name+"-"+el.name+"-default\" value=\""+el.defaultValue+"\">";
					
					$("#collectionReaderParameters").append($("<p></p>").append(label, toTextArea, input, defaultValue));
				}
			}else if(el.type == "resource"){
				addResource(el);
				resourcesString += el.key + ",";
			}else{
				console.log("Didn't understand data - unknown type '"+el.type+"'");
			}
		});
		
		$("#collectionReaderResources").val(resourcesString);
	}).error(function(data){
		$("#alertCouldntLoadComponent").removeClass("hidden");
	});
}

function getContentExtractor(name){
	if(name == null || name == "")
		return;
		
	var contentExtractorResources = $("#contentExtractorResources").val().split(",");
	contentExtractorResources.forEach(function(el){
		removeResource({key: el});
	});
	
	$("#contentExtractorParameters").empty();
	
	$.ajax({
		url: baleenUrl + "api/1/contentextractors/"+name,
	}).done(function(data){
		name = sanitizeName(name);

		var resourcesString = "";
		
		data.forEach(function(el){
			if(el.type == "parameter"){
				var label = "<label for=\""+name+"-"+el.name+"\">"+el.name+"</label>";
				var toTextArea = " <a href=\"#\" onclick=\"toTextArea('"+name+"-"+el.name+"'); $(this).remove(); return false;\"><span class=\"glyphicon glyphicon-edit\"></span></a>";
				var input = "<input type=\"text\" id=\""+name+"-"+el.name+"\" placeholder=\"Enter value here if you wish to set one\" class=\"form-control\" value=\""+el.defaultValue+"\">";
				var defaultValue = "<input type=\"hidden\" id=\""+name+"-"+el.name+"-default\" value=\""+el.defaultValue+"\">";
					
				$("#contentExtractorParameters").append($("<p></p>").append(label, toTextArea, input, defaultValue));
			}else if(el.type == "resource"){
				addResource(el);
				resourcesString += el.key + ",";
			}else{
				console.log("Didn't understand data - unknown type '"+el.type+"'");
			}
		});
		
		$("#contentExtractorResources").val(resourcesString);
	}).error(function(data){
		$("#alertCouldntLoadComponent").removeClass("hidden");
	});
}

function getAnnotator(name, id){
	if(name == null || name == "")
		return;
	
	var annotatorResources = $("#annotatorResources-"+id).val().split(",");
	annotatorResources.forEach(function(el){
		removeResource({key: el});
	});
	
	$("#annotatorParameters-"+id).empty();
	
	$.ajax({
		url: baleenUrl + "api/1/annotators/"+name,
	}).done(function(data){
		name = sanitizeName(name);
		
		var resourcesString = "";
		
		data.forEach(function(el){
			if(el.type == "parameter"){
				var label = "<label for=\""+name+"-"+el.name+"-"+id+"\">"+el.name+"</label>";
				var toTextArea = " <a href=\"#\" onclick=\"toTextArea('"+name+"-"+el.name+"-"+id+"'); $(this).remove(); return false;\"><span class=\"glyphicon glyphicon-edit\"></span></a>";
				var input = "<input type=\"text\" id=\""+name+"-"+el.name+"-"+id+"\" placeholder=\"Enter value here if you wish to set one\" class=\"form-control\" value=\""+el.defaultValue+"\">";
				var defaultValue = "<input type=\"hidden\" id=\""+name+"-"+el.name+"-"+id+"-default\" value=\""+el.defaultValue+"\">";
					
				$("#annotatorParameters-"+id).append($("<p></p>").append(label, toTextArea, input, defaultValue));
			}else if(el.type == "resource"){
				addResource(el);
				resourcesString += el.key + ",";
			}else{
				console.log("Didn't understand data - unknown type '"+el.type+"'");
			}
		});
		
		$("#annotatorResources-"+id).val(resourcesString);
	}).error(function(data){
		$("#alertCouldntLoadComponent").removeClass("hidden");
	});
}

function getConsumer(name, id){
	if(name == null || name == "")
		return;
	
	var consumerResources = $("#consumerResources-"+id).val().split(",");
	consumerResources.forEach(function(el){
		removeResource({key: el});
	});
	
	$("#consumerParameters-"+id).empty();
	
	$.ajax({
		url: baleenUrl + "api/1/consumers/"+name,
	}).done(function(data){
		name = sanitizeName(name);
		
		var resourcesString = "";
		
		data.forEach(function(el){
			if(el.type == "parameter"){
				var label = "<label for=\""+name+"-"+el.name+"-"+id+"\">"+el.name+"</label>";
				var toTextArea = " <a href=\"#\" onclick=\"toTextArea('"+name+"-"+el.name+"-"+id+"'); $(this).remove(); return false;\"><span class=\"glyphicon glyphicon-edit\"></span></a>";
				var input = "<input type=\"text\" id=\""+name+"-"+el.name+"-"+id+"\" placeholder=\"Enter value here if you wish to set one\" class=\"form-control\" value=\""+el.defaultValue+"\">";
				var defaultValue = "<input type=\"hidden\" id=\""+name+"-"+el.name+"-"+id+"-default\" value=\""+el.defaultValue+"\">";
					
				$("#consumerParameters-"+id).append($("<p></p>").append(label, toTextArea, input, defaultValue));
			}else if(el.type == "resource"){
				addResource(el);
				resourcesString += el.key + ",";
			}else{
				console.log("Didn't understand data - unknown type '"+el.type+"'");
			}
		});
		
		$("#consumerResources-"+id).val(resourcesString);
	}).error(function(data){
		$("#alertCouldntLoadComponent").removeClass("hidden");
	});
}

var annotatorId = 0;
function addAnnotator(){
	addAnnotator(null);
}
function addAnnotator(name){
	var currId = annotatorId;
	currId++;
	var annotator = $("#annotatorDivTemplate").clone();
	annotator.removeClass("hidden");
	annotator.attr("id", "annotatorDiv-"+currId);
	
	$(".panel-heading", annotator).attr("data-target", "#annotatorPanelBody-"+currId);
	$("#annotatorPanelBody", annotator).attr("id", "annotatorPanelBody-"+currId);

	$("label", annotator).attr("for", "annotator-"+currId);
	$("select", annotator).attr("id", "annotator-"+currId);
	$("#annotatorResources", annotator).attr("id", "annotatorResources-"+currId);
	$("#annotatorParameters", annotator).attr("id", "annotatorParameters-"+currId);
	$("#annotatorName", annotator).attr("id", "annotatorName-"+currId);
	$("#annotatorId", annotator).val(currId);
	
	$("#annotators").append(annotator);
	
	if(name != null){
		$("#annotator-"+currId).val(name);
	}
	
	$("#annotator-"+currId).change(function(){
		getAnnotator($("#annotator-"+currId).val(), currId);
		$("#annotatorName-"+currId).text($("#annotator-"+currId).val());
	});
	$("#annotator-"+currId).change();
	
	annotatorId = currId;
}

function addAllAnnotators(){
	$("#annotator option").each(function(index, el){
		addAnnotator($(el).text());
	});
}

var consumerId = 0;
function addConsumer(){
	addConsumer(null);
}
function addConsumer(name){
	var currId = consumerId;
	currId++;
	var consumer = $("#consumerDivTemplate").clone();
	consumer.removeClass("hidden");
	consumer.attr("id", "consumerDiv-"+currId);
	
	$(".panel-heading", consumer).attr("data-target", "#consumerPanelBody-"+currId);
	$("#consumerPanelBody", consumer).attr("id", "consumerPanelBody-"+currId);
	
	$("label", consumer).attr("for", "consumer-"+currId);
	$("select", consumer).attr("id", "consumer-"+currId);
	$("#consumerResources", consumer).attr("id", "consumerResources-"+currId);
	$("#consumerParameters", consumer).attr("id", "consumerParameters-"+currId);
	$("#consumerName", consumer).attr("id", "consumerName-"+currId);
	$("#consumerId", consumer).val(currId);
	
	$("#consumers").append(consumer);
	
	if(name != null){
		$("#consumer-"+currId).val(name);
	}
	
	$("#consumer-"+currId).change(function(){
		getConsumer($("#consumer-"+currId).val(), currId);
		$("#consumerName-"+currId).text($("#consumer-"+currId).val());
	});
	$("#consumer-"+currId).change();
	
	consumerId = currId;
}

function addAllConsumers(){
	$("#consumer option").each(function(index, el){
		addConsumer($(el).text());
	});
}

var resources = {}
function addResource(resource){
	resource.key = sanitizeName(resource.key);
	if(resource.key != "__baleenHistory"){
		if(resources[resource.key] == undefined){
			resources[resource.key] = 0
		}
		resources[resource.key]++;
		
		$("#noResources").remove();
		
		if(resources[resource.key] == 1){
			//New resource - add it in
			var resourceDiv = $("#resourceDivTemplate").clone();
			resourceDiv.removeClass("hidden");
			resourceDiv.attr("id", "resourceDiv-"+resource.key);

			$(".panel-heading", resourceDiv).attr("data-target", "#resourcePanelBody-"+resource.key);
			$("#resourcePanelBody", resourceDiv).attr("id", "resourcePanelBody-"+resource.key);
			
			$("#resource", resourceDiv).attr("id", "resource-"+resource.key).text(resource.class);
			$("#resourceKey", resourceDiv).attr("id", "resourceKey-"+resource.key).text(resource.key);
			$("#resourceUsage", resourceDiv).attr("id", "resourceUsage-"+resource.key).text(resources[resource.key]);
			$("#resourceName", resourceDiv).attr("id", "resourceName-"+resource.key).text(resource.class + " ("+resource.key+")");
			
			var params = $("#resourceParameters", resourceDiv).attr("id", "resourceParameters-"+resource.key);		
		
			resource.parameters.forEach(function(el){
				el.name = sanitizeName(el.name);
				if(el.type == "parameter"){
					var label = "<label for=\""+resource.key+"-"+el.name+"\">"+el.name+"</label>";
					var toTextArea = " <a href=\"#\" onclick=\"toTextArea('"+resource.key+"-"+el.name+"'); $(this).remove(); return false;\"><span class=\"glyphicon glyphicon-edit\"></span></a>";
					var input = "<input type=\"text\" id=\""+resource.key+"-"+el.name+"\" placeholder=\"Enter value here if you wish to set one\" class=\"form-control\" value=\""+el.defaultValue+"\">";
					var defaultValue = "<input type=\"hidden\" id=\""+resource.key+"-"+el.name+"-default\" value=\""+el.defaultValue+"\">";
						
					params.append($("<p></p>").append(label, toTextArea, input, defaultValue));
				}else{
					console.log("Didn't understand data - unknown type '"+el.type+"'");
				}
			});
			
			$("#resources").append(resourceDiv);
		}else{
			//Existing resource - update usage
			$("#resourceUsage-"+resource.key).text(resources[resource.key]);
		}
	}
}

function removeResource(resource){
	resource.key = sanitizeName(resource.key);
	
	if(resource.key != "__baleenHistory" && resource.key != "" && resources[resource.key] != undefined && resources[resource.key] >= 1){
		resources[resource.key]--;
		
		if(resources[resource.key] == 0){
			$("#resourceDiv-"+resource.key).remove();
		}else{
			$("#resourceUsage-"+resource.key).text(resources[resource.key]);
		}
		
		showNoResources()
	}
}

function removeAnnotator(annotator){
	var div = $(annotator).parent().parent().parent();
	var id = $("#annotatorId", div).val();
	
	var annotatorResources = $("#annotatorResources-"+id).val().split(",");
	annotatorResources.forEach(function(el){
		removeResource({key: el});
	});
	
	div.remove();
}

function removeAllAnnotators(){
	var annotators = $("#annotators");
	$("[id^=annotatorDiv] button", annotators).each(function(index, el){
		removeAnnotator(el);
	});
}

function removeConsumer(consumer){
	var div = $(consumer).parent().parent().parent();
	var id = $("#consumerId", div).val();
	
	var consumerResources = $("#consumerResources-"+id).val().split(",");
	consumerResources.forEach(function(el){
		removeResource({key: el});
	});
	
	div.remove();
}

function removeAllConsumers(){
	var consumers = $("#consumers");
	$("[id^=consumerDiv] button", consumers).each(function(index, el){
		removeConsumer(el);
	});
}

function createOrdererYaml(){
	var orderer = $("#orderer").val();
	return "orderer: "+orderer+"\n"; 
}

function createCollectionReaderYaml(){
	var content = "collectionreader:\n";
	
	var collectionreader = $("#collectionReader").val();
	
	content += "  class: "+ collectionreader + "\n";
	$("#collectionReaderParameters .form-control").each(function(index, element){
		var value = $(element).val();
		var id = $(element).attr("id");

		if(value != null && value != $("#"+id+"-default").val()){
			content += "  " + id.substring(collectionreader.length + 1) + ": ";
			if(value.indexOf("\n") != -1){
				var values = value.match(/[^\r\n]+/g);
				content += "\n";
				values.forEach(function(el){
					content += "  - "+el + "\n";
				});
			}else if(value == ""){
					params.push(paramName + "\"\"");
			}else{
				content += value + "\n";
			}
		}
	});
	
	var contentextractor = $("#contentExtractor").val();
	if(contentextractor != $("#contentExtractor-default").val()){
		content += "  contentExtractor: "+ contentextractor + "\n";
	}
	
	$("#contentExtractorParameters .form-control").each(function(index, element){
		var value = $(element).val();
		var id = $(element).attr("id");

		if(value != null && value != $("#"+id+"-default").val()){
			content += "  " + id.substring(contentextractor.length + 1) + ": ";
			if(value.indexOf("\n") != -1){
				var values = value.match(/[^\r\n]+/g);
				content += "\n";
				values.forEach(function(el){
					content += "  - "+el + "\n";
				});
			}else if(value == ""){
					params.push(paramName + "\"\"");
			}else{
				content += value + "\n";
			}
		}
	});
	
	return content;
}

function createAnnotatorYaml(){
	var content = "annotators:\n";
	
	$("#annotators .annotatorForm").each(function(index, form){
		var id = $("#annotatorId", form).val();
		var annotator = $("#annotator-"+id, form).val();
		var params = [];
		
		$("#annotatorParameters-"+id+" .form-control").each(function(index, element){
			var value = $(element).val();
			var paramId = $(element).attr("id");

			if(value != null && value != $("#"+paramId+"-default").val()){
				var paramName = paramId.substring(annotator.length + 1, paramId.length - (1+id.length)) + ": ";
				
				if(value.indexOf("\n") != -1){
					params.push(paramName);
					var values = value.match(/[^\r\n]+/g);
					values.forEach(function(el){
						params.push("- "+el);
					});
				}else if(value == ""){
					params.push(paramName + "\"\"");
				}else{
					params.push(paramName + value);
				}
			}
		});
		
		if(params.length == 0){
			content += "  - "+annotator+"\n";
		}else{
			content += "  - class: "+annotator+"\n";
			params.forEach(function(param){
				content += "    "+ param + "\n";
			});
		}
	});
	
	return content;
}

function createConsumerYaml(){
	var content = "consumers:\n";
	
	$("#consumers .consumerForm").each(function(index, form){
		var id = $("#consumerId", form).val();
		var consumer = $("#consumer-"+id, form).val();
		var params = [];
		
		$("#consumerParameters-"+id+" .form-control").each(function(index, element){
			var value = $(element).val();
			var paramId = $(element).attr("id");

			if(value != null && value != $("#"+paramId+"-default").val()){
				var paramName = paramId.substring(consumer.length + 1, paramId.length - (1+id.length)) + ": ";
				
				if(value.indexOf("\n") != -1){
					params.push(paramName);
					var values = value.match(/[^\r\n]+/g);
					values.forEach(function(el){
						params.push("- "+el);
					});
				}else if(value == ""){
					params.push(paramName + "\"\"");
				}else{
					params.push(paramName + value);
				}
			}
		});
		
		if(params.length == 0){
			content += "  - "+consumer+"\n";
		}else{
			content += "  - class: "+consumer+"\n";
			params.forEach(function(param){
				content += "    "+ param + "\n";
			});
		}
	});
	
	return content;
}

function createResourcesYaml(){
	var content = "";
	var resources = $("#resources");
	
	var groups = {};
	var values = {};
	var defaultValues = {};
	$(":text", resources).each(function(index, element){
		var key = $(element).attr("id").split("-",2)[1];
		var keyParts = key.split("_",2);
		
		var existing = groups[keyParts[0]];
		if(existing == null || existing == undefined || !Array.isArray(existing)){
			existing = [];
		}
		existing.push(keyParts[1]);
		groups[keyParts[0]] = existing;
		
		values[key] = $(element).val();
		defaultValues[key] = $("#" + $(element).attr("id") + "-default").val();
	});
	
	for(var group in groups){
		var params = "";
		for(var key in groups[group]){
			var value = values[group + "_" + groups[group][key]];
			var defaultValue = defaultValues[group + "_" + groups[group][key]]
			if(value != null && value != defaultValue){
				params += "  " + groups[group][key] + ": ";
				
				if(value.indexOf("\n") != -1){
					params += "\n";
					var valLines = value.match(/[^\r\n]+/g);
					valLines.forEach(function(el){
						params += "    - "+el + "\n";
					});
				}else if(value == ""){
					params += "\"\"\n";
				}else{
					params += value + "\n";
				}
			}
		}
		
		if(params != ""){
			content += group +":\n";
			content += params += "\n";
		}
	}
	
	return content;
}

function toTextArea(id){
	var value = $("#"+id).val();
	var textarea = "<textarea class=\"form-control\" id=\""+id+"\" placeholder=\"Enter value here if you wish to set one\">"+value+"</textarea>";
	
	$("#"+id).replaceWith(textarea);
}

function sanitizeName(name){
	if(name == undefined || name == null){
		return "";
	}else{
		return name.replace(".", "_");
	}
}

function createPipelineName(){
	var adjectives = ["Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Black", "Grey", "White", "Fizzing", "Noisy", "Enormous", "Tiny", "Spinning", "Sleeping", "Peaceful", "Heavy", "Average", "Genuine", "Fake", "Peculiar", "Colossal", "Bewildered", "Clever", "Intense", "Fanciful", "Scrumptious", "Inspired", "Fluffy", "Tangy", "Fuzzy", "Selective", "Blurry", "Heroic", "Gooey", "Witty", "Brilliant", "Zealous", "Courageous", "Dynamic", "Valiant", "Rambunctious", "Confident", "Daring", "Opulent", "Sour", "Vivacious", "Murky", "Quaint", "Stupendous", "Sparkling", "Zany", "Ferocious", "Curious", "Determined", "Dubious", "Energetic", "Remarkable", "Wistful", "Exuberant", "Bold", "Inquisitive", "Mysterious", "Ecstatic", "Collapsible", "Disposable", "Disconnected", "Enchanted", "Disillusioned", "Unavailable", "Erroneous", "Challenging", "Adventurous", "Capable", "Mistaken", "Snotty", "Quenchable", "Delicious", "Destructive", "Bias", "Masked", "Musical", "Dodgy", "Magnanimous", "Shady", "Untouchable", "Indescribable", "Worthy"];
	var nouns = ["Banana", "Apple", "Pear", "Strawberry", "Burger", "Pizza", "Cake", "Car", "Lorry", "Boat", "Submarine", "Helicopter", "Pyjamas", "Jumper", "Hat", "Wizard", "Witch", "Magician", "Programmer", "Coder", "River", "Pond", "Lake", "Sea", "Doctor", "Tardis", "King", "Queen", "Knight", "Bishop", "Castle", "Rook", "Pawn", "Raven", "Seagull", "Crow", "Pigeon", "Chicken", "Duck", "Flower", "Gnome", "Tree", "Garden", "Balloon", "Mountain", "Hill", "Train", "Frog", "Cauldron", "School", "Hut", "Flag", "Uniform", "Hotel", "Gulf", "Island", "Mouth", "Eye", "Ear", "Nose", "Cloud", "Sun", "Moon", "Zoo", "X-ray", "Quilt", "Duvet", "Pillow", "Telly", "Crocodile", "Alligator", "Goat", "Carbuncle", "Pimpernel", "Avenger", "Hiker", "Scout", "Trout", "Thirst", "Native", "Handset", "Monitor", "Angel", "Baby", "Wise Men", "Shepherd", "Donkey", "Egg", "Peasant", "Christmas Tree", "Bracelet", "Necklace", "Lanyard", "Bottle", "Footballer", "Cricketer", "Referee", "Conductor", "Opponent", "Lion", "Tiger", "Zebra", "Emperor", "Empress", "Pencil", "Sock"];
	
	return adjectives[Math.floor(Math.random() * adjectives.length)] + " " + nouns[Math.floor(Math.random() * adjectives.length)];
}

function createYaml(){
	var yaml = createOrdererYaml();
	yaml += "\n" + createCollectionReaderYaml();
	yaml += "\n" + createAnnotatorYaml();
	yaml += "\n" + createConsumerYaml();
	yaml += "\n" + createResourcesYaml();
	
	return yaml;
}

function viewYaml(){
	$("#yaml").html("<pre>"+createYaml()+"</pre>");
	$("#yamlTitle").text("Generated YAML for Pipeline '"+$("#pipelineName").val()+"'");
	$("#yamlModal").modal("show");
}

function createPipeline(){
	$.ajax({
		url: baleenUrl + "api/1/pipelines",
		type: "POST",
		data: {
			name: $("#pipelineName").val(),
			yaml: createYaml(),
		}
	}).done(function(data){
		$("#successPipeline").removeClass("hidden");
		$(document).scrollTop($("#successPipeline").offset().top - 75);
		getExistingPipelines();
	}).error(function(data){
		$("#alertCouldntCreate").removeClass("hidden");
		$(document).scrollTop($("#alertCouldntCreate").offset().top - 75);
	});
}

function showNoResources(){
	if($("#resources div").length == 0){
		$("#resources").append("<p id=\"noResources\">There are currently no annotators that require external resources</p>");
	}
}

$("#collectionReader").change(function(){
	getCollectionReader($("#collectionReader").val());
	$("#collectionReaderName").text($("#collectionReader").val());
});
$("#contentExtractor").change(function(){
	getContentExtractor($("#contentExtractor").val());
	$("#contentExtractorName").text($("#contentExtractor").val());
});

$("#pipelineName").val(createPipelineName());
