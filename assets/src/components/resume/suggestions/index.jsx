var React = require('react');
var actions = require('../actions');
var moment = require('moment');
var Avatar = require('../../avatar');
var Diff = require('../../diff/react-diff');
var AcceptBtn = require('../../buttons/accept');
var DeclineBtn = require('../../buttons/decline');
var EditBtn = require('../../buttons/edit');

var SuggestedEdits = React.createClass({
  render: function () {
    var section = this.props.section || {};

    if( this.props.section.advices !== undefined ){
      var showHighlights = function( highlight ,index){
        return(
          <li className="highlight"><Diff key={index} inputA={section.highlights[index]} inputB={highlight} type="words" /></li>
        )
      }.bind(this);

      var showTags = function(tag ,index){
        return(
          <Diff className="tag" key={index} inputA={section.tags[index]} inputB={tag} type="words" />
        )
      }.bind(this);

      var SuggestedEditLists = this.props.section.advices.map(function(advice ,index){
        if(section.type === 'EducationSection' || section.type === 'JobExperienceSection') {
          var startDateA = section.startDate ? section.startDate : '';
          var startDateB = advice.documentSection.startDate ? advice.documentSection.startDate : '';
          var endDateA = section.endDate ? section.endDate : section.isCurrent ? 'Present' : '';
          var endDateB = advice.documentSection.endDate ? advice.documentSection.endDate : advice.documentSection.isCurrent ? 'Present' : '';
        }

        if( advice.status == 'pending' ) {
        return <div key={index} className='comment'>
                <div className='suggestion-content'>
                    { this.props.owner ? <div className="actions">
                      <AcceptBtn onClick={this._acceptHandler.bind(this, advice)} />
                      <DeclineBtn onClick={this._cancelHandler.bind(this, advice)} />
                    </div> : null }

                  <div className='wrapper'>
                    <div className='info'>
                      <div className="author">{advice.userName?advice.userName:'Vyllage User'}</div>
                      <div className="timestamp">
                        {moment(advice.lastModified).isValid() ? moment.utc(advice.lastModified).fromNow(): ''}
                      </div>
                    </div>
                    <div className="message">
                      { section.type === 'SummarySection' ? <div>
                        <Diff inputA={section.description} inputB={advice.documentSection.description} type="words" />
                      </div>: null}

                      { section.type === 'EducationSection' || section.type === 'JobExperienceSection' ? <div>
                        <h2><Diff inputA={section.organizationName} inputB={advice.documentSection.organizationName} type="words" /></h2>
                        <Diff inputA={section.organizationDescription} inputB={advice.documentSection.organizationDescription} type="words" />
                        <h3><Diff inputA={section.role} inputB={advice.documentSection.role} type="words" /></h3>
                        <Diff inputA={section.roleDescription} inputB={advice.documentSection.roleDescription} type="words" /> <br/>
                        <Diff className="start date" inputA={startDateA} inputB={startDateB} type="words" /> -
                        <Diff className="end date" inputA={endDateA} inputB={endDateB} type="words" />
                        <Diff inputA={section.location} inputB={advice.documentSection.location} type="words" />
                        <ul className="highlights">{advice.documentSection.highlights != undefined ? advice.documentSection.highlights.map(showHighlights) : null }</ul>
                      </div>: null}

                      { section.type === 'SkillsSection' || section.type === 'CareerInterestsSection' ? <div>
                        <div className="tags">{advice.documentSection.tags != undefined ? advice.documentSection.tags.map(showTags) : null}</div>
                      </div>: null}

                    </div>

                  </div>
                </div>
              </div>
            }
      }.bind(this));
    }

    if (section.showEdits) {
      return (
        <div className='comments'>
          { this.props.section.advices.length > 0 ?  SuggestedEditLists : null }
        </div>
      );
    } else {
      return null;
    }
  },
  _acceptHandler : function( suggestion ){
    suggestion.status = 'accepted';
    actions.mergeAdvice( suggestion , this.props.section );
  },
  _cancelHandler : function( suggestion ){
    suggestion.status = 'rejected';
    actions.deleteAdvice( suggestion , this.props.section );
  },
  _editHandler : function( suggestion ){

  }
});

module.exports = SuggestedEdits;