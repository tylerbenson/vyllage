var React = require('react');
var Reflux = require('reflux');
var filter = require('lodash.filter');
var Modal = require('../modal');

var ExportButton = React.createClass({
  getInitialState: function(){
    return {
      isPrintModalOpen: false
    };
  },
  print: function(e) {
    e.preventDefault();
    if(filter(this.props.sections, {isSupported: true}).length > 0) {
      window.location = "/document/"+this.props.documentId+"/export";
    }
    else {
      this.togglePrintModal(true);
    }
  },
  closeModal: function() {
    this.togglePrintModal(false);
  },
  togglePrintModal: function(flag){
    this.setState({
      isPrintModalOpen: (flag !== undefined ? flag : !this.state.isPrintModalOpen)
    });
  },
	render: function() {
		return (
      <span className="wrapper">
			<a onClick={this.print} className="flat print button">
          <i className="ion-printer"></i>
          <span>Export</span>
        </a>
        <Modal isOpen={this.state.isPrintModalOpen} close={this.closeModal}>
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

module.exports = ExportButton;