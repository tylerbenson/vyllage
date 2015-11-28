var React = require('react');
var Highlight = require('../highlights/Highlight');
var HighlightInput = require('../highlights/Input');
var Sortable = require('../../util/Sortable');
var clone = require('clone');

var Highlights = React.createClass({
	getInitialState: function() {
		return {
			highlights: this.props.highlights ? this.props.highlights : []
		};
	},
	componentWillReceiveProps(nextProps) {
		this.setState({
			highlights: nextProps.highlights != undefined ? nextProps.highlights : []
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

	stop: function(e, ui){
		var temp = [];
    jQuery(e.target).children('li').each(function(index) {
      var tempText= jQuery(this).text();
      if( tempText != "")
      	temp.push(tempText);
    });
  	this.setState({
      highlights: temp
    });
	},
	render: function() {
		var uiEditMode = this.props.uiEditMode;
		var highlights = this.state.highlights.map(function(highlight, index){
      return (
        <Highlight key={index} text={highlight} onDelete={this.onHighlightDelete.bind(this, index)} onEdit={this.onHighlightEdit.bind(this , index)} uiEditMode={uiEditMode} />
      );
    }.bind(this));

    var classes = "highlights";
    if(uiEditMode){
    	classes += " boxed";
    }

    var config = {
    	list : ".highlights",
      items: "li.highlight",
      stop: this.stop
    };
		return (
			<Sortable className={classes} config={config}>
					{this.state.highlights != undefined ? highlights : null }
					{uiEditMode ? <HighlightInput onAdd={this.onHighlightAdd} /> : null}
			</Sortable>
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
