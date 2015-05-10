module.exports = {
  dragSource:  {
    beginDrag: function(component) {
      return {
        item: {
          index: component.props.index
        }
      };
    }
  },
  dropTarget: {
    over: function (component, item) {
      component.props.moveSection(item.index, component.props.index);
    }
  }
}