(function(){

	// handle auto resizing of textareas
	var resizeTextAreas = function () {
		var resizingTextareas = [].slice.call(document.querySelectorAll('textarea'));

		resizingTextareas.forEach(function(textarea) {
		  textarea.addEventListener('input', autoresize, false);
		});

		function autoresize() {
		    this.style.height = 0;
		    this.style.height = this.scrollHeight  +'px';
		}
	}

	resizeTextAreas();
})();
	