var React = require('react');
var actions = require('../actions');

var CareerGoal = React.createClass({
  getInitialState: function () {
    return {
      edit: false,
      description: this.props.careerGoal.description
    };
  },
  getDefaultProps: function () {
    return {
      careerGoal: {
        description: ""
      }
    }
  },
  editHandler: function (e) {
    e.preventDefault();
    this.setState({edit: true});
  },
  changeHandler: function (e) {
    e.preventDefault();
    this.setState({description: e.target.value});
  },
  saveHandler: function (e) {
    e.preventDefault();
    var careerGoal = this.props.careerGoal;
    careerGoal.description = this.state.description;
    actions.updateSection(careerGoal); 
    this.setState({edit: false});
  },
  cancelHandler: function (e) {
    e.preventDefault();
    this.setState({edit: false});
  },
  renderForm: function () {
    return (
      <div>
        <textarea 
          className='u-full-width'
          value={this.state.description}
          onChange={this.changeHandler}
        >
        </textarea>
        <a className='button' onClick={this.cancelHandler}>Cancel</a>
        <a className='button' onClick={this.saveHandler}>Save</a>
      </div>
    );
  },
  renderText: function () {
    return this.props.careerGoal.description;
  },
  render: function () {
    
    return (
      <article className='career-goal'>
        <div className='row'>
          <h4 className='u-pull-left'>Career Goal</h4>
          <a className='u-pull-right button' onClick={this.editHandler}> edit </a>
        </div>
        <div className="row">
          <div className="twelve columns">
            {this.state.edit ? this.renderForm(): this.renderText()} 
          </div>
        </div>
      </article>
    );
  }
});

module.exports = CareerGoal;