var React = require('react');
var Header = require('./Header');
var ArticleContent = require('../organization/article-content');
var SectionFooter = require('./Footer');

var Experience = React.createClass({
  render: function () {
    var sectionNodes;
    var sections = this.props.sections || [];
    if (sections.length > 0) {
      sectionNodes = sections.map(function (section, index) {
        return (
          <div>
            <ArticleContent  key={index} organizationData={section}/>
            <SectionFooter />
          </div>
        );
      })
    } else {
      sectionNodes = <p className='add-more'>No experience added yet</p>;
    }
    return (
      <article className='experience forceEditMode'>
        <div className="row">
          <div className="twelve columns">
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Experience;