var React = require('react');
var To = require('./FormTo');
var Subject = require('./FormSubject');
var Message = require('./FormMessage');

var Form = React.createClass({
  submitHandler: function (e) {
    e.preventDefault();
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
              <To />
              <Subject />
              <div className='offset-by-one nine columns'>
                <div className='request-advice-form-message'>
                  <Message />
                  <div className='u-pull-right'>
                    <button className="send-btn">send</button>
                    <button className="cancel-btn">cancel</button>
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