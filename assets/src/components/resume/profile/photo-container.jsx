var React = require('react');

var ProfilePhotoContainer = React.createClass({

    render: function() {
        return (
            <div className="four columns">
                <img className="profile-photo" src="images/profile-photo.png" width="115" height="115" />
            </div>
        );
    }
});


module.exports = ProfilePhotoContainer;