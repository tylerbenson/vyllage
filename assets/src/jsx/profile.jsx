var Headline = React.createClass({	

    render: function() {
		return (
            <div className={"paragraph"} >
                <p className={"headline"} >{this.props.products.firstName + " " + this.props.products.middleName + " " + this.props.products.lastName}</p>
            </div>
		);
    }
});

var Tagline = React.createClass({ 

    render: function() {
        return (
            <div className={"paragraph"}>
                <p className={"tagline"} >{this.props.products.tagline}</p>
            </div>
        );
    }
});

var HeadlineContainerMain = React.createClass({
    render: function() {
        return (
            <div className={"headline-container main"}>
                <Headline products={this.props.products}/>
                <Tagline products={this.props.products} />
            </div>
        );
    }
});

var PRODUCTS = {firstName: 'Nathan', middleName: 'M', lastName: 'Benson', tagline: 'Technology Enthusiast analyzing, building, and expanding solutions'};

React.render(<HeadlineContainerMain products={PRODUCTS} />, document.getElementById('headline-container-main'));


