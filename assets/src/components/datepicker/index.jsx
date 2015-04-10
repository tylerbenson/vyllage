var React = require('react');
var moment = require('moment');
var classnames = require('classnames');
var cloneWithProps = require('react/lib/cloneWithProps');
var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

var Datepicker = React.createClass({
  getInitialState: function () {
    var date = new Date();
    return {
      isOpen: false,
      isFocused: false,
      onDatepicker: false,
      year: parseInt(moment(date).format('YYYY')),
      month:moment(date).format('MMM'),
      date: this.props.date
    }
  },
  componentWillReceiveProps: function (nextProps) {
    if (nextProps.data !== this.props.date) {
      this.setState({date: nextProps.date});
    }
  },
  setDate: function () {
    this.props.setDate(this.props.name, {
      target: {
        value: this.state.date
      }
    });
  },
  selectMonth: function (month) {
    this.setState({
      month: month,
      date: month + ' ' + this.state.year
    }, function () {
      this.setDate();
    }.bind(this));
  },
  setYear: function (e) {
    var year = parseInt(e.target.value);
    this.setState({
      year: year,
      date: this.state.month + ' ' + year
    }, function () {
      this.setDate();
    }.bind(this));
  },
  incrementYear: function (e) {
    e.preventDefault();
    var year = this.state.year + 1;
    this.setState({
      year: year,
      date: this.state.month + ' ' + year
    }, function () {
      this.setDate();
    }.bind(this));
  },
  decrementYear: function (e) {
    e.preventDefault();
    var year = this.state.year - 1;
    this.setState({
      year: year,
      date: this.state.month + ' ' + year
    }, function () {
      this.setDate();
    }.bind(this));
  },
  changeHandler: function (e) {
    this.setState({
      isOpen: true,
      date: e.target.value
    }, function () {
      this.setDate();
    }.bind(this))
  },
  onFocus: function () {
    this.setState({
        isFocused: true,
        isOpen: true
    });
  },
  onBlur: function () {
    this.setState({
        isFocused: false,
        isOpen: this.state.onDatepicker
    });
  },
  showDatepicker: function () {
    this.setState({isOpen: this.state.isFocused});
  },
  hideDatepicker: function () {
    if (!this.state.isFocused) {
      this.setState({isOpen: false});
    }
  },
  enterDatepicker: function () {
    this.setState({onDatepicker: true})
  },
  leaveDatepicker: function () {
    this.setState({
      onDatepicker: false,
      isOpen: false
    })
  },
  renderDatepicker: function () {
    if (this.state.isOpen) {
      var monthNodes = months.map(function (month, index) {
        var className = classnames('month', {
          active: this.state.month === month
        });
        return (
          <span
            key={index}
            className={className}
            onClick={!Modernizr.touch ? this.selectMonth.bind(this, month): null}
            onTouchStart={Modernizr.touch ? this.selectMonth.bind(this, month): null}
          >
            {month}
          </span>
        );
      }.bind(this))
      return (
        <div
          className="datepicker"
          onMouseEnter={this.enterDatepicker}
          onMouseLeave={this.leaveDatepicker}
        >
          <div className="header">
            <button className="small inverted primary icon" onClick={this.decrementYear}>
              <i className="ion-arrow-left-c"></i>
            </button>

            <input type="text" className="inline year" placeholder="Year" value={this.state.year} onChange={this.setYear} />

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
  },
  render: function () {
    return (
      <span className='datepicker-trigger'>
        {cloneWithProps(this.props.children, {
          onFocus: this.onFocus,
          onBlur: this.onBlur,
          value: this.state.date,
          onChange: this.changeHandler,
          onClick: this.showDatepicker
        })}
        {this.renderDatepicker()}
      </span>
    );
  }
});

module.exports = Datepicker;