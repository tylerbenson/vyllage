var React = require('react');

var Tag = React.createClass({
	render: function() {
		return (
			<div className="tag">
				{this.props.text}
				{this.props.uiEditMode ?
					<button className="flat icon small secondary" onClick={this.props.onDelete}>
						<i className="ion-close"></i>
					</button>
				: null}
			</div>
		);
	}

});

module.exports = Tag;