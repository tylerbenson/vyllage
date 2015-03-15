var React = require('react');
var Header = require('./Header');
var ArticleContent = require('../../organization/article-content');

var Experience = React.createClass({
  render: function () {
    var sections = this.props.sections || [];
    var sectionNodes = sections.map(function (section, index) {
      return <ArticleContent  key={index} organizationData={section}/>
    })
    return (
      <article className='experience forceEditMode'>
        <div className="row">
          <div className="twelve columns">
            <Header title='experience' type='experience'/> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Experience;