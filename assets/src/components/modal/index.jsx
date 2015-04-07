var React = require('react');
var classnames = require('classnames');

var Modal = React.createClass({
  getDefaultProps: function () {
    return {
      isOpen: false,
    }
  },
  stopPropagation: function (e) {
    e.stopPropagation();
    e.preventDefault();
  },
  render: function () {
    var overlayClasses = classnames({'overlay': this.props.isOpen});
    var modalClasses = classnames({'modal': this.props.isOpen});
    return (
      <div className={overlayClasses} onClick={this.props.close}>
        <div className={modalClasses} onClick={this.stopPropagation}>
          {this.props.isOpen? this.props.children: null}
        </div>
      </div>
    );
  }
});

module.exports = Modal;