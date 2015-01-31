var CompanyMain = React.createClass({	

    render: function() {
		return (
            <div className="company main">
                <p className="company-name">{this.props.experienceData.companyName}</p>
            </div>
		);
    }
});

var CompanyEdit = React.createClass({   

    render: function() {
        return (
            <div className="company edit">
                <input type="text" className="company-name" name="company-name" value={this.props.experienceData.companyName} />
                <input type="text" className="industry" name="industry" value={this.props.experienceData.industry} />
            </div>
        );
    }
});


var CompanyName = React.createClass({   

    render: function() {
        return (
            <div>
                <CompanyMain experienceData={this.props.experienceData}/>
                <CompanyEdit experienceData={this.props.experienceData} />
            </div>
        );
    }
});

var CompanyMainDescription = React.createClass({   

    render: function() {
        return (
            <div className="company main">
                <p className="description">
                    {this.props.experienceData.companyDescription}
                </p>
            </div>
        );
    }
});

var CompanyEditDescription = React.createClass({   

    render: function() {
        return (
            <div className="company edit">
                <textarea className="description" name="description" cols="20" rows="3">{this.props.experienceData.companyDescription}</textarea>
            </div>
        );
    }
});

var CompanyDescription = React.createClass({   

    render: function() {
        return (
            <div>
                <CompanyMainDescription experienceData={this.props.experienceData}/>
                <CompanyEditDescription experienceData={this.props.experienceData} />
            </div>
        );
    }
});

var JobMain = React.createClass({   

    render: function() {
        return (
            <div className="job main">
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
        );
    }
});

var JobEdit = React.createClass({   

    render: function() {
        return (
             <div className="job edit">
                <input type="text" className="title" value={this.props.experienceData.jobTitle} />
                <input type="text" className="start-date" value= {this.props.experienceData.startDate} />
                <input type="text" className="end-date" value={this.props.experienceData.endDate} />
                <input type="text" className="location" value={this.props.experienceData.location} />
            </div>
        );
    }
});


var Title = React.createClass({   

    render: function() {
        return (
            <div>
                <JobMain experienceData={this.props.experienceData}/>
                <JobEdit experienceData={this.props.experienceData}/>
            </div>
        );
    }
});

var DescriptionMain = React.createClass({   

    render: function() {
        return (
            <div className="job main">
                <div className="paragraph">
                    <p className="description">
                        {this.props.experienceData.jobDescription}
                    </p>
                </div>
            </div>
        );
    }
});

var DescriptionEdit = React.createClass({   

    render: function() {
        return (
            <div className="job edit">
                <textarea className="description" name="description" cols="40" rows="6">{this.props.experienceData.jobDescription}</textarea>
            </div>
        );
    }
});

var Description = React.createClass({   

    render: function() {
        return (
            <div>
                <DescriptionMain experienceData={this.props.experienceData}/>
                <DescriptionEdit experienceData={this.props.experienceData}/>
            </div>
        );
    }
});

var ResponsibilitiesMain = React.createClass({   

    render: function() {
        return (
            <div className="job main">
                <div className="responsibilities">
                    {this.props.experienceData.responsibilities}
                </div>
            </div>
        );
    }
});

var ResponsibilitiesEdit = React.createClass({   

    render: function() {
        return (
             <div className="job edit">
                <textarea className="responsibilities" cols="40" rows="6">{this.props.experienceData.responsibilities}</textarea>
            </div>
        );
    }
});

var Responsibilities = React.createClass({   

    render: function() {
        return (
            <div>
                <ResponsibilitiesMain experienceData={this.props.experienceData}/>
                <ResponsibilitiesEdit experienceData={this.props.experienceData}/>
            </div>
        );
    }
});

var Buttons = React.createClass({   

    render: function() {
        return (
            <div className="edit">
                <button className="save-btn">save</button>
                <button className="cancel-btn">cancel</button>
            </div>
        );
    }
});

var ArticleContentExperience = React.createClass({   

    render: function() {
        return (
            <div className="article-content experience">
                <CompanyName experienceData={this.props.experienceData}/>
                <CompanyDescription experienceData={this.props.experienceData}/>
                <Title experienceData={this.props.experienceData}/>
                <Description experienceData={this.props.experienceData}/>
                <Responsibilities experienceData={this.props.experienceData}/>
                <Buttons experienceData={this.props.experienceData}/>
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
    "companyDescription": "Blah Blah Blah.",
    "jobTitle": "Manager, Local Accounts",
    "startDate": "September 2010",
    "endDate": "",
    "isCurrent": true,
    "location": "Portland, Oregon",
    "jobDescription": "Blah Blah Blah",
    "responsibilities": "I was in charge of..."
};

React.render(<ExperienceCantainer experienceData={DATA} />, document.getElementById('experience-container'));


