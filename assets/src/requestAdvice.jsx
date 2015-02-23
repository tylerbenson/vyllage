var React = require('react');

var RequestAdvice = React.createClass({
    render: function () {
        return (
            <div className="rcpent-list" contentEditable>
                <p className="rcpent">John Smith</p>
                <p className="rcpent">Edward Johnson</p>
                <p className="rcpent"> Cindy Lawson</p>
                <p className="rcpent">Julie Kim</p>
                <p className="rcpent">Sarah Shore</p>
                <p className="rcpent"> Sally Ride</p>
            </div>
        );
    }
});

React.render(<RequestAdvice />, document.getElementById('rcpent-list'));