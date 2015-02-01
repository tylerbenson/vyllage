var EstablishmentMain = React.createClass({	

    render: function() {
		return (
            <div className="education main">
                <p className="establishment">
                    {this.props.educationData.schoolName}
                </p>
            </div>
		);
    }
});

var EstablishmentEdit = React.createClass({   

    render: function() {
        return (
            <div className="education edit">
                <input type="text" className="description_edu" name="job-title" value={this.props.educationData.schoolName}/>
            </div>
        );
    }
});


var Establishment = React.createClass({   
    render: function() {
        return (
            <div>
                <EstablishmentMain educationData={this.props.educationData}/>
                <EstablishmentEdit educationData={this.props.educationData} />
            </div>
        );
    }
});

var DescriptionMain = React.createClass({ 

    render: function() {
        return (
            <div className="education main">
                <div className="paragraph">
                    <p className="description">
                        {this.props.educationData.schoolDescription}
                    </p>
                </div>
            </div>
        );
    }
});

var DescriptionEdit = React.createClass({ 

    render: function() {
        return (
            <div className="education edit">
                <textarea className="description" name="description">{this.props.educationData.schoolDescription}</textarea>
            </div>
        );
    }
});


var Description = React.createClass({   
    render: function() {
        return (
            <div>
                <DescriptionMain educationData={this.props.educationData}/>
                <DescriptionEdit educationData={this.props.educationData} />
            </div>
        );
    }
});


var TitleMain = React.createClass({   

    render: function() {
        return (
            <div className="education main">
                <p className="title">
                     {this.props.educationData.degree}
                </p>
                <p className="start-date">
                    {this.props.educationData.startDate}
                </p>
                <p className="end-date">
                    {this.props.educationData.endDate}
                </p>
                <p className="location">
                    {this.props.educationData.location}
                </p>
            </div>
        );
    }
});

var TitleEdit = React.createClass({   

    render: function() {
        return (
            <div className="education edit">
                <input type="text" className="title" value={this.props.educationData.degree} />
                <input type="text" className="start-date" value={this.props.educationData.startDate} />
                <input type="text" className="end-date" value={this.props.educationData.endDate} />
                <input type="text" className="location" value={this.props.educationData.location} />
            </div>
        );
    }
});

var Title = React.createClass({   
    render: function() {
        return (
            <div>
                <TitleMain educationData={this.props.educationData}/>
                <TitleEdit educationData={this.props.educationData} />
            </div>
        );
    }
});

var GpaMain = React.createClass({   

    render: function() {
        return (
            <div className="education main">
                <p className="gpa"> {this.props.educationData.highlights} </p>
            </div>
        );
    }
});

var GpaEdit = React.createClass({   

    render: function() {
        return (
            <div className="education edit">
                <input type="text" className="gpa" name="gpa" value={this.props.educationData.highlights} />
            </div>
        );
    }
});

var Gpa = React.createClass({   
    render: function() {
        return (
            <div>
                <GpaMain educationData={this.props.educationData}/>
                <GpaEdit educationData={this.props.educationData} />
            </div>
        );
    }
});


var Education = React.createClass({   

    render: function() {
        return (
             <div className="article-content education">
                <Establishment educationData={this.props.educationData}/>
                <Description educationData={this.props.educationData}/>
                <Title educationData={this.props.educationData}/>
                <Gpa educationData={this.props.educationData}/>
                <div className="edit">
                    <button className="save-btn">save</button>
                    <button className="cancel-btn">cancel</button>
                </div>
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

var EducationCantainer = React.createClass({   

    render: function() {
        return (
            <div>
                <div>
                    <button className="article-btn"> {this.props.educationData.type} </button>
                </div>
                <Education educationData={this.props.educationData}/>

                <ArticleControlls />

                <CommentsBlog />
            </div>
        );
    }
});


var EducationData = {
    "type": "education",
    "sectionPosition": 3,
    "schoolName": "Keller Graduate School of Management new",
    "schoolDescription": "Blah Blah Blah.",
    "degree": "Masters of Project Management",
    "startDate": "September 2010",
    "endDate": "September 2012",
    "isCurrent": false,
    "location": "Portland, Oregon",
    "highlights": "GPA 3.84, Summa Cum Laude, Awesome Senior Project"
};

React.render(<EducationCantainer educationData={EducationData} />, document.getElementById('education-cantainer'));


