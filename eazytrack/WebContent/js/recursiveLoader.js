function loadRecursive2(context, cb) {
	//search matching elements that have 'place-holder' class
	$('.place-holder', context).each(function() {
		var self = $(this);
		$.get(self.attr('include-file'), function(data, textStatus) {
			self.html(data);
			// Load the data into the placeholder
			//cb();
			// fire the callback
			loadRecursive2(self);
			// Fix sub placeholders
			self.replaceWith(self.get(0).childNodes);
			// Unwrap the content
		}, 'html');
	});
}

function loadRecursive(context) {
	//search matching elements that have 'place-holder' class
	$('.place-holder', context).each(function() {
		var that = $(this);
		$.get(that.attr('include-file'), function(data, textStatus) {
			repl = $(data);
			that.replaceWith(repl);
			loadRecursive(repl);
		}, 'html');
	});
}

function loadTree($element) {
	$element.each(function() {
		self = $(this);
		self.load(self.attr("include-file"), function() {
			loadTree($(this).find('.children'));
			//TODO:
			//var cnt = this.contents()
			$(this).replaceWith($(this).contents());
		});
	});
}
