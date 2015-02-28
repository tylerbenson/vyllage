var React = require('react');
var Header = require('./components/header');
var Profile = require('./components/profile');
var ContactInfo = require('./components/contact/contact');
var ShareInfo = require('./components/contact/share');
var FreeformContainer = require('./components/freeform/container');
var ArticleContent = require('./components/organization/article-content');
var ArticleControlls =require('./components/freeform/article-controlls');
var CommentsBlog = require('./components/freeform/comments-blog');
var AddSections = require('./components/addSections/addSections');
var request = require('superagent');


var MainData =[
{
    "type": "freeform",
    "title": "career goal",
    "sectionId": 123,
    "sectionPosition": 1,
    "state": "shown",
    "description": "this is my goal statement."
},
{
    "type": "experience",
    "title": "experience",
    "sectionId": 124,
    "sectionPosition": 6,
    "state": "shown",
    "organizationName": "DeVry Education Group",
    "organizationDescription": "Blah Blah Blah.",
    "role": "Manager, Local Accounts",
    "startDate": "September 2010",
    "endDate": "",
    "isCurrent": true,
    "location": "Portland, Oregon",
    "roleDescription": "Blah Blah Blah",
    "highlights": "I was in charge of..."
},
{
    "type": "experience",
    "title": "education",
    "sectionId": 125,
    "sectionPosition": 3,
    "state": "hidden",
    "organizationName": "Keller Graduate School of Management",
    "organizationDescription": "Blah Blah Blah.",
    "role": "Masters of Project Management",
    "startDate": "September 2010",
    "endDate": "September 2012",
    "isCurrent": false,
    "location": "Portland, Oregon",
    "roleDescription": "tryrty",
    "highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
},
{
    "type": "freeform",
    "title": "skills",
    "sectionId": 126,
    "sectionPosition": 4,
    "description": "basket weaving, spear fishing, dominion"
},
{
    "type": "freeform",
    "title": "skills",
    "sectionId": 127,
    "sectionPosition": 7,
    "description": "basket weaving, spear fishing, dominion"
},
{
    "type": "freeform",
    "title": "skills",
    "sectionId": 128,
    "sectionPosition": 5,
    "description": "basket weaving, spear fishing, dominion"
},
{
    "type": "experience",
    "title": "education",
    "sectionId": 129,
    "sectionPosition": 2,
    "state": "hidden",
    "organizationName": "Pisik",
    "organizationDescription": "Blah Blah Blah.",
    "role": "Masters of Project Management",
    "startDate": "September 2010",
    "endDate": "September 2012",
    "isCurrent": false,
    "location": "Portland, Oregon",
    "roleDescription": "tyrty",
    "highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
},
];

function compare(a,b) {
    
    if (a.sectionPosition < b.sectionPosition){
        return -1;
    }
    if (a.sectionPosition > b.sectionPosition) {
        return 1;
    }
    return 0;
}

