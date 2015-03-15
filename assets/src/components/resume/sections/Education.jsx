var React = require('react');
var Header = require('./Header');
var ArticleContent = require('../../organization/article-content');

var Education = React.createClass({
  render: function () {
    var sections = this.props.sections || [];
    var sectionNodes = sections.map(function (section, index) {
      return <ArticleContent  key={index} organizationData={section}/>
    })
    return (
      <article className='experience forceEditMode'>
        <div className="row">
          <div className="twelve columns">
            <Header title='education'  type='education' /> 
            {sectionNodes}
          </div>
        </div>
      </article>
    );
  }
});

module.exports = Education;