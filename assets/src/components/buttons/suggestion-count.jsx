var React = require('react');

var SuggestedEditsCount = React.createClass({
  render: function() {
  	var count = this.props.count;
    var showText;
    if(this.props.showEdits ){
      showText = 'Hide edit' + (count !== 1 ? 's' : '');
    }else{
      showText = count + ' edit' + (count !== 1 ? 's' : '');
    }

    return (
      <button className="flat" onClick={this.props.onClick}>
        <i className="ion-edit"></i>
        {showText}
      </button>
    );
  }
});

module.exports = SuggestedEditsCount;