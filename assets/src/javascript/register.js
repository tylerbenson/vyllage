(function () {

	window.onload = function(){

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
}

})();
