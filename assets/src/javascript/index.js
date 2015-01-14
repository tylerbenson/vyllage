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
				if(edit){
					this.className = this.className + " editMode";
					this.parentNode.parentNode.className = this.parentNode.parentNode.className + " editMode";
				} else {
					this.classList.remove("editMode");
					this.parentNode.parentNode.classList.remove("editMode");
				}
			}

			return false;
		};

	window.onload = function(){

		// articles hover state implementation

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

		// Contact info sections implementation
		var shareBtn = document.getElementById('shareInfoBtn'),
		    contactBtn = document.getElementById('contactInfoBtn');

		    if (shareBtn.addEventListener) {  

			    shareBtn.addEventListener("click", function () {

			    	document.getElementById('share-info').style.display =  "block" ;
		    		document.getElementById('contact-info').style.display =  "none";

		    		this.style.backgroundColor =  "#ece7e4";
		    		contactBtn.style.backgroundColor =  "#ffffff";

			    } , true );
			    
			} else if (shareBtn.attachEvent) {  

			   shareBtn.attachEvent("onclick", function () {
			    	document.getElementById('share-info').style.display =  "block" ;
		    		document.getElementById('contact-info').style.display =  "none";

		    		this.style.backgroundColor =  "#ece7e4";
		    		contactBtn.style.backgroundColor =  "#ffffff";
			    } , false );
			}

			if (contactBtn.addEventListener) {  

			    contactBtn.addEventListener("click", function () {
			    	document.getElementById('share-info').style.display =  "none" ;
		    		document.getElementById('contact-info').style.display =  "block";

		    		this.style.backgroundColor =  "#ece7e4";
		    		shareBtn.style.backgroundColor =  "#ffffff";
			    } , true );
			    
			} else if (contactBtn.attachEvent) {  

			   contactBtn.attachEvent("onclick", function () {
			    	document.getElementById('share-info').style.display =  "none" ;
		    		document.getElementById(' contact-info').style.display =  "block";

		    		this.style.backgroundColor =  "#ece7e4";
		    		shareBtn.style.backgroundColor =  "#ffffff";
			    } , false );
			}

			// show comments implementation

			var commentsButtons = document.getElementsByClassName('comments');

			for(var i = 0; i < commentsButtons.length; i++) {

			    if (commentsButtons[i].addEventListener) {  

				    commentsButtons[i].addEventListener("click", function (event) {

			    		event.preventDefault();
						event.stopPropagation();
						this.parentNode.className = this.parentNode.className + " article-controll-btn-wrapper";
						this.parentNode.parentNode.parentNode.nextElementSibling.style.display="block";
				    } , true );
				    
				} else if (commentsButtons[i].attachEvent) {  

					event.preventDefault();
					event.stopPropagation();

				   commentsButtons[i].attachEvent("onclick", function () {	
				   		this.parentNode.className = this.parentNode.className + " article-controll-btn-wrapper";
				   		this.parentNode.parentNode.parentNode.nextElementSibling.style.display="block";
				    } , true );
				}
			}

	}

})();
