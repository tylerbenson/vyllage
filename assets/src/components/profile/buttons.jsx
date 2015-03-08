var React = require('react');

var ButtonsContainer = React.createClass({  

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
            <div className="buttons-container">
                <button className="save-btn" onClick={this.saveHandler}>save</button>
                <button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
            </div>
        );
    }
});

module.exports = ButtonsContainer;