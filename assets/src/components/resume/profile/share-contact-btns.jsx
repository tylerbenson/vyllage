var React = require('react');

var ShareContactButtons = React.createClass({ 

    showShare: function() {
        document.getElementById('share-info').style.display = 'block';
        document.getElementById('contact-info').style.display = 'none';
    },

    showContact: function() {
        document.getElementById('contact-info').style.display = 'block';
        document.getElementById('share-info').style.display = 'none';
    },

    render: function() {
        return ( 
            <div className="four columns btns-grid">
                <div className="share-contact-btns-container">
                    <button className="u-pull-left share" onClick={this.showShare}>share</button>
                    <button className="u-pull-left contact" onClick={this.showContact}>contact</button>
                </div>
            </div>
        );
    }
});

module.exports = ShareContactButtons;