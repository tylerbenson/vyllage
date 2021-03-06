var React = require('react');
var cookie = require('tiny-cookie');
var classnames = require('classnames');

var slides = require('./slides');
var Modal = require('../modal');

var Tour = React.createClass({
	getInitialState: function(){
		return {
			isOpen: false,
			index: 0
		};
	},
	componentWillMount: function(){
    var page = this.props.page;
    var tour = parseInt(cookie.get(page + '_tour'));

    //no cookie yet
    if(isNaN(tour)) {
      this.setState({
        isOpen: true,
        index: 0
      });
    }
    else {
      //finished tour
      if(tour === -1) {
        this.setState({
          isOpen: false
        });
      }
      //unfinished tour
      else {
        this.setState({
          isOpen: true,
          index: tour
        });
      }
    }
  },
  closeModal: function(e) {
    e.preventDefault();
  },
  goToSlide: function(index) {
    var page = this.props.page;

    if(index < 0) {
      this.setState({
        isOpen: false
      });
      cookie.set(page + '_tour', -1);
    }
    else {
      this.setState({
        index: index
      });
      cookie.set(page + '_tour', index);
    }
  },
  render: function() {
    var slide = slides[this.props.page][this.state.index];

    var bannerImages = slides[this.props.page].map(function(slide, i){
      var classes = classnames({
        'visible': i === this.state.index,
        'banner': true
      });
      return <img key={i} className={classes} src={slide.image} alt={slide.title} />;
    }.bind(this));

    return (
    <Modal isOpen={this.state.isOpen} close={this.closeModal} className="medium tour">
      <div className="header">
        <div className="title">
         <h1>{slide.title}</h1>
        </div>
      </div>
      <div className="content">
        {bannerImages}
        <p>{slide.content}</p>
      </div>
      <div className="footer">
        {this.state.index > 0 ?
          <button className="flat" onClick={this.goToSlide.bind(this, parseInt(this.state.index) - 1)}>
            Previous
          </button>
        : null}
        {this.state.index < slides[this.props.page].length-1 ?
          <button onClick={this.goToSlide.bind(this, parseInt(this.state.index) + 1)}>
            <i className="ion-android-arrow-forward"></i>
            Next
          </button>
          :
          <button onClick={this.goToSlide.bind(this, -1)}>
            <i className="ion-checkmark"></i>
            Finish
          </button>
        }
        </div>
      </Modal>
    );
  }
});

module.exports = Tour;