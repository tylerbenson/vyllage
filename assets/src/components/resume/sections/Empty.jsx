var React = require('react');
var Header = require('./Header');
var filter = require('lodash.filter');

var EmptySections = React.createClass({
  render: function () {
    var sectionTitles = ['career goal', 'education', 'experience', 'skills']
    var emptyNodes = sectionTitles.map(function (title) {
      var sections = filter(this.props.sections, {title: title})
      if (sections.length === 0) {
        return (
          <div key={Math.random()} className='section'>
            <div className='container'>
              <Header title={title} owner={this.props.owner} />
              <p className='empty content'>No {title} added yet</p>  
            </div>
          </div>
        );
      }
    }.bind(this));
    return (
      <div>
        {emptyNodes}
      </div>
    );
  }
});

module.exports = EmptySections;