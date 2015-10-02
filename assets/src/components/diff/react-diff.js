'use strict';

var React = require('react');
var jsdiff = require('diff');
var classnames = require('classnames');

var fnMap = {
  chars: jsdiff.diffChars,
  words: jsdiff.diffWords,
  sentences: jsdiff.diffSentences,
  json: jsdiff.diffJson
};

module.exports = React.createClass({
  displayName: 'Diff',

  getDefaultProps: function getDefaultProps() {
    return {
      inputA: '',
      inputB: '',
      type: 'chars'
    };
  },

  propTypes: {
    inputA: React.PropTypes.oneOfType([React.PropTypes.string, React.PropTypes.object]),
    inputB: React.PropTypes.oneOfType([React.PropTypes.string, React.PropTypes.object]),
    type: React.PropTypes.oneOf(['chars', 'words', 'sentences', 'json'])
  },

  render: function render() {
    var diff = fnMap[this.props.type](this.props.inputA, this.props.inputB);
    var isChanged = false;
    var result = diff.map(function (part , diffIndex ) {
      var className = part.added ? 'added' : part.removed ? 'deleted' : '';
      isChanged = isChanged || className !== '';

      return React.createElement(
        'span',
        { className: className , key : diffIndex },
        part.value
      );
    });

    var diffClasses = classnames(
      [{
        'diff': true,
        'unchanged': !isChanged
      }]
      .concat([this.props.className])
    );

    return React.createElement(
      'div',
      { className: diffClasses },
      result
    );
  } });

