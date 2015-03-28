var React = require('react');

var HeaderContainer = React.createClass({ 
  render: function() {
    var name = this.props.name || user;
    return (
        <div className="content">
          <div className="logo">
            <img src="images/logo-white.png" alt="Vyllage" />
            <span>Vyllage</span>
          </div>
          <span className="page-title">Resume</span>
          <nav>
            <ul>
              <li><a><i className="ion-ios-bell"></i></a></li>
              <li><a><i className="ion-gear-a"></i></a></li>
              <li className="dropdown-trigger user">
                <a><span className="name">{name}</span><i className="caret"></i></a>
                <ul className="dropdown-list">
                  <li><a>
                    <i className="ion-document"></i>
                    Resume
                  </a></li>
                  <li><a>
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
React.render(<HeaderContainer name={name}/>, document.getElementById('header-container'));

module.exports = HeaderContainer;


