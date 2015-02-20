var React = require('react');

var CommentsBlog = React.createClass({
    render: function() {
        return (
            <div className="comments-content">
                <div className="comment-list-block">
                    <div className="comment-person-info">
                        <img className="u-pull-left" src="images/comment-person.png" width="40" height="40" />
                        <p className="u-pull-left name">Richard Zielke</p>
                        <p className="u-pull-left date">2 hrs ago</p>
                    </div>
                    <div className="comment-body">
                        <p className="">
                            I like what you are saying here about your experience with DeVry. You have done an excellent job of outlining your successes.
                        </p>
                    </div>
                    <div className="comment-ctrl">
                        <div className="u-pull-right">
                            <ul className="comment-ctrl-list">
                                <li className="comment-ctrl-btn"><a href="#">thank</a>
                                </li>
                                <li className="comment-ctrl-btn"><a href="#">hide</a>
                                </li>
                                <li className="comment-ctrl-btn"><a href="#">reply</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div className="comment-divider"></div>
            </div>
        );
    }
});

module.exports = CommentsBlog;