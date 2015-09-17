var React = require('react');
var actions = require('../actions');
var moment = require('moment');
var Avatar = require('../../avatar');
var Diff = require('react-diff');
var AcceptBtn = require('../../buttons/accept');
var CancelBtn = require('../../buttons/cancel');

var Advices = React.createClass({
  render: function () {
    var section = this.props.section || {};

    if( this.props.section.advices != undefined )
    var adviceList = this.props.section.advices.map(function(advice ,index){

      return <div key={index} className='comment'>
        <div className='content'>
          <div className="actions">
            <AcceptBtn onClick={this._acceptHandler.bind(this, advice)} />
            <CancelBtn onClick={this._cancelHandler.bind(this, advice)} />
          </div>
          <div className='avatar-container'>
            <Avatar src={advice.avatarUrl} size="30" />
          </div>
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
                <Diff inputA={section.roleDescription} inputB={advice.documentSection.roleDescription} type="words" />
                
              </div>: null}


            </div>
          </div>
        </div>
       </div>
    }.bind(this));

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
    
    // it will replace the doc with the advice.doc 
  },
  _cancelHandler : function( advice ){


  }

});

module.exports = Advices;