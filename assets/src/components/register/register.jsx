// this will be a separate component in new structural changes

var AddSections = React.createClass({  

     addSection: function() {
            
        if(this.props.addSection){
            this.props.addSection(this.props.type);
        }
    },

    render: function() {

        return (
            <article className="add-section education">
                <div className="row">
                    <div className="twelve columns">
                        <div>
                            <button className="article-btn"> education </button>
                            <p className="add-more"> add more education </p>
                            <div className="icon-wrapper" onClick={this.addSection}>
                                <img className="icon add" src="images/add.png" width="25" height="25"/>
                            </div>
                        </div>
                    </div>
                </div>
            </article>
        );
    }
});

var MainContainer = React.createClass({  
    
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
            <div>
            <div>
                {
                    results.map(function(result) {
                    if(result.type =="freeform")
                    {
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
                    }
                    else
                    {
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

React.render(<MainContainer />, document.getElementById('main-container'));




