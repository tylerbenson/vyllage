var React = require('react');

var Menu = React.createClass({  

    render: function() {
        return (
            <div id="menu-container">
                <div id='triangle-up'></div>
                <div id="account-menu"> 
                    <ul className="account-menu">
                        <li>profile</li>
                        <li>account</li>
                        <li>sign out</li>
                    </ul>
                </div>
            </div>
        );
    }
});

var HeaderContainer = React.createClass({  

    handleClick: function(){
        var menuContainer = this.refs.menuContainer.getDOMNode();

        if(menuContainer.style.display =='block') {
           menuContainer.style.display ='none'
        } else {
            menuContainer.style.display ='block'
        }
    },
    
    render: function() {
        return (
            <div>
               <div className="row">
                    <div className="twelve columns">
                        <div className="u-pull-left header-logo"></div>
                        <div className="u-pull-right header-profile">
                            <img className="u-pull-left header-profile-photo" alt="" src="images/profile-photo.png"/>
                            <span className="u-pull-left header-username" id="account-name">
                                <p onClick={this.handleClick}>nathan</p>
                            </span>
                            <button className="u-pull-left ask-advice-btn">ask advice</button>
                        </div>
                    </div>
                </div>

                <Menu ref="menuContainer"/>
            </div>
        );
    }
});


React.render(<HeaderContainer />, document.getElementById('header-container'));




