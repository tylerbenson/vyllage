var React = require('react');
var Header = require('./Header');
var ArticleContent = require('../organization/article-content');

var Education = React.createClass({
  render: function () {
    var sectionNodes
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return <ArticleContent  key={index} organizationData={section}/>
      })
    } else {
      sectionNodes = <p className='add-more'>No education added yet</p>
    }
    return (
      <article className='experience forceEditMode'>
        <div className="row">
          <div className="twelve columns">
            <Header title='Education' /> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Education;