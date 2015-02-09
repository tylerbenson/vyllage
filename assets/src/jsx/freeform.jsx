var FreeformMain = React.createClass({  

    render: function() {
        return (
            <div className="nonEditable">

                <div className="main">
                    <div className="paragraph">
                        <p className="freeform-description">
                           {this.props.freeformData.description};
                        </p>
                    </div>
                </div>
            </div>
        );
    }
});

var  FreeformEdit = React.createClass({ 

    getInitialState: function() {
        return {descriptionData:''}; 
    },

    componentDidUpdate: function () {
        this.state.descriptionData = this.props.freeformData.description;
    },

    handleChange: function(event) {
        this.setState({descriptionData: event.target.value});

        if (this.props.updateDescription) {
            this.props.updateDescription(event.target.value);
        }
    },

    render: function() {

        var descriptionData = this.state.descriptionData;

        return (
            <div className="editable">
                <div className="edit">
                    <textarea className="freeform-description" 
                        placeholder="tell us about your career goal..." 
                        onChange={this.handleChange} value ={descriptionData} >
                    </textarea>
                </div>
            </div>
        );
    }
});

var ButtonsContainer = React.createClass({  

    saveHandler: function(event) {

        event.preventDefault();
        event.stopPropagation();

        if (this.props.save) {
            this.props.save();
        }
    },    

    cancelHandler: function(event) {
        event.preventDefault();
        event.stopPropagation();

        if (this.props.cancel) {
            this.props.cancel();
        }
    },    

    render: function() {
        return (
            <div className="buttons-container">
                <button className="save-btn" onClick={this.saveHandler}>save</button>
                <button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
            </div>
        );
    }
});

var FreeformContainer = React.createClass({

    getInitialState: function() {
        return {
            isMain: true,
            freeformData: ''
        };
    },

    updateDescription: function (value) {
        this.state.freeformData.description = value;
    },

    save: function () {
        if(this.props.saveChanges){
            this.props.saveChanges(this.state.freeformData);
        }
      this.handleModeChange();
    },

    cancel: function () {
        this.handleModeChange();
    },

    handleModeChange: function () {

        if(!this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.freeformData));

            this.setState({ 
                freeformData :data,
                isMain: true 
            });

            this.refs.goalMain.getDOMNode().style.display="block";
            this.refs.goalEdit.getDOMNode().style.display="none";
            this.refs.buttonContainer.getDOMNode().style.display="none";
            this.state.isMain=true ;
        }
        return false;
    },

    goToEditMode: function() {

        if(this.state.isMain) {
            var data = JSON.parse(JSON.stringify(this.props.freeformData));
            this.setState({ 
                freeformData :data,
                isMain: false 
            });

            this.refs.goalMain.getDOMNode().style.display="none";
            this.refs.goalEdit.getDOMNode().style.display="block";
            this.refs.buttonContainer.getDOMNode().style.display="block";
            this.state.isMain=false;
        }
    },

    render: function() {
        return (
            <div className="article-content" onClick={this.goToEditMode}>

                <FreeformMain ref="goalMain" freeformData={this.props.freeformData}/>
                <FreeformEdit ref="goalEdit" freeformData={this.props.freeformData} updateDescription={this.updateDescription} />

                <ButtonsContainer ref="buttonContainer" save={this.save} cancel={this.cancel}/>
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

    getInitialState: function() {
        return {freeformData: []};
    },

    componentDidMount: function() {
        // ajax call will go here and fetch the profileData

        // $.ajax({
        //     url: this.props.url,
        //     dataType: 'json',
        //     success: function(data) {
        //         this.setState({profileData: data});
        //     }.bind(this),
        //     error: function(xhr, status, err) {
        //         console.error(this.props.url, status, err.toString());
        //     }.bind(this)
        // });

        this.setState({freeformData: FreeFormData});
    },

    saveChanges: function (data) {
        this.setState({freeformData: data});

        // here ajax call will go to the server, and update the data
    },
    
    render: function() {
        return (
            <div  className="twelve columns">
                <div>
                    <button className="article-btn"> {this.props.freeformData.title} </button>
                </div>

                <FreeformContainer freeformData={this.state.freeformData} saveChanges={this.saveChanges}/>

                <ArticleControlls/>

                <CommentsBlog/>  
            </div>
        );
    }
});

var FreeFormData = {
    "type": "freeform",
    "title": "skills",
    "sectionId": 126,
    "sectionPosition": 4,
    "description": "basket weaving, spear fishing, dominion"
};

React.render(<CareerGoalContainer freeformData={FreeFormData} />, document.getElementById('career-goal-container'));