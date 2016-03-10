/**
 * @author rv
 */

/**
 * Returns today's date as a string.
 */


 window.result_clone = '' ;

function today() {
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth() + 1;
	//January is 0!

	var yyyy = today.getFullYear();
	if (dd < 10) {
		dd = '0' + dd;
	}
	if (mm < 10) {
		mm = '0' + mm;
	}
	//today = mm + '/' + dd + '/' + yyyy;
	today = dd + "/" + mm + "/" + yyyy;
	return today;
}

/**
 * Loads list of available items into a dilaog (a given element)
 * @param {Object} element
 */
function loadItems(element) {

	/* If already loaded, return */
	if (element.attr("loaded") == "true") {
		return;
	}

	/* Make an ajax call n retrieve the data */
	$.ajax({
		url : "/eazytrack/EzTrackServlet?opcode=3&data={}",
		//	url: "responses/item-list.json",
		type : "GET",
		dataType : "json",
		success : function(data) {
			var html = "";
			for (var i = 0; i < data.length; i++) {
				html += "<tr>";
				html += "<td>" + data[i].code + "</td>";
				html += "<td>" + data[i].name + "</td>";
				html += "<td>" + data[i].qty + "</td>";
				html += "</tr>";
			}

			element.find("tbody").empty().append(html);
			element.attr("loaded", "true");
		}
	});
}

/**
 * Converts incoming json data into something suitable for datatables loading.
 */
function transform2DataTableFormat(data) {

	var result = new Object();
	result.aaData = [];
	for (var i = 0; i < data.length; i++) {
		var row = [];
		row.push("");
		row.push(data[i].code);
		row.push(data[i].name);
		row.push(data[i].qty);
		row.push(data[i].id);
		row.push(data[i].category);
		row.push(data[i].rate);
		row.push(data[i].discnt_percent);
		row.push(data[i].catid);
		row.push(data[i].cost);
		row.push(data[i].taxes);
		row.push(data[i].units);
		row.push(data[i].vat);
		result.aaData.push(row);
	}
	return result;
}

/**
 * Scrolls a given row element into the view of the container.
 * @param {Object} element
 * @param {Object} container
 */
function scrollIntoView(element, container) {

	var containerTop = $(container).scrollTop();
	var containerBottom = containerTop + $(container).height();
	var elemTop = element.offset().top;
	//element.offsetTop;
	var elemBottom = elemTop + $(element).height();
	if (elemTop < containerTop) {
		$(container).scrollTop(elemTop);
	} else if (elemBottom > containerBottom) {
		$(container).scrollTop(elemBottom - $(container).height());
	}
}

function isCharacterKeyPress(evt) {
	if ( typeof evt.which == "undefined") {
		// This is IE, which only fires keypress events for printable keys
		return true;
	} else if ( typeof evt.which == "number" && evt.which > 0) {
		// In other browsers except old versions of WebKit, evt.which is
		// only greater than zero if the keypress is a printable key.
		// We need to filter out backspace and ctrl/alt/meta key combinations
		return !evt.ctrlKey && !evt.metaKey && !evt.altKey && evt.which != 8;
	}
	return false;
}

/**
 * Allows entering of numbers only in a textbox.
 */
function allowNumbers(event) {
	// Allow: backspace, delete, tab, escape, enter and .
	if ($.inArray(event.keyCode, [46, 8, 9, 27, 13, 190]) !== -1 ||
	// Allow: Ctrl+A
	(event.keyCode == 65 && event.ctrlKey === true) ||
	// Allow: home, end, left, right
	(event.keyCode >= 35 && event.keyCode <= 39)) {
		// let it happen, don't do anything
		return;
	} else {
		// Ensure that it is a number and stop the keypress
		if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) {
			event.preventDefault();
		}
	}
}

/**
 * Displays an alert message to the top of the page.
 * @param {Object} msg
 * @param {Object} type - info, danger, warning
 * @param {Object} replace - whether or not to replace old message if existing.
 */
