var React = require('react');
var LayerMixin = require('react-layer-mixin');
var assign = require('lodash.assign');
var cx = require('react/lib/cx');

var Suggesions = React.createClass({
  // mixins: [LayerMixin],
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
  clickHandler: function (index, e) {
    e.preventDefault();
    this.props.selectSuggestion(index);
    this.setState({
      isFocused: false,
    });
  },
  renderRecipientList: function (recipients, baseIndex) {
    return recipients.map(function (recipient, index) {
      var classes = {
        'is-active': this.props.selectedSuggestion === baseIndex + index
      }
      return (
        <li key={baseIndex + index} className={cx(classes)}>
          <p className='item-text' onClick={this.clickHandler.bind(this, baseIndex + index)}>
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
      style = assign({}, style);
      return (
        <div onMouseDown={this.enterHandler} onMouseUp={this.leaveHanlder}>
          <div id='suggested-users-list' style={style} className={"suggested-users-list"}>
            <ul className="">
              <li className="topper">
                <p className="topper-text">recent</p>
              </li>
              {this.renderRecipientList(this.props.suggestions.recent, 0)}
              <li className="topper">
                <p className="topper-text">recommended</p>
              </li>
              {this.renderRecipientList(this.props.suggestions.recommended, this.props.suggestions.recent.length)}
            </ul>  
          </div>
        </div>
      );
    } else {
      return null;
    }
  },
  // render: function () {
  //   return null;
  // }
});

module.exports = Suggesions;
