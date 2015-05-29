var React = require('react');
var PubSub = require('pubsub-js');

var HeaderContainer = React.createClass({
  getInitialState: function () {
    return {
      guest: false
    }
  },
  componentDidMount: function () {
    PubSub.subscribe('GuestViewer', function (id, data) {
      this.setState({
        guest: data
      });
    }.bind(this))
  },
  componentWillUnmount: function () {
    PubSub.unsubscribe('GuestViewer');
  },
  render: function() {
    var name = this.props.name || 'user';
    var title = this.props.title;
    return (
        <div className="content">
          <div className="logo">
            <img src="images/logo-white.png" alt="Vyllage" />
            <span>Vyllage</span>
          </div>
          <span className="page-title">{title}</span>
          <nav>
            <ul>
              <li><a href='/account/setting'><i className="ion-gear-a"></i></a></li>
              <li className="dropdown-trigger user">
                <a><i className="avatar ion-person"></i><span className="name">{name?name:'User'}</span><i className="caret"></i></a>
                <ul className="dropdown-list">
                  {!this.state.guest ? <li><a href='/resume'>
                    <i className="ion-document"></i>
                    Resume
                  </a></li>: null}
                  {!this.state.guest ? <li><a href='/resume/ask-advice'>
                    <i className="ion-person-stalker"></i>
                    Ask Advice
                  </a></li>: null}
                  <li><a href='/logout'>
                    <i className="ion-log-out"></i>
                    Sign out
                    </a></li>
                </ul>
              </li>
            </ul>
          </nav>
        </div>
    );
  }
});

var header = document.getElementById('header-container');
if (header) {
  var name = header.getAttribute('name');
  var title = header.getAttribute('title');
  React.render(<HeaderContainer title={title} name={name}/>, header);
}

module.exports = HeaderContainer;


