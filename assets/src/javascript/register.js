(function () {

	window.onload = function(){

	// ---------------------------- Contact info sections implementation -----------------

		var shareBtn = document.getElementById('shareInfoBtn'),
		    contactBtn = document.getElementById('contactInfoBtn');

	    if (shareBtn.addEventListener) {  

		    shareBtn.addEventListener("click", function (event) {
		    	event.preventDefault();
				event.stopPropagation();

		    	document.getElementById('share-info').style.display =  "block" ;
	    		document.getElementById('contact-info').style.display =  "none";

	    		this.style.backgroundColor =  "#ece7e4";
	    		contactBtn.style.backgroundColor =  "#ffffff";

		    } , true );
		    
		} else if (shareBtn.attachEvent) {  

		   shareBtn.attachEvent("onclick", function (event) {
		   		event.preventDefault();
				event.stopPropagation();

		    	document.getElementById('share-info').style.display =  "block" ;
	    		document.getElementById('contact-info').style.display =  "none";

	    		this.style.backgroundColor =  "#ece7e4";
	    		contactBtn.style.backgroundColor =  "#ffffff";
		    } , false );
		}

		if (contactBtn.addEventListener) {  

		    contactBtn.addEventListener("click", function (event) {
		    	event.preventDefault();
				event.stopPropagation();

		    	document.getElementById('share-info').style.display =  "none" ;
	    		document.getElementById('contact-info').style.display =  "block";

	    		this.style.backgroundColor =  "#ece7e4";
	    		shareBtn.style.backgroundColor =  "#ffffff";
		    } , true );
		    
		} else if (contactBtn.attachEvent) {  

		   contactBtn.attachEvent("onclick", function (event) {
		   		event.preventDefault();
				event.stopPropagation();

		    	document.getElementById('share-info').style.display =  "none" ;
	    		document.getElementById(' contact-info').style.display =  "block";

	    		this.style.backgroundColor =  "#ece7e4";
	    		shareBtn.style.backgroundColor =  "#ffffff";
		    } , false );
		}

	// ---------------------------- End Contact info sections implementation -----------------

	// ---------------------------- Profile edit buttons implementation -----------------

		var editButtons = document.getElementsByClassName('edit-icon');

		for(var i = 0; i < editButtons.length; i++) {

			if (editButtons[i].addEventListener) {

			    editButtons[i].addEventListener("click", function (event){
			    	event.preventDefault();
					event.stopPropagation();

			    	document.getElementsByClassName('headline-container main')[0].style.display =  "none";
			    	document.getElementsByClassName('headline-container edit')[0].style.display =  "block";
			    	document.getElementsByClassName('headline-container edit')[0].getElementsByClassName('edit')[0].style.display =  "block";
			    }, true);

			} else if (editButtons[i].attachEvent) { 

				editButtons[i].attachEvent("onclick", function (event){
					event.preventDefault();
					event.stopPropagation();
					
					document.getElementsByClassName('headline-container main').style.display =  "none";
			    	document.getElementsByClassName('headline-container edit').style.display =  "block";

		    	}, false);
			}
		}
	// ---------------------------- End Profile edit buttons implementation -----------------

	}
})();
