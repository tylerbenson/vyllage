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
    var groupPosition = this.props.sections.length || 0;
    var emptyNodes = sectionOptions.map(function (options, index) {
      var sections = filter(this.props.sections, {title: options.title})
      if (sections.length === 0) {
        return (
          <div key={Math.random()} className='section'>
            <div className='container'>
              <Header title={options.title} type={options.type} owner={this.props.owner} groupPosition={groupPosition} />
              <p className='empty content'>No {options.title} added yet</p>  
            </div>
          </div>
        );
        // Increment groupPosition for sections types that are empty
        groupPosition += 1;
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