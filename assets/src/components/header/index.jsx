var React = require('react');
var Reflux = require('reflux');
var ExportButton = require('../export/ExportButton');
var ShareButton = require('../getFeedback/share');
var Milestone = require('../milestones');
var NavToggle = require('./NavToggle');
var resumeStore = require('../resume/store');
var resumeActions = require('../resume/actions');
var FeatureToggle = require('../util/FeatureToggle');
var AdminLink = require('../admin/AdminLink');

var HeaderContainer = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentWillMount: function(){
    resumeActions.getResume();
  },
  componentDidMount: function () {
    var path = window.location.href;
    var re = new RegExp(/\/resume\/[0-9]+$/)
    this.isResumePage = re.test(path);
  },
  render: function() {
    var name = this.props.name || 'user';
    var title = this.props.title;
    var resume = this.state.resume;
    // var owner = this.state.resume.header.owner;
    // var showLink = owner || !this.isResumePage;

    return (
      <div>
        <div className="content">
          <a href="/" className="logo">
            <img src="images/logo-orange.png" alt="Vyllage" />
            <span>Vyllage</span>
          </a>
          <span className="page-title">{title}</span>
          <nav>
            <NavToggle />
            <ul className={this.state.resume.isNavOpen?'active':''}>
              <li><a href='/resume' className='flat button'>
                <i className="ion-document"></i>
                <span>Resume</span>
              </a></li>
              <FeatureToggle name="PRINTING">
                <li><ExportButton documentId={resume.ownDocumentId} sections={resume.sections}  /></li>
              </FeatureToggle>
              <li><ShareButton documentId={resume.ownDocumentId} sections={resume.sections}  /></li>
              <li>
              	<AdminLink />
              </li>
              <li><a href='/resume/get-feedback' className='embossed button'>
                <i className="ion-person-stalker"></i>
                <span>Get Feedback</span>
              </a></li>
              <li className="milestone-toggle">
                <Milestone />
              </li>
              <li><a href='/account/setting' className='flat settings button'>
                <i className="ion-gear-a"></i>
                <span>Settings</span>
              </a></li>
              {/*<li className="dropdown-trigger user">
                <a><i className="avatar ion-person"></i><span className="name">{name?name:'User'}</span></a>
              </li>*/}
            </ul>
          </nav>
        </div>
      </div>
    );
  }
});

var header = document.getElementById('header-container');
if (header) {
  var name = header.getAttribute('name');
  var title = header.getAttribute('title');
  React.render(<HeaderContainer title={title} name={name}/>, header);
}

module.exports = HeaderContainer;


