var React = require('react');
var Modal = require('../modal');
var AddSectionOption = require('./AddSectionOption');
var sections = require('../sections');
var actions = require('./actions');

var AddSection = React.createClass({
	getInitialState: function () {
    return {
      isOpen: false
    };
	},
  closeModal: function (e) {
    e.preventDefault();
    this.setState({isOpen: false});
  },
  openModal: function (e) {
    e.preventDefault();
    this.setState({isOpen: true});
  },
  addSection: function (index) {
    var option = sections[index];
    actions.postSection({
      title: option.title,
      type: option.type,
      sectionPosition: 1
    });
    this.setState({isOpen: false});
  },
	render: function() {
    var index = 0;
    var sectionOptions = sections.map(function(option) {
      return (
        <AddSectionOption key={index} option={option} onClick={this.addSection.bind(this, index++)} />
      );
    }.bind(this));

		return (
			<span>
			<button onClick={this.openModal} className="floating add alternate">
        <i className="ion-android-add"></i>
        <span>Add Item</span>
      </button>
			<Modal isOpen={this.state.isOpen} close={this.closeModal} className="large add-modal">
				<div className="header">
          <div className="title">
            <h1>Add Section</h1>
          </div>
          <div className="actions">
            <button className="secondary flat icon" onClick={this.closeModal}>
              <i className="ion-close"></i>
            </button>
          </div>
        </div>
        <div className="content">
          <div className="subheading">Select from one of the items below to add to your resumé.</div>
          {sectionOptions}
        </div>
			</Modal>
			</span>
		);
	}

});

module.exports = AddSection;