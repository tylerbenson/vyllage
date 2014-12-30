(function () {
	var changeMode =  function (event, edit){
		event.preventDefault();
		event.stopPropagation();

		var mainElements = this.getElementsByClassName('main'),
			editElements = this.getElementsByClassName('edit');

			for(var i = 0; i < mainElements.length; i++){
				mainElements[i].style.display = edit ? "none" : "block";
			}
			for(var i = 0; i < editElements.length; i++){
				editElements[i].style.display = edit ? "block" : "none";
			}
		};

	window.onload = function(){
		var elem = document.getElementsByClassName('article-content'),
		    saveBtn, cancelBtn, currentElem;

		for(var i = 0; i < elem.length; i++) {

			currentElem = elem[i],
			saveBtn = currentElem.getElementsByClassName('save-btn'),
		    cancelBtn = currentElem.getElementsByClassName('cancel-btn');

			if (currentElem.addEventListener) {  // For all major browsers, except IE 8 and earlier

			    currentElem.addEventListener("click", changeMode.bind(currentElem, event, true) , true );
			    
			    if(saveBtn.length >0  && cancelBtn.length >0) {
			    	saveBtn[0].addEventListener("click", changeMode.bind(currentElem, event, false), false );
				    cancelBtn[0].addEventListener("click", changeMode.bind(currentElem, event, false), false );
			    }
			} else if (currentElem.attachEvent) {  // For IE 8 and earlier versions

			   currentElem.attachEvent("onclick", changeMode.bind(currentElem,  event, true) , false );

			    if(saveBtn.length >0  && cancelBtn.length >0) {
			    	saveBtn[0].attachEvent("onclick", changeMode.bind(currentElem,  event, false), false );
				    cancelBtn[0].attachEvent("onclick", changeMode.bind(currentElem,  event, false), false );
			    }
			}
		}
	}

})();
