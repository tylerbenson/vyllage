var React = require('react');
var moment = require('moment');
var classnames = require('classnames');
var cloneWithProps = require('react/lib/cloneWithProps');
var LayerMixin = require('react-layer-mixin');
var Tether = require('tether/tether');
var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
var isTouch = require('../isTouch');

var Datepicker = React.createClass({
  mixins: [LayerMixin],
  getInitialState: function () {
    var date = this.props.date;
    return {
      isOpen: false,
      top: '-9999em',
      left: '-9999em',
      isFocused: false,
      onDatepicker: false,
      year: parseInt(moment(date).format('YYYY')),
      month: moment(date).format('MMM'),
      date: date
    };
  },
  componentWillReceiveProps: function (nextProps) {
    if (nextProps.date !== this.props.date) {
      this.setState({
        date: nextProps.date,
        year: parseInt(moment(nextProps.date).format('YYYY')),
        month: moment(nextProps.date).format('MMM')
      });
    }
  },
  setDate: function () {
    this.props.setDate(this.props.name, {
      target: {
        value: this.state.date
      }
    });
  },
  selectMonth: function (month, year) {
    this.setState({
      month: month,
      date: month + ' ' + year
    }, function () {
      this.setDate();
    }.bind(this));
  },
  setYear: function (e) {
    var year = parseInt(e.target.value) || '';
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
    }.bind(this));
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
  showDatepicker: function (e) {
    this.setState({
      isOpen: this.state.isFocused,
    });
  },
  hideDatepicker: function () {
    if (!this.state.isFocused) {
      this.setState({
        isOpen: false
      });
    }
  },
  tetherElement: function () {
    this.tether = new Tether({
      element: this._layer,
      target: this.getDOMNode(),
      attachment: 'top center',
      targetAttachment: 'bottom left',
    });
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
  componentDidUpdate: function () {
    if (this.state.isOpen) {
      this.tetherElement();
    } else {
      this.tether && this.tether.destroy();
    }
  },
  renderLayer: function () {
    if (this.state.isOpen) {
      var isCurrent = this.props.isCurrent;
      var activeMonth = this.state.month !== 'Invalid date' ? this.state.month : moment(new Date()).format('MMM');
      var activeYear = this.state.year ? this.state.year : parseInt(moment(new Date()).format('YYYY'));
      var monthNodes = months.map(function (month, index) {
        var className = classnames('month', {
          active: activeMonth === month
        });
        return (
          <span
            key={index}
            className={className}
            onClick={!isTouch ? this.selectMonth.bind(this, month, activeYear): null}
            onTouchStart={isTouch ? this.selectMonth.bind(this, month, activeYear): null}
          >
            {month}
          </span>
        );
      }.bind(this));
      return (
        <div
          className="datepicker"
          onMouseEnter={this.enterDatepicker}
          onMouseLeave={this.leaveDatepicker}
        >
          <div className={(isCurrent?'disabled':'') + ' header'}>
            <button className="small inverted primary icon" onClick={this.decrementYear}>
              <i className="ion-arrow-left-c"></i>
            </button>

            <input type="text" className="year" placeholder="Year" value={activeYear} onChange={this.setYear} />

            <button className="small inverted primary icon" onClick={this.incrementYear}>
              <i className="ion-arrow-right-c"></i>
            </button>
          </div>
          <div className={(isCurrent?'disabled':'') + ' content'}>
            {monthNodes}
          </div>
          {(this.props.name==="endDate")?
          <div className={(isCurrent?'enabled':'') + ' footer'}>
            <input type="checkbox" className="current" checked={this.props.isCurrent} onChange={this.props.toggleCurrent} />
            current position
          </div>
          :null}
        </div>
      );
    } else {
      return null;
    }
  },
  render: function () {
    return (
      <div className='datepicker-trigger'>
        {cloneWithProps(this.props.children, {
          onFocus: this.onFocus,
          onBlur: this.onBlur,
          value: this.state.date,
          onChange: this.changeHandler,
          onClick: this.showDatepicker
        })}
      </div>
    );
  }
});

module.exports = Datepicker;