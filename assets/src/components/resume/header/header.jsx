var React = require('react');

var Header = React.createClass({ 

    render: function() {
      return (
        <div className ="container">
          <div className="row">
            <div className="six columns">
              <div className="profileInfo">
                <p className="headline">Nathan Benson</p>
                <p className="tagline">Technologie Enthusiast analizing, building, and expanding solutions</p>
                <p className="address">1906 NE 151st Cicle, Vancouver WA g8589 </p>
              </div>
            </div>
            <div className="six columns">
              <div className="contactInfo">
                <p className="email"> nathanbenson@gmail.com</p>
                <p className="mobile">971-671-1654</p>
                <p className="twitter">nathanbenson</p>
                <p className="email">nathanbenson</p>
              </div>
            </div>
          </div>
        </div>
      );
    }
});

React.render(<Header />, document.getElementById('resume-contactInfo'));