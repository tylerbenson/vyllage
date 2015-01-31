// ----------------------- PROFILE SECTION --------------------------

// -------------------------- main mode -------------------------- 

var Headline = React.createClass({displayName: "Headline",	

    render: function() {
		return (
            React.createElement("div", {className: "paragraph"}, 
                React.createElement("p", {className: "headline"}, 
                     this.props.profileData.firstName + " "
                      + this.props.profileData.middleName + " "
                      + this.props.profileData.lastName
                    
                )
            )
		);
    }
});

var Tagline = React.createClass({displayName: "Tagline", 

    render: function() {
        return (
            React.createElement("div", {className: "paragraph"}, 
                React.createElement("p", {className: "tagline"}, this.props.profileData.tagline)
            )
        );
    }
});

var HeadlineContainerMain = React.createClass({displayName: "HeadlineContainerMain",

    render: function() {
        return (
            React.createElement("div", {className: "headline-container main"}, 
                React.createElement(Headline, {profileData: this.props.profileData}), 
                React.createElement(Tagline, {profileData: this.props.profileData})
            )
        );
    }
});


// -------------------------- edit mode -------------------------- 

var HeadlineEdit = React.createClass({displayName: "HeadlineEdit",  

    getInitialState: function() {
        return {value:'Nathan M Benson'}; // this will be changed to get data from props: TBD
    },

    handleChange: function(event) {
        this.setState({value: event.target.value});

        if (this.props.onChange) {
            this.props.onChange( event.target.value, true);
        }
    },

    render: function() {
        var value = this.state.value;

        return (
            React.createElement("input", {type: "text", className: "headline", placeholder: "name, surname", 
                value: value, onChange: this.handleChange})         
        );
    }
});

var TaglineEdit = React.createClass({displayName: "TaglineEdit", 

    getInitialState: function() {
        return {value: 'Technology Enthusiast analyzing, building, and expanding solutions'}; // this will be changed to get data from props: TBD
    },

    handleChange: function(event) {
        this.setState({value: event.target.value});

        if (this.props.onChange) {
            this.props.onChange(event.target.value, false);
        }
    },

    render: function() {
        var value = this.state.value;

        return (
            React.createElement("input", {type: "text", className: "tagline", 
                placeholder: "add a professional tagline", 
                value: value, 
                onChange: this.handleChange})
        );
    }
});

var HeadlineContainerEdit = React.createClass({displayName: "HeadlineContainerEdit",  

    handleChange: function (tagline, isHeadline){
        if (this.props.onChange) {
            this.props.onChange(tagline, isHeadline);
        }
    },

    render: function() {
        return (
            React.createElement("div", {className: "headline-container edit"}, 
                React.createElement(HeadlineEdit, {profileData: this.props.profileData, onChange: this.handleChange}), 
                React.createElement(TaglineEdit, {profileData: this.props.profileData, onChange: this.handleChange})
            )
        );
    }
});


// -------------------------- main container for both modes -------------------------- 

var HeadlineContainer = React.createClass({displayName: "HeadlineContainer", 

    getInitialState: function() {
        return {profileData: []};
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

        this.setState({profileData: Data});
    },

    handleChange: function (data, isHeadline) {

        if(isHeadline) {
            this.state.profileData.firstName = data;
        } else {
            this.state.profileData.tagline = data;
        }

        this.setState({profileData: this.state.profileData});
    },

    render: function() {
        return (
            React.createElement("div", null, 
                React.createElement(HeadlineContainerMain, {profileData: this.state.profileData}), 
                React.createElement(HeadlineContainerEdit, {profileData: this.state.profileData, onChange: this.handleChange})
            )
        );
    }

});

//   ---------------------------------- render --------------------------------

var Data = { 
    firstName: 'Nathan',
    middleName: 'M',
    lastName: 'Benson',
    tagline: 'Technology Enthusiast analyzing, building, and expanding solutions'
};

React.render(React.createElement(HeadlineContainer, null), document.getElementById('headline-container'));


