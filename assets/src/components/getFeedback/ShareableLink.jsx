var React = require('react');
var Clipboard = require('react-zeroclipboard');
var SuggestionSidebar = require('../suggestions/SuggestionSidebar');

var ShareableLink = React.createClass({
	getInitialState: function(){
		return {
			isReady: false,
			isCopied: false
		}
	},
	copyHandler: function(){
		this.setState({
			isCopied: true
		});
		setTimeout(function(){
			this.setState({
				isCopied: false
			});
		}.bind(this), 2500);
	},
	readyHandler: function(){
		this.setState({
			isReady: true
		});
	},
	render: function(){
		var isCopied = this.state.isCopied;
		var isReady = this.state.isReady;
		var classes = 'padded copy';
		var message = 'Copy';
		var icon = 'ion-android-clipboard';

		if(isCopied) {
			icon = 'ion-checkmark';
			message = 'Copied';
			classes += ' copied';
		}
		if(!isReady) {
			icon = 'ion-load-d';
			message = 'Wait..';
			classes += ' disabled';
		}

		return (
			<div className="sections">
				<div className="shareable section">
					<div className="container">
						<div className="content">
							<p className="tip">Anyone with the link below will be able to add feedback to your resume.</p>
							<input id="shareable-link" className="padded" type="text" value={this.props.url} readOnly />
							<Clipboard text={this.props.url} onAfterCopy={this.copyHandler} onReady={this.readyHandler}>
								<button className={classes}>
									<i className={icon}></i>
									{message}
								</button>
	            </Clipboard>
						</div>
					</div>
				</div>
				<SuggestionSidebar />
			</div>
		);
	}
});

module.exports = ShareableLink;