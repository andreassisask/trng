function get(format) {
	log('get, format=' + format);
	if (format == 'raw') {
		download(getQuantity(), format);
	}
}

function getQuantity() {
	var amountSpinner = document.getElementById("quantity");
	return amountSpinner.value;
}

function download(quantity, format) {
	var params = ['quantity=' + quantity, 'format=' + format].join("&");
	log(params);
	location.href="trng?" + params;
}

function log(msg) {
	window.console.info(msg);
}