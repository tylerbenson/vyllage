var React = require('react');
var Header = require('./components/header');
var Profile = require('./components/profile');
var ContactInfo = require('./components/contact/contact');
var ShareInfo = require('./components/contact/share');
var FreeformContainer = require('./components/freeform/container');
var ArticleContent = require('./components/organization/article-content');
var ArticleControlls =require('./components/freeform/article-controlls');
var CommentsBlog = require('./components/freeform/comments-blog');

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
        // ajax call will go here and fetch the whoole data
        MainData.sort(compare);
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

        for(var i = 0; i < this.state.mainData.length; i++){

            if(this.state.mainData[i].sectionId === data.sectionId){

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
                    if(result.type === "freeform")
                    {
                        return (
                         <article className="career-goal">
                             <div className="row" id = {result.sectionId}>
                                 <div  className="twelve columns">
                                    <div>
                                        <button className="article-btn"> {result.title} </button>
                                    </div>

                                    <FreeformContainer freeformData={result} saveChanges={that.saveChanges}/>

                                    <ArticleControlls/>

                                    <CommentsBlog/>  
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

                                    <ArticleControlls />

                                    <CommentsBlog />
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

var Main = React.createClass({
  render: function () {
    return (
      <div>
        <header>
            <div className="container">
                <Header />
            </div>
        </header>

       <section className="container">

            <article className="profile">
                <Profile />
            </article>

            <article className="info-sections">
               <ShareInfo />
            </article>

            <article className="info-sections">
                <ContactInfo />  
            </article>

            <MainContainer />
        </section>
      </div>
    );
  }
});

React.render(<Main />, document.getElementById('main'));




