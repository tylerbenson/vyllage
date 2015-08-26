var React = require('react');

var CommentsCount = React.createClass({
  render: function() {
  	var count = this.props.count;
  	var showText;
  	if(this.props.showComments ){
  		showText = 'Hide comment' + (count !== 1 ? 's' : '');
  	}else{
  		showText = count + ' comment' + (count !== 1 ? 's' : '');
  	}

    return (
      <button className="flat" onClick={this.props.onClick}>
        <i className="ion-chatbox"></i>
        {showText}
      </button>
    );
  }
});

module.exports = CommentsCount;