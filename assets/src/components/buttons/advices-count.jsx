var React = require('react');

var CommentsCount = React.createClass({


  render: function() {
  	var count = this.props.count;

    console.log(count);

  //	if( count == undefined || count == 'NaN' ) count = 0;

    var showText;
    showText = count + ' edit' + (count !== 1 && count !== 0 ? 's' : '');
    return (
      <button className="flat" onClick={this.props.onClick}>
        <i className="ion-edit"></i>
        {showText}
      </button>
    );
  }
});

module.exports = CommentsCount;