var React = require('react');
var actions = require('../actions');
var moment = require('moment');
var Avatar = require('../../avatar');
var Diff = require('../../diff/react-diff');
var AcceptBtn = require('../../buttons/accept');
var CancelBtn = require('../../buttons/cancel');
var EditBtn = require('../../buttons/edit');

var Advices = React.createClass({
  render: function () {
    var section = this.props.section || {};

    if( this.props.section.advices != undefined ){

      var showHighlights = function( highlight ,index){
        return(
          <Diff inputA={section.highlights[index]} inputB={highlight} type="words" /> 
        )
      }.bind(this);

      var showTags = function( tag ,index){
        return(
          <Diff inputA={section.tags[index]} inputB={tag} type="words" /> 
        )
      }.bind(this);

      var adviceList = this.props.section.advices.map(function(advice ,index){
        if( advice.status == 'pending' )
        return <div key={index} className='comment'>
                <div className='advice-content'>
                    { this.props.owner ? <div className="actions">
                      <AcceptBtn onClick={this._acceptHandler.bind(this, advice)} />
                      <CancelBtn onClick={this._cancelHandler.bind(this, advice)} />
                    </div> : null }

                  <div className='wrapper'>
                    <div className='info'>
                      <div className="author">{advice.userName?advice.userName:'Vyllage User'}</div>
                      <div className="timestamp">
                        {moment(advice.lastModified).isValid() ? moment.utc(advice.lastModified).fromNow(): ''}
                      </div>
                    </div>
                    <div className="message">
                      { section.type == 'SummarySection' ? <div> 
                        <Diff inputA={section.description} inputB={advice.documentSection.description} type="words" />
                      </div>: null}

                      { section.type == 'EducationSection' || section.type == 'JobExperienceSection' ? <div> 
                        <Diff inputA={section.organizationName} inputB={advice.documentSection.organizationName} type="words" />
                        <Diff inputA={section.organizationDescription} inputB={advice.documentSection.organizationDescription} type="words" />
                        <Diff inputA={section.role} inputB={advice.documentSection.role} type="words" />
                        <Diff inputA={section.roleDescription} inputB={advice.documentSection.roleDescription} type="words" /> <br/>
                        <Diff inputA={section.location} inputB={advice.documentSection.location} type="words" /> <br/>
                        {advice.documentSection.highlights != undefined ? advice.documentSection.highlights.map(showHighlights) : null }
                      </div>: null}

                      { section.type == 'SkillsSection' || section.type == 'CareerInterestsSection' ? <div> 
                        {advice.documentSection.tags != undefined ? advice.documentSection.tags.map(showTags)  :null }
                      </div>: null}

                    </div>

                  </div>
                </div>
              </div>
      }.bind(this));
    }

    if (section.showEdits) {
      return (
        <div className='comments'>
          { this.props.section.advices.length > 0 ?  adviceList : null }
        </div>
      );
    } else {
      return null;
    }
  },
  _acceptHandler : function(advice){
    advice.status = 'accepted';
    actions.mergeAdvice( advice , this.props.section );
  },
  _cancelHandler : function( advice ){
    advice.status = 'rejected';
    actions.deleteAdvice(advice , this.props.section );
  },
  _editHandler : function( advice ){

  }


});

module.exports = Advices;