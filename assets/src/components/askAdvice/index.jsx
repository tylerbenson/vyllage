var React = require('react');
var RecipientList = require('./RecipientList');
var To = require('./FormTo');
var Subject = require('./FormSubject');
var Message = require('./FormMessage');
var Reflux = require('reflux');
var AskAdviceStore = require('./store');
var Actions = require('./actions');
var Modal = require('../modal');

var InviteForm = React.createClass({
  mixins: [Reflux.connect(AskAdviceStore)],
  submitHandler: function (e) {
    e.preventDefault();
    Actions.postAskAdvice();
  },
  cancelHandler: function (e) {
    window.location = '/resume';
  },
  render: function () {
    return (
      <div className="sections">
        <div className='section'>
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
      </div>
    );
  }
});

module.exports = InviteForm;