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

    render: function() {
        var results = this.state.mainData,
            that = this;
        return (
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




