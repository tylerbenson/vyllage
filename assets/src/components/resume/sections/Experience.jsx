var React = require('react');
var Header = require('./Header');

var Experience = React.createClass({
  render: function () {
    return (
      <article className='career-goal'>
        <div className="row">
          <div className="twelve columns">
            <Header title='experience' /> 
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Experience;