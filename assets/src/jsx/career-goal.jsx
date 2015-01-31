var GoalMain = React.createClass({	

    render: function() {
		return (
            <div className="goal main">
                <div className="paragraph">
                    <p className="goal-description">
                       {this.props.careerData.description};
                    </p>
                </div>
            </div>
		);
    }
});

var GoalEdit = React.createClass({ 

    render: function() {
        return (
            <div className="goal edit">
                <textarea className="goal-description" 
                            placeholder="tell us about your career goal..." 
                            cols="40" rows="6">
                    {this.props.careerData.description}
                </textarea>
            </div>
        );
    }
});

var GoalContainer = React.createClass({
    render: function() {
        return (
            <div className="article-content career-goal">
                <div>
                    <GoalMain careerData={this.props.careerData}/>
                    <GoalEdit careerData={this.props.careerData} />
                </div>
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



var CareerGoalContainer = React.createClass({
    render: function() {
        return (
            <div>
                <div>
                    <button className="article-btn"> {this.props.careerData.title} </button>
                </div>

                <GoalContainer careerData={this.props.careerData}/>

                <ArticleControlls/>

                <CommentsBlog/>  
            </div>
        );
    }
});

var CareerGoalData = {
    "type": "freeform",
    "title": "career goal",
    "sectionPosition": 1,
    "description": " I am a fervid promoter of creating solutions. The rush of working with a team to launch a project that solves critical problems and intersects with technology is extremely satisfying. I have a successful track record, in leading projects, growing new."
};

React.render(<CareerGoalContainer careerData={CareerGoalData} />, document.getElementById('career-goal-container'));


