var React = require('react');

var  FreeformEdit = React.createClass({ 

    getInitialState: function() {
        return {description:this.props.description}; 
    },

    componentDidUpdate: function () {
        this.state.description = this.props.description;
    },

    handleChange: function(event) {
        this.setState({description: event.target.value});

        if (this.props.updateDescription) {
            this.props.updateDescription(event.target.value);
        }
    },

    render: function() {

        var description = this.state.description;

        return (
            <div className="editable">
                <div className="edit">
                    <textarea className="freeform-description" 
                        placeholder="tell us about your career goal..." 
                        onChange={this.handleChange} value ={description} >
                    </textarea>
                </div>
            </div>
        );
    }
});

module.exports = FreeformEdit;