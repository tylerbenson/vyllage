var React = require('react');
var To = require('./FormTo');
var Subject = require('./FormSubject');
var Message = require('./FormMessage');
var Reflux = require('reflux');
var RequestAdviceStore = require('./store');
var Actions = require('./actions');

var Form = React.createClass({
  mixins: [Reflux.connect(RequestAdviceStore)],
  submitHandler: function (e) {
    e.preventDefault();
    Actions.postRequestAdvice();
  },
  cancelHandler: function (e) {
    window.location = '/resume';
  },
  render: function () {
    return (
      <div className="sections">
        <div className='section'>
          <div className='container'>
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
          </div>
        </div>
      </div>
    );
  }
});

module.exports = Form;