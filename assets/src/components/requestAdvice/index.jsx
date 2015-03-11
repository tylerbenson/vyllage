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
      <section className="container request-advice">
        <div className="request-advice-header">
          <p>advice request</p>
        </div>
        <div className='row'>
          <div className='twelve columns'>
            <form className='request-advice-form' onSubmit={this.submitHandler}>
              <To {...this.state} />
              <Subject subject={this.state.subject}/>
              <div className='offset-by-one nine columns'>
                <div className='request-advice-form-message'>
                  <Message message={this.state.message} />
                  <div className='u-pull-right'>
                    <button type='submit' className="send-btn" onClick={this.submitHandler}>send</button>
                    <button className="cancel-btn" onClick={this.cancelHandler}>cancel</button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </section>
    );
  }
});

module.exports = Form;