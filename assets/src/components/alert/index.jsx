var React = require('react');

var Alert = React.createClass({
  getInitialState: function () {
    return {
      isOpen: false
    }
  },
  stopPropagation: function (e) {
    e.stopPropagation();
    e.preventDefault();
  },
  closeHandler: function(e) {
  	this.setState({
  		isOpen: false
  	});
  },
  render: function () {
    return (
      <div className={(this.state.isOpen?'visible ':'') + 'alert'} >
      	{this.props.message}
      	<button className='pull right small flat icon button' onClick={this.closeHandler}>
      		<i className='ion-close'></i>
      	</button>
      </div>
    );
  }
});

module.exports = Alert;