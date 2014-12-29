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

			return false;
		},
		
		goToSaveMode =  function (event){

		var mainElements = this.parentNode.parentNode.getElementsByClassName('main'),
			editElements = this.parentNode.parentNode.getElementsByClassName('edit');

			for(var i = 0; i < mainElements.length; i++){
				mainElements[i].style.display = "block";
			}
			for(var i = 0; i < editElements.length; i++){
				editElements[i].style.display = "none";
			}
			event.stopPropagation();
			return false;
		};

	window.onload = function(){
		var elem = document.getElementsByClassName('article-content'),
		    saveBtn, cancelBtn;

		for(var i = 0; i < elem.length; i++) {

			saveBtn = elem[i].getElementsByClassName('save-btn');
		    cancelBtn = elem[i].getElementsByClassName('cancel-btn');

			if (elem[i].addEventListener) {  // For all major browsers, except IE 8 and earlier
			    elem[i].addEventListener("click", goToEditMode , false );
			    
			    if(saveBtn.length >0  && cancelBtn.length >0) {
			    	saveBtn[0].addEventListener("click", goToSaveMode, false );
				    cancelBtn[0].addEventListener("click", goToSaveMode, false );
			    }
			} else if (elem[i].attachEvent) {  // For IE 8 and earlier versions
			    elem[i].attachEvent("onclick", goToEditMode , false );

			     if(saveBtn.length >0  && cancelBtn.length >0) {
			    	saveBtn[0].attachEvent("onclick", goToSaveMode, false );
				    cancelBtn[0].attachEvent("onclick", goToSaveMode, false );
			    }
			}
		}
	}

})();
