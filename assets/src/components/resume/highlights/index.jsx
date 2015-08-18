var React = require('react');
var Highlight = require('../highlights/Highlight');
var HighlightInput = require('../highlights/Input');

var Highlights = React.createClass({
	getInitialState: function() {
		return {
			highlights: this.props.highlights
		};
	},
	componentWillReceiveProps(nextProps) {
		this.setState({
			highlights: nextProps.highlights
		});
	},
  onHighlightDelete: function(i) {
    var temp = this.state.highlights.slice();
    temp.splice(i,1);

    this.setState({
      highlights: temp
    });
  },
  onHighlightAdd: function(value) {
  	var temp = this.state.highlights.slice();
  	temp.push(value);

  	this.setState({
      highlights: temp
    });
  },
	getHighlights: function() {
		return this.state.highlights;
	},
	render: function() {
		var uiEditMode = this.props.uiEditMode;
		var highlights = this.state.highlights.map(function(highlight, index){
      return (
        <Highlight key={index} text={highlight} onDelete={this.onHighlightDelete.bind(this, index)} uiEditMode={uiEditMode} />
      );
    }.bind(this));

		return (
			<ul className="highlights">
				{highlights}
				{uiEditMode ? <HighlightInput onAdd={this.onHighlightAdd} /> : null}
			</ul>
		);
	}

});

module.exports = Highlights;