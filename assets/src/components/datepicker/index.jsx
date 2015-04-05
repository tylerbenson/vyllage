var React = require('react');
var moment = require('moment');
var classnames = require('classnames');
var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

var Datepicker = React.createClass({
  getInitialState: function () {
    var date = new Date();
    return {
      isOpen: false,
      year: parseInt(moment(date).format('YYYY')),
      month:moment(date).format('MMM'),
    }
  },
  setDate: function () {
    this.props.setDate(this.props.name, {
      target: {
        value: this.state.month + ' ' + this.state.year
      }
    });
  },
  selectMonth: function (month) {
    this.props.open();
    this.setState({month: month});
    this.setDate();
  },
  incrementYear: function () {
    this.props.open();
    this.setState({year: this.state.year + 1});
    this.setDate();
  },
  decrementYear: function () {
    this.props.open();
    this.setState({year: this.state.year - 1});
    this.setDate();
  },
  renderDatepicker: function () {
    if (this.props.isOpen) {
      var monthNodes = months.map(function (month, index) {
        var className = classnames('month', {
          active: this.state.month === month
        });
        return (
          <span className={className} onClick={this.selectMonth.bind(this, month)}>
            {month}
          </span> 
        );
      }.bind(this))
      return (
        <div className="datepicker">
          <div className="header">
            <button className="small inverted primary icon" onClick={this.decrementYear}>
              <i className="ion-arrow-left-c"></i>
            </button>

            <span className="inline year">{this.state.year}</span> 

            <button className="small inverted primary icon" onClick={this.incrementYear}>
              <i className="ion-arrow-right-c"></i>
            </button>
          </div>
          <div className="content">
            {monthNodes}
          </div>
        </div>  
      );
    } else {
      return null;
    }
  }
});

module.exports = Datepicker;