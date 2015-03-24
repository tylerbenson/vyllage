var React = require('react');
var EditBtn = require('../../buttons/edit');

var OrganizationMain = React.createClass({   

	goToEditMode: function() {
		if(this.props.goToEditMode) {
			this.props.goToEditMode();
		}
	},

	render: function() {
		return (
			<div className="nonEditable">
			 <div className='row'>
         <div className="twelve columns section-title">
            <p className='u-pull-left section-title'>{this.props.organizationData.title}</p>
            <p className='u-pull-right'><EditBtn editHandler={this.editHandler}/></p>
          </div>
        </div>
				<div onClick={this.goToEditMode}>

					<div className="main">
						<p className="organization-name">
							{this.props.organizationData.organizationName} 
						</p>
					</div>

					<div className="main">
						<div className="paragraph">
							<p className="organization-description">
								{this.props.organizationData.organizationDescription}
							</p>
						</div>
					</div>

					<div className="main">
						<p className="title">
							{this.props.organizationData.role}
						</p>
						<p className="start-date">
							{this.props.organizationData.startDate}
						</p>
						<p className="end-date">
							{this.props.organizationData.endDate}
						</p>
						<p className="location">
							{this.props.organizationData.location}
						</p>
					</div>

					<div className="main">
						<div className="paragraph">
							<p className="role-description">
								{this.props.organizationData.roleDescription}
							</p>
						</div>
					</div>

					<div className="main">
						<div className="paragraph">
							<p className="highlights">
								{this.props.organizationData.highlights}
							</p>
						</div>
					</div>
				</div>
			</div>
		);
	}
});

module.exports = OrganizationMain;