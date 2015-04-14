var React = require('react');

var HeaderContainer = React.createClass({
  getInitialState: function () {
    return { documentId: '' };
  },
  componentDidMount: function () {
    var documentId = window.location.pathname.split('/')[2];
    this.setState({documentId: documentId});
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
                <a><i className="avatar ion-person"></i><span className="name">{name}</span><i className="caret"></i></a>
                <ul className="dropdown-list">
                  <li><a href={'/resume/' + this.state.documentId}>
                    <i className="ion-document"></i>
                    Resume
                  </a></li>
                  <li><a href={'/resume/' + this.state.documentId + '/ask-advice'}>
                    <i className="ion-person-stalker"></i>
                    Ask Advice
                  </a></li>
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

var name = document.getElementById('header-container').getAttribute('name');
var title = document.getElementById('header-container').getAttribute('title');
React.render(<HeaderContainer title={title} name={name}/>, document.getElementById('header-container'));

module.exports = HeaderContainer;


