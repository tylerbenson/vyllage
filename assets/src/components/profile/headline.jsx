var React = require('react');

var HeadlineContainer = React.createClass({

    render: function() {
        return (
            <div className="headline-container headline-field">
                <p className="headline">
                    {this.props.profileData.firstName}&nbsp;
                    {this.props.profileData.middleName}&nbsp;
                    {this.props.profileData.lastName} 
                </p>
            </div>
        );
    }
});

module.exports = HeadlineContainer;