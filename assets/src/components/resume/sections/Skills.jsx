var React = require('react');
var Header = require('./Header');

var Skills = React.createClass({
  render: function () {
    return (
      <article className='career-goal'>
        <div className="row">
          <div className="twelve columns">
            <Header title='skills' /> 
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Skills;