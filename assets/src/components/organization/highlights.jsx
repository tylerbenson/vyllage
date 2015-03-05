var React = require('react');

var Highlights = React.createClass({

    getInitialState: function() {
        return {highlights: this.props.highlights}; 
    },

    componentDidUpdate: function () {
        this.state.highlights = this.props.highlights;
    },

    componentWillReceiveProps: function(nextProps) {
        this.state.highlights = nextProps.highlights;
    },

    handleChange: function(event) {
        this.setState({highlights: event.target.value});

        if (this.props.updateRoleHighlights) {
            this.props.updateRoleHighlights(event.target.value);
        }
    },

    render: function() {
        var highlights = this.state.highlights; 

        return (
             <div className="edit">
                <textarea className="highlights" placeholder="Highlights"
                value={highlights} onChange={this.handleChange}></textarea>
            </div>
        );
    }
});

module.exports = Highlights;