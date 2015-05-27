var React = require('react');
var Header = require('./Header');
var filter = require('lodash.filter');

var EmptySections = React.createClass({
  render: function () {
    var sectionOptions = [
      { title: 'career goal', type: 'freeform' },
      { title: 'experience', type: 'experience' },
      { title: 'education', type: 'experience' },
      { title: 'skills', type: 'freeform' },
    ]
    var emptyNodes = sectionOptions.map(function (options) {
      var sections = filter(this.props.sections, {title: options.title})
      if (sections.length === 0) {
        return (
          <div key={Math.random()} className='section'>
            <div className='container'>
              <Header title={options.title} type={options.type} owner={this.props.owner} />
              <p className='empty content'>No {options.title} added yet</p>  
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