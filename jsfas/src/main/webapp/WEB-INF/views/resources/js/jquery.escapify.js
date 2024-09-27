/*!
 * Escapify JQuery Plugin Library v0.1.0
 * http://www.therubinway.com/escapify
 *
 * Copyright 2010, Alan Rubin
 * Licensed under the MIT license.
 */
(function($){
	
	// Escapify HTML
	$.escapifyHTML = function(text){
		if(text) {
			return $('<div/>').text(text).html();
		} else {
			return ' ';
		}
	};
	
	// Unescapify HTML
	$.unescapifyHTML = function(text){
		if(text) {
			return $('<div/>').html(text).text();
		} else {
			return ' ';
		}
	};
	
})(jQuery);