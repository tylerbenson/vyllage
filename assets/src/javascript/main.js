var MainContainer = React.createClass({displayName: "MainContainer",  
    
    getInitialState: function() {
        return {organizationData: []};
    },

    componentDidMount : function() {
        // ajax call will go here and fetch the whoole data
       // this.setState({organizationData: OrganizationData});
    },

    saveChanges: function (data) {
        this.setState({organizationData: data});
        // here ajax call will go to the server, and update the data
    },

    render: function() {
        var results = this.props.mainData;
        return (
            React.createElement("div", null, 
                
                    results.map(function(result) {
                    if(result.type =="freeform")
                    {
                        return (
                         React.createElement("article", {className: "career-goal"}, 
                             React.createElement("div", {className: "row", id: result.sectionId}, 
                                 React.createElement("div", {className: "twelve columns"}, 
                                    React.createElement("div", null, 
                                        React.createElement("button", {className: "article-btn"}, " ", result.title, " ")
                                    ), 

                                    React.createElement(FreeformContainer, {freeformData: result, saveChanges: this.saveChanges}), 

                                    React.createElement(ArticleControlls, null), 

                                    React.createElement(CommentsBlog, null)
                                )
                            )
                        ))
                    }
                    else
                    {
                        return (
                        React.createElement("article", {className: "experience"}, 
                            React.createElement("div", {className: "row"}, 
                                 React.createElement("div", {className: "twelve columns"}, 
                                    React.createElement("div", null, 
                                        React.createElement("button", {className: "article-btn"}, " ", result.type, " ")
                                    ), 
                                    React.createElement(ArticleContent, {organizationData: result, saveChanges: this.saveChanges}), 

                                    React.createElement(ArticleControlls, null), 

                                    React.createElement(CommentsBlog, null)
                                )
                            )
                        ))
                    }
                    
                    })
                
            )
        );
    }
});

function compare(a,b) {
    
    if (a.sectionPosition < b.sectionPosition){
        return -1;
    }
    if (a.sectionPosition > b.sectionPosition) {
        return 1;
    }
    return 0;
}

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
    "title": "job experience",
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
    "roleDescription": "",
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
    "sectionId": 126,
    "sectionPosition": 7,
    "description": "basket weaving, spear fishing, dominion"
},
{
    "type": "freeform",
    "title": "skills",
    "sectionId": 126,
    "sectionPosition": 5,
    "description": "basket weaving, spear fishing, dominion"
},
{
    "type": "experience",
    "title": "education",
    "sectionId": 125,
    "sectionPosition": 2,
    "state": "hidden",
    "organizationName": "Pisik",
    "organizationDescription": "Blah Blah Blah.",
    "role": "Masters of Project Management",
    "startDate": "September 2010",
    "endDate": "September 2012",
    "isCurrent": false,
    "location": "Portland, Oregon",
    "roleDescription": "",
    "highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
},
];

MainData.sort(compare);

React.render(React.createElement(MainContainer, {mainData: MainData}), document.getElementById('main-container'));




