// this will be a separate component in new structural changes

var AddSections = React.createClass({displayName: "AddSections",  

     addSection: function() {
            
        if(this.props.addSection){
            this.props.addSection(this.props.type);
        }
    },

    render: function() {

        return (
            React.createElement("article", {className: "add-section education"}, 
                React.createElement("div", {className: "row"}, 
                    React.createElement("div", {className: "twelve columns"}, 
                        React.createElement("div", null, 
                            React.createElement("button", {className: "article-btn"}, " education "), 
                            React.createElement("p", {className: "add-more"}, " add more education "), 
                            React.createElement("div", {className: "icon-wrapper", onClick: this.addSection}, 
                                React.createElement("img", {className: "icon add", src: "images/add.png", width: "25", height: "25"})
                            )
                        )
                    )
                )
            )
        );
    }
});

var MainContainer = React.createClass({displayName: "MainContainer",  
    
    getInitialState: function() {
        return {mainData: []};
    },

    componentDidMount : function() {
        // ajax call will go here and fetch the whoole data

        this.setState({mainData: MainData});
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

        for(i = 0; i < this.state.mainData.length; i++){

            if(this.state.mainData[i].sectionId == data.sectionId){

                this.state.mainData[i] = data;
                this.setState({mainData:   this.state.mainData});
                return;
            }
        }
    },

    addSection: function (type) {
        var id = MainData.length + 1;

        if(type == 'freeForm') {

            this.state.mainData.push({
                "type": "freeform",
                "title": "career goal",
                "sectionId": id,
                "sectionPosition": 1,
                "state": "shown",
                "description": ""
            });

        } else if(type == 'organization') {

            this.state.mainData.push({
                "type": "experience",
                "title": "job experience",
                "sectionId": id,
                "sectionPosition": 2,
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

        }

        this.setState({mainData: this.state.mainData});

    },

    render: function() {
        var results = this.state.mainData,
            that = this;
        return (
            React.createElement("div", null, 
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

                                    React.createElement(FreeformContainer, {freeformData: result, saveChanges: that.saveChanges})

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
                                    React.createElement(ArticleContent, {organizationData: result, saveChanges: that.saveChanges})

                                )
                            )
                        ))
                    }
                    
                    })
                
            ), 

            React.createElement(AddSections, {addSection: that.addSection, type: 'freeForm'}), 

            React.createElement(AddSections, {addSection: that.addSection, type: 'organization'})

            )
        );
    }
});

var MainData =[
{
    "type": "freeform",
    "title": "career goal",
    "sectionId": 1,
    "sectionPosition": 1,
    "state": "shown",
    "description": ""
},
{
    "type": "experience",
    "title": "job experience",
    "sectionId": 2,
    "sectionPosition": 2,
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
}
];

React.render(React.createElement(MainContainer, null), document.getElementById('main-container'));




