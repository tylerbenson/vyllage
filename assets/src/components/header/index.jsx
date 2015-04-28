var React = require('react');

var HeaderContainer = React.createClass({
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
                  <li><a href='/resume'>
                    <i className="ion-document"></i>
                    Resume
                  </a></li>
                  <li><a href='/resume/ask-advice'>
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

var header = document.getElementById('header-container');
if (header) {
  var name = header.getAttribute('name');
  var title = header.getAttribute('title');
  React.render(<HeaderContainer title={title} name={name}/>, header);
}

module.exports = HeaderContainer;


