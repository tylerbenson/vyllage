var React = require('react');
var Comments = require('../comments');

var ArticleControlls = React.createClass({
    render: function() {
        return (
            <div className="article-controll">
                <div className="article-controll-btns">
                    <div className="u-pull-left">
                        <a href="" className="suggestions">suggestions</a>
                        <span className="suggestions-count count">2</span>
                    </div>
                    <Comments />
                </div>
            </div>
        );
    }
});

module.exports = ArticleControlls;