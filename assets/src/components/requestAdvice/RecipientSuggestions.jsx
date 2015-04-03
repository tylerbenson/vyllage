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
  recentClick: function (index) {
    Actions.selectRecentSuggestion(index);
    this.setState({
      isFocused: false,
    });
  },
  recommendedClick: function (index) {
    Actions.selectRecommendedSuggestion(index);
    this.setState({
      isFocused: false,
    });
  },
  renderRecentList: function () {
    return this.props.suggestions.recent.map(function (recipient, index) {
      var classes = {
        'is-active': this.props.selectedSuggestion === index
      }
      return (
        <li key={index} className={cx(classes)}>
          <p className='item-text' onClick={this.recentClick.bind(this, index)}>
            {recipient.firstName + " " + recipient.lastName}
          </p>
         </li>
      );
    }.bind(this));
  },
  renderRecommendedList: function () {
    return this.props.suggestions.recent.map(function (recipient, index) {
      var classes = {
        'is-active': this.props.selectedSuggestion === this.props.suggestions.recent.length + index
      }
      return (
        <li key={this.props.suggestions.recent.length + index} className={cx(classes)}>
          <p className='item-text' onClick={this.recommendedClick.bind(this, index)}>
            {recipient.firstName + " " + recipient.lastName}
          </p>
         </li>
      );
    }.bind(this));
  },
  render: function () {
    if (this.props.show || this.state.isFocused) {
      var style = {
        position: 'absolute'
      };
      return (
        <div onMouseDown={this.enterHandler} onMouseUp={this.leaveHanlder} style={{marginTop: '40px'}}>
          <ul id='suggested-users-list' style={style} className="autocomplete">
            <li className="title">
              Recent
            </li>
            {this.renderRecentList()}
            <li className="title">
              Recommended
            </li>
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
