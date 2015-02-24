var React = require('react');

var data = ['John Smith', 'Edward Johnson', 'Cindy Lawson', 'Julie Kim', 'Sarah Shore', 'Sally Ride'];

var RequestAdvice = React.createClass({
    render: function () {
        return (
            <div className="row">
              <div className="twelve columns">
                  <div><p className="rqst-key-word">to:</p></div>
                  <div>
                    <div className='rcpent-form'>form</div>
                    <div className='rcpent-list'>list</div>
                  </div>
                  <div className="fb-button">
                    <p className="small-text">ask your</p>
                    <p className="big-text">facebook</p>
                    <p className="small-text">friends</p>
                  </div>
              </div>
            </div>
        );
    }
});

React.render(<RequestAdvice />, document.getElementById('rcpents'));