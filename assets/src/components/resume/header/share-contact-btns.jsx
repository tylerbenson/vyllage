var React = require('react');

var ShareContactButtons = React.createClass({ 
    render: function() {
        return ( 
            <div className="four columns">
                <div className="">
                    <button className="share" onClick={this.props.toggleShare}>share</button>
                    <button className="contact" onClick={this.props.toggleContact}>contact</button>
                </div>
            </div>
        );
    }
});

module.exports = ShareContactButtons;