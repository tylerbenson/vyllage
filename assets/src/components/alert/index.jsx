var React = require('react');
var PubSub = require('pubsub-js');
var classnames = require('classnames');

var Alert = React.createClass({
  getInitialState: function () {
    return {
      isOpen: false,
      message: "",
      timeout: 4000
    }
  },
  getDefaultProps: function () {
    return {
      id: Math.random(),
      className: 'alert'
    }
  },
  componentDidMount: function () {
    var timeout = {};
    PubSub.subscribe(this.props.id, function (id, data) {
      var self = this;
      self.setState(data);
      clearTimeout(timeout);
      timeout = setTimeout(self.closeHandler, self.state.timeout);
    }.bind(this))
  },
  componentWillUnmount: function () {
    PubSub.unsubscribe(this.props.id);
  },
  closeHandler: function(e) {
  	this.setState({
  		isOpen: false,
      message: ""
  	});
  },
  render: function () {
    var className = classnames({
      visible: this.state.isOpen,
      [this.props.className]: true
    })
    return (
      <div className={className } >
      	{this.state.message}
      	<button className='pull right small flat secondary icon button' onClick={this.closeHandler}>
      		<i className='ion-close'></i>
      	</button>
      </div>
    );
  }
});

module.exports = Alert;