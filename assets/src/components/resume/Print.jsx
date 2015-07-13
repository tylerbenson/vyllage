var React = require('react');
var Reflux = require('reflux');
var Modal = require('../modal');
var actions = require('./actions');
var resumeStore = require('./store');

var Print = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentWillMount: function () {
    actions.getResume();
  },
  handlePrint: function(e) {
    if(this.state.resume.sections.length < 1) {
      e.preventDefault();
      actions.openEmptyResumeModal();
    }
  },
  closeModal: function(e) {
    e.preventDefault();
    actions.closeEmptyResumeModal();
  },
	render: function() {
		return (
      <span>
  			<a onClick={this.handlePrint} href={"/resume/"+this.state.resume.documentId+"/file/pdf"} className="flat print button">
          <i className="ion-printer"></i>
          <span>Print</span>
        </a>
        <Modal isOpen={this.state.resume.isEmptyResumeModalOpen} close={this.closeModal}>
          <div className="header">
            <div className="title">
              <h1>Resumé Empty</h1>
            </div>
            <div className="actions">
              <button className="secondary flat icon" onClick={this.closeModal}>
                <i className="ion-close"></i>
              </button>
            </div>
          </div>
          <div className="content">
            <p>Please add sections before printing your resumé.</p>
          </div>
          <div className="footer">
            <button onClick={this.closeModal} className="small inverted">
              <i className="ion-checkmark"></i>
              Okay
            </button>
          </div>
        </Modal>
      </span>
		);
	}

});

module.exports = Print;