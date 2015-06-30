var React = require('react');
var RecipientList = require('./RecipientList');
var To = require('./FormTo');
var Subject = require('./FormSubject');
var Message = require('./FormMessage');
var Reflux = require('reflux');
var GetFeedbackStore = require('./store');
var Actions = require('./actions');
var Modal = require('../modal');
var SuggestionSidebar = require('../suggestions/SuggestionSidebar');
var FeatureToggle = require('../util/FeatureToggle');

var InviteForm = React.createClass({
  mixins: [Reflux.connect(GetFeedbackStore)],
  submitHandler: function (e) {
    e.preventDefault();
    Actions.postFeedback();
  },
  cancelHandler: function (e) {
    window.location = '/resume';
  },
  render: function () {
    return (
      <div className="sections">
        <div className='invite-form section'>
          <div className='container'>
            <RecipientList
              users={this.state.users}
              notRegisteredUsers={this.state.notRegisteredUsers}
              selectedRecipient={this.state.selectedRecipient} />
            <To {...this.state} />
            <Subject subject={this.state.subject}/>
            <Message message={this.state.message} />
            <div className='footer'>
              <div className='pull right'>
                <button onClick={this.submitHandler}>
                  <i className="ion-paper-airplane"></i>
                  Send
                </button>
                <button className="secondary" onClick={this.cancelHandler}>
                  Cancel
                </button>
              </div>
            </div>
            <Modal isOpen={this.state.processing}>
              <p>Processing this request</p>
              <p>Please wait</p>
            </Modal>
          </div>
        </div>
        <FeatureToggle name="SUGGESTIONS">
          <SuggestionSidebar />
        </FeatureToggle>
      </div>
    );
  }
});

module.exports = InviteForm;