var React = require('react');
var classnames = require('classnames');

var Modal = React.createClass({
  getDefaultProps: function () {
    return {
      isOpen: false,
    }
  },
  render: function () {
    var overlayClasses = classnames({'modal-overlay': this.props.isOpen});
    var modalClasses = classnames({'modal': this.props.isOpen});
    return (
      <div className={overlayClasses}>
        <div className={modalClasses}>
          {this.props.isOpen? this.props.children: null}
        </div>
      </div>
    );
  }
});

module.exports = Modal;