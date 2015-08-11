var React = require('react');
var classnames = require('classnames');
var OnResize = require('react-window-mixins').OnResize;

var Modal = React.createClass({
  mixins: [OnResize],
  onResize: function() {
    this.center();
  },
  center: function(){
    var modal = this.refs.modal;

    if(modal) {
      modal = modal.getDOMNode();
      var body = document.querySelector('body');

      if(body && this.props.isOpen) {
        body.className += ' modal-open';

        modal.removeAttribute('style');

        modal.style.display = 'inline-block';
        modal.style.height = modal.offsetHeight + 'px';
        modal.style.position = 'absolute';

        //modal is larger than viewport
        modal.style.margin = (modal.offsetTop < 0 ? '0 ' : '' ) + 'auto';
        modal.style.borderRadius = (modal.offsetTop < 0 ? '0 ' : '5px' ) + 'auto';
      }
      else {
        body.className = body.className.replace(/ modal-open/g,'');
        modal.style.display = 'none';
      }
    }
  },
  componentDidUpdate: function(){
    this.center();
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