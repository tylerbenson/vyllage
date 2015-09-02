var React = require('react/addons');
var moment = require('moment');
var classnames = require('classnames');
var cloneWithProps = require('react/lib/cloneWithProps');
var LayerMixin = require('react-layer-mixin');
var Tether = require('tether/tether');
var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
var isTouch = require('../isTouch');


var Datepicker = React.createClass({
  mixins: [React.addons.LinkedStateMixin , LayerMixin],
  getInitialState: function () {
    var date = this.props.date;

    var active_year;
    if( date == undefined || date =='NaN' || date == 'Present' || date == '' ){
      active_year = parseInt(moment(new Date()).format('YYYY'))
    }else{
      active_year = parseInt( date.split(' ')[1] );
    }

    return {
      isOpen: false,
      top: '-9999em',
      left: '-9999em',
      isFocused: false,
      onDatepicker: false,
      year: parseInt(moment(new Date(date)).format('YYYY')),
      month: moment(new Date(date)).format('MMM'),
      date: date ,
      active_year:active_year
    };
  },
  componentWillReceiveProps: function (nextProps) {
    if (nextProps.date !== this.props.date) {
      this.setState({
        date: nextProps.date,
        year: parseInt(moment(new Date(nextProps.date)).format('YYYY')),
        month: moment(new Date(nextProps.date)).format('MMM')
      });
    }
  },

  updateDate : function(key){
    var self = this;
    return {
      value: self.state[key],
      requestChange: function(year) {
        var newState = {};
        newState[key] = year;
        self.setState(newState);
        var yearPattern = /^\d{4}$/;
        if(year.match(yearPattern)) {
          var month;
          if( self.state.month == 'Invalid date' ){
            month = moment(new Date()).format('MMM');
          }else{
            month = self.state.month;
          } 
          self.setState({
            year: year,
            date: month + ' ' + year
          }, function () {
            self.setDate();
          });
        }
      }
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
  incrementYear: function (e) {
    e.preventDefault();

    if( this.state.date ){
      var tmp_date = this.state.date.split(' '),
          tmp_month = tmp_date[0],
          tmp_year = parseInt( tmp_date[1] );    
    }

    var year = this.state.date ? tmp_year : parseInt(moment(new Date()).format('YYYY'));
    var month = this.state.date ? tmp_month : moment(new Date()).format('MMM');
    year = year + 1;
 

    this.setState({
      year: year,
      date: month + ' ' + year,
      active_year: year
    }, function () {
      this.setDate();
    }.bind(this));


  },
  decrementYear: function (e) {
    e.preventDefault();

    if( this.state.date ){
      var tmp_date = this.state.date.split(' '),
          tmp_month = tmp_date[0],
          tmp_year = parseInt( tmp_date[1] );    
    }
    var year = this.state.date ? tmp_year : parseInt(moment(new Date()).format('YYYY'));
    var month = this.state.date ? tmp_month : moment(new Date()).format('MMM');
    year = year - 1;

    this.setState({
      year: year,
      date: month + ' ' + year,
      active_year: year
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
  onBlur: function (e) {
    var input = e.target.value;
    if(input.length > 0) {
      var date = moment(new Date(input)).format('MMM YYYY');
      if(date != 'Invalid date') {    
        this.setState({
          date: date,
          month: moment(new Date(input)).format('MMM'),
          year: moment(new Date(input)).format('YYYY')
        });
        this.setDate();
      } else {
        if( input != this.state.date ){
          this.setState({
            date: '',
            month: '',
            year: ''
          });
        }
      }
    }

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

      var activeMonth;
      if( this.state.date && this.state.date.search("Invalid") != 0 ){
        activeMonth = this.state.date.split(' ')[0];
      }else{
        activeMonth = moment(new Date()).format('MMM');
      }

      var monthNodes = months.map(function (month, index) {
        var className = classnames('month', {
          active: activeMonth === month
        });
        return (
          <span
            key={index}
            className={className}
            onClick={!isTouch ? this.selectMonth.bind(this, month, this.state.active_year): null}
            onTouchStart={isTouch ? this.selectMonth.bind(this, month, this.state.active_year): null}
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
            
           <input type="text" className="year" placeholder="Year" valueLink={this.updateDate('active_year')} /> 

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