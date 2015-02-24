var React = require('react');
var Header = require('./components/header');
var Profile = require('./components/profile');
var FreeformContainer = require('./components/freeform/container');
var ArticleContent = require('./components/organization/article-content');
var AddSections = require('./components/addSections/addSections');

var RegisterData =[
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

var RegisterContainer = React.createClass({  
    
    getInitialState: function() {
        return {registerData: []};
    },

    componentDidMount : function() {
        // ajax call will go here and fetch the whoole data

        this.setState({registerData: RegisterData});
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

        for(var i = 0; i < this.state.registerData.length; i++){

            if(this.state.registerData[i].sectionId == data.sectionId){

                this.state.registerData[i] = data;
                this.setState({registerData: this.state.registerData});
                return;
            }
        }
    },

    addSection: function (type) {
        var id = RegisterData.length + 1;

        if(type == 'freeForm') {

            this.state.registerData.push({
                "type": "freeform",
                "title": "career goal",
                "sectionId": id,
                "sectionPosition": 1,
                "state": "shown",
                "description": ""
            });

        } else if(type == 'organization') {

            this.state.registerData.push({
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

        this.setState({registerData: this.state.registerData});
    },

    render: function() {
        var results = this.state.registerData,
            that = this;
            
        return (
            <div>
                <div> {
                    results.map(function(result) {
                        if(result.type =="freeform") {
                            return (
                             <article className="career-goal">
                                 <div className="row" id = {result.sectionId}>
                                     <div  className="twelve columns">
                                        <div>
                                            <button className="article-btn"> {result.title} </button>
                                        </div>

                                        <FreeformContainer freeformData={result} saveChanges={that.saveChanges}/>

                                    </div>
                                </div>
                            </article>)
                        } else {
                            return (
                                <article className="experience">
                                    <div className="row">
                                         <div className="twelve columns">
                                            <div>
                                                <button className="article-btn"> {result.type} </button>
                                            </div>
                                            <ArticleContent organizationData={result} saveChanges={that.saveChanges} />

                                        </div>
                                    </div>
                                </article>)
                            }
                        })
                    }
                </div>

                <AddSections addSection={that.addSection} type={'freeForm'}/>

                <AddSections addSection={that.addSection} type={'organization'}/>

            </div>
        );
    }
});


React.render(<RegisterContainer />, document.getElementById('main-container'));




