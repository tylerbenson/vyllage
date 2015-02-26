var React = require('react');
var LayerMixin = require('react-layer-mixin');
var assign = require('lodash.assign');

var Suggesions = React.createClass({
  mixins: [LayerMixin],
  renderLayer: function () {
    console.log(this.props.show);
    if (this.props.show) {
      var style = {
        position: 'absolute'
      };
      style = assign({}, style, this.props.position);
      return <div style={style}> Suggesions </div>;
    } else {
      return null;
    }
  },
  render: function () {
    return null;
  }
});

module.exports = Suggesions;
