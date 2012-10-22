function get(format) {
	log('get, format=' + format);
	var quantity = getQuantity();
	if (format == 'raw') {
		download(quantity, format);
	} else {
		display(quantity, format);
	}
}

function getQuantity() {
	var amountSpinner = document.getElementById("quantity");
	return amountSpinner.value;
}

function getUrl(quantity, format) {
	var params = ['quantity=' + quantity, 'format=' + format].join("&");
	return "trng?" + params;
}

function display(quantity, format) {
	postStuffToServlet({
		url: getUrl(quantity, format), 
		success: 
			function(data){
				$("#result").val(data);
			}, 
	});
}

function download(quantity, format) {
	location.href = getUrl(quantity, format);
}

function postStuffToServlet(props){
	var servletUrl = props.url || "";
	log('out@' + servletUrl + '->' + JSON.stringify(props.data));
	
	var errorFunction = props.error || function(request, status, error) {
		alert("Error" + (request.status == 499? (": \"" + request.responseText + "\"") : "!"));
	};
	
	$.ajax({
		type : "GET", 
		url : servletUrl, 
		data : props.data, 
		async : props.async,
		contentType : props.contentType,
		processData : props.processData,
		success : function(data, statusText, jqXHR){
				log('in@' + servletUrl + '->' + data);
				if (props.success){
					props.success(data, statusText, jqXHR);
				}
			}, 
		error : errorFunction,
		complete : props.complete
	});
}

function log(msg) {
	window.console.info(msg);
}