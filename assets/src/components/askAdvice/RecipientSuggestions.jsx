var React = require('react');
var cx = require('react/lib/cx');
var Actions = require('./actions');

var Suggesions = React.createClass({
  getInitialState: function () {
    return { 
      isFocused: false,
      isOpen: false
    };
  },
  enterHandler: function (e) {
    this.setState({
      isFocused: true,
    });
  },
  leaveHandler: function (e) {
    this.setState({
      isFocused: false,
    });
  },
  recentClick: function (index, e) {
    e.preventDefault();
    Actions.selectRecentSuggestion(index);
    this.setState({
      isFocused: false,
    });
  },
  recommendedClick: function (index, e) {
    e.preventDefault();
    Actions.selectRecommendedSuggestion(index);
    this.setState({
      isFocused: false,
    });
  },
  renderRecentList: function () {
    var suggestions = this.props.suggestions.recent || [];
    return suggestions.map(function (recipient, index) {
      var classes = {
        'active': this.props.selectedSuggestion === index
      }
      return (
        <li key={index} className={cx(classes)}>
          <div
            className='name'
            onClick={!Modernizr.touch? this.recentClick.bind(this, index): null}
            onTouchStart={Modernizr.touch? this.recentClick.bind(this, index): null}
          >
            {recipient.firstName + " " + recipient.lastName}
          </div>
         </li>
      );
    }.bind(this));
  },
  renderRecommendedList: function () {
    var suggestions = this.props.suggestions.recommended || [];
    return suggestions.map(function (recipient, index) {
      var classes = {
        'active': this.props.selectedSuggestion === this.props.suggestions.recent.length + index
      }
      return (
        <li key={this.props.suggestions.recent.length + index} className={cx(classes)}>
          <div 
            className='name'
            onClick={!Modernizr.touch? this.recommendedClick.bind(this, index): null}
            onTouchStart={Modernizr.touch? this.recommendedClick.bind(this, index): null}
          >
            {recipient.firstName + " " + recipient.lastName}
          </div>
         </li>
      );
    }.bind(this));
  },
  render: function () {
    var recent = this.props.suggestions.recent || [];
    var recommended = this.props.suggestions.recommended || [];
    var suggestionCount = recent.length + recommended.length;
    if ((this.props.show || this.state.isFocused) && (suggestionCount > 0)) {
      var style = {
        position: 'absolute'
      };
      return (
        <div 
          onMouseDown={!Modernizr.touch? this.enterHandler: null}
          onMouseUp={!Modernizr.touch? this.leaveHanlder: null}
          onTouchStart={Modernizr.touch? this.enterHandler: null}
          onTouchEnd={Modernizr.touch? this.leaveHanlder: null}
          style={{marginTop: '100px'}}
        >
          <ul id='suggested-users-list' style={style} className="autocomplete">
            {(recent.length > 0) ? <li className="title">
              Recent
            </li>: null}
            {this.renderRecentList()}
            {(recommended.length > 0) ? <li className="title">
              Recommended
            </li>: null}
            {this.renderRecommendedList()}
          </ul>  
        </div>
      );
    } else {
      return null;
    }
  },
}); 

module.exports = Suggesions;
