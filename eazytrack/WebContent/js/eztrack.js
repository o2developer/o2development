/**
 * 
 */

/* Global ajax error handler function that redirects to login page */
$(document).ajaxError(function(event, jqxhr, settings, exception) {
	var errorCodes = [401, 403, 302];
	console.log("Global error status: " + jqxhr.status);
	if (errorCodes.indexOf(jqxhr.status) != -1) {
		window.location.replace("login.html");
	}
});