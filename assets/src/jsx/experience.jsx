var ExperienceMain = React.createClass({   

    render: function() {
        return (
            <div className="nonEditable">

                <div className="main">
                    <p className="company-name">
                        {this.props.experienceData.companyName} 
                        {this.props.experienceData.industry} 
                    </p>
                </div>

                <div className="main">
                    <div className="paragraph">
                        <p className="company-description">
                            {this.props.experienceData.companyDescription}
                        </p>
                    </div>
                </div>

                <div className="main">
                    <p className="title">
                        {this.props.experienceData.jobTitle}
                    </p>
                    <p className="start-date">
                        {this.props.experienceData.startDate}
                    </p>
                    <p className="end-date">
                        {this.props.experienceData.endDate}
                    </p>
                    <p className="location">
                        {this.props.experienceData.location}
                    </p>
                </div>

                <div className="main">
                    <div className="paragraph">
                        <p className="job-description">
                            {this.props.experienceData.jobDescription}
                        </p>
                    </div>
                </div>

                <div className="main">
                    <div className="paragraph">
                        <p className="responsibilities">
                            {this.props.experienceData.responsibilities}
                        </p>
                    </div>
                </div>

            </div>
        );
    }
});


var CompanyName = React.createClass({   

    render: function() {
        return (
            <div className="edit">
                <input type="text" className="company-name" value={this.props.experienceData.companyName} />
                <input type="text" className="industry" value={this.props.experienceData.industry} />
            </div>
        );
    }
});

var CompanyDescription = React.createClass({   

    render: function() {
        return (
            <div className="edit">
                <textarea className="company-description">{this.props.experienceData.companyDescription}</textarea>
            </div>
        );
    }
});

var Job = React.createClass({   

    render: function() {
        return (
             <div className="edit">
                <input type="text" className="title" value={this.props.experienceData.jobTitle} />
                <input type="text" className="start-date" value= {this.props.experienceData.startDate} />
                <input type="text" className="end-date" value={this.props.experienceData.endDate} />
                <input type="text" className="location" value={this.props.experienceData.location} />
            </div>
        );
    }
});

var JobDescription = React.createClass({   

    render: function() {
        return (
            <div className="edit">
                <textarea className="job-description">{this.props.experienceData.jobDescription}</textarea>
            </div>
        );
    }
});

var Responsibilities = React.createClass({   

    render: function() {
        return (
             <div className="edit">
                <textarea className="responsibilities">{this.props.experienceData.responsibilities}</textarea>
            </div>
        );
    }
});


var Buttons = React.createClass({   

    saveHandler: function(event) {

        if (this.props.save) {
            this.props.save();
        }

        event.preventDefault();
        event.stopPropagation();
    },    

    cancelHandler: function(event) {

        if (this.props.cancel) {
            this.props.cancel();
        }

        event.preventDefault();
        event.stopPropagation();
    },

    render: function() {
        return (
            <div className="edit">
                <button className="save-btn" onClick={this.saveHandler}>save</button>
                <button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
            </div>
        );
    }
});

var ExperienceEdit = React.createClass({   

    render: function() {
        return (
            <div className="editable">
                <CompanyName experienceData={this.props.experienceData} />
                <CompanyDescription experienceData={this.props.experienceData} />
                <Job experienceData={this.props.experienceData} />
                <JobDescription experienceData={this.props.experienceData} />
                <Responsibilities experienceData={this.props.experienceData} />
                
              
            </div>
        );
    }
});

var ArticleContentExperience = React.createClass({  

    getInitialState: function() {
        return {
                isMain: true,
                experienceData: ''
            };
    }, 

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.state.experienceData);
        }
        this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            var data = JSON.parse( JSON.stringify( this.props.experienceData ));
            this.setState({ experienceData :data,
                            isMain: true });

            this.refs.mainContainer.getDOMNode().style.display="block";
            this.refs.editContainer.getDOMNode().style.display="none";

            this.refs.buttonContainer.getDOMNode().style.display="none";

            this.state.isMain=true ;
        }

        return false;
    },

     goToEditMode: function() {

        if(this.state.isMain) {
             var data = JSON.parse( JSON.stringify( this.props.experienceData ));
             this.setState({ experienceData :data,
                        isMain: false });

            this.refs.mainContainer.getDOMNode().style.display="none";
            this.refs.editContainer.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block";

            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="article-content experience" onClick={this.goToEditMode}>

                <ExperienceMain ref="mainContainer" experienceData={this.props.experienceData}/>
                <ExperienceEdit ref="editContainer" experienceData={this.props.experienceData}/>
                <Buttons ref="buttonContainer" experienceData={this.props.experienceData} save={this.save} cancel={this.cancel}/>
            </div>
        );
    }
});

var ArticleControlls = React.createClass({
    render: function() {
        return (
            <div className="article-controll">
                <div className="article-controll-btns">
                    <div className="u-pull-left">
                        <a href="" className="suggestions">suggestions</a>
                        <span className="suggestions-count count">2</span>
                    </div>
                    <div className=" u-pull-left">
                        <a href="" className="comments">comments</a>
                        <span className="suggestions-count count">3</span>
                    </div>
                </div>
            </div>
        );
    }
});

var CommentsBlog = React.createClass({
    render: function() {
        return (
            <div className="comments-content">
                <div className="comment-list-block">
                    <div className="comment-person-info">
                        <img className="u-pull-left" src="images/comment-person.png" width="40" height="40" />
                        <p className="u-pull-left name">Richard Zielke</p>
                        <p className="u-pull-left date">2 hrs ago</p>
                    </div>
                    <div className="comment-body">
                        <p className="">
                            I like what you are saying here about your experience with DeVry. You have done an excellent job of outlining your successes.
                        </p>
                    </div>
                    <div className="comment-ctrl">
                        <div className="u-pull-right">
                            <ul className="comment-ctrl-list">
                                <li className="comment-ctrl-btn"><a href="#">thank</a>
                                </li>
                                <li className="comment-ctrl-btn"><a href="#">hide</a>
                                </li>
                                <li className="comment-ctrl-btn"><a href="#">reply</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div className="comment-divider"></div>
            </div>
        );
    }
});

var ExperienceCantainer = React.createClass({   

    render: function() {
        return (
            <div className="twelve columns">
                <div>
                    <button className="article-btn"> experience </button>
                </div>
                <ArticleContentExperience experienceData={this.props.experienceData}/>

                <ArticleControlls />

                <CommentsBlog />
            </div>
        );
    }
});

var DATA = {
    "type": "experience",
    "sectionPosition": 2,
    "companyName": "DeVry1",
    "industry": "Education Group",
    "companyDescription": "Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah.",
    "jobTitle": "Manager, Local Accounts",
    "startDate": "September 2010",
    "endDate": "",
    "isCurrent": true,
    "location": "Portland, Oregon",
    "jobDescription": "Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah",
    "responsibilities": "I was in charge of..."

    // "type": "experience",
    // "title": "job experience",
    // "sectionId": 124,
    // "sectionPosition": 2,
    // "state": "shown",
    // "organizationName": "DeVry Education Group",
    // "organizationDescription": "Blah Blah Blah.",
    // "role": "Manager, Local Accounts",
    // "startDate": "September 2010",
    // "endDate": "",
    // "isCurrent": true,
    // "location": "Portland, Oregon",
    // "roleDescription": "Blah Blah Blah",
    // "highlights": "I was in charge of..."
};

React.render(<ExperienceCantainer experienceData={DATA} />, document.getElementById('experience-container'));


