var React = require('react');
var Reflux = require('reflux');
var actions = require('./actions');
var resumeStore = require('./store');
var filter = require('lodash.filter');
var Modal = require('../modal');

var Print = React.createClass({
  mixins: [Reflux.connect(resumeStore, 'resume')],
  componentWillMount: function() {
  	actions.getDocumentId();
  },
  print: function(e) {
    e.preventDefault();
    if(filter(this.state.resume.sections, {isSupported: true}).length > 0) {
      window.location = "/resume/"+this.state.resume.documentId+"/file/pdf";
    }
    else {
      actions.togglePrintModal(true);
    }
  },
  closeModal: function() {
    actions.togglePrintModal(false);
  },
	render: function() {
		return (
      <span className="wrapper">
  			<a onClick={this.print} className="flat print button">
          <i className="ion-printer"></i>
          <span>Print</span>
        </a>
        <Modal isOpen={this.state.resume.isPrintModalOpen} close={this.closeModal}>
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
            <p>Add sections to print your resumé.</p>
          </div>
          <div className="footer">
            <button className="small inverted" onClick={this.closeModal}>
              Okay
            </button>
          </div>
        </Modal>
      </span>
		);
	}

});

module.exports = Print;