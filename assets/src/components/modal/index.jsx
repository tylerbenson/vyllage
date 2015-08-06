var React = require('react');
var classnames = require('classnames');

var Modal = React.createClass({
  getDefaultProps: function () {
    return {
      isOpen: false,
    }
  },
  componentDidUpdate: function(){
    var body = document.querySelector('body');
    var modal = this.refs.modal.getDOMNode();

    if(body && this.props.isOpen) {
      body.className += ' modal-open';
      modal.style.display = 'inline-block';
      modal.style.height = modal.offsetHeight + 'px';
      modal.style.position = 'absolute';
    }
    else {
      body.className = body.className.replace(/ modal-open/g,'');
      modal.style.display = 'none';
    }
  },
  stopPropagation: function (e) {
    e.stopPropagation();
    e.preventDefault();
  },
  render: function () {
    var overlayClasses = classnames({'overlay': this.props.isOpen});
    var modalClasses = classnames(
      [{'modal': this.props.isOpen}].concat([this.props.className])
    );

    return (
      <div className={overlayClasses} onClick={this.props.close}>
        <div ref="modal" className={modalClasses} onClick={this.stopPropagation}>
          {this.props.isOpen? this.props.children: null}
        </div>
      </div>
    );
  }
});

module.exports = Modal;