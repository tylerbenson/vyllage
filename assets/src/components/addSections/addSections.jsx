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
                            <div className ="add-more-container">
                                <button className="u-pull-left article-btn"> {this.props.title} </button>
                                <button className="u-pull-right article-btn addSection-btn">{this.props.title}</button>
                            </div>
                            <p className="add-more"> no {this.props.title} added yet </p>
                        </div>
                    </div>
                </div>
            </article>
        );
    }
});

module.exports = AddSections;