var React = require('react');
var Reflux = require('reflux');
var CommentsButton = require('./Button');
var CommmentsStore = require('./store');
var CommentsForm = require('./Form');

var Controls = React.createClass({
  mixins: [Reflux.connect(CommmentsStore)],
  render: function() {
    return (
      <div className="article-controll">
        <div className="article-controll-btns">
          <div className="u-pull-left">
            <a href="" className="suggestions">suggestions</a>
            <span className="suggestions-count count">2</span>
          </div>
          <CommentsButton />
        </div>
        {this.state.showComments? <CommentsForm />: null}
      </div>
    );
  }
});

module.exports = Controls;