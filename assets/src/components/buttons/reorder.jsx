var React = require('react');
var Reflux = require('reflux');
var classnames = require('classnames');
var Modal = require('../modal');

var ResumeStore = require('../resume/store');
var ResumeActions = require('../resume/actions');

var Reorder = React.createClass({
	mixins: [Reflux.connect(ResumeStore, 'resume')],
	getInitialState: function(){
    return {
      isReorderModalOpen: false
    };
  },
  closeModal: function() {
    this.togglePrintModal(false);
  },
  openModal: function() {
    this.togglePrintModal(true);
  },
  togglePrintModal: function(flag){
    this.setState({
      isReorderModalOpen: (flag !== undefined ? flag : !this.state.isReorderModalOpen)
    });
  },
  toggleSorting: function(){
  	if(this.state.resume.all_section && this.state.resume.all_section.length > 1){
	  	ResumeActions.toggleSorting();
  	}
  	else {
  		this.openModal();
  	}
  },
	render: function() {
		var classes = classnames({
			'secondary': !this.state.resume.isSorting,
			'flat': true,
			'reorder': true
		});

		return (
			<span className="wrapper">
				<button onClick={this.toggleSorting} className={classes}>
	        <i className="ion-arrow-swap"></i>
	        <span>Reorder</span>
	      </button>
	      <Modal isOpen={this.state.isReorderModalOpen} close={this.closeModal}>
          <div className="header">
            <div className="title">
              <h1>No Sections to Reorder</h1>
            </div>
            <div className="actions">
              <button className="secondary flat icon" onClick={this.closeModal}>
                <i className="ion-close"></i>
              </button>
            </div>
          </div>
          <div className="content">
            <p>You must have more than one section to reorder.</p>
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

module.exports = Reorder;