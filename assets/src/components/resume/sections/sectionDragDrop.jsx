module.exports = {
  dragSource:  {
    beginDrag: function(component) {
      return {
        item: {
          title: component.props.title
        }
      };
    }
  },
  dropTarget: {
    over: function (component, item) {
      component.props.moveCard(item.title, component.props.title);
    }
  }
}