var MainContainer = React.createClass({  
    
    getInitialState: function() {
        return {mainData: []};
    },

    componentDidMount : function() {
        var self = this, documentId,
            pathItems = window.location.pathname.split("/");
        
        if(pathItems.length > 1) {
            documentId = pathItems[pathItems.length-1];

            request
               .get('/resume/' + documentId + '/section')
               .set('Accept', 'application/json')
               .end(function(error, res) {

                    if (res.ok) {
                        if(res.body.length == 0) {
                            // if data from server is empty , apply hardcoded data
                            MainData.sort(compare);
                            self.setState({mainData: MainData});
                        } else {
                            self.setState({mainData: res.body});
                        }
                    } else {
                       alert( res.text );
                   }             
            });
        } else {
            // this case should not happen, throw error
        }
    },

    componentDidUpdate: function() {

        var resizingTextareas = [].slice.call(document.querySelectorAll('textarea'));

        resizingTextareas.forEach(function(textarea) {
          textarea.addEventListener('input', autoresize, false);
        });

        function autoresize() {
            this.style.height = 0;
            this.style.height = this.scrollHeight  +'px';
        }
    },

    saveChanges: function (data) {
        var self = this, documentId,
            pathItems = window.location.pathname.split("/");
        
        if(pathItems.length > 1) {
            documentId = pathItems[pathItems.length-1];

            request
                .post('/resume/' + documentId + '/section/' + data.sectionId +'')
                .set('Accept', 'application/json')
                .send(data)
                .end(function(error, res) {

                    //if (res.ok) { commenting out the success check , since now server returns error 
                        for(var i = 0; i < self.state.mainData.length; i++){

                            if(self.state.mainData[i].sectionId === data.sectionId){

                                self.state.mainData[i] = data;
                                self.setState({mainData: self.state.mainData});
                                return;
                            }
                        }
                    // } else {
                    //    alert( res.text );
                    // }  
                });
        }
    },

    addSection: function (type,sectionPosition) {
        var id = MainData.length + 1,
                 position = sectionPosition,
                 data = this.state.mainData;

         // Set position           
         data.map(function(result) {
            if(result.sectionPosition >= position)
            {
                result.sectionPosition = result.sectionPosition + 1;
            }
        });

        this.state.editModePosition = position;

        switch(type) {
            case 'career goal':
                    data.push({
                    "type": "freeform",
                    "title": "career goal",
                    "sectionId": id,
                    "sectionPosition": position,
                    "state": "shown",
                    "description": ""
                });
                break;
            case 'skills':
                data.push({
                    "type": "freeform",
                    "title": "skills",
                    "sectionId": id,
                    "sectionPosition": position,
                    "state": "shown",
                    "description": ""
                });
                break;
            case 'experience':
                    data.push({
                    "type": "experience",
                    "title": "experience",
                    "sectionId": id,
                    "sectionPosition": position,
                    "state": "shown",
                    "organizationName": "",
                    "organizationDescription": "",
                    "role": "",
                    "startDate": "",
                    "endDate": "",
                    "isCurrent": false,
                    "location": "",
                    "roleDescription": "",
                    "highlights": ""
                });
                break;
            case 'education':
                    data.push({
                    "type": "experience",
                    "title": "education",
                    "sectionId": id,
                    "sectionPosition": position,
                    "state": "shown",
                    "organizationName": "",
                    "organizationDescription": "",
                    "role": "",
                    "startDate": "",
                    "endDate": "",
                    "isCurrent": false,
                    "location": "",
                    "roleDescription": "",
                    "highlights": ""
                });
                break;
        }      

        // Sort by sectionPosition
        data.sort(compare);
        this.setState({mainData: data});
    },

    render: function() {
        var results = this.state.mainData,
            that = this,
            experienceCount = 0,
            educationCount = 0,
            careergoalCount = 0,
            skillsCount = 0,
            addNewItem = 0;

        return (
            <div>
                {
                results.map(function(result) {
                    if(result.type === "freeform"){
                        addNewItem = 0;
                        if(careergoalCount == 0  && result.title == "career goal") {
                            addNewItem = 1;
                            careergoalCount = careergoalCount + 1;
                        }

                        if(skillsCount == 0 && result.title == "skills") {
                            addNewItem = 1;
                            skillsCount = skillsCount + 1;
                        }

                        if (addNewItem == 1) {
                            addNewItem = 0;
                            return (
                             <article className="career-goal">
                                 <div className="row" id = {result.sectionId}>
                                     <div  className="twelve columns">
                                        <div>
                                            <button className="article-btn"> {result.title} </button>
                                        </div>

                                        <div>
                                            <button className="article-btn" onClick={that.addSection.bind(null, result.title,result.sectionPosition)}> + </button>
                                        </div>
                                         
                                        <FreeformContainer freeformData={result} saveChanges={that.saveChanges}/>

                                        <ArticleControlls/>

                                        <CommentsBlog/>  
                                    </div>
                                </div>
                            </article>)
                        }
                        else {
                            return (
                             <article className="career-goal">
                                 <div className="row" id = {result.sectionId}>
                                     <div  className="twelve columns">
                                                                                
                                        <FreeformContainer freeformData={result} saveChanges={that.saveChanges}/>

                                        <ArticleControlls/>

                                        <CommentsBlog/>  
                                    </div>
                                </div>
                            </article>)
                        }
                    }
                    else {
                        addNewItem = 0;
                        if(experienceCount == 0 && result.title == "experience"){
                            addNewItem = 1;
                            experienceCount = experienceCount + 1;
                        }

                        if(educationCount == 0 && result.title == "education"){
                            addNewItem = 1;
                            educationCount = educationCount + 1;
                        }

                        if (addNewItem == 1) {
                            addNewItem = 0;
                            return (
                                <article className="experience forceEditMode">
                                    <div className="row">
                                         <div className="twelve columns">
                                            <div>
                                                <button className="article-btn"> {result.title} </button>
                                            </div>
                                            
                                            <div>
                                                <button className="article-btn" onClick={that.addSection.bind(null, result.title,result.sectionPosition)}> + </button>
                                            </div>
                                            
                                            <ArticleContent organizationData={result} saveChanges={that.saveChanges} />

                                            <ArticleControlls />

                                            <CommentsBlog />
                                        </div>
                                    </div>
                                </article>)
                        } else {
                             return (
                                <article className="experience">
                                    <div className="row">
                                         <div className="twelve columns">
                                                                                                                         
                                            <ArticleContent organizationData={result} saveChanges={that.saveChanges} />

                                            <ArticleControlls />

                                            <CommentsBlog />
                                        </div>
                                    </div>
                                </article>)
                        }
                    }
                    
                    })
                }
            </div>
        );
    }
});

React.render(<MainContainer />, document.getElementById('main-container'));




