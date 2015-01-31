// ----------------------- PROFILE SECTION --------------------------

// -------------------------- main mode -------------------------- 

var Headline = React.createClass({	

    render: function() {
		return (
            <div className="paragraph">
                <p className="headline">
                    { this.props.profileData.firstName + " "
                      + this.props.profileData.middleName + " "
                      + this.props.profileData.lastName
                    }
                </p>
            </div>
		);
    }
});

var Tagline = React.createClass({ 

    render: function() {
        return (
            <div className="paragraph">
                <p className="tagline">{this.props.profileData.tagline}</p>
            </div>
        );
    }
});

var HeadlineContainerMain = React.createClass({

    render: function() {
        return (
            <div className="headline-container main">
                <Headline profileData={this.props.profileData}/>
                <Tagline profileData={this.props.profileData} />
            </div>
        );
    }
});


// -------------------------- edit mode -------------------------- 

var HeadlineEdit = React.createClass({  

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
            <input type="text" className="headline" placeholder="name, surname" 
                value= {value} onChange={this.handleChange} />         
        );
    }
});

var TaglineEdit = React.createClass({ 

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
            <input type="text" className="tagline"
                placeholder="add a professional tagline" 
                value = {value} 
                onChange={this.handleChange} />
        );
    }
});

var HeadlineContainerEdit = React.createClass({  

    handleChange: function (tagline, isHeadline){
        if (this.props.onChange) {
            this.props.onChange(tagline, isHeadline);
        }
    },

    render: function() {
        return (
            <div className="headline-container edit">
                <HeadlineEdit profileData={this.props.profileData} onChange={this.handleChange }/>
                <TaglineEdit profileData={this.props.profileData} onChange={this.handleChange } />
            </div>
        );
    }
});


// -------------------------- main container for both modes -------------------------- 

var HeadlineContainer = React.createClass({ 

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
            <div>
                <HeadlineContainerMain profileData={this.state.profileData} />
                <HeadlineContainerEdit profileData={this.state.profileData} onChange={this.handleChange}/>
            </div>
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

React.render(<HeadlineContainer />, document.getElementById('headline-container'));


