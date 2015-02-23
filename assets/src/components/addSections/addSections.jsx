var React = require('react');

var AddSections = React.createClass({  

     addSection: function() {
            
        if(this.props.addSection){
            this.props.addSection(this.props.type);
        }
    },

    render: function() {

        return (
            <article className="add-section education">
                <div className="row">
                    <div className="twelve columns">
                        <div onClick={this.addSection}>
                            <button className="article-btn"> education </button>
                            <p className="add-more"> add more education </p>
                            <div className="icon-wrapper" >
                                <img className="icon add" src="images/add.png" width="25" height="25"/>
                            </div>
                        </div>
                    </div>
                </div>
            </article>
        );
    }
});

module.exports = AddSections;