function showMessage(msg, type, replace) {

	/* if already there is an alert, and its message is the same
	 * then shake it.
	 */
	if ($("#content-row #msg.alert").length && replace != true) {
		return;
	} else if ($("#content-row #msg.alert").length) {
		$("#content-row #msg.alert p").text(msg);
		$("#content-row #msg.alert").effect("bounce");
		return;
	}

	if (type == undefined)
		type = "danger";

	var html = "<div id='msg' class='alert alert-dismissable alert-";
	html += type + "'>";
	html += "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>";
	html += "<p>" + msg + "</p></div>";

	$("#content-row").prepend(html);

}

/**
 *
 */
function removeMessage() {
	$("#content-row #msg.alert").remove();
}

/**
 * Tries to find val in target object.
 * if found, returns the matching object, else "".
 * @param {Object} target
 * @param {Object} val
 */
function findEntry(target, val) {
	console.log("taget in exception area"+target[key]);
	for (var key in target) {
		if (target.hasOwnProperty(key)) {
			if (target[key].value.toLowerCase() == val.toLowerCase()) {
				return target[key].datum;
			}
		}
	}
}



$("div#canvas").delegate("a.panel-close", "click", function() {
	//$(".panel-close").on("click", function() {
	var result = confirm("Do you want to leave this page?");
	if (result == true) {
		$("#canvas").load("home.html");
	}
});


/*
 *    To close the panel and go to the sales again  
 */
$("div#canvas").delegate("a.sale-panel-close", "click", function() {
	//$(".panel-close").on("click", function() {
	var result = confirm("Do you want to go back to sales report?");
	if (result == true) {
		$("#canvas").load('sales-report.html');   //window.result_clone.val
	}
});


/*
 *    To close the panel and go to the Purchase Reports Again 
 */
$("div#canvas").delegate("a.purchase-panel-close", "click", function() {
	//$(".panel-close").on("click", function() {
	var result = confirm("Do you want to go back to Purchases report?");
	if (result == true) {
		$("#canvas").load('purchases-report.html');   //window.result_clone.val
	}
});

/**
 * Returns a blank cpanel entity with heading and body.
 */
function getPanelTemplate(expanded) {
	var panelTemplate = "<div class='cpanel cpanel-default'>";
	panelTemplate += "<div class='cpanel-heading'>";
	if (expanded == undefined || expanded == true) {
		panelTemplate += "<a href='#' class='panel-expand'><i class='fa fa-minus-square'></i> </a> &nbsp;";
	} else {
		panelTemplate += "<a href='#' class='panel-expand'><i class='fa fa-plus-square'></i> </a> &nbsp;";
	}
	panelTemplate += "<p class='inline'></p>";
	// panelTemplate += "<span class='glyphicon glyphicon-remove titlebar-icon pull-right'></span>";
	panelTemplate += "</div>";
	if (expanded == undefined || expanded == true) {
		panelTemplate += "<div class='cpanel-body'></div>";
	} else {
		panelTemplate += "<div class='cpanel-body' hidden></div>";
	}
	panelTemplate += "</div>";

	return panelTemplate;

}


/**
 * Returns a url for all ajax GET/POST requests for this application.
 * URLs will be mentioned in a static json file called 'request-map.json'.
 *
 * @param {Object} opCode - for which the url is requested.
 * @param {Object} local - a boolean value indicating if local sample url
 * 							should be used, or actual remote URL.
 * 							Local URL helps in quick development of pages
 * 							without having to retrieve real data from a server.
 */
function getRequestUrl(opCode, local) { 
	/** Retrieve the json file, if not already done so. **/
	if (window.requestMap == undefined) {
		$.ajax({
			url : "responses/request-map.json",
			dataType : "json",
			async : false,
			success : function(data) {
				window.requestMap = data;
			},
			error : function(xhr, status, error) {
				return null;
			}
		});
	}

	var urlType = local == true ? "local_url" : $.cookie("local_url") == "true" ? "local_url" : "server_url";
	console.log("url type: " + urlType);
	for (var i = 0; i < window.requestMap.length; i++) {
		if (window.requestMap[i].req_code == opCode) {
			return window.requestMap[i][urlType];
		}
	}
	return null;
}

function errorP(msg) {
	var html = "<div class='inline'><i class='fa fa-times'></i>";
	html += "<p class='inline'>" + msg + "</p> </div>";
	return html;
}
