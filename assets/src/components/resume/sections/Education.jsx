var React = require('react');
var Header = require('./Header');

var Education = React.createClass({
  render: function () {
    return (
      <article className='career-goal'>
        <div className="row">
          <div className="twelve columns">
            <Header title='education' /> 
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Education;