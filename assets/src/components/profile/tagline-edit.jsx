var React = require('react');

var TaglineEdit = React.createClass({ 

    getInitialState: function() {
        return {taglineData: ''}; 
    },

    componentDidUpdate: function () {
        this.state.taglineData = this.props.profileData.tagline;
    },

    componentWillReceiveProps: function(nextProps) {
        this.state.taglineData = nextProps.taglineData;
    },

    handleChange: function(event) {
        this.setState({taglineData: event.target.value});

        if (this.props.updateTagline) {
            this.props.updateTagline(event.target.value);
        }
    },

    render: function() {
        var taglineData = this.state.taglineData;

        return (
            <input type="text" className="tagline-edit"
                placeholder="add a professional tagline" 
                value = {taglineData} 
                onChange={this.handleChange} />
        );
    }
});

module.exports = TaglineEdit;