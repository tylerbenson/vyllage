var React = require('react');
var Highlight = require('../highlights/Highlight');
var HighlightInput = require('../highlights/Input');
var clone = require('clone');
var classnames = require('classnames');

var Highlights = React.createClass({
	getInitialState: function() {
		return {
			highlights: this.props.highlights ? this.props.highlights : [],
			focused: false
		};
	},
	componentWillReceiveProps(nextProps) {
		this.setState({
			highlights: nextProps.highlights != undefined ? nextProps.highlights : []
		});
	},
	onHighlightsFocus: function(flag) {
		this.setState({
			focused: flag
		});
	},
  onHighlightDelete: function(i) {
     var temp = clone( this.state.highlights);
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
        <Highlight key={index}
        	text={highlight}
        	onDelete={this.onHighlightDelete.bind(this, index)}
        	onEdit={this.onHighlightEdit.bind(this , index)}
        	onFocus={this.onHighlightsFocus.bind(this, true)}
        	onBlur={this.onHighlightsFocus.bind(this, false)}
        	uiEditMode={uiEditMode} />
      );
    }.bind(this));

    var classes = classnames({
    	'editMode': uiEditMode,
    	'focused': this.state.focused,
    	'highlights': true
    });

		return (
			<ul className={classes}>
					{this.state.highlights != undefined ? highlights : null }
					{uiEditMode ?
						<HighlightInput
							onAdd={this.onHighlightAdd}
		        	onFocus={this.onHighlightsFocus.bind(this, true)}
		        	onBlur={this.onHighlightsFocus.bind(this, false)}
	        	/>
					: null}
			</ul>
		);
	},
	onHighlightEdit : function(index , data){
		var allHighlights = this.state.highlights;
		if(allHighlights.length){
			allHighlights[index] = data;
		}
		this.setState({ 'highlights' : allHighlights });
	}
});

module.exports = Highlights;
