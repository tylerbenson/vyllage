(function () {
	var goToEditMode =  function (event){
							var mainElements = this.getElementsByClassName('main'),
								editElements = this.getElementsByClassName('edit');

								for(var i = 0; i < mainElements.length; i++){
									mainElements[i].style.display = "none";
								}
								for(var i = 0; i < editElements.length; i++){
									editElements[i].style.display = "block";
								}
							};

	window.onload = function(){
		var elem = document.getElementsByClassName('article-content');

		for(var i = 0; i < elem.length; i++){
			if (elem[i].addEventListener) {                    // For all major browsers, except IE 8 and earlier
			    elem[i].addEventListener("click", goToEditMode , false );
			} else if (elem[i].attachEvent) {                  // For IE 8 and earlier versions
			    elem[i].attachEvent("onclick", goToEditMode , false );
			}
		}
	}

})